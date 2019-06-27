package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.internal.progress.ProgressManager.JobMonitor;

public final class Progress_Monitors {

    private Progress_Monitors () {}

    public static Progress_Monitor create_meager () {
        return new Meager_Progress_Monitor();
    }

    public static Progress_Monitor enhance (IProgressMonitor monitor) {
        if (monitor instanceof Progress_Monitor_IProgressMonitor) {
            return ((Progress_Monitor_IProgressMonitor) monitor).delegate;
        }
        if (monitor instanceof JobMonitor) {
            return new JobMonitor_Progress_Monitor((JobMonitor) monitor);
        }
        return new IProgressMonitor_Progress_Monitor(monitor);
    }

    public static IProgressMonitor degrade (Progress_Monitor monitor) {
        if (monitor instanceof Wraps_an_IProgressMonitor) {
            return ((Wraps_an_IProgressMonitor) monitor).unwrap_IProgressMonitor();
        }
        return new Progress_Monitor_IProgressMonitor(monitor);
    }

}
