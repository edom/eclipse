package com.spacetimecat.eclipse.admin.core.internal;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

import com.spacetimecat.eclipse.admin.core.internal.p2.P2_Agent;
import com.spacetimecat.eclipse.commons.Eclipse_Plugin_Base;
import com.spacetimecat.eclipse.commons.My_Closeable;
import com.spacetimecat.eclipse.commons.Progress_Monitor;
import com.spacetimecat.eclipse.commons.Progress_Monitors;

public final class Activator extends Eclipse_Plugin_Base implements CommandProvider {

    private static Activator instance;

    private P2_Agent agent;

    @Override
    public void start (BundleContext context) throws Exception {
        super.start(context);
        instance = this;
        context.registerService(CommandProvider.class, this, null);
    }

    @Override
    public String getHelp () {
        return
            "---Erik---\n"
            + "\t<work in progress - please see source code for commands>";
    }

    // How do we make Gogo print the stack trace of an unhandled exception?

    public void _upmir (CommandInterpreter cli) {
        try {
            update_mirror(Progress_Monitors.create_meager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update_mirror (Progress_Monitor monitor) throws Exception {
        ensure_initialized();
        agent._update_my_mirror(monitor);
    }

    public void _clr (CommandInterpreter cli) {
        reset_state();
    }

    public void reset_state () {
        if (agent != null) {
            try (My_Closeable _100 = () -> { agent = null; }) {
                agent.close();
            }
        }
    }

    private void ensure_initialized () throws Exception {
        if (agent == null) {
            agent = P2_Agent._create_my_agent(this);
        }
    }

    public static Activator get_instance () {
        return instance;
    }

    @Override
    public void stop (BundleContext context) throws Exception {
        if (agent != null) {
            agent.close();
            agent = null;
        }
        instance = null;
        super.stop(context);
    }

}
