package com.spacetimecat.eclipse.commons;

import java.util.concurrent.Future;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public final class Schedulers {

    private Schedulers () {}

    public static Scheduler create_Scheduler (Eclipse_Plugin plugin) {
        return new Default_Scheduler(plugin);
    }

    static Future<?> schedule (Eclipse_Plugin plugin, IViewSite site, Consumer0<Progress_Monitor> task) {
        final Job_Future<?> future = create_Job(plugin, task);
        site.getService(IWorkbenchSiteProgressService.class).schedule(future.job);
        return future;
    }

    static Job_Future<?> create_Job (Eclipse_Plugin plugin, Consumer0<Progress_Monitor> task) {
        final Job_Future<?> future = new Job_Future<>();
        final Job job = new Job("") {
            @Override
            protected IStatus run (IProgressMonitor monitor) {
                try {
                    monitor.beginTask("", IProgressMonitor.UNKNOWN);
                    task.accept(Progress_Monitors.enhance(monitor));
                    future.complete_normally(null);
                    return Status.OK_STATUS;
                } catch (InterruptedException e) {
                    future.complete_with_cancellation();
                    Thread.currentThread().interrupt();
                    return Status.CANCEL_STATUS;
                } catch (OperationCanceledException e) {
                    future.complete_with_cancellation();
                    return Status.CANCEL_STATUS;
                } catch (Exception e) {
                    future.complete_abnormally(e);
                    return plugin.create_Status(e);
                } catch (Throwable e) {
                    future.complete_abnormally(e);
                    throw e;
                }
            }
        };
        future.job = job;
        return future;
    }

}
