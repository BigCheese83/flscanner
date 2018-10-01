package ru.bigcheese.flscanner.task;

import ru.bigcheese.flscanner.model.ParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParseExecutorTask implements Runnable {

    private final ExecutorService executorService;
    private final List<ParseTask> tasks;
    private final ParseTaskService parseTaskService;

    public ParseExecutorTask(ExecutorService executorService, List<ParseTask> tasks, ParseTaskService parseTaskService) {
        this.executorService = executorService;
        this.tasks = tasks;
        this.parseTaskService = parseTaskService;
    }

    @Override
    public void run() {
        CompletionService<ParseResult> ecs = new ExecutorCompletionService<>(executorService);
        List<ParseTask> activeList = new ArrayList<>();

        for (ParseTask task : tasks) {
            Future<ParseResult> future = ecs.submit(task);
            parseTaskService.registerTask(task, future);
            activeList.add(task);
        }

        for (int i = 0; i < tasks.size(); i++) {
            try {
                ParseResult result = ecs.take().get();
                parseTaskService.processParseResult(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
                parseTaskService.fireErrorEvent(e.getCause());
            }
        }

        parseTaskService.unregisterTasks(activeList);
    }
}
