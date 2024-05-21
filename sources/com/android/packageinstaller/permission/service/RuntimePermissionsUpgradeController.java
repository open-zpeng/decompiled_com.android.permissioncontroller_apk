package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.permission.PermissionManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
class RuntimePermissionsUpgradeController {
    private static final String LOG_TAG = "RuntimePermissionsUpgradeController";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void upgradeIfNeeded(Context context) {
        PermissionManager permissionManager = (PermissionManager) context.getSystemService(PermissionManager.class);
        int runtimePermissionsVersion = permissionManager.getRuntimePermissionsVersion();
        whitelistAllSystemAppPermissions(context);
        int onUpgradeLocked = onUpgradeLocked(context, runtimePermissionsVersion);
        if (onUpgradeLocked == 8) {
            if (runtimePermissionsVersion != onUpgradeLocked) {
                permissionManager.setRuntimePermissionsVersion(8);
                return;
            }
            return;
        }
        Log.wtf("PermissionControllerService", "warning: upgrading permission database to version 8 left it at " + runtimePermissionsVersion + " instead; this is probably a bug. Did you update LATEST_VERSION?", new Throwable());
        throw new RuntimeException("db upgrade error");
    }

    private static void whitelistAllSystemAppPermissions(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(2109440);
        ArrayMap arrayMap = new ArrayMap();
        int size = installedPackages.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            String[] strArr = packageInfo.requestedPermissions;
            if (strArr != null) {
                for (String str : strArr) {
                    PermissionInfo permissionInfo = (PermissionInfo) arrayMap.get(str);
                    if (permissionInfo == null) {
                        try {
                            permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                            arrayMap.put(str, permissionInfo);
                        } catch (PackageManager.NameNotFoundException unused) {
                        }
                    }
                    if ((permissionInfo.flags & 12) != 0) {
                        context.getPackageManager().addWhitelistedRestrictedPermission(packageInfo.packageName, str, 4);
                    }
                }
            }
        }
    }

    private static int onUpgradeLocked(Context context, int i) {
        boolean z;
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(135168);
        int size = installedPackages.size();
        int i2 = i;
        if (i2 <= -1) {
            Log.i(LOG_TAG, "Upgrading from Android P");
            z = true;
            i2 = 0;
        } else {
            z = false;
        }
        if (i2 == 0) {
            Log.i(LOG_TAG, "Grandfathering SMS and CallLog permissions");
            List<String> platformPermissionNamesOfGroup = Utils.getPlatformPermissionNamesOfGroup("android.permission-group.SMS");
            List<String> platformPermissionNamesOfGroup2 = Utils.getPlatformPermissionNamesOfGroup("android.permission-group.CALL_LOG");
            for (int i3 = 0; i3 < size; i3++) {
                PackageInfo packageInfo = installedPackages.get(i3);
                String[] strArr = packageInfo.requestedPermissions;
                if (strArr != null) {
                    for (String str : strArr) {
                        if (platformPermissionNamesOfGroup.contains(str) || platformPermissionNamesOfGroup2.contains(str)) {
                            context.getPackageManager().addWhitelistedRestrictedPermission(packageInfo.packageName, str, 4);
                        }
                    }
                }
            }
            i2 = 1;
        }
        if (i2 == 1) {
            i2 = 2;
        }
        if (i2 == 2) {
            i2 = 3;
        }
        if (i2 == 3) {
            Log.i(LOG_TAG, "Grandfathering location background permissions");
            for (int i4 = 0; i4 < size; i4++) {
                PackageInfo packageInfo2 = installedPackages.get(i4);
                String[] strArr2 = packageInfo2.requestedPermissions;
                if (strArr2 != null) {
                    int length = strArr2.length;
                    int i5 = 0;
                    while (true) {
                        if (i5 >= length) {
                            break;
                        } else if (strArr2[i5].equals("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                            context.getPackageManager().addWhitelistedRestrictedPermission(packageInfo2.packageName, "android.permission.ACCESS_BACKGROUND_LOCATION", 4);
                            break;
                        } else {
                            i5++;
                        }
                    }
                }
            }
            i2 = 4;
        }
        if (i2 == 4) {
            i2 = 5;
        }
        if (i2 == 5) {
            Log.i(LOG_TAG, "Grandfathering Storage permissions");
            List<String> platformPermissionNamesOfGroup3 = Utils.getPlatformPermissionNamesOfGroup("android.permission-group.STORAGE");
            for (int i6 = 0; i6 < size; i6++) {
                PackageInfo packageInfo3 = installedPackages.get(i6);
                String[] strArr3 = packageInfo3.requestedPermissions;
                if (strArr3 != null) {
                    for (String str2 : strArr3) {
                        if (platformPermissionNamesOfGroup3.contains(str2)) {
                            context.getPackageManager().addWhitelistedRestrictedPermission(packageInfo3.packageName, str2, 4);
                        }
                    }
                }
            }
            i2 = 6;
        }
        if (i2 == 6) {
            if (z) {
                Log.i(LOG_TAG, "Expanding location permissions");
                for (int i7 = 0; i7 < size; i7++) {
                    PackageInfo packageInfo4 = installedPackages.get(i7);
                    String[] strArr4 = packageInfo4.requestedPermissions;
                    if (strArr4 != null) {
                        int length2 = strArr4.length;
                        int i8 = 0;
                        while (true) {
                            if (i8 < length2) {
                                String str3 = strArr4[i8];
                                if (TextUtils.equals(Utils.getGroupOfPlatformPermission(str3), "android.permission-group.LOCATION")) {
                                    AppPermissionGroup create = AppPermissionGroup.create(context, packageInfo4, str3, false);
                                    AppPermissionGroup backgroundPermissions = create.getBackgroundPermissions();
                                    if (create.areRuntimePermissionsGranted() && backgroundPermissions != null && !backgroundPermissions.isUserSet() && !backgroundPermissions.isSystemFixed() && !backgroundPermissions.isPolicyFixed()) {
                                        backgroundPermissions.grantRuntimePermissions(create.isUserFixed());
                                        logRuntimePermissionUpgradeResult(backgroundPermissions, packageInfo4.applicationInfo.uid, packageInfo4.packageName, new String[0]);
                                    }
                                } else {
                                    i8++;
                                }
                            }
                        }
                    }
                }
            } else {
                Log.i(LOG_TAG, "Not expanding location permissions as this is not an upgrade from Android P");
            }
            i2 = 7;
        }
        if (i2 == 7) {
            Log.i(LOG_TAG, "Expanding read storage to access media location");
            for (int i9 = 0; i9 < size; i9++) {
                PackageInfo packageInfo5 = installedPackages.get(i9);
                if (ArrayUtils.contains(packageInfo5.requestedPermissions, "android.permission.ACCESS_MEDIA_LOCATION") && context.checkPermission("android.permission.READ_EXTERNAL_STORAGE", 0, packageInfo5.applicationInfo.uid) == 0) {
                    AppPermissionGroup create2 = AppPermissionGroup.create(context, packageInfo5, "android.permission.ACCESS_MEDIA_LOCATION", false);
                    Permission permission = create2.getPermission("android.permission.ACCESS_MEDIA_LOCATION");
                    if (!permission.isUserSet() && !permission.isSystemFixed() && !permission.isPolicyFixed() && !permission.isGrantedIncludingAppOp()) {
                        create2.grantRuntimePermissions(false, new String[]{"android.permission.ACCESS_MEDIA_LOCATION"});
                        logRuntimePermissionUpgradeResult(create2, packageInfo5.applicationInfo.uid, packageInfo5.packageName, "android.permission.ACCESS_MEDIA_LOCATION");
                    }
                }
            }
            return 8;
        }
        return i2;
    }

    private static void logRuntimePermissionUpgradeResult(AppPermissionGroup appPermissionGroup, int i, String str, String... strArr) {
        ArrayList<Permission> permissions = appPermissionGroup.getPermissions();
        int size = permissions.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (strArr == null || ArrayUtils.contains(strArr, permissions)) {
                Permission permission = permissions.get(i2);
                PermissionControllerStatsLog.write(212, permission.getName(), i, str);
                String str2 = LOG_TAG;
                Log.v(str2, "Runtime permission upgrade logged for permissionName=" + permission.getName() + " uid=" + i + " packageName=" + str);
            }
        }
    }
}
