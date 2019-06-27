package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.internal.progress.ProgressManager.JobMonitor;

final class JobMonitor_Progress_Monitor implements Progress_Monitor, Wraps_an_IProgressMonitor {

    final JobMonitor delegate;

    public JobMonitor_Progress_Monitor (JobMonitor monitor) {
        this.delegate = monitor;
    }

    @Override
    public void set_progress (int done, int total) {
        delegate.setProgress(done, total);
    }

    @Override
    public void set_caption (String text) {
        delegate.setTaskName(text);
    }

    @Override
    public boolean is_canceled () {
        return delegate.isCanceled();
    }

    @Override
    public IProgressMonitor unwrap_IProgressMonitor () {
        return delegate;
    }

}
