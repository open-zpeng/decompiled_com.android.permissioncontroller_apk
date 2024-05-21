package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.permission.PermissionControllerService;
import android.permission.PermissionManager;
import android.permission.RuntimePermissionPresentationInfo;
import android.permission.RuntimePermissionUsageInfo;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Xml;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.utils.Utils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public final class PermissionControllerServiceImpl extends PermissionControllerService {
    private static final String LOG_TAG = "PermissionControllerServiceImpl";

    private ArrayList<String> addSplitPermissions(List<String> list, int i) {
        List splitPermissions = ((PermissionManager) getSystemService(PermissionManager.class)).getSplitPermissions();
        ArrayList<String> arrayList = new ArrayList<>(list);
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str = list.get(i2);
            int size2 = splitPermissions.size();
            for (int i3 = 0; i3 < size2; i3++) {
                PermissionManager.SplitPermissionInfo splitPermissionInfo = (PermissionManager.SplitPermissionInfo) splitPermissions.get(i3);
                if (i < splitPermissionInfo.getTargetSdk() && splitPermissionInfo.getSplitPermission().equals(str)) {
                    arrayList.addAll(splitPermissionInfo.getNewPermissions());
                }
            }
        }
        return arrayList;
    }

    private PackageInfo getPkgInfo(String str) {
        try {
            return getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = LOG_TAG;
            Log.w(str2, str + " not found", e);
            return null;
        }
    }

    private ArrayList<AppPermissionGroup> getRevocableGroupsForPermissions(ArrayList<String> arrayList, AppPermissions appPermissions) {
        ArrayList<AppPermissionGroup> arrayList2 = new ArrayList<>();
        int size = appPermissions.getPermissionGroups().size();
        for (int i = 0; i < size; i++) {
            AppPermissionGroup appPermissionGroup = appPermissions.getPermissionGroups().get(i);
            if (!appPermissionGroup.isPolicyFixed() && !appPermissionGroup.isSystemFixed()) {
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String str = arrayList.get(i2);
                    if (appPermissionGroup.hasPermission(str)) {
                        arrayList2.add(appPermissionGroup);
                        AppPermissionGroup backgroundPermissions = appPermissionGroup.getBackgroundPermissions();
                        if (backgroundPermissions != null) {
                            arrayList2.add(backgroundPermissions);
                        }
                    } else {
                        AppPermissionGroup backgroundPermissions2 = appPermissionGroup.getBackgroundPermissions();
                        if (backgroundPermissions2 != null && backgroundPermissions2.hasPermission(str)) {
                            arrayList2.add(backgroundPermissions2);
                        }
                    }
                }
            }
        }
        return arrayList2;
    }

    private ArrayList<String> revokePermissionGroups(ArrayList<AppPermissionGroup> arrayList) {
        ArrayList<String> arrayList2 = new ArrayList<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            AppPermissionGroup appPermissionGroup = arrayList.get(i);
            ArrayList<Permission> permissions = appPermissionGroup.getPermissions();
            appPermissionGroup.unsetReviewRequired();
            int size2 = permissions.size();
            for (int i2 = 0; i2 < size2; i2++) {
                Permission permission = permissions.get(i2);
                if (permission.isGrantedIncludingAppOp()) {
                    arrayList2.add(permission.getName());
                }
            }
            appPermissionGroup.revokeRuntimePermissions(false);
        }
        return arrayList2;
    }

    public /* synthetic */ void lambda$onRevokeRuntimePermissions$0$PermissionControllerServiceImpl(Consumer consumer, Map map, boolean z, int i, String str) {
        consumer.accept(onRevokeRuntimePermissions(map, z, i, str));
    }

    public void onRevokeRuntimePermissions(final Map<String, List<String>> map, final boolean z, final int i, final String str, final Consumer<Map<String, List<String>>> consumer) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$nNDNFYOCW5nRJI5ERHAziHxjZ18
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onRevokeRuntimePermissions$0$PermissionControllerServiceImpl(consumer, map, z, i, str);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Map<String, List<String>> onRevokeRuntimePermissions(Map<String, List<String>> map, boolean z, int i, String str) {
        int i2;
        String[] packagesForUid;
        int i3;
        int i4;
        String[] strArr;
        int i5 = 2;
        if (i != 1 && i != 2) {
            Log.e(LOG_TAG, "Invalid reason " + i);
            return Collections.emptyMap();
        }
        PackageManager packageManager = getPackageManager();
        PackageInfo pkgInfo = getPkgInfo(str);
        if (pkgInfo == null) {
            return Collections.emptyMap();
        }
        int i6 = pkgInfo.applicationInfo.targetSdkVersion;
        ArrayMap arrayMap = new ArrayMap();
        ArrayList arrayList = new ArrayList();
        Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, List<String>> next = it.next();
            PackageInfo pkgInfo2 = getPkgInfo(next.getKey());
            if (pkgInfo2 != null && (packagesForUid = packageManager.getPackagesForUid(pkgInfo2.applicationInfo.uid)) != null) {
                int length = packagesForUid.length;
                int i7 = 0;
                while (i7 < length) {
                    String str2 = packagesForUid[i7];
                    PackageInfo pkgInfo3 = getPkgInfo(str2);
                    if (pkgInfo3 != null) {
                        if (i == i5 && !str.equals(packageManager.getInstallerPackageName(str2))) {
                            Log.i(LOG_TAG, "Ignoring " + str2 + " as it is not installed by " + str);
                        } else {
                            ArrayList<String> addSplitPermissions = addSplitPermissions(next.getValue(), i6);
                            i3 = i7;
                            i4 = length;
                            strArr = packagesForUid;
                            AppPermissions appPermissions = new AppPermissions(this, pkgInfo3, false, true, null);
                            ArrayList<String> revokePermissionGroups = revokePermissionGroups(getRevocableGroupsForPermissions(addSplitPermissions, appPermissions));
                            if (!revokePermissionGroups.isEmpty()) {
                                arrayMap.put(str2, revokePermissionGroups);
                                arrayList.add(appPermissions);
                            }
                            i7 = i3 + 1;
                            length = i4;
                            packagesForUid = strArr;
                            i5 = 2;
                        }
                    }
                    i3 = i7;
                    i4 = length;
                    strArr = packagesForUid;
                    i7 = i3 + 1;
                    length = i4;
                    packagesForUid = strArr;
                    i5 = 2;
                }
            }
        }
        if (!z) {
            int size = arrayList.size();
            for (i2 = 0; i2 < size; i2++) {
                ((AppPermissions) arrayList.get(i2)).persistChanges(true);
            }
        }
        return arrayMap;
    }

    public void onGetRuntimePermissionsBackup(final UserHandle userHandle, final OutputStream outputStream, final Runnable runnable) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$fcXc0gE4v7slKkatKh2wmeXCgwg
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onGetRuntimePermissionsBackup$1$PermissionControllerServiceImpl(userHandle, outputStream, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$onGetRuntimePermissionsBackup$1$PermissionControllerServiceImpl(UserHandle userHandle, OutputStream outputStream, Runnable runnable) {
        onGetRuntimePermissionsBackup(userHandle, outputStream);
        runnable.run();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void onGetRuntimePermissionsBackup(UserHandle userHandle, OutputStream outputStream) {
        BackupHelper backupHelper = new BackupHelper(this, userHandle);
        try {
            XmlSerializer newSerializer = Xml.newSerializer();
            newSerializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
            backupHelper.writeState(newSerializer);
            newSerializer.flush();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unable to write permissions backup", e);
        }
    }

    public void onRestoreRuntimePermissionsBackup(final UserHandle userHandle, final InputStream inputStream, final Runnable runnable) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$vT3hRblLhqCa659M1n8u9oYMIpc
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onRestoreRuntimePermissionsBackup$2$PermissionControllerServiceImpl(userHandle, inputStream, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$onRestoreRuntimePermissionsBackup$2$PermissionControllerServiceImpl(UserHandle userHandle, InputStream inputStream, Runnable runnable) {
        onRestoreRuntimePermissionsBackup(userHandle, inputStream);
        runnable.run();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void onRestoreRuntimePermissionsBackup(UserHandle userHandle, InputStream inputStream) {
        try {
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(inputStream, StandardCharsets.UTF_8.name());
            new BackupHelper(this, userHandle).restoreState(newPullParser);
        } catch (Exception e) {
            String str = LOG_TAG;
            Log.e(str, "Exception restoring permissions: " + e.getMessage());
        }
    }

    public void onRestoreDelayedRuntimePermissionsBackup(final String str, final UserHandle userHandle, final Consumer<Boolean> consumer) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$wHAhob5PsX8wKMTaFp8IJWkb1LE
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onRestoreDelayedRuntimePermissionsBackup$3$PermissionControllerServiceImpl(consumer, str, userHandle);
            }
        });
    }

    public /* synthetic */ void lambda$onRestoreDelayedRuntimePermissionsBackup$3$PermissionControllerServiceImpl(Consumer consumer, String str, UserHandle userHandle) {
        consumer.accept(Boolean.valueOf(onRestoreDelayedRuntimePermissionsBackup(str, userHandle)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean onRestoreDelayedRuntimePermissionsBackup(String str, UserHandle userHandle) {
        try {
            return new BackupHelper(this, userHandle).restoreDelayedState(str);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            Log.e(str2, "Exception restoring delayed permissions: " + e.getMessage());
            return false;
        }
    }

    public void onGetAppPermissions(final String str, final Consumer<List<RuntimePermissionPresentationInfo>> consumer) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$QbwwSlwXOl5zG1OcPXaytrS_C-o
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onGetAppPermissions$4$PermissionControllerServiceImpl(consumer, str);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$onGetAppPermissions$4$PermissionControllerServiceImpl(Consumer consumer, String str) {
        consumer.accept(onGetAppPermissions((Context) this, str));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<RuntimePermissionPresentationInfo> onGetAppPermissions(Context context, String str) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 4096);
            ArrayList arrayList = new ArrayList();
            for (AppPermissionGroup appPermissionGroup : new AppPermissions(context, packageInfo, false, null).getPermissionGroups()) {
                if (Utils.shouldShowPermission(context, appPermissionGroup)) {
                    arrayList.add(new RuntimePermissionPresentationInfo(appPermissionGroup.getLabel(), appPermissionGroup.areRuntimePermissionsGranted(), "android".equals(appPermissionGroup.getDeclaringPackage())));
                }
            }
            return arrayList;
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = LOG_TAG;
            Log.e(str2, "Error getting package:" + str, e);
            return Collections.emptyList();
        }
    }

    public void onRevokeRuntimePermission(final String str, final String str2, final Runnable runnable) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$yJSh2j6Mb8NIKmq4mDHiDguPc_E
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onRevokeRuntimePermission$5$PermissionControllerServiceImpl(str, str2, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$onRevokeRuntimePermission$5$PermissionControllerServiceImpl(String str, String str2, Runnable runnable) {
        onRevokeRuntimePermission(str, str2);
        runnable.run();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void onRevokeRuntimePermission(String str, String str2) {
        try {
            AppPermissionGroup groupForPermission = new AppPermissions(this, getPackageManager().getPackageInfo(str, 4096), false, null).getGroupForPermission(str2);
            if (groupForPermission != null) {
                groupForPermission.revokeRuntimePermissions(false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            String str3 = LOG_TAG;
            Log.e(str3, "Error getting package:" + str, e);
        }
    }

    public void onCountPermissionApps(final List<String> list, final int i, final IntConsumer intConsumer) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$D2g7I5Y7aGt6lLUlp9LZZw5FoNs
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onCountPermissionApps$6$PermissionControllerServiceImpl(intConsumer, list, i);
            }
        });
    }

    public /* synthetic */ void lambda$onCountPermissionApps$6$PermissionControllerServiceImpl(IntConsumer intConsumer, List list, int i) {
        intConsumer.accept(onCountPermissionApps(list, i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int onCountPermissionApps(List<String> list, int i) {
        boolean z = (i & 2) != 0;
        boolean z2 = (i & 1) != 0;
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(4096);
        int size = installedPackages.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            PackageInfo packageInfo = installedPackages.get(i3);
            int size2 = list.size();
            for (int i4 = 0; i4 < size2; i4++) {
                String str = list.get(i4);
                AppPermissionGroup create = AppPermissionGroup.create(this, packageInfo, list.get(i4), true);
                if (create != null && Utils.shouldShowPermission(this, create)) {
                    if (!create.hasPermission(str) && ((create = create.getBackgroundPermissions()) == null || !create.hasPermission(str))) {
                        create = null;
                    }
                    if (create != null && ((z || create.isUserSensitive()) && (!z2 || create.areRuntimePermissionsGranted()))) {
                        i2++;
                        break;
                    }
                }
            }
        }
        return i2;
    }

    public void onGetPermissionUsages(final boolean z, final long j, final Consumer<List<RuntimePermissionUsageInfo>> consumer) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$dImieHWAhnJMIMopQPLL-IlYaaE
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onGetPermissionUsages$7$PermissionControllerServiceImpl(consumer, z, j);
            }
        });
    }

    public /* synthetic */ void lambda$onGetPermissionUsages$7$PermissionControllerServiceImpl(Consumer consumer, boolean z, long j) {
        consumer.accept(onGetPermissionUsages(z, j));
    }

    private List<RuntimePermissionUsageInfo> onGetPermissionUsages(boolean z, long j) {
        return Collections.emptyList();
    }

    public /* synthetic */ void lambda$onSetRuntimePermissionGrantStateByDeviceAdmin$8$PermissionControllerServiceImpl(Consumer consumer, String str, String str2, String str3, int i) {
        consumer.accept(Boolean.valueOf(onSetRuntimePermissionGrantStateByDeviceAdmin(str, str2, str3, i)));
    }

    public void onSetRuntimePermissionGrantStateByDeviceAdmin(final String str, final String str2, final String str3, final int i, final Consumer<Boolean> consumer) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$fwO9GYBGMrarIvSwFPcyCW4zdn0
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onSetRuntimePermissionGrantStateByDeviceAdmin$8$PermissionControllerServiceImpl(consumer, str, str2, str3, i);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean onSetRuntimePermissionGrantStateByDeviceAdmin(String str, String str2, String str3, int i) {
        Permission permission;
        PackageInfo pkgInfo = getPkgInfo(str);
        boolean z = false;
        if (pkgInfo == null) {
            Log.w(LOG_TAG, "Cannot fix " + str3 + " as admin " + str + " cannot be found");
            return false;
        }
        PackageInfo pkgInfo2 = getPkgInfo(str2);
        if (pkgInfo2 == null) {
            Log.w(LOG_TAG, "Cannot fix " + str3 + " as " + str2 + " cannot be found");
            return false;
        }
        ArrayList<String> addSplitPermissions = addSplitPermissions(Collections.singletonList(str3), pkgInfo.applicationInfo.targetSdkVersion);
        AppPermissions appPermissions = new AppPermissions(this, pkgInfo2, false, true, null);
        int size = addSplitPermissions.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str4 = addSplitPermissions.get(i2);
            AppPermissionGroup groupForPermission = appPermissions.getGroupForPermission(str4);
            if (groupForPermission != null && !groupForPermission.isSystemFixed() && (permission = groupForPermission.getPermission(str4)) != null) {
                if (i == 0) {
                    permission.setPolicyFixed(false);
                } else if (i == 1) {
                    permission.setPolicyFixed(true);
                    groupForPermission.grantRuntimePermissions(false, new String[]{str4});
                } else if (i != 2) {
                    return false;
                } else {
                    permission.setPolicyFixed(true);
                    groupForPermission.revokeRuntimePermissions(false, new String[]{str4});
                }
            }
        }
        appPermissions.persistChanges((i == 2 || !str.equals(str2)) ? true : true);
        return true;
    }

    public void onGrantOrUpgradeDefaultRuntimePermissions(final Runnable runnable) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$PermissionControllerServiceImpl$Qi7gxnenPri84RbPk35Tql4fMhg
            @Override // java.lang.Runnable
            public final void run() {
                PermissionControllerServiceImpl.this.lambda$onGrantOrUpgradeDefaultRuntimePermissions$9$PermissionControllerServiceImpl(runnable);
            }
        });
    }

    public /* synthetic */ void lambda$onGrantOrUpgradeDefaultRuntimePermissions$9$PermissionControllerServiceImpl(Runnable runnable) {
        onGrantOrUpgradeDefaultRuntimePermissions();
        runnable.run();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void onGrantOrUpgradeDefaultRuntimePermissions() {
        RuntimePermissionsUpgradeController.upgradeIfNeeded(this);
    }
}
