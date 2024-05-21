package com.android.packageinstaller.role.model;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Process;
import android.permission.PermissionManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class Permissions {
    private static final String LOG_TAG = "Permissions";
    private static ArrayMap<String, List<String>> sBackgroundToForegroundPermissions;
    private static final Object sForegroundBackgroundPermissionMappingsLock = new Object();
    private static ArrayMap<String, String> sForegroundToBackgroundPermission;

    public static boolean grant(String str, List<String> list, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, Context context) {
        PackageInfo factoryPackageInfo;
        if (z3 == z4) {
            throw new IllegalArgumentException("Permission must be either granted by role, or granted by default, but not both");
        }
        PackageInfo packageInfo = getPackageInfo(str, context);
        if (packageInfo == null || ArrayUtils.isEmpty(packageInfo.requestedPermissions)) {
            return false;
        }
        List splitPermissions = ((PermissionManager) context.getSystemService(PermissionManager.class)).getSplitPermissions();
        ArraySet arraySet = new ArraySet(list);
        ArraySet arraySet2 = new ArraySet(arraySet);
        int size = splitPermissions.size();
        for (int i = 0; i < size; i++) {
            PermissionManager.SplitPermissionInfo splitPermissionInfo = (PermissionManager.SplitPermissionInfo) splitPermissions.get(i);
            if (packageInfo.applicationInfo.targetSdkVersion < splitPermissionInfo.getTargetSdk() && arraySet.contains(splitPermissionInfo.getSplitPermission())) {
                arraySet2.addAll(splitPermissionInfo.getNewPermissions());
            }
        }
        CollectionUtils.retainAll(arraySet2, packageInfo.requestedPermissions);
        if (arraySet2.isEmpty()) {
            return false;
        }
        if (!z && isUpdatedSystemApp(packageInfo) && (factoryPackageInfo = getFactoryPackageInfo(str, context)) != null) {
            if (ArrayUtils.isEmpty(factoryPackageInfo.requestedPermissions)) {
                return false;
            }
            CollectionUtils.retainAll(arraySet2, factoryPackageInfo.requestedPermissions);
            if (arraySet2.isEmpty()) {
                return false;
            }
        }
        int size2 = arraySet2.size();
        String[] strArr = new String[size2];
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size2; i4++) {
            String str2 = (String) arraySet2.valueAt(i4);
            if (isForegroundPermission(str2, context)) {
                strArr[i3] = str2;
                i3++;
            } else {
                strArr[(size2 - 1) - i2] = str2;
                i2++;
            }
        }
        PackageManager packageManager = context.getPackageManager();
        ArraySet arraySet3 = new ArraySet(packageManager.getWhitelistedRestrictedPermissions(str, 1));
        List<String> platformPermissionNamesOfGroup = Utils.getPlatformPermissionNamesOfGroup("android.permission-group.SMS");
        List<String> platformPermissionNamesOfGroup2 = Utils.getPlatformPermissionNamesOfGroup("android.permission-group.CALL_LOG");
        int length = strArr.length;
        int i5 = 0;
        boolean z6 = false;
        while (i5 < length) {
            String str3 = strArr[i5];
            if ((platformPermissionNamesOfGroup.contains(str3) || platformPermissionNamesOfGroup2.contains(str3)) && arraySet3.add(str3)) {
                packageManager.addWhitelistedRestrictedPermission(str, str3, 1);
            }
            z6 |= grantSingle(str, str3, z2, z3, z4, z5, context);
            i5++;
            length = length;
            platformPermissionNamesOfGroup2 = platformPermissionNamesOfGroup2;
        }
        return z6;
    }

    private static boolean grantSingle(String str, String str2, boolean z, boolean z2, boolean z3, boolean z4, Context context) {
        boolean z5;
        boolean isPermissionAndAppOpGranted = isPermissionAndAppOpGranted(str, str2, context);
        int i = 0;
        if (!isPermissionFixed(str, str2, false, z, context) || isPermissionAndAppOpGranted) {
            if (isBackgroundPermission(str2, context)) {
                List<String> foregroundPermissions = getForegroundPermissions(str2, context);
                int size = foregroundPermissions.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        z5 = false;
                        break;
                    } else if (isPermissionAndAppOpGranted(str, foregroundPermissions.get(i2), context)) {
                        z5 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z5) {
                    return false;
                }
            }
            boolean grantPermissionAndAppOp = grantPermissionAndAppOp(str, str2, context);
            if (!isPermissionAndAppOpGranted && z2) {
                i = 32768;
            }
            if (z3) {
                i |= 32;
            }
            if (z4) {
                i |= 16;
            }
            int i3 = i | 64;
            if (!isPermissionAndAppOpGranted) {
                i3 |= 3;
            }
            if (z3 && !z4) {
                int permissionFlags = getPermissionFlags(str, str2, context);
                if ((permissionFlags & 32) != 0 && (permissionFlags & 16) != 0) {
                    i3 |= 16;
                }
            }
            setPermissionFlags(str, str2, i, i3, context);
            return grantPermissionAndAppOp;
        }
        return false;
    }

    private static boolean isPermissionAndAppOpGranted(String str, String str2, Context context) {
        Integer appOpMode;
        if (isPermissionGrantedWithoutCheckingAppOp(str, str2, context) && !isPermissionReviewRequired(str, str2, context)) {
            if (!isBackgroundPermission(str2, context)) {
                String permissionAppOp = getPermissionAppOp(str2);
                if (permissionAppOp == null) {
                    return true;
                }
                Integer appOpMode2 = getAppOpMode(str, permissionAppOp, context);
                if (appOpMode2 == null) {
                    return false;
                }
                return !isForegroundPermission(str2, context) ? appOpMode2.intValue() == 0 : appOpMode2.intValue() == 4 || appOpMode2.intValue() == 0;
            }
            List<String> foregroundPermissions = getForegroundPermissions(str2, context);
            int size = foregroundPermissions.size();
            for (int i = 0; i < size; i++) {
                String permissionAppOp2 = getPermissionAppOp(foregroundPermissions.get(i));
                if (permissionAppOp2 != null && (appOpMode = getAppOpMode(str, permissionAppOp2, context)) != null && appOpMode.intValue() == 0) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean grantPermissionAndAppOp(String str, String str2, Context context) {
        boolean grantPermissionWithoutAppOp = grantPermissionWithoutAppOp(str, str2, context);
        int i = 0;
        if (!isBackgroundPermission(str2, context)) {
            String permissionAppOp = getPermissionAppOp(str2);
            if (permissionAppOp == null) {
                return false;
            }
            if (isForegroundPermission(str2, context) && !isPermissionAndAppOpGranted(str, getBackgroundPermission(str2, context), context)) {
                i = 4;
            }
            return setAppOpMode(str, permissionAppOp, i, context);
        }
        List<String> foregroundPermissions = getForegroundPermissions(str2, context);
        int size = foregroundPermissions.size();
        boolean z = grantPermissionWithoutAppOp;
        for (int i2 = 0; i2 < size; i2++) {
            String permissionAppOp2 = getPermissionAppOp(foregroundPermissions.get(i2));
            if (permissionAppOp2 != null) {
                z |= setAppOpMode(str, permissionAppOp2, 0, context);
            }
        }
        return z;
    }

    public static boolean revoke(String str, List<String> list, boolean z, boolean z2, boolean z3, Context context) {
        PackageInfo packageInfo = getPackageInfo(str, context);
        if (packageInfo == null || ArrayUtils.isEmpty(packageInfo.requestedPermissions)) {
            return false;
        }
        ArraySet arraySet = new ArraySet(list);
        CollectionUtils.retainAll(arraySet, packageInfo.requestedPermissions);
        if (arraySet.isEmpty()) {
            return false;
        }
        int size = arraySet.size();
        String[] strArr = new String[size];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            String str2 = (String) arraySet.valueAt(i3);
            if (isBackgroundPermission(str2, context)) {
                strArr[i2] = str2;
                i2++;
            } else {
                strArr[(size - 1) - i] = str2;
                i++;
            }
        }
        PackageManager packageManager = context.getPackageManager();
        Set<String> whitelistedRestrictedPermissions = packageManager.getWhitelistedRestrictedPermissions(str, 7);
        boolean z4 = false;
        for (String str3 : strArr) {
            z4 |= revokeSingle(str, str3, z, z2, z3, context);
            if (!isPermissionGrantedByDefault(str, str3, context) && whitelistedRestrictedPermissions.remove(str3)) {
                packageManager.removeWhitelistedRestrictedPermission(str, str3, 1);
            }
        }
        return z4;
    }

    private static boolean revokeSingle(String str, String str2, boolean z, boolean z2, boolean z3, Context context) {
        if (z == z2) {
            throw new IllegalArgumentException("Permission can be revoked only if either granted by role, or granted by default, but not both");
        }
        if (z) {
            if (!isPermissionGrantedByRole(str, str2, context)) {
                return false;
            }
            setPermissionFlags(str, str2, 0, 32768, context);
        }
        if (z2) {
            if (!isPermissionGrantedByDefault(str, str2, context)) {
                return false;
            }
            setPermissionFlags(str, str2, 0, 32, context);
        }
        if (isPermissionFixed(str, str2, z3, false, context) && isPermissionAndAppOpGranted(str, str2, context)) {
            return false;
        }
        if (isForegroundPermission(str2, context) && isPermissionAndAppOpGranted(str, getBackgroundPermission(str2, context), context)) {
            return false;
        }
        return revokePermissionAndAppOp(str, str2, context);
    }

    private static boolean revokePermissionAndAppOp(String str, String str2, Context context) {
        String permissionAppOp;
        boolean isRuntimePermissionsSupported = isRuntimePermissionsSupported(str, context);
        boolean revokePermissionWithoutAppOp = isRuntimePermissionsSupported ? revokePermissionWithoutAppOp(str, str2, context) | false : false;
        if (!isBackgroundPermission(str2, context)) {
            String permissionAppOp2 = getPermissionAppOp(str2);
            if (permissionAppOp2 == null) {
                return false;
            }
            int defaultAppOpMode = getDefaultAppOpMode(permissionAppOp2);
            boolean appOpMode = setAppOpMode(str, permissionAppOp2, defaultAppOpMode, context);
            revokePermissionWithoutAppOp |= appOpMode;
            if (appOpMode && !isRuntimePermissionsSupported && (defaultAppOpMode == 4 || defaultAppOpMode == 0)) {
                setPermissionFlags(str, str2, 64, 64, context);
            }
        } else {
            List<String> foregroundPermissions = getForegroundPermissions(str2, context);
            int size = foregroundPermissions.size();
            for (int i = 0; i < size; i++) {
                String str3 = foregroundPermissions.get(i);
                if (isPermissionAndAppOpGranted(str, str3, context) && (permissionAppOp = getPermissionAppOp(str3)) != null) {
                    revokePermissionWithoutAppOp |= setAppOpMode(str, permissionAppOp, 4, context);
                }
            }
        }
        return revokePermissionWithoutAppOp;
    }

    private static PackageInfo getPackageInfo(String str, Context context) {
        return getPackageInfo(str, 0, context);
    }

    private static PackageInfo getFactoryPackageInfo(String str, Context context) {
        return getPackageInfo(str, 2097152, context);
    }

    private static PackageInfo getPackageInfo(String str, int i, Context context) {
        return PackageUtils.getPackageInfo(str, i | 8192 | 4096, context);
    }

    private static boolean isUpdatedSystemApp(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        return (applicationInfo == null || (applicationInfo.flags & 128) == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isRuntimePermissionsSupported(String str, Context context) {
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
        return applicationInfo != null && applicationInfo.targetSdkVersion >= 23;
    }

    private static int getPermissionFlags(String str, String str2, Context context) {
        return context.getPackageManager().getPermissionFlags(str2, str, Process.myUserHandle());
    }

    private static boolean isPermissionFixed(String str, String str2, boolean z, boolean z2, Context context) {
        int permissionFlags = getPermissionFlags(str, str2, context);
        int i = !z ? 20 : 4;
        if (!z2) {
            i |= 3;
        }
        return (permissionFlags & i) != 0;
    }

    private static boolean isPermissionGrantedByDefault(String str, String str2, Context context) {
        return (getPermissionFlags(str, str2, context) & 32) != 0;
    }

    private static boolean isPermissionGrantedByRole(String str, String str2, Context context) {
        return (getPermissionFlags(str, str2, context) & 32768) != 0;
    }

    private static boolean isPermissionReviewRequired(String str, String str2, Context context) {
        return (getPermissionFlags(str, str2, context) & 64) != 0;
    }

    private static void setPermissionFlags(String str, String str2, int i, int i2, Context context) {
        context.getPackageManager().updatePermissionFlags(str2, str, i2, i, Process.myUserHandle());
    }

    private static boolean isPermissionGrantedWithoutCheckingAppOp(String str, String str2, Context context) {
        return context.getPackageManager().checkPermission(str2, str) == 0;
    }

    private static boolean grantPermissionWithoutAppOp(String str, String str2, Context context) {
        if (isPermissionGrantedWithoutCheckingAppOp(str, str2, context)) {
            return false;
        }
        context.getPackageManager().grantRuntimePermission(str, str2, Process.myUserHandle());
        return true;
    }

    private static boolean revokePermissionWithoutAppOp(String str, String str2, Context context) {
        if (isPermissionGrantedWithoutCheckingAppOp(str, str2, context)) {
            context.getPackageManager().revokeRuntimePermission(str, str2, Process.myUserHandle());
            return true;
        }
        return false;
    }

    private static boolean isForegroundPermission(String str, Context context) {
        ensureForegroundBackgroundPermissionMappings(context);
        return sForegroundToBackgroundPermission.containsKey(str);
    }

    private static String getBackgroundPermission(String str, Context context) {
        ensureForegroundBackgroundPermissionMappings(context);
        return sForegroundToBackgroundPermission.get(str);
    }

    private static boolean isBackgroundPermission(String str, Context context) {
        ensureForegroundBackgroundPermissionMappings(context);
        return sBackgroundToForegroundPermissions.containsKey(str);
    }

    private static List<String> getForegroundPermissions(String str, Context context) {
        ensureForegroundBackgroundPermissionMappings(context);
        return sBackgroundToForegroundPermissions.get(str);
    }

    private static void ensureForegroundBackgroundPermissionMappings(Context context) {
        synchronized (sForegroundBackgroundPermissionMappingsLock) {
            if (sForegroundToBackgroundPermission == null && sBackgroundToForegroundPermissions == null) {
                createForegroundBackgroundPermissionMappings(context);
            }
        }
    }

    private static void createForegroundBackgroundPermissionMappings(Context context) {
        PermissionGroupInfo permissionGroupInfo;
        ArrayList arrayList = new ArrayList();
        sBackgroundToForegroundPermissions = new ArrayMap<>();
        PackageManager packageManager = context.getPackageManager();
        List<PermissionGroupInfo> allPermissionGroups = packageManager.getAllPermissionGroups(0);
        int size = allPermissionGroups.size();
        for (int i = 0; i < size; i++) {
            try {
                List<PermissionInfo> permissionInfosForGroup = Utils.getPermissionInfosForGroup(packageManager, allPermissionGroups.get(i).name);
                int size2 = permissionInfosForGroup.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    PermissionInfo permissionInfo = permissionInfosForGroup.get(i2);
                    String str = permissionInfo.name;
                    arrayList.add(str);
                    String str2 = permissionInfo.backgroundPermission;
                    if (str2 != null) {
                        List<String> list = sBackgroundToForegroundPermissions.get(str2);
                        if (list == null) {
                            list = new ArrayList<>();
                            sBackgroundToForegroundPermissions.put(str2, list);
                        }
                        list.add(str);
                    }
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e(LOG_TAG, "Cannot get permissions for group: " + permissionGroupInfo.name);
            }
        }
        sBackgroundToForegroundPermissions.retainAll(arrayList);
        sForegroundToBackgroundPermission = new ArrayMap<>();
        int size3 = sBackgroundToForegroundPermissions.size();
        for (int i3 = 0; i3 < size3; i3++) {
            String keyAt = sBackgroundToForegroundPermissions.keyAt(i3);
            List<String> valueAt = sBackgroundToForegroundPermissions.valueAt(i3);
            int size4 = valueAt.size();
            for (int i4 = 0; i4 < size4; i4++) {
                sForegroundToBackgroundPermission.put(valueAt.get(i4), keyAt);
            }
        }
    }

    private static String getPermissionAppOp(String str) {
        return AppOpsManager.permissionToOp(str);
    }

    private static Integer getAppOpMode(String str, String str2, Context context) {
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
        if (applicationInfo == null) {
            return null;
        }
        return Integer.valueOf(((AppOpsManager) context.getSystemService(AppOpsManager.class)).unsafeCheckOpRaw(str2, applicationInfo.uid, str));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDefaultAppOpMode(String str) {
        return AppOpsManager.opToDefaultMode(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean setAppOpMode(String str, String str2, int i, Context context) {
        Integer appOpMode = getAppOpMode(str, str2, context);
        if (appOpMode == null || appOpMode.intValue() != i) {
            ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
            if (applicationInfo == null) {
                String str3 = LOG_TAG;
                Log.e(str3, "Cannot get ApplicationInfo for package to set app op mode: " + str);
                return false;
            }
            ((AppOpsManager) context.getSystemService(AppOpsManager.class)).setUidMode(str2, applicationInfo.uid, i);
            return true;
        }
        return false;
    }
}
