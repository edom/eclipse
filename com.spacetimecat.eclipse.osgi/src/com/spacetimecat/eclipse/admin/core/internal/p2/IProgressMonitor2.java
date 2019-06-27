package com.spacetimecat.eclipse.admin.core.internal.p2;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IProgressMonitor2 extends IProgressMonitor, AutoCloseable {

    @Override
    void close ();

}
