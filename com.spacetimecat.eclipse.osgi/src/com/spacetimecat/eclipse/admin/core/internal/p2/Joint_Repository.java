package com.spacetimecat.eclipse.admin.core.internal.p2;

import java.net.URI;
import java.util.Collection;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;

/**
 * A coupled {@link IMetadataRepository} and {@link IArtifactRepository},
 * which should be the case most of the time.
 */
public final class Joint_Repository {

    final URI uri;
    final IMetadataRepository metadata;
    final IArtifactRepository artifact;

    public Joint_Repository (URI uri, IMetadataRepository metadata, IArtifactRepository artifact) {
        this.uri = uri;
        this.metadata = metadata;
        this.artifact = artifact;
    }

    /**
     * Add the units into the metadata repository in this joint repository.
     */
    public void add_units (Collection<IInstallableUnit> units) {
        metadata.addInstallableUnits(units);
    }

}
