package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IProgressMonitor;

final class IProgressMonitor_Progress_Monitor implements Progress_Monitor, Wraps_an_IProgressMonitor {

    final IProgressMonitor delegate;

    public IProgressMonitor_Progress_Monitor (IProgressMonitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean is_canceled () {
        synchronized (this) {
            return delegate.isCanceled();
        }
    }

    @Override
    public void set_caption (String text) {
        synchronized (this) {
            delegate.setTaskName(text);
        }
    }

    @Override
    public void set_progress (int done, int total) {
        // Not implemented.
    }

    @Override
    public IProgressMonitor unwrap_IProgressMonitor () {
        return delegate;
    }

}
