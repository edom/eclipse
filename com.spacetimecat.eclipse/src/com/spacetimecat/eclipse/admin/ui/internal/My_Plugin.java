package com.spacetimecat.eclipse.admin.ui.internal;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.framework.BundleContext;

import com.spacetimecat.eclipse.admin.core.internal.p2.P2_Agent;
import com.spacetimecat.eclipse.commons.Eclipse_Plugin_Base;

public final class My_Plugin extends Eclipse_Plugin_Base {

    private static My_Plugin instance;
    private boolean ui_installed;

    public My_Plugin () {
    }

    public void install_to (IWorkbench workbench) {
        synchronized (this) {
            if (ui_installed) {
                return;
            }
            ui_installed = true;
        }
        workbench.getDisplay().asyncExec(() -> ui_install_to(workbench));
    }

    private void ui_install_to (IWorkbench workbench) {
        final ICommandService commands = workbench.getService(ICommandService.class);
        final Category category = commands.getCategory("com.spacetimecat.eclipse.admin.ui.category.dynamic");
        category.define("STC Eclipse Dynamic Commands", null);
        {
            final Command command = commands.getCommand("com.spacetimecat.eclipse.admin.ui.command.say_hello");
            command.define("STC Eclipse Say Hello", null, category);
            command.setHandler(new AbstractHandler() {
                @Override
                public Object execute (ExecutionEvent event) throws ExecutionException {
                    System.out.println("HELLO STC WORLD");
                    return null;
                }
            });
        }
    }

    public P2_Agent create_agent () throws ProvisionException, OperationCanceledException {
        return P2_Agent.create(this, new File(P2_Agent.MY_ECLIPSE_P2_DIR));
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
