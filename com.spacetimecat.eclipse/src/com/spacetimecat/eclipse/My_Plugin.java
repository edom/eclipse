package com.spacetimecat.eclipse;

import org.osgi.framework.BundleContext;

public class My_Plugin extends Eclipse_Plugin {

    private static My_Plugin instance;

    public My_Plugin () {
    }

    @Override
    public void start (BundleContext context) throws Exception {
        super.start(context);
        instance = this;
    }

    @Override
    public void stop (BundleContext context) throws Exception {
        instance = null;
        super.stop(context);
    }

    public static My_Plugin get_instance () {
        return instance;
    }

}
