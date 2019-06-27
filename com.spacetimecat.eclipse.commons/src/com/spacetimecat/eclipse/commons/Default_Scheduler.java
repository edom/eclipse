package com.spacetimecat.eclipse.commons;

import java.util.concurrent.Future;

final class Default_Scheduler implements Scheduler {

    private final Eclipse_Plugin plugin;

    public Default_Scheduler (Eclipse_Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Future<?> schedule (Consumer0<Progress_Monitor> task) {
        final Job_Future<?> future = Schedulers.create_Job(plugin, task);
        future.job.schedule();
        return future;
    }

}
