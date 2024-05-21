package com.android.packageinstaller.role.ui;

import android.content.pm.ApplicationInfo;
import com.android.packageinstaller.role.model.Role;
import java.util.List;
/* loaded from: classes.dex */
public class RoleItem {
    private final List<ApplicationInfo> mHolderApplicationInfos;
    private final Role mRole;

    public RoleItem(Role role, List<ApplicationInfo> list) {
        this.mRole = role;
        this.mHolderApplicationInfos = list;
    }

    public Role getRole() {
        return this.mRole;
    }

    public List<ApplicationInfo> getHolderApplicationInfos() {
        return this.mHolderApplicationInfos;
    }
}
