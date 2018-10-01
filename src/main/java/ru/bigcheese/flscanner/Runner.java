package ru.bigcheese.flscanner;

import ru.bigcheese.flscanner.config.SettingsOld;
import ru.bigcheese.flscanner.config.SiteConfigOld;
import ru.bigcheese.flscanner.config.SysProps;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by BigCheese on 23.06.16.
 */
public abstract class Runner {

    private final static List<ParseTask> tasks = getTasks();
    private final static List<ScheduledFuture<?>> futures = new ArrayList<>();
    private final static List<PropertyChangeListener> listeners = new ArrayList<>();
    private final static PropertyChangeSupport pcs = new PropertyChangeSupport(futures);
    private final static ScheduledExecutorService executor
            = Executors.newScheduledThreadPool(tasks.size() + 1);

    public static void runScan() {
        futures.clear();
        for (ParseTask task : tasks) {
            ScheduledFuture<?> future =
                    executor.scheduleAtFixedRate(task, 0, SysProps.getInstance().getPullInterval(), TimeUnit.SECONDS);
            futures.add(future);
        }
        pcs.firePropertyChange("scan", false, true);
        System.out.println("Run scan ...");
    }

    public static void stopScan() {
        for (ScheduledFuture<?> f : futures) {
            f.cancel(true);
        }
        pcs.firePropertyChange("scan", true, false);
        System.out.println("Stop scan ...");
    }

    public static void runOnce() {
        for (ParseTask task : getTasks()) {
            for (PropertyChangeListener l : listeners) {
                task.addPropertyChangeListener(l);
            }
            executor.schedule(task, 0, TimeUnit.SECONDS);
        }
        System.out.println("Run once ...");
    }

    public static void shutdownNow() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("Shutdown ScheduledExecutorService is interrupted.");
        }
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("PropertyChangeListener argument is null.");
        }
        for (ParseTask task : tasks) {
            task.addPropertyChangeListener(listener);
        }
        listeners.add(listener);
        pcs.addPropertyChangeListener(listener);
    }

    private static List<ParseTask> getTasks() {
        List<ParseTask> tasks = new ArrayList<>();
        for (SiteConfigOld config : SettingsOld.getConfigs()) {
            tasks.add(new ParseTask(config));
        }
        return tasks;
    }
}
