package com.android.packageinstaller.permission.utils;

import android.content.pm.PackageInfo;
import com.android.packageinstaller.permission.model.Permission;
/* loaded from: classes.dex */
public abstract class SoftRestrictedPermissionPolicy {
    public static boolean shouldShow(PackageInfo packageInfo, Permission permission) {
        char c;
        String name = permission.getName();
        int hashCode = name.hashCode();
        if (hashCode != -406040016) {
            if (hashCode == 1365911975 && name.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                c = 1;
            }
            c = 65535;
        } else {
            if (name.equals("android.permission.READ_EXTERNAL_STORAGE")) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0 || c == 1) {
            return ((permission.getFlags() & 14336) != 0) || packageInfo.applicationInfo.targetSdkVersion >= 29;
        }
        return true;
    }
}
