package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * <p>
 * This exploits the fact that an Eclipse plugin is an OSGi bundle.
 * </p>
 * <p>
 * The plugin must be singleton.
 * </p>
 * <p>
 * Do not override the default methods, unless otherwise allowed.
 * </p>
 */
public interface Eclipse_Plugin {

    /**
     * For provider, not for user.
     */
    final class Private {

        private final BundleContext context;
        private final Scheduler scheduler;

        public Private (BundleContext context, Scheduler scheduler) {
            this.context = context;
            this.scheduler = scheduler;
        }

    }

    /**
     * For provider, not for user.
     */
    Private _Eclipse_Plugin__Private ();

    // ---------- OSGi

    default BundleContext get_BundleContext () {
        return _Eclipse_Plugin__Private().context;
    }

    default Bundle get_Bundle () {
        return get_BundleContext().getBundle();
    }

    /**
     * See {@link BundleContext#getServiceReference(Class)}.
     */
    default <T> ServiceReference<T> get_ServiceReference (Class<T> cls) {
        return get_BundleContext().getServiceReference(cls);
    }

    /**
     * See {@link BundleContext#getService(ServiceReference)}.
     */
    default <T> T get_service (ServiceReference<T> ref) {
        return get_BundleContext().getService(ref);
    }

    /**
     * See {@link BundleContext#ungetService(ServiceReference)}.
     */
    default <T> boolean unget_service (ServiceReference<T> ref) {
        return get_BundleContext().ungetService(ref);
    }

    // ---------- Eclipse

    default Scheduler get_scheduler () {
        return _Eclipse_Plugin__Private().scheduler;
    }

    default StatusManager get_StatusManager () {
        return StatusManager.getManager();
    }

    /**
     * The Eclipse plugin ID the same as the
     * {@linkplain Bundle#getSymbolicName() OSGi bundle symbolic name}.
     */
    default String get_plugin_id () {
        return get_Bundle().getSymbolicName();
    }

    /**
     * Create a Status with {@linkplain IStatus#ERROR ERROR} severity.
     */
    default Status create_Status (Throwable throwable) {
        return new Status(IStatus.ERROR, get_plugin_id(), throwable.getMessage(), throwable);
    }

    /**
     * @see StatusManager
     */
    default void handle_status (IStatus status) {
        get_StatusManager().handle(status);
    }

    /**
     * Convenience method that combines
     * {@link #handle_status(IStatus)} and
     * {@link #create_Status(Throwable)}.
     */
    default void handle_status (Throwable throwable) {
        handle_status(create_Status(throwable));
    }

}
