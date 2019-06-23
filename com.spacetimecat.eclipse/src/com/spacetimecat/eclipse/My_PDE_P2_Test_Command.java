package com.spacetimecat.eclipse;

import java.io.File;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

import com.spacetimecat.eclipse.commons.My_Closeable;

public class My_PDE_P2_Test_Command extends AbstractHandler {

    @Override
    public Object execute (ExecutionEvent event) throws ExecutionException {
        Job job = new The_Job();
        job.setUser(true);
        job.schedule();
        return null;
    }

    private static final class The_Job extends Job {

        public The_Job () {
            super("Erik's PDE/P2 Test Job");
        }

        // RuntimeInstallJob
        // Could not find the exported unit with id: com.spacetimecat.eclipse.feature.feature.group version: 0.0.0.201906241458
        @Override
        protected IStatus run (IProgressMonitor monitor_) {
            SubMonitor monitor = SubMonitor.convert(monitor_);
            monitor.beginTask("Erik's PDE/P2 Task", 1);
            try (My_Closeable _100 = My_Closeable.of_sub_monitor(monitor)) {
                final ProvisioningUI ui = ProvisioningUI.getDefaultUI();
                // It seems that somehow p2 caches the contents.jar.
                // https://www.eclipse.org/forums/index.php/t/333102/
                final IProvisioningAgent agent0 = ui.getSession().getProvisioningAgent();
                final IMetadataRepositoryManager mrm0 = agent0.getService(IMetadataRepositoryManager.class);
                final IArtifactRepositoryManager arm0 = agent0.getService(IArtifactRepositoryManager.class);
                final URI destination = new File("/junk/runtime-EclipseApplication/.metadata/.plugins/org.eclipse.pde.core/install/").toURI();
                mrm0.removeRepository(destination);
                arm0.removeRepository(destination);
                final IArtifactRepository arti_repo = ui.loadArtifactRepository(destination, false, monitor.split(1));
                final IMetadataRepository meta_repo = ui.loadMetadataRepository(destination, false, monitor.split(1));
                final String id = "com.spacetimecat.eclipse.feature.feature.group";
                final String version = "0.0.0.201906242212";
                final Version new_version = Version.parseVersion(version);
                My_Plugin plugin = My_Plugin.get_instance();
                final IProvisioningAgentProvider provider = plugin.get_service(IProvisioningAgentProvider.class);
                final IProvisioningAgent agent = provider.createAgent(null);
                final IMetadataRepositoryManager meta_repo_man = agent.getService(IMetadataRepositoryManager.class);
                System.out.println("meta_repo_man = " + meta_repo_man);
                System.out.println("-------------------- dump p2 metadata repository");
                System.out.println(destination);
                System.out.println("-------------------- query 1");
                {
                    final IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
                    final IQueryResult<IInstallableUnit> result = meta_repo.query(query, monitor);
                    System.out.println("isEmpty: " + result.isEmpty());
                    result.forEach(iu -> {
                        System.out.println(iu);
                    });
                }
                System.out.println("-------------------- query 2");
                {
                    final IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(id, new_version);
                    final IQueryResult<IInstallableUnit> result = meta_repo.query(query, monitor);
                    System.out.println("isEmpty: " + result.isEmpty());
                    result.forEach(iu -> {
                        System.out.println(iu);
                    });
                }
                monitor.worked(1);
                return Status.OK_STATUS;
            } catch (Exception e) {
                return My_Plugin.get_instance().create_Status(e);
            }
        }

    }

}
