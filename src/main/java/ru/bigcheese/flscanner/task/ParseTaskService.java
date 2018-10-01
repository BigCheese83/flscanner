package ru.bigcheese.flscanner.task;

import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.event.ParseTaskEvent;
import ru.bigcheese.flscanner.event.ParseTaskEventListener;
import ru.bigcheese.flscanner.event.ParseTaskEventType;
import ru.bigcheese.flscanner.model.*;
import ru.bigcheese.flscanner.util.CommonUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static ru.bigcheese.flscanner.event.ParseTaskEventType.STOPPED_ALL;
import static ru.bigcheese.flscanner.util.CommonUtils.*;

public class ParseTaskService {

    private static final ParseTaskService instance = new ParseTaskService();

    private final Settings settings = Settings.getInstance();
    private final Map<String, List<Post>> posts = new ConcurrentHashMap<>();
    private final Map<String, Long> timeouts = new ConcurrentHashMap<>();
    private final Map<TaskMetaInfo, Future<ParseResult>> activeTasks = new ConcurrentHashMap<>();

    private List<ParseTaskEventListener> listeners = new CopyOnWriteArrayList<>();


    private ExecutorService executorService = Executors.newFixedThreadPool(settings.getAllSiteConfigs().size()*3);
    private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(3);

    private ScheduledFuture<?> scheduledFuture;
    private ScheduledFuture<?> scanOnceFuture;
    private boolean isScheduledScan = false;

    private ParseTaskService() {
    }

    public static ParseTaskService getInstance() {
        return instance;
    }

    public synchronized void scheduledScan() {
        long start = System.currentTimeMillis();
        if (!isScheduledScan) {
            ParseExecutorTask executorTask = new ParseExecutorTask(executorService, createTaskList(true), this);
            scheduledService.scheduleAtFixedRate(
                    executorTask, 0, settings.getAppProps().getPullInterval(), TimeUnit.SECONDS);
            isScheduledScan = true;
        }
        System.out.println("execution at " + (System.currentTimeMillis() - start) + " ms");
    }

    public synchronized void stopScan() {
        long start = System.currentTimeMillis();

        //scheduledFuture.cancel(true);
        //scanOnceFuture.cancel(true);
        activeTasks.forEach((k, v) -> v.cancel(true));
        activeTasks.clear();

        isScheduledScan = false;
        fireEvent(new ParseTaskEvent(this, STOPPED_ALL));
        System.out.println("execution at " + (System.currentTimeMillis() - start) + " ms");
    }

    public synchronized void scanOnce() {
        ParseExecutorTask executorTask = new ParseExecutorTask(executorService, createTaskList(false), this);
        scheduledService.schedule(executorTask, 0, TimeUnit.MILLISECONDS);
    }

    public void registerTask(ParseTask task, Future<ParseResult> future) {
        activeTasks.put(task.getMetaInfo(), future);
    }

    public void unregisterTasks(List<ParseTask> tasks) {
        tasks.forEach(t -> activeTasks.remove(t.getMetaInfo()));
    }

    public void processParseResult(ParseResult result) {
        String name = result.getMetaInfo().getName();
        posts.putIfAbsent(name, new ArrayList<>());
        posts.get(name).addAll(result.getPosts());
        if (result.getTimestamp() > 0) {
            timeouts.put(name, result.getTimestamp());
        }
        activeTasks.remove(result.getMetaInfo());
    }

    public void fireErrorEvent(Throwable e) {
        ParseError error = new ParseError(e.getLocalizedMessage(), getStacktrace(e));
        fireEvent(new ParseTaskEvent(this, ParseTaskEventType.ERROR, error));
    }

    public void printActiveTasks() {
        System.out.println(activeTasks);
    }

    public void addListener(ParseTaskEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ParseTaskEventListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(ParseTaskEvent event) {
        for (ParseTaskEventListener el : listeners) {
            el.handleEvent(event);
        }
    }

    private List<ParseTask> createTaskList(boolean isScheduled) {
        final AppProps appProps = settings.getAppProps();
        return settings.getSiteConfigs().stream()
                .map(sc -> new ParseTask(sc, appProps,
                        getLastTimestamp(sc.getName(), appProps.getScanDaysOnStart()), isScheduled))
                .collect(Collectors.toList());
    }

    private long getLastTimestamp(String name, int scanDaysOnStart) {
        Long last = timeouts.get(name);
        if (last != null && last > 0) {
            return last;
        } else {
            return System.currentTimeMillis() - TimeUnit.DAYS.toMillis(scanDaysOnStart);
        }
    }
}
