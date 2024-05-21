package com.android.packageinstaller.role.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
/* loaded from: classes.dex */
public final class PackageUtils {
    public static PackageInfo getPackageInfo(String str, int i, Context context) {
        try {
            return context.getPackageManager().getPackageInfo(str, i | 786432);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public static ApplicationInfo getApplicationInfo(String str, Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 786432);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public static ApplicationInfo getApplicationInfoAsUser(String str, UserHandle userHandle, Context context) {
        return getApplicationInfo(str, UserUtils.getUserContext(context, userHandle));
    }
}
