package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public interface My_Closeable extends AutoCloseable {

    @Override
    void close ();

    static My_Closeable begin_task (IProgressMonitor monitor, String name, int total_work) {
        monitor.beginTask(name, total_work);
        return monitor::done;
    }

    static My_Closeable of_sub_monitor (SubMonitor monitor) {
        return monitor::done;
    }

}