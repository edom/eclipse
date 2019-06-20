package com.spacetimecat.mylyn.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class My_Plugin extends Plugin {

    private static My_Plugin INSTANCE;

    public static int ERROR_UNKNOWN = -1;

    public static My_Plugin getInstance () {
        return INSTANCE;
    }

    @Override
    public void start (BundleContext context) throws Exception {
        super.start(context);
        if (INSTANCE != null) {
            throw new AssertionError();
        }
        INSTANCE = this;
    }

    public static String get_plugin_id () {
        return getInstance().getBundle().getSymbolicName();
    }

    public static IStatus make_error (String message) {
        return new Status(Status.ERROR, get_plugin_id(), message);
    }

    public static IStatus make_error (TaskRepository repository, String message) {
        return new RepositoryStatus(Status.ERROR, get_plugin_id(), ERROR_UNKNOWN, message);
    }

    public static CoreException make_CoreException (TaskRepository repository, String message) {
        return new CoreException(My_Plugin.make_error(repository, message));
    }

}
