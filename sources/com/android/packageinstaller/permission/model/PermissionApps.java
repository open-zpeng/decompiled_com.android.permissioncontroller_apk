package com.android.packageinstaller.permission.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PermissionApps {
    private final AppDataCache mAppDataCache;
    private ArrayMap<String, PermissionApp> mAppLookup;
    private final Callback mCallback;
    private final Context mContext;
    private CharSequence mDescription;
    private CharSequence mFullLabel;
    private final String mGroupName;
    private Drawable mIcon;
    private CharSequence mLabel;
    private final String mPackageName;
    private List<PermissionApp> mPermApps;
    private final PackageManager mPm;
    private final PmCache mPmCache;
    private boolean mRefreshing;
    private boolean mSkipUi;

    /* loaded from: classes.dex */
    public interface Callback {
        void onPermissionsLoaded(PermissionApps permissionApps);
    }

    public PermissionApps(Context context, String str, Callback callback) {
        this(context, str, null, callback, null, null);
    }

    public PermissionApps(Context context, String str, String str2, Callback callback, PmCache pmCache, AppDataCache appDataCache) {
        this.mPmCache = pmCache;
        this.mAppDataCache = appDataCache;
        this.mContext = context;
        this.mPm = this.mContext.getPackageManager();
        this.mGroupName = str;
        this.mCallback = callback;
        this.mPackageName = str2;
        loadGroupInfo();
    }

    public String getGroupName() {
        return this.mGroupName;
    }

    public void refresh(boolean z) {
        if (this.mRefreshing) {
            return;
        }
        this.mRefreshing = true;
        this.mSkipUi = !z;
        new PermissionAppsLoader().execute(new Void[0]);
    }

    public void refreshSync(boolean z) {
        this.mSkipUi = !z;
        createMap(loadPermissionApps());
    }

    public int getGrantedCount() {
        int i = 0;
        for (PermissionApp permissionApp : this.mPermApps) {
            if (Utils.shouldShowPermission(this.mContext, permissionApp.getPermissionGroup()) && Utils.isGroupOrBgGroupUserSensitive(permissionApp.mAppPermissionGroup) && permissionApp.areRuntimePermissionsGranted()) {
                i++;
            }
        }
        return i;
    }

    public int getTotalCount() {
        int i = 0;
        for (PermissionApp permissionApp : this.mPermApps) {
            if (Utils.shouldShowPermission(this.mContext, permissionApp.getPermissionGroup()) && Utils.isGroupOrBgGroupUserSensitive(permissionApp.mAppPermissionGroup)) {
                i++;
            }
        }
        return i;
    }

    public List<PermissionApp> getApps() {
        return this.mPermApps;
    }

    public PermissionApp getApp(String str) {
        return this.mAppLookup.get(str);
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public CharSequence getFullLabel() {
        return this.mFullLabel;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    private List<PackageInfo> getPackageInfos(UserHandle userHandle) {
        PmCache pmCache = this.mPmCache;
        List<PackageInfo> packages = pmCache != null ? pmCache.getPackages(userHandle.getIdentifier()) : null;
        if (packages != null) {
            if (this.mPackageName != null) {
                int size = packages.size();
                for (int i = 0; i < size; i++) {
                    PackageInfo packageInfo = packages.get(i);
                    if (this.mPackageName.equals(packageInfo.packageName)) {
                        ArrayList arrayList = new ArrayList(1);
                        arrayList.add(packageInfo);
                        return arrayList;
                    }
                }
            }
            return packages;
        }
        String str = this.mPackageName;
        if (str == null) {
            return this.mPm.getInstalledPackagesAsUser(4096, userHandle.getIdentifier());
        }
        try {
            PackageInfo packageInfo2 = this.mPm.getPackageInfo(str, 4096);
            ArrayList arrayList2 = new ArrayList(1);
            arrayList2.add(packageInfo2);
            return arrayList2;
        } catch (PackageManager.NameNotFoundException unused) {
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<PermissionApp> loadPermissionApps() {
        Drawable drawable;
        int i;
        int i2;
        PermissionInfo permissionInfo;
        PackageInfo packageInfo;
        int i3;
        List<PackageInfo> list;
        UserHandle userHandle;
        PackageInfo packageInfo2;
        Pair<String, Drawable> pair;
        String charSequence;
        PackageItemInfo groupInfo = Utils.getGroupInfo(this.mGroupName, this.mContext);
        if (groupInfo == null) {
            return Collections.emptyList();
        }
        List<PermissionInfo> groupPermissionInfos = Utils.getGroupPermissionInfos(this.mGroupName, this.mContext);
        if (groupPermissionInfos == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(groupPermissionInfos.size());
        for (int i4 = 0; i4 < groupPermissionInfos.size(); i4++) {
            PermissionInfo permissionInfo2 = groupPermissionInfos.get(i4);
            if ((permissionInfo2.protectionLevel & 15) == 1) {
                int i5 = permissionInfo2.flags;
                if ((1073741824 & i5) != 0 && (i5 & 2) == 0) {
                    arrayList.add(permissionInfo2);
                }
            }
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        CharSequence loadLabel = groupInfo.loadLabel(packageManager);
        CharSequence loadSafeLabel = groupInfo.loadSafeLabel(packageManager, 0.0f, 5);
        ArrayList arrayList2 = new ArrayList();
        for (UserHandle userHandle2 : ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles()) {
            List<PackageInfo> packageInfos = getPackageInfos(userHandle2);
            int size = packageInfos.size();
            int i6 = 0;
            while (i6 < size) {
                PackageInfo packageInfo3 = packageInfos.get(i6);
                if (packageInfo3.requestedPermissions != null) {
                    int i7 = 0;
                    while (true) {
                        String[] strArr = packageInfo3.requestedPermissions;
                        if (i7 >= strArr.length) {
                            break;
                        }
                        String str = strArr[i7];
                        Iterator it = arrayList.iterator();
                        while (true) {
                            drawable = null;
                            if (!it.hasNext()) {
                                i = i7;
                                i2 = i6;
                                permissionInfo = null;
                                break;
                            }
                            i = i7;
                            permissionInfo = (PermissionInfo) it.next();
                            i2 = i6;
                            if (str.equals(permissionInfo.name)) {
                                break;
                            }
                            i6 = i2;
                            i7 = i;
                        }
                        if (permissionInfo == null) {
                            packageInfo = packageInfo3;
                            i3 = size;
                            list = packageInfos;
                            userHandle = userHandle2;
                        } else {
                            packageInfo = packageInfo3;
                            i3 = size;
                            list = packageInfos;
                            userHandle = userHandle2;
                            AppPermissionGroup create = AppPermissionGroup.create(this.mContext, packageInfo3, groupInfo, groupPermissionInfos, loadLabel, loadSafeLabel, false);
                            if (create != null) {
                                AppDataCache appDataCache = this.mAppDataCache;
                                if (appDataCache == null || this.mSkipUi) {
                                    packageInfo2 = packageInfo;
                                    pair = null;
                                } else {
                                    packageInfo2 = packageInfo;
                                    pair = appDataCache.getAppData(userHandle.getIdentifier(), packageInfo2.applicationInfo);
                                }
                                if (this.mSkipUi) {
                                    charSequence = packageInfo2.packageName;
                                } else if (pair != null) {
                                    charSequence = (String) pair.first;
                                } else {
                                    charSequence = packageInfo2.applicationInfo.loadLabel(this.mPm).toString();
                                }
                                String str2 = charSequence;
                                if (!this.mSkipUi) {
                                    if (pair != null) {
                                        drawable = (Drawable) pair.second;
                                    } else {
                                        drawable = Utils.getBadgedIcon(this.mContext, packageInfo2.applicationInfo);
                                    }
                                }
                                arrayList2.add(new PermissionApp(packageInfo2.packageName, create, str2, drawable, packageInfo2.applicationInfo));
                            }
                        }
                        i7 = i + 1;
                        i6 = i2;
                        packageInfo3 = packageInfo;
                        size = i3;
                        packageInfos = list;
                        userHandle2 = userHandle;
                    }
                    i6 = i2 + 1;
                    size = i3;
                    packageInfos = list;
                    userHandle2 = userHandle;
                }
                i2 = i6;
                i3 = size;
                list = packageInfos;
                userHandle = userHandle2;
                i6 = i2 + 1;
                size = i3;
                packageInfos = list;
                userHandle2 = userHandle;
            }
        }
        Collections.sort(arrayList2);
        return arrayList2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createMap(List<PermissionApp> list) {
        this.mAppLookup = new ArrayMap<>();
        for (PermissionApp permissionApp : list) {
            this.mAppLookup.put(permissionApp.getKey(), permissionApp);
        }
        this.mPermApps = list;
    }

    private void loadGroupInfo() {
        PermissionGroupInfo permissionGroupInfo;
        try {
            try {
                permissionGroupInfo = this.mPm.getPermissionGroupInfo(this.mGroupName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("PermissionApps", "Can't find permission: " + this.mGroupName, e);
                return;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            PermissionInfo permissionInfo = this.mPm.getPermissionInfo(this.mGroupName, 0);
            if ((permissionInfo.protectionLevel & 15) != 1) {
                Log.w("PermissionApps", this.mGroupName + " is not a runtime permission");
                return;
            }
            permissionGroupInfo = permissionInfo;
        }
        this.mLabel = permissionGroupInfo.loadLabel(this.mPm);
        this.mFullLabel = permissionGroupInfo.loadSafeLabel(this.mPm, 0.0f, 5);
        if (permissionGroupInfo.icon != 0) {
            this.mIcon = permissionGroupInfo.loadUnbadgedIcon(this.mPm);
        } else {
            this.mIcon = this.mContext.getDrawable(R.drawable.ic_perm_device_info);
        }
        this.mIcon = Utils.applyTint(this.mContext, this.mIcon, 16843817);
        if (permissionGroupInfo instanceof PermissionGroupInfo) {
            this.mDescription = ((PermissionGroupInfo) permissionGroupInfo).loadDescription(this.mPm);
        } else if (permissionGroupInfo instanceof PermissionInfo) {
            this.mDescription = ((PermissionInfo) permissionGroupInfo).loadDescription(this.mPm);
        }
    }

    /* loaded from: classes.dex */
    public static class PermissionApp implements Comparable<PermissionApp> {
        private final AppPermissionGroup mAppPermissionGroup;
        private Drawable mIcon;
        private final ApplicationInfo mInfo;
        private String mLabel;
        private final String mPackageName;

        public PermissionApp(String str, AppPermissionGroup appPermissionGroup, String str2, Drawable drawable, ApplicationInfo applicationInfo) {
            this.mPackageName = str;
            this.mAppPermissionGroup = appPermissionGroup;
            this.mLabel = str2;
            this.mIcon = drawable;
            this.mInfo = applicationInfo;
        }

        public ApplicationInfo getAppInfo() {
            return this.mInfo;
        }

        public String getKey() {
            return this.mPackageName + getUid();
        }

        public String getLabel() {
            return this.mLabel;
        }

        public Drawable getIcon() {
            return this.mIcon;
        }

        public boolean areRuntimePermissionsGranted() {
            return this.mAppPermissionGroup.areRuntimePermissionsGranted();
        }

        public boolean isReviewRequired() {
            return this.mAppPermissionGroup.isReviewRequired();
        }

        public void grantRuntimePermissions() {
            this.mAppPermissionGroup.grantRuntimePermissions(false);
        }

        public void revokeRuntimePermissions() {
            this.mAppPermissionGroup.revokeRuntimePermissions(false);
        }

        public boolean isPolicyFixed() {
            return this.mAppPermissionGroup.isPolicyFixed();
        }

        public boolean isSystemFixed() {
            return this.mAppPermissionGroup.isSystemFixed();
        }

        public boolean hasGrantedByDefaultPermissions() {
            return this.mAppPermissionGroup.hasGrantedByDefaultPermission();
        }

        public boolean doesSupportRuntimePermissions() {
            return this.mAppPermissionGroup.doesSupportRuntimePermissions();
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public AppPermissionGroup getPermissionGroup() {
            return this.mAppPermissionGroup;
        }

        @Override // java.lang.Comparable
        public int compareTo(PermissionApp permissionApp) {
            int compareTo = this.mLabel.compareTo(permissionApp.mLabel);
            return compareTo == 0 ? getKey().compareTo(permissionApp.getKey()) : compareTo;
        }

        public int getUid() {
            return this.mAppPermissionGroup.getApp().applicationInfo.uid;
        }
    }

    /* loaded from: classes.dex */
    private class PermissionAppsLoader extends AsyncTask<Void, Void, List<PermissionApp>> {
        private PermissionAppsLoader() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<PermissionApp> doInBackground(Void... voidArr) {
            return PermissionApps.this.loadPermissionApps();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<PermissionApp> list) {
            PermissionApps.this.mRefreshing = false;
            PermissionApps.this.createMap(list);
            if (PermissionApps.this.mCallback != null) {
                PermissionApps.this.mCallback.onPermissionsLoaded(PermissionApps.this);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class PmCache {
        private final SparseArray<List<PackageInfo>> mPackageInfoCache = new SparseArray<>();
        private final PackageManager mPm;

        public PmCache(PackageManager packageManager) {
            this.mPm = packageManager;
        }

        public synchronized List<PackageInfo> getPackages(int i) {
            List list;
            list = this.mPackageInfoCache.get(i);
            if (list == null) {
                list = this.mPm.getInstalledPackagesAsUser(4096, i);
                this.mPackageInfoCache.put(i, list);
            }
            return list;
        }
    }

    /* loaded from: classes.dex */
    public static class AppDataCache {
        private final SparseArray<ArrayMap<String, Pair<String, Drawable>>> mCache = new SparseArray<>();
        private final Context mContext;
        private final PackageManager mPm;

        public AppDataCache(PackageManager packageManager, Context context) {
            this.mPm = packageManager;
            this.mContext = context;
        }

        public Pair<String, Drawable> getAppData(int i, ApplicationInfo applicationInfo) {
            ArrayMap<String, Pair<String, Drawable>> arrayMap = this.mCache.get(i);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mCache.put(i, arrayMap);
            }
            Pair<String, Drawable> pair = arrayMap.get(applicationInfo.packageName);
            if (pair == null) {
                Pair<String, Drawable> create = Pair.create(applicationInfo.loadLabel(this.mPm).toString(), Utils.getBadgedIcon(this.mContext, applicationInfo));
                arrayMap.put(applicationInfo.packageName, create);
                return create;
            }
            return pair;
        }
    }
}
