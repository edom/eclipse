package com.spacetimecat.eclipse.admin.core.internal.p2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

// XXX What happens if this class is instantiated while there is no workbench?
/**
 * <p>
 * A {@link Job} that exists only to provide a {@link IProgressMonitor},
 * with the assumption that one running Job corresponds to one row in the Progress View.
 * </p>
 * <p>
 * This is wasteful but convenient.
 * </p>
 */
public final class Monitor_Job extends Job implements AutoCloseable {

    private IProgressMonitor2 monitor;
    private boolean done;

    private Monitor_Job (String name) {
        super(name);
    }

    /**
     * Create and start the job.
     */
    private static Monitor_Job create (String name) {
        final Monitor_Job job = new Monitor_Job(name);
        job.schedule();
        return job;
    }

    /**
     * See {@link #get_monitor()} for notes about leakage.
     *
     * @param job_name this is not task name
     */
    public static IProgressMonitor2 create_monitor (String job_name) {
        return create(job_name).get_monitor();
    }

    /**
     * <p>
     * To avoid leakage, at least one of these must be done after the caller has finished using the monitor:
     * </p>
     * <ul>
     * <li>Call the monitor's {@link IProgressMonitor2#close()} method.</li>
     * <li>Call the monitor's {@link IProgressMonitor2#done()} method.</li>
     * <li>Call the job's {@link #close()} method.</li>
     * </ul>
     */
    public IProgressMonitor2 get_monitor () {
        synchronized (this) {
            while (monitor == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
            return monitor;
        }
    }

    @Override
    protected IStatus run (IProgressMonitor monitor) {
        synchronized (this) {
            this.monitor = new IProgressMonitor2() {

                @Override
                public void worked (int work) {
                    monitor.worked(work);
                }

                @Override
                public void subTask (String name) {
                    monitor.subTask(name);
                }

                @Override
                public void setTaskName (String name) {
                    monitor.setTaskName(name);
                }

                @Override
                public void setCanceled (boolean value) {
                    monitor.setCanceled(value);
                }

                @Override
                public boolean isCanceled () {
                    return monitor.isCanceled();
                }

                @Override
                public void internalWorked (double work) {
                    monitor.internalWorked(work);
                }

                @Override
                public void done () {
                    close();
                    monitor.done();
                }

                @Override
                public void beginTask (String name, int totalWork) {
                    monitor.beginTask(name, totalWork);
                }

                @Override
                public void close () {
                    Monitor_Job.this.close();
                }
            };
            notifyAll();
            while (!done) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                }
            }
            return Status.OK_STATUS;
        }
    }

    /**
     * This is safe to call many times.
     */
    @Override
    public void close () {
        synchronized (this) {
            done = true;
            notifyAll();
        }
    }

}
