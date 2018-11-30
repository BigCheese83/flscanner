package ru.bigcheese.flscanner.task;

import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.event.ParseTaskEvent;
import ru.bigcheese.flscanner.event.ParseTaskEventListener;
import ru.bigcheese.flscanner.model.AppProps;
import ru.bigcheese.flscanner.model.ParseError;
import ru.bigcheese.flscanner.model.ParseResult;
import ru.bigcheese.flscanner.model.Post;
import ru.bigcheese.flscanner.model.TaskMetaInfo;
import ru.bigcheese.flscanner.tray.SystemTrayService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableMap;
import static ru.bigcheese.flscanner.event.ParseTaskEventType.UPDATES_FOUND;
import static ru.bigcheese.flscanner.event.ParseTaskEventType.ERROR;
import static ru.bigcheese.flscanner.util.CommonUtils.getSystemTrayService;
import static ru.bigcheese.flscanner.util.CommonUtils.getStacktrace;

public class ParseTaskService {

    private static final ParseTaskService instance = new ParseTaskService();

    private static final int SCHEDULED_POOL_SIZE = 3;
    private static final int THREAD_POOL_MAX_SIZE = 100;

    private final Settings settings = Settings.getInstance();
    private final Map<String, List<Post>> posts = new ConcurrentHashMap<>();
    private final Map<String, Long> timeouts = new ConcurrentHashMap<>();
    private final Map<TaskMetaInfo, Future<ParseResult>> activeTasks = new ConcurrentHashMap<>();
    private final Map<TaskMetaInfo, ScheduledFuture<?>> executorTasks = new ConcurrentHashMap<>();
    private final List<ParseTaskEventListener> listeners = new CopyOnWriteArrayList<>();

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledService;

    private volatile boolean isScheduledScan = false;

    private ParseTaskService() {
        int poolSize = settings.getAllSiteConfigs().size() * SCHEDULED_POOL_SIZE;
        if (poolSize > THREAD_POOL_MAX_SIZE) {
            poolSize = THREAD_POOL_MAX_SIZE;
        }
        executorService = Executors.newFixedThreadPool(poolSize);
        scheduledService = Executors.newScheduledThreadPool(SCHEDULED_POOL_SIZE);

        addListener(event -> {
            SystemTrayService trayService = getSystemTrayService();
            switch (event.getType()) {
                case UPDATES_FOUND:
                    ParseResult parseResult = (ParseResult) event.getPayload();
                    trayService.displayMessage(
                            "Updates found for",
                            parseResult.getMetaInfo().getName() + " (" + parseResult.getPosts().size() + ")",
                            UPDATES_FOUND);
                    break;
                case ERROR:
                    ParseError error = (ParseError) event.getPayload();
                    trayService.displayMessage(
                            "Error!",
                            error.getException() + ": " + error.getMessage(),
                            ERROR);
                    break;
            }
        });
    }

    public static ParseTaskService getInstance() {
        return instance;
    }

    public synchronized void scheduledScan() {
        if (!isScheduledScan) {
            ParseExecutorTask executorTask = new ParseExecutorTask(
                    executorService, createTaskList(true), this);
            ScheduledFuture<?> future = scheduledService.scheduleAtFixedRate(
                    executorTask, 0, settings.getAppProps().getPullInterval(), TimeUnit.SECONDS);
            String taskName = "Scheduled task " + System.currentTimeMillis();
            executorTasks.put(new TaskMetaInfo(taskName, true), future);
            isScheduledScan = true;
        }
    }

    public synchronized void stopScan() {
        activeTasks.forEach((meta, future) -> future.cancel(true));
        activeTasks.clear();
        executorTasks.forEach((meta, future) -> future.cancel(true));
        executorTasks.clear();
        isScheduledScan = false;
    }

    public synchronized void scanOnce() {
        ParseExecutorTask executorTask = new ParseExecutorTask(
                executorService, createTaskList(false), this);
        ScheduledFuture<?> future = scheduledService.schedule(executorTask, 0, TimeUnit.MILLISECONDS);
        String taskName = "Scan once task " + System.currentTimeMillis();
        executorTasks.put(new TaskMetaInfo(taskName, false), future);
    }

    public synchronized void refreshStateTasks() {
        activeTasks.forEach((meta, future) -> {
            if (future.isDone() || future.isCancelled()) {
                activeTasks.remove(meta);
            }
        });
        executorTasks.forEach((meta, future) -> {
            if (future.isDone() || future.isCancelled()) {
                executorTasks.remove(meta);
            }
        });
    }

    public Map<String, List<Post>> getAllPosts() {
        return unmodifiableMap(posts);
    }

    public void addListener(ParseTaskEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ParseTaskEventListener listener) {
        listeners.remove(listener);
    }

    void registerTask(ParseTask task, Future<ParseResult> future) {
        activeTasks.put(task.getMetaInfo(), future);
    }

    void processParseResult(ParseResult result) {
        System.out.println(result); //TODO
        String name = result.getMetaInfo().getName();
        posts.putIfAbsent(name, new ArrayList<>());
        posts.get(name).addAll(result.getPosts());
        if (result.getTimestamp() > 0) {
            timeouts.put(name, result.getTimestamp());
        }
        activeTasks.remove(result.getMetaInfo());
        if (!result.getPosts().isEmpty()) {
            fireEvent(new ParseTaskEvent(this, UPDATES_FOUND, result));
        }
    }

    void fireErrorEvent(Throwable e) {
        ParseError error = new ParseError(e.getLocalizedMessage(), e.getClass().getName(), getStacktrace(e));
        fireEvent(new ParseTaskEvent(this, ERROR, error));
    }

    public void printActiveTasks() {
        System.out.println(activeTasks);
    }

    private void fireEvent(ParseTaskEvent event) {
        for (ParseTaskEventListener el : listeners) {
            el.handleEvent(event);
        }
    }

    private List<ParseTask> createTaskList(boolean isScheduled) {
        final AppProps appProps = settings.getAppProps();
        return settings.getSiteConfigs().stream()
                .map(sc -> new ParseTask(sc, appProps, unmodifiableMap(timeouts), isScheduled))
                .collect(Collectors.toList());
    }

}
