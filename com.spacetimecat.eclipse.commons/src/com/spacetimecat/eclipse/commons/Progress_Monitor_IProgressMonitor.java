package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IProgressMonitor;

final class Progress_Monitor_IProgressMonitor implements IProgressMonitor {

    final Progress_Monitor delegate;

    private int done;
    private int total;

    public Progress_Monitor_IProgressMonitor (Progress_Monitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void beginTask (String name, int totalWork) {
        synchronized (this) {
            delegate.set_caption(name);
            delegate.set_progress(0, totalWork);
            this.total = totalWork;
        }
    }

    @Override
    public void done () {
        // Users should not call this.
        // Providers call this automatically.
    }

    @Override
    public void internalWorked (double work) {
        throw new IllegalStateException("should be called by provider, not by user");
    }

    @Override
    public boolean isCanceled () {
        synchronized (this) {
            return delegate.is_canceled();
        }
    }

    @Override
    public void setCanceled (boolean value) {
        throw new IllegalStateException("should be called by provider, not by user");
    }

    @Override
    public void setTaskName (String name) {
        synchronized (this) {
            delegate.set_caption(name);
        }
    }

    @Override
    public void subTask (String name) {
        synchronized (this) {
            delegate.set_caption(name);
        }
    }

    @Override
    public void worked (int work) {
        synchronized (this) {
            done += work;
            delegate.set_progress(done, total);
        }
    }

}
