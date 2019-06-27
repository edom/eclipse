package com.spacetimecat.eclipse.admin.core.internal.p2;

import org.eclipse.equinox.p2.operations.InstallOperation;

public final class Install_Operation {

    final InstallOperation self;

    Install_Operation (InstallOperation self) {
        this.self = self;
    }

    public String get_profile_id () {
        return self.getProfileId();
    }

    public void set_profile_id (String id) {
        self.setProfileId(id);
    }

}
