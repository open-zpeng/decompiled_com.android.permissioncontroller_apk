package com.android.packageinstaller.permission.model;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.PermissionApps;
import com.android.packageinstaller.permission.model.PermissionGroups;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public final class PermissionGroups implements LoaderManager.LoaderCallbacks<List<PermissionGroup>> {
    private final PermissionsGroupsChangeCallback mCallback;
    private final Context mContext;
    private final boolean mGetAppUiInfo;
    private final boolean mGetNonPlatformPermissions;
    private final ArrayList<PermissionGroup> mGroups = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface PermissionsGroupsChangeCallback {
        void onPermissionGroupsChanged();
    }

    public PermissionGroups(Context context, final LoaderManager loaderManager, PermissionsGroupsChangeCallback permissionsGroupsChangeCallback, boolean z, boolean z2) {
        this.mContext = context;
        this.mCallback = permissionsGroupsChangeCallback;
        this.mGetAppUiInfo = z;
        this.mGetNonPlatformPermissions = z2;
        new Handler().post(new Runnable() { // from class: com.android.packageinstaller.permission.model.-$$Lambda$PermissionGroups$K2jn2bfSAL7EiZ22Md7FGV6htqA
            @Override // java.lang.Runnable
            public final void run() {
                PermissionGroups.this.lambda$new$0$PermissionGroups(loaderManager);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$PermissionGroups(LoaderManager loaderManager) {
        loaderManager.initLoader(0, null, this);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<List<PermissionGroup>> onCreateLoader(int i, Bundle bundle) {
        return new PermissionsLoader(this.mContext, this.mGetAppUiInfo, this.mGetNonPlatformPermissions);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<PermissionGroup>> loader, List<PermissionGroup> list) {
        if (this.mGroups.equals(list)) {
            return;
        }
        this.mGroups.clear();
        this.mGroups.addAll(list);
        this.mCallback.onPermissionGroupsChanged();
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<PermissionGroup>> loader) {
        this.mGroups.clear();
        this.mCallback.onPermissionGroupsChanged();
    }

    public List<PermissionGroup> getGroups() {
        return this.mGroups;
    }

    public PermissionGroup getGroup(String str) {
        Iterator<PermissionGroup> it = this.mGroups.iterator();
        while (it.hasNext()) {
            PermissionGroup next = it.next();
            if (next.getName().equals(str)) {
                return next;
            }
        }
        return null;
    }

    private static CharSequence loadItemInfoLabel(Context context, PackageItemInfo packageItemInfo) {
        CharSequence loadSafeLabel = packageItemInfo.loadSafeLabel(context.getPackageManager(), 0.0f, 5);
        return loadSafeLabel == null ? packageItemInfo.name : loadSafeLabel;
    }

    private static Drawable loadItemInfoIcon(Context context, PackageItemInfo packageItemInfo) {
        Drawable loadDrawable = packageItemInfo.icon > 0 ? Utils.loadDrawable(context.getPackageManager(), packageItemInfo.packageName, packageItemInfo.icon) : null;
        return loadDrawable == null ? context.getDrawable(R.drawable.ic_perm_device_info) : loadDrawable;
    }

    public static List<PermissionGroup> getAllPermissionGroups(Context context, Supplier<Boolean> supplier, boolean z, boolean z2) {
        return getPermissionGroups(context, supplier, z, z2, null, null);
    }

    public static List<PermissionGroup> getPermissionGroups(Context context, Supplier<Boolean> supplier, boolean z, boolean z2, String[] strArr, String str) {
        int i;
        int i2;
        PermissionInfo[] permissionInfoArr;
        int i3;
        PackageManager packageManager;
        PermissionApps.PmCache pmCache = new PermissionApps.PmCache(context.getPackageManager());
        PermissionApps.AppDataCache appDataCache = new PermissionApps.AppDataCache(context.getPackageManager(), context);
        ArrayList arrayList = new ArrayList();
        ArraySet arraySet = new ArraySet();
        PackageManager packageManager2 = context.getPackageManager();
        Iterator<PermissionGroupInfo> it = getPermissionGroupInfos(context, strArr).iterator();
        while (true) {
            int i4 = 1073741824;
            boolean z3 = false;
            int i5 = 1;
            if (it.hasNext()) {
                PermissionGroupInfo next = it.next();
                if (supplier != null && supplier.get().booleanValue()) {
                    return Collections.emptyList();
                }
                if (z2 || Utils.isModernPermissionGroup(next.name)) {
                    try {
                        for (PermissionInfo permissionInfo : Utils.getPermissionInfosForGroup(packageManager2, next.name)) {
                            arraySet.add(permissionInfo.name);
                            if (permissionInfo.getProtection() == 1) {
                                int i6 = permissionInfo.flags;
                                if ((i6 & 1073741824) != 0 && (i6 & 2) == 0) {
                                    z3 = true;
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                        packageManager = packageManager2;
                    }
                    if (z3) {
                        CharSequence loadItemInfoLabel = loadItemInfoLabel(context, next);
                        Drawable loadItemInfoIcon = loadItemInfoIcon(context, next);
                        packageManager = packageManager2;
                        PermissionApps permissionApps = new PermissionApps(context, next.name, str, null, pmCache, appDataCache);
                        permissionApps.refreshSync(z);
                        arrayList.add(new PermissionGroup(next.name, next.packageName, loadItemInfoLabel, loadItemInfoIcon, permissionApps.getTotalCount(), permissionApps.getGrantedCount(), permissionApps));
                        packageManager2 = packageManager;
                    }
                }
            } else {
                List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(4096);
                ArraySet arraySet2 = new ArraySet();
                for (PackageInfo packageInfo : installedPackages) {
                    String[] strArr2 = packageInfo.requestedPermissions;
                    if (strArr2 != null) {
                        for (String str2 : strArr2) {
                            arraySet2.add(str2);
                        }
                    }
                }
                for (PackageInfo packageInfo2 : installedPackages) {
                    PermissionInfo[] permissionInfoArr2 = packageInfo2.permissions;
                    if (permissionInfoArr2 != null) {
                        int length = permissionInfoArr2.length;
                        int i7 = 0;
                        while (i7 < length) {
                            PermissionInfo permissionInfo2 = permissionInfoArr2[i7];
                            if (arraySet.add(permissionInfo2.name) && permissionInfo2.getProtection() == i5 && (permissionInfo2.flags & i4) != 0 && ((z2 || Utils.isModernPermissionGroup(permissionInfo2.name)) && arraySet2.contains(permissionInfo2.name))) {
                                CharSequence loadItemInfoLabel2 = loadItemInfoLabel(context, permissionInfo2);
                                Drawable loadItemInfoIcon2 = loadItemInfoIcon(context, permissionInfo2);
                                i = i7;
                                i2 = length;
                                permissionInfoArr = permissionInfoArr2;
                                i3 = i5;
                                PermissionApps permissionApps2 = new PermissionApps(context, permissionInfo2.name, str, null, pmCache, appDataCache);
                                permissionApps2.refreshSync(z);
                                arrayList.add(new PermissionGroup(permissionInfo2.name, permissionInfo2.packageName, loadItemInfoLabel2, loadItemInfoIcon2, permissionApps2.getTotalCount(), permissionApps2.getGrantedCount(), permissionApps2));
                            } else {
                                i = i7;
                                i2 = length;
                                permissionInfoArr = permissionInfoArr2;
                                i3 = i5;
                            }
                            i7 = i + 1;
                            length = i2;
                            permissionInfoArr2 = permissionInfoArr;
                            i5 = i3;
                            i4 = 1073741824;
                        }
                    }
                }
                int size = arrayList.size();
                int i8 = 0;
                while (true) {
                    if (i8 >= size) {
                        break;
                    }
                    PermissionGroup permissionGroup = (PermissionGroup) arrayList.get(i8);
                    if (permissionGroup.getName().equals("android.permission-group.UNDEFINED") && permissionGroup.getTotal() == 0) {
                        arrayList.remove(i8);
                        break;
                    }
                    i8++;
                }
                Collections.sort(arrayList);
                return arrayList;
            }
        }
    }

    private static List<PermissionGroupInfo> getPermissionGroupInfos(Context context, String[] strArr) {
        if (strArr == null) {
            return context.getPackageManager().getAllPermissionGroups(0);
        }
        try {
            ArrayList arrayList = new ArrayList(strArr.length);
            for (String str : strArr) {
                arrayList.add(context.getPackageManager().getPermissionGroupInfo(str, 0));
            }
            return arrayList;
        } catch (PackageManager.NameNotFoundException unused) {
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PermissionsLoader extends AsyncTaskLoader<List<PermissionGroup>> implements PackageManager.OnPermissionsChangedListener {
        private final boolean mGetAppUiInfo;
        private final boolean mGetNonPlatformPermissions;

        PermissionsLoader(Context context, boolean z, boolean z2) {
            super(context);
            this.mGetAppUiInfo = z;
            this.mGetNonPlatformPermissions = z2;
        }

        @Override // android.content.Loader
        protected void onStartLoading() {
            getContext().getPackageManager().addOnPermissionsChangeListener(this);
            forceLoad();
        }

        @Override // android.content.Loader
        protected void onStopLoading() {
            getContext().getPackageManager().removeOnPermissionsChangeListener(this);
        }

        @Override // android.content.AsyncTaskLoader
        public List<PermissionGroup> loadInBackground() {
            return PermissionGroups.getAllPermissionGroups(getContext(), new Supplier() { // from class: com.android.packageinstaller.permission.model.-$$Lambda$STdGSsBsAPWT3cHR9wpC362b8SI
                @Override // java.util.function.Supplier
                public final Object get() {
                    return Boolean.valueOf(PermissionGroups.PermissionsLoader.this.isLoadInBackgroundCanceled());
                }
            }, this.mGetAppUiInfo, this.mGetNonPlatformPermissions);
        }

        public void onPermissionsChanged(int i) {
            forceLoad();
        }
    }
}
