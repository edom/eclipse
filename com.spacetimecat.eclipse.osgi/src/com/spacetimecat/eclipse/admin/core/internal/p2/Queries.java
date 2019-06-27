package com.spacetimecat.eclipse.admin.core.internal.p2;

import java.util.Arrays;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IVersionedId;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.QueryUtil;

/**
 * @see QueryUtil
 */
public final class Queries {

    private Queries () {}

    public static IQuery<IInstallableUnit> all () {
        return QueryUtil.createIUAnyQuery();
    }

    public static <T extends IVersionedId> IQuery<T> latest (IQuery<T> query) {
        return QueryUtil.createLatestQuery(query);
    }

    @SafeVarargs
    public static <T> IQuery<T> union (IQuery<T>... queries) {
        return QueryUtil.createCompoundQuery(Arrays.asList(queries), false);
    }

    public static IQuery<IInstallableUnit> range (String id, String version_range)
    {
        final VersionRange range = VersionRange.create(version_range);
        return QueryUtil.createIUQuery(id, range);
    }

    public static IQuery<IInstallableUnit> id (String id) {
        return QueryUtil.createIUQuery(id);
    }

}
