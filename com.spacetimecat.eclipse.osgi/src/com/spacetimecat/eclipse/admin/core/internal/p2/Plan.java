package com.spacetimecat.eclipse.admin.core.internal.p2;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;

import com.spacetimecat.eclipse.commons.Progress_Monitors;

public final class Plan {

    final IProvisioningPlan self;

    Plan (IProvisioningPlan self) {
        this.self = self;
    }

    /**
     * Consider using {@link #get_units_that_will_be_added()}.
     */
    public IQueryable<IInstallableUnit> get_additions () {
        return self.getAdditions();
    }

    public Set<IInstallableUnit> get_units_that_will_be_added () {
        return all_units_in(get_additions());
    }

    private static Set<IInstallableUnit> all_units_in (IQueryable<IInstallableUnit> queriable) {
        return queriable.query(QueryUtil.createIUAnyQuery(), create_monitor()).toUnmodifiableSet();
    }

    private static IProgressMonitor create_monitor () {
        return Progress_Monitors.degrade(Progress_Monitors.create_meager());
    }

}
