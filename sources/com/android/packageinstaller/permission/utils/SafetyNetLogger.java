package com.android.packageinstaller.permission.utils;

import android.content.pm.PackageInfo;
import android.os.Process;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class SafetyNetLogger {
    public static void logPermissionsRequested(PackageInfo packageInfo, List<AppPermissionGroup> list) {
        EventLog.writeEvent(1397638484, "individual_permissions_requested", Integer.valueOf(packageInfo.applicationInfo.uid), buildChangedPermissionForPackageMessage(packageInfo.packageName, list));
    }

    public static void logPermissionsToggled(ArraySet<AppPermissionGroup> arraySet) {
        ArrayMap arrayMap = new ArrayMap();
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            AppPermissionGroup valueAt = arraySet.valueAt(i);
            ArrayList arrayList = (ArrayList) arrayMap.get(valueAt.getApp().packageName);
            if (arrayList == null) {
                arrayList = new ArrayList();
                arrayMap.put(valueAt.getApp().packageName, arrayList);
            }
            arrayList.add(valueAt);
            if (valueAt.getBackgroundPermissions() != null) {
                arrayList.add(valueAt.getBackgroundPermissions());
            }
        }
        int size2 = arrayMap.size();
        for (int i2 = 0; i2 < size2; i2++) {
            EventLog.writeEvent(1397638484, "individual_permissions_toggled", Integer.valueOf(Process.myUid()), buildChangedPermissionForPackageMessage((String) arrayMap.keyAt(i2), (List) arrayMap.valueAt(i2)));
        }
    }

    public static void logPermissionToggled(AppPermissionGroup appPermissionGroup) {
        ArraySet arraySet = new ArraySet(1);
        arraySet.add(appPermissionGroup);
        logPermissionsToggled(arraySet);
    }

    private static void buildChangedPermissionForGroup(AppPermissionGroup appPermissionGroup, StringBuilder sb) {
        int size = appPermissionGroup.getPermissions().size();
        for (int i = 0; i < size; i++) {
            Permission permission = appPermissionGroup.getPermissions().get(i);
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(permission.getName());
            sb.append('|');
            sb.append(permission.isGrantedIncludingAppOp());
            sb.append('|');
            sb.append(permission.getFlags());
        }
    }

    private static String buildChangedPermissionForPackageMessage(String str, List<AppPermissionGroup> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(':');
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppPermissionGroup appPermissionGroup = list.get(i);
            buildChangedPermissionForGroup(appPermissionGroup, sb);
            if (appPermissionGroup.getBackgroundPermissions() != null) {
                buildChangedPermissionForGroup(appPermissionGroup.getBackgroundPermissions(), sb);
            }
        }
        return sb.toString();
    }
}
