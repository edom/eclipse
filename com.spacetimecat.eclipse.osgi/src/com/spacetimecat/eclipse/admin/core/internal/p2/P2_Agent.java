package com.spacetimecat.eclipse.admin.core.internal.p2;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.ArtifactDescriptorQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRequest;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.spacetimecat.eclipse.commons.Eclipse_Plugin;
import com.spacetimecat.eclipse.commons.My_Closeable;
import com.spacetimecat.eclipse.commons.Progress_Monitor;

/**
 * <p>
 * Use the static method {@link #create(BundleContext, File, Supplier)} to instantiate this class.
 * </p>
 * <p>
 * The owner of the agent should clean-up by calling {@link #close()}
 * after that owner has finished using it.
 * </p>
 */
public final class P2_Agent implements AutoCloseable {

    private final ExecutorService thread_pool = Executors.newCachedThreadPool();
    private final List<Future<?>> futures = new ArrayList<>();
    private final Collection<URI> pending_artifact_repos = new ArrayList<>();
    private final Collection<URI> pending_metadata_repos = new ArrayList<>();

    private final Eclipse_Plugin plugin;
    private final ServiceReference<IProvisioningAgentProvider> provider_ref;
    private final IProvisioningAgent agent;
    private final IArtifactRepositoryManager artifact_manager;
    private final IMetadataRepositoryManager metadata_manager;
    private final IArtifactRepository my_artifact_repo;
    private final IMetadataRepository my_metadata_repo;

    /**
     * See {@link #create(BundleContext, File, SubMonitor)}.
     */
    private P2_Agent (
    Eclipse_Plugin plugin
    , File eclipse_install_dir
    , File update_site_dir
    ) throws ProvisionException, OperationCanceledException {
        this.plugin = plugin;

        final URI uri = eclipse_install_dir.toURI();

        provider_ref = plugin.get_ServiceReference(IProvisioningAgentProvider.class);

        agent = plugin.get_service(provider_ref).createAgent(uri);

        artifact_manager = get_agent_service("org.eclipse.equinox.p2.artifact.repository", IArtifactRepositoryManager.class);
        metadata_manager = get_agent_service("org.eclipse.equinox.p2.metadata.repository", IMetadataRepositoryManager.class);

        my_artifact_repo = ensure_artifact_repository(update_site_dir);
        my_metadata_repo = ensure_metadata_repository(update_site_dir);

        // Do not download from the destination repository itself.
        set_enabled(my_artifact_repo, false);
        set_enabled(my_metadata_repo, false);
    }

    private <T> T get_agent_service (String bundle_symbolic_name, Class<T> cls) {
        final T instance = agent.getService(cls);
        if (instance == null) {
            throw new IllegalStateException(
                "Please ensure that the following bundle has been required and activated: "
                + bundle_symbolic_name
            );
        }
        return instance;
    }

    private void set_enabled (IArtifactRepository repository, boolean enabled) {
        artifact_manager.setEnabled(repository.getLocation(), enabled);
    }

    private void set_enabled (IMetadataRepository repository, boolean enabled) {
        metadata_manager.setEnabled(repository.getLocation(), enabled);
    }

    /**
     * @param context the {@link BundleContext} that was passed to {@link BundleActivator#start(BundleContext)}
     * @param eclipse_install_dir
     * the "p2" directory inside the Eclipse installation directory that will be modified by this agent
     */
    public static P2_Agent create (
    Eclipse_Plugin plugin
    , File eclipse_install_dir
    ) throws ProvisionException, OperationCanceledException {
        ensure_directory(eclipse_install_dir);
        return new P2_Agent(plugin, eclipse_install_dir, new File(MY_MIRROR_UPDATE_SITE));
    }

    public Joint_Repository ensure_joint_repository (File directory) throws ProvisionException, OperationCanceledException {
        return new Joint_Repository(
            directory.toURI(),
            ensure_metadata_repository(directory),
            ensure_artifact_repository(directory)
        );
    }

    private IArtifactRepository ensure_artifact_repository (File directory)
    throws ProvisionException, OperationCanceledException {
        final URI location = directory.toURI();
        try {
            return with_monitor("ensure_artifact_repository: " + directory, monitor -> artifact_manager.loadRepository(location, monitor));
        } catch (ProvisionException e) {
            return artifact_manager.createRepository(location, "Erik's p2 artifact repository", "org.eclipse.equinox.p2.artifact.repository.simpleRepository", null);
        }
    }

    private IMetadataRepository ensure_metadata_repository (File directory)
    throws ProvisionException, OperationCanceledException {
        final URI location = directory.toURI();
        try {
            return with_monitor("ensure_metadata_repository: " + directory, monitor -> metadata_manager.loadRepository(location, monitor));
        } catch (ProvisionException e) {
            return metadata_manager.createRepository(location, "Erik's p2 metadata repository", "org.eclipse.equinox.p2.metadata.repository.simpleRepository", null);
        }
    }

    private static void ensure_directory (File directory) {
        if (directory.mkdirs()) {
            return;
        }
        if (directory.isDirectory()) {
            return;
        }
        throw new IllegalStateException("Could not create directory: " + directory);
    }

    /**
     * @see IProvisioningAgent#stop()
     */
    @Override
    public void close () {
        try (
            My_Closeable _300 = () -> plugin.unget_service(provider_ref);
            My_Closeable _200 = () -> agent.stop();
            My_Closeable _100 = () -> thread_pool.shutdownNow();
        ) {
            // Shouldn't each of "futures" be canceled?
        }
    }

    /**
     * Ensure that the profile exists.
     *
     * @param profile_id
     * must be non-empty, and
     * must contain only characters acceptable by the underlying file system
     * @return the profile
     */
    private IProfile ensure_profile (String profile_id) throws ProvisionException {
        final IProfileRegistry registry = agent.getService(IProfileRegistry.class);
        final IProfile profile = registry.getProfile(profile_id);
        return profile == null ? registry.addProfile(profile_id) : profile;
    }

    public Joint_Repository load_joint_repository (URI uri) throws ProvisionException, OperationCanceledException {
        return new Joint_Repository(
            uri,
            load_metadata_repository(uri),
            load_artifact_repository(uri)
        );
    }

    /**
     * Consider using {@link #register_joint_repository(URI)}.
     */
    private IMetadataRepository load_metadata_repository (URI uri) throws ProvisionException, OperationCanceledException {
        return with_monitor("load_metadata_repository: " + uri, monitor -> metadata_manager.loadRepository(uri, monitor));
    }

    /**
     * Consider using {@link #load_joint_repository(URI)}.
     */
    private IArtifactRepository load_artifact_repository (URI uri) throws ProvisionException {
        return with_monitor("load_artifact_repository: " + uri, monitor -> artifact_manager.loadRepository(uri, monitor));
    }

    /**
     * Register the repository for loading later when it is necessary.
     */
    public void register_joint_repository (URI uri) {
        pending_artifact_repos.add(uri);
        pending_metadata_repos.add(uri);
    }

    /**
     * Repeated {@link #register_joint_repository(URI)}.
     */
    public void register_joint_repositories (URI... uris) {
        for (URI uri : uris) {
            register_joint_repository(uri);
        }
    }

    private void load_pending_artifact_repositories () throws ProvisionException, OperationCanceledException {
        for (URI uri : pending_artifact_repos) {
            schedule(() -> load_artifact_repository(uri));
        }
        join();
        pending_artifact_repos.clear();
    }

    private void load_pending_metadata_repositories () throws ProvisionException, OperationCanceledException {
        for (URI uri : pending_metadata_repos) {
            schedule(() -> load_metadata_repository(uri));
        }
        join();
        pending_metadata_repos.clear();
    }

    private void schedule (Callable<?> r) {
        futures.add(thread_pool.submit(r));
    }

    private void join () throws ProvisionException {
        try {
            MultiStatus status = null;
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    final Throwable cause = e.getCause();
                    if (status == null) {
                        status = new MultiStatus("<FIXME>pluginId", -1, cause.getMessage(), cause);
                    } else {
                        status.add(new Status(IStatus.ERROR, "pluginId", -1, cause.getMessage(), cause));
                    }
                }
            }
            futures.clear();
            if (status != null) {
                throw new ProvisionException(status);
            }
        } catch (InterruptedException e) {
            // FIXME
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Use {@link #with_monitor(String, Function1)} instead, to avoid leakage.
     */
    private IProgressMonitor2 create_monitor (String job_name) {
        return Monitor_Job.create_monitor(job_name);
    }

    private interface Function1<A, B> {
        B apply (A arg) throws ProvisionException;
    }

    private <R> R with_monitor (String job_name, Function1<IProgressMonitor2, R> consumer) throws ProvisionException {
        try (IProgressMonitor2 monitor = create_monitor(job_name)) {
            return consumer.apply(monitor);
        }
    }

    /**
     * <p>
     * Query all enabled metadata repositories known by this agent.
     * </p>
     *
     * @param query can be constructed using static methods in {@link Queries}
     */
    public IQueryResult<IInstallableUnit> query_metadata (IQuery<IInstallableUnit> query) throws ProvisionException, OperationCanceledException {
        load_pending_metadata_repositories();
        return with_monitor("query_metadata", monitor -> metadata_manager.query(query, monitor));
    }

    /**
     * <p>
     * Query only the given repository.
     * </p>
     */
    public IQueryResult<IInstallableUnit> query_metadata (Joint_Repository repository, IQuery<IInstallableUnit> query) throws ProvisionException, OperationCanceledException {
        return with_monitor("query_metadata: " + repository.uri, monitor -> repository.metadata.query(query, monitor));
    }

    /**
     * <p>
     * Create a request for downloading from another repository into the destination artifact repository.
     * </p>
     * <p>
     * The request should be passed to the
     * {@link IArtifactRepository#getArtifacts(IArtifactRequest[], IProgressMonitor) getArtifacts} method of
     * the <em>other</em> repository that contains the artifact, not to the destination.
     * </p>
     * @param destination where to store what is going to be downloaded
     * @param key what to download
     */
    private IArtifactRequest create_mirror_request (IArtifactRepository destination, IArtifactKey key) {
        return artifact_manager.createMirrorRequest(key, destination, null, null);
    }

    private ProvisioningSession create_session () {
        return new ProvisioningSession(agent);
    }

    /**
     * @see InstallOperation
     */
    public Install_Operation create_install_operation (String profile_id, Collection<IInstallableUnit> units) {
        final InstallOperation operation = new InstallOperation(create_session(), units);
        operation.setProfileId(profile_id);
        final ProvisioningContext pcontext = operation.getProvisioningContext();
        pcontext.setMetadataRepositories(metadata_manager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL));
        pcontext.setArtifactRepositories(artifact_manager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL));
        return new Install_Operation(operation);
    }

    public void download_artifacts_of_units (Joint_Repository destination, Collection<IInstallableUnit> units) throws CoreException {
        download_artifacts_of_units(destination.artifact, units);
    }

    private void download_artifacts_of_units (IArtifactRepository destination, Collection<IInstallableUnit> units) throws CoreException {
        final Set<IArtifactKey> keys = new HashSet<>();
        units.forEach(unit -> keys.addAll(unit.getArtifacts()));
        download_artifacts(destination, keys);
    }

    private void download_artifacts (IArtifactRepository destination, Collection<IArtifactKey> keys) throws CoreException {
        load_pending_artifact_repositories();
        final IQuery<IArtifactDescriptor> query = Queries.union(
            keys.stream()
                .map(key -> new ArtifactDescriptorQuery(key))
                .toArray(ArtifactDescriptorQuery[]::new)
        );
        final Map<URI, IArtifactRequest[]> groups = new HashMap<>();
        for (URI uri : artifact_manager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL)) {
            with_monitor("query artifact repository: " + uri, monitor -> {
                groups.put(uri,
                    load_artifact_repository(uri)
                    .descriptorQueryable()
                    .query(query, monitor)
                    .toUnmodifiableSet().stream()
                    .map(desc -> create_mirror_request(destination, desc.getArtifactKey()))
                    .toArray(IArtifactRequest[]::new)
                );
                return null;
            });
        }
        for (Map.Entry<URI, IArtifactRequest[]> entry : groups.entrySet()) {
            final URI uri = entry.getKey();
            final IArtifactRequest[] requests = entry.getValue();
            final IArtifactRepository repo = load_artifact_repository(uri);
            final IStatus status = with_monitor(
                String.format("download_artifacts: Downloading %d artifacts from %s", requests.length, uri)
                , monitor -> repo.getArtifacts(requests, monitor)
            );
            if (!status.isOK()) {
                throw new CoreException(status);
            }
        }
    }

    /**
     * @see InstallOperation#resolveModal(IProgressMonitor)
     */
    public Plan resolve_modal (Install_Operation operation) throws CoreException {
        load_pending_metadata_repositories();
        final InstallOperation op = operation.self;
        ensure_profile(op.getProfileId());
        final IStatus status = with_monitor("resolve_modal", monitor -> op.resolveModal(monitor));
        if (!status.isOK()) {
            throw new CoreException(status);
        }
        return new Plan(op.getProvisioningPlan());
    }

    // ---------- extremely internal methods, only expected to work on my machine

    public static final String MY_PROFILE = "Erik";
    public static final String MY_ECLIPSE_P2_DIR = "/junk/eclipse2/p2";
    public static final String MY_MIRROR_UPDATE_SITE = "/home/erik/eclipse/data/p2";

    public static P2_Agent _create_my_agent (Eclipse_Plugin plugin) throws ProvisionException, OperationCanceledException {
        final P2_Agent agent = P2_Agent.create(plugin, new File(MY_MIRROR_UPDATE_SITE));
        return agent;
    }

    public Joint_Repository _ensure_my_local_mirror_update_site () throws ProvisionException, OperationCanceledException {
        return ensure_joint_repository(new File(MY_MIRROR_UPDATE_SITE));
    }

    public void _update_my_mirror (Progress_Monitor monitor) throws OperationCanceledException, CoreException {
        monitor.set_caption("Determining initial units");

        register_joint_repositories(
            URI.create("https://download.eclipse.org/releases/2019-06")
            , URI.create("https://download.eclipse.org/technology/m2e/releases/1.12")
            , URI.create("https://download.eclipse.org/tools/orbit/downloads/drops/R20190602212107/repository/")
        );

        final Set<IInstallableUnit> initial_units =
            query_metadata(
                Queries.latest(
                    Queries.union(
                        Queries.range("org.eclipse.platform.feature.group", "[4,5)")
                        , Queries.id("org.eclipse.jdt.feature.group")
                        , Queries.id("org.eclipse.m2e.feature.feature.group")
                    )
                )
            ).toUnmodifiableSet();

        initial_units.forEach(unit -> System.out.printf("initial unit: %s\n", unit));

        final Install_Operation operation = create_install_operation(P2_Agent.MY_PROFILE, initial_units);

        monitor.set_caption("Resolving dependencies");

        final Plan plan = resolve_modal(operation);
        final Set<IInstallableUnit> units_to_install = plan.get_units_that_will_be_added();

        monitor.set_caption(String.format("Adding %d units to metadata repository\n", units_to_install.size()));

        units_to_install.forEach(unit -> System.out.printf("will copy installable unit: %s\n", unit));

        final Joint_Repository mirror = _ensure_my_local_mirror_update_site();
        mirror.add_units(units_to_install);

        monitor.set_caption("Downloading artifacts into mirror artifact repository");

        download_artifacts_of_units(mirror, units_to_install);
    }

}
