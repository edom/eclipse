package com.spacetimecat.eclipse.admin.ui.internal;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.spacetimecat.eclipse.admin.core.internal.Activator;
import com.spacetimecat.eclipse.admin.core.internal.p2.IProgressMonitor2;
import com.spacetimecat.eclipse.admin.core.internal.p2.Monitor_Job;
import com.spacetimecat.eclipse.commons.View_Part;

public class STC_Admin_View_Part extends ViewPart {

    private View_Part part;

    Tree a;

    @Override
    public void init (IViewSite site) throws PartInitException {
        super.init(site);
        final My_Plugin plugin = My_Plugin.get_instance();
        this.part = new View_Part(site, plugin);
    }

    @Override
    public void createPartControl (Composite parent) {
        a = new Tree(parent, SWT.MULTI);
        {
            TreeItem a_0 = new TreeItem(a, 0);
            a_0.setText("0");
            a_0.setExpanded(true);
            {
                TreeItem a_0_0 = new TreeItem(a_0, 0);
                a_0_0.setText("0.0");
                a_0_0.setExpanded(true);
                {
                    TreeItem a_0_0_0 = new TreeItem(a_0_0, 0);
                    a_0_0_0.setText("0.0.0");
                }
            }
        }
        final Composite c0 = new Composite(parent, SWT.NONE);
        c0.setLayout(new RowLayout());
        create_admin_buttons(c0);
        final Composite c1 = new Composite(parent, SWT.NONE);
        c1.setLayout(new RowLayout());
        create_test_buttons(c1);
    }

    private void create_admin_buttons (Composite parent) {
        part.button(parent, "Test P2/PDE", (e) -> {
            new Ensure_STC_Eclipse_Initialized_Command().execute(null);
        });
        part.button(parent, "Update my local p2 mirror", (e) -> {
            part.schedule(monitor -> {
                Activator.get_instance().update_mirror(monitor);
            });
        });
        part.button(parent, "Reset p2 agent state", (e) -> {
            part.schedule(monitor -> {
                Activator.get_instance().reset_state();
            });
        });
    }

    private void create_test_buttons (Composite parent) {
        part.button(parent, "Single", (e) -> {
            part.schedule(monitor -> {
                monitor.set_caption("Progress monitor");
                final int n = 100;
                for (int i = 0; i < n; ++i) {
                    if (monitor.is_canceled()) {
                        return;
                    }
                    monitor.set_progress(i, n);
                    Thread.sleep(1000L);
                }
            });
        });
        part.button(parent, "Random", (e) -> {
            part.schedule(monitor -> {
                monitor.set_caption("Random progress monitor");
                final Random random = new Random();
                final int n = 100;
                for (int i = 0; i < n; ++i) {
                    if (monitor.is_canceled()) {
                        return;
                    }
                    monitor.set_progress(random.nextInt(n), n);
                    Thread.sleep(1000L);
                }
            });
        });
        part.button(parent, "Multiple", (e) -> {
            part.schedule(monitor -> {
                try (
                    IProgressMonitor2 m1 = Monitor_Job.create_monitor("Job 1");
                    IProgressMonitor2 m2 = Monitor_Job.create_monitor("Job 2");
                    IProgressMonitor2 m3 = Monitor_Job.create_monitor("Job 3");
                ) {
                    final int n = 100;
                    monitor.set_caption("Multiple progress monitors");
                    m1.beginTask("Task 1", 100);
                    m2.beginTask("Task 2", 100);
                    m3.beginTask("Task 3", 100);
                    for (int i = 0; i < n; ++i) {
                        if (monitor.is_canceled()) {
                            return;
                        }
                        monitor.set_progress(i, n);
                        if (m1.isCanceled()) {
                            m1.close();
                        } else {
                            m1.worked(1);
                        }
                        if (m2.isCanceled()) {
                            m2.close();
                        } else {
                            m2.worked(1);
                        }
                        if (m3.isCanceled()) {
                            m3.close();
                        } else {
                            m3.worked(1);
                        }
                        Thread.sleep(1000L);
                    }
                }
            });
        });
    }

    @Override
    public void setFocus () {
        a.setFocus();
    }

}
