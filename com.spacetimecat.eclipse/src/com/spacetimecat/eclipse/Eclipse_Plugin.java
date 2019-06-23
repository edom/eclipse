package com.spacetimecat.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * This exploits the fact that an Eclipse plugin is an OSGi bundle.
 */
public class Eclipse_Plugin extends Plugin {

    /**
     * The Eclipse plugin ID is assumed to be the same as the OSGi bundle
     * {@linkplain Bundle#getSymbolicName() symbolic name}.
     */
    public String get_plugin_id () {
        return getBundle().getSymbolicName();
    }

    /**
     * Create a Status with {@linkplain IStatus#ERROR ERROR} severity.
     */
    public Status create_Status (Throwable throwable) {
        return new Status(IStatus.ERROR, get_plugin_id(), throwable.getMessage(), throwable);
    }

    /**
     * @see StatusManager
     */
    public void handle_status (IStatus status) {
        StatusManager.getManager().handle(status);
    }

    /**
     * Convenience method that combines
     * {@link #handle_status(IStatus)} and
     * {@link #create_Status(Throwable)}.
     */
    public void handle_status (Throwable throwable) {
        handle_status(create_Status(throwable));
    }

    public <T> T get_service (Class<T> cls) {
        final BundleContext context = getBundle().getBundleContext();
        final ServiceReference<T> ref = context.getServiceReference(cls);
        if (ref == null) {
            throw new NullPointerException("No such service: " + cls);
        }
        final T service = context.getService(ref);
        if (service == null) {
            throw new IllegalStateException("Service has been unregistered: " + cls);
        }
        return service;
    }

}
