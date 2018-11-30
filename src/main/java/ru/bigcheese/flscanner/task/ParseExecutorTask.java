package ru.bigcheese.flscanner.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bigcheese.flscanner.model.ParseResult;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ParseExecutorTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ParseExecutorTask.class);

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

        for (ParseTask task : tasks) {
            Future<ParseResult> future = ecs.submit(task);
            parseTaskService.registerTask(task, future);
        }

        for (int i = 0; i < tasks.size(); i++) {
            try {
                ParseResult result = ecs.take().get();
                parseTaskService.processParseResult(result);
            } catch (InterruptedException e) {
                log.warn("interrupted task", e);
            } catch (ExecutionException e) {
                log.error("task error", e);
                parseTaskService.fireErrorEvent(e.getCause());
            }
        }

        parseTaskService.refreshStateTasks();
    }
}
