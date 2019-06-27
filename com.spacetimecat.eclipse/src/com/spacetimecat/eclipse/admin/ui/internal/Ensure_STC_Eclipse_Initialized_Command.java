package com.spacetimecat.eclipse.admin.ui.internal;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.ui.PlatformUI;

import com.spacetimecat.eclipse.admin.core.internal.p2.Joint_Repository;
import com.spacetimecat.eclipse.admin.core.internal.p2.P2_Agent;
import com.spacetimecat.eclipse.admin.core.internal.p2.Queries;

public class Ensure_STC_Eclipse_Initialized_Command extends AbstractHandler {

    @Override
    public Object execute (ExecutionEvent event) throws ExecutionException {
        My_Plugin.get_instance().install_to(PlatformUI.getWorkbench());
        return null;
    }

    private static final class The_Job extends Job {

        private Consumer<Supplier<IProgressMonitor>> runnable;

        public The_Job () {
            super("Erik's PDE/P2 Test Job");
        }

        // RuntimeInstallJob
        // Could not find the exported unit with id: com.spacetimecat.eclipse.feature.feature.group version: 0.0.0.201906241458
        @Override
        protected IStatus run (IProgressMonitor monitor) {
            monitor.beginTask("Ensure_STC_Eclipse_Initialized_Command", IProgressMonitor.UNKNOWN);
            final My_Plugin plugin = My_Plugin.get_instance();
            final File eclipse_p2_dir = new File(P2_Agent.MY_ECLIPSE_P2_DIR);
            try (
                P2_Agent agent = P2_Agent.create(plugin, eclipse_p2_dir);
            ) {
                final Joint_Repository mirror = agent.ensure_joint_repository(new File(P2_Agent.MY_MIRROR_UPDATE_SITE));
                final String id = "com.spacetimecat.eclipse.feature.feature.group";
                final String version = "0.0.0.201906242212";
                System.out.println("-------------------- query 1");
                {
                    final IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
                    final IQueryResult<IInstallableUnit> result = agent.query_metadata(mirror, query);
                    System.out.println("isEmpty: " + result.isEmpty());
                    result.forEach(iu -> {
                        System.out.println(iu);
                    });
                }
                System.out.println("-------------------- query 2");
                {
                    final IQuery<IInstallableUnit> query = Queries.range(id, "0.0.0.201906242212");
                    final IQueryResult<IInstallableUnit> result = agent.query_metadata(mirror, query);
                    System.out.println("isEmpty: " + result.isEmpty());
                    result.forEach(iu -> {
                        System.out.println(iu);
                    });
                }
                return Status.OK_STATUS;
            } catch (Exception e) {
                return My_Plugin.get_instance().create_Status(e);
            }
        }

    }

}
