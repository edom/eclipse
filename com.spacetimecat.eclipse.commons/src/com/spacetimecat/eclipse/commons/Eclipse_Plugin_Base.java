package com.spacetimecat.eclipse.commons;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Make OSGi {@link BundleActivator} more useful.
 * </p>
 * <p>
 * Usage:
 * </p>
 * <ul>
 * <li>Subclass this class.</li>
 * <li>In your bundle's MANIFEST.MF, specify that subclass as your bundle activator.</li>
 * <li>Pass around the object as an {@link Eclipse_Plugin}.</li>
 * </ul>
 * <p>
 * Reminder: An Eclipse plugin is an OSGi bundle.
 * </p>
 */
public class Eclipse_Plugin_Base extends Plugin implements Eclipse_Plugin {

    private Eclipse_Plugin.Private _0;

    protected Eclipse_Plugin_Base () {}

    @Override
    public void start (BundleContext context) throws Exception {
        super.start(context);
        _0 = new Eclipse_Plugin.Private(context, Schedulers.create_Scheduler(this));
    }

    @Override
    public void stop (BundleContext context) throws Exception {
        _0 = null;
        super.stop(context);
    }

    @Override
    public Private _Eclipse_Plugin__Private () {
        return _0;
    }

}
