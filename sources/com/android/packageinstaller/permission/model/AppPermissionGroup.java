package com.android.packageinstaller.permission.model;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.UserHandle;
import android.permission.PermissionManager;
import android.util.ArrayMap;
import android.util.Log;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.service.LocationAccessCheck;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.SoftRestrictedPermissionPolicy;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public final class AppPermissionGroup implements Comparable<AppPermissionGroup> {
    private static final String LOG_TAG = "AppPermissionGroup";
    private final ActivityManager mActivityManager;
    private final AppOpsManager mAppOps;
    private final boolean mAppSupportsRuntimePermissions;
    private AppPermissionGroup mBackgroundPermissions;
    private final int mBackgroundRequest;
    private final int mBackgroundRequestDetail;
    private final Collator mCollator;
    private boolean mContainsEphemeralPermission;
    private boolean mContainsPreRuntimePermission;
    private final Context mContext;
    private final String mDeclaringPackage;
    private final boolean mDelayChanges;
    private final CharSequence mDescription;
    private final CharSequence mFullLabel;
    private boolean mHasPermissionWithBackgroundMode;
    private final String mIconPkg;
    private final int mIconResId;
    private final boolean mIsEphemeralApp;
    private final boolean mIsNonIsolatedStorage;
    private final CharSequence mLabel;
    private final String mName;
    private final PackageInfo mPackageInfo;
    private final PackageManager mPackageManager;
    private final ArrayMap<String, Permission> mPermissions = new ArrayMap<>();
    private final int mRequest;
    private final int mRequestDetail;
    private boolean mTriggerLocationAccessCheckOnPersist;
    private final UserHandle mUserHandle;

    public static AppPermissionGroup create(Context context, PackageInfo packageInfo, String str, boolean z) {
        List<PermissionInfo> list = null;
        try {
            PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
            if ((permissionInfo.protectionLevel & 15) == 1) {
                int i = permissionInfo.flags;
                if ((1073741824 & i) != 0 && (i & 2) == 0) {
                    String groupOfPermission = Utils.getGroupOfPermission(permissionInfo);
                    PermissionInfo permissionInfo2 = permissionInfo;
                    if (groupOfPermission != null) {
                        try {
                            permissionInfo2 = context.getPackageManager().getPermissionGroupInfo(groupOfPermission, 0);
                        } catch (PackageManager.NameNotFoundException unused) {
                        }
                    }
                    if (permissionInfo2 instanceof PermissionGroupInfo) {
                        try {
                            list = Utils.getPermissionInfosForGroup(context.getPackageManager(), ((PackageItemInfo) permissionInfo2).name);
                        } catch (PackageManager.NameNotFoundException unused2) {
                        }
                    }
                    return create(context, packageInfo, permissionInfo2, list, z);
                }
            }
        } catch (PackageManager.NameNotFoundException unused3) {
        }
        return null;
    }

    public static AppPermissionGroup create(Context context, PackageInfo packageInfo, PackageItemInfo packageItemInfo, List<PermissionInfo> list, boolean z) {
        PackageManager packageManager = context.getPackageManager();
        return create(context, packageInfo, packageItemInfo, list, packageItemInfo.loadLabel(packageManager), packageItemInfo.loadSafeLabel(packageManager, 0.0f, 5), z);
    }

    public static AppPermissionGroup create(Context context, PackageInfo packageInfo, PackageItemInfo packageItemInfo, List<PermissionInfo> list, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        ArrayList arrayList;
        Permission permission;
        PermissionInfo permissionInfo;
        AppOpsManager appOpsManager;
        boolean z2;
        PackageManager packageManager;
        UserHandle userHandle;
        UserHandle userHandle2;
        PackageManager packageManager2;
        AppPermissionGroup appPermissionGroup;
        PackageManager packageManager3 = context.getPackageManager();
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid);
        if (packageItemInfo instanceof PermissionInfo) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add((PermissionInfo) packageItemInfo);
            arrayList = arrayList2;
        } else {
            arrayList = list;
        }
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        AppOpsManager appOpsManager2 = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        AppPermissionGroup appPermissionGroup2 = r13;
        AppOpsManager appOpsManager3 = appOpsManager2;
        UserHandle userHandle3 = userHandleForUid;
        PackageManager packageManager4 = packageManager3;
        AppPermissionGroup appPermissionGroup3 = new AppPermissionGroup(context, packageInfo, packageItemInfo.name, packageItemInfo.packageName, charSequence, charSequence2, loadGroupDescription(context, packageItemInfo, packageManager3), getRequest(packageItemInfo), getRequestDetail(packageItemInfo), getBackgroundRequest(packageItemInfo), getBackgroundRequestDetail(packageItemInfo), packageItemInfo.packageName, packageItemInfo.icon, userHandle3, z, appOpsManager2);
        Set<String> whitelistedRestrictedPermissions = context.getPackageManager().getWhitelistedRestrictedPermissions(packageInfo.packageName, 7);
        ArrayMap arrayMap = new ArrayMap();
        String[] strArr = packageInfo.requestedPermissions;
        int length = strArr == null ? 0 : strArr.length;
        String str = packageInfo.packageName;
        int i = 0;
        while (i < length) {
            String str2 = packageInfo.requestedPermissions[i];
            Iterator<PermissionInfo> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    permissionInfo = null;
                    break;
                }
                PermissionInfo next = it.next();
                if (str2.equals(next.name)) {
                    permissionInfo = next;
                    break;
                }
            }
            if (permissionInfo != null && (permissionInfo.protectionLevel & 15) == 1 && (packageInfo.applicationInfo.targetSdkVersion > 22 || "android".equals(packageItemInfo.packageName))) {
                boolean z3 = (packageInfo.requestedPermissionsFlags[i] & 2) != 0;
                String permissionToOp = "android".equals(permissionInfo.packageName) ? AppOpsManager.permissionToOp(permissionInfo.name) : null;
                if (permissionToOp == null) {
                    userHandle = userHandle3;
                    appOpsManager = appOpsManager3;
                    packageManager = packageManager4;
                    z2 = false;
                } else {
                    appOpsManager = appOpsManager3;
                    int unsafeCheckOpRaw = appOpsManager.unsafeCheckOpRaw(permissionToOp, packageInfo.applicationInfo.uid, str);
                    z2 = unsafeCheckOpRaw == 0 || unsafeCheckOpRaw == 4;
                    packageManager = packageManager4;
                    userHandle = userHandle3;
                }
                userHandle2 = userHandle;
                packageManager2 = packageManager;
                appOpsManager3 = appOpsManager;
                Permission permission2 = new Permission(str2, permissionInfo, z3, permissionToOp, z2, packageManager.getPermissionFlags(str2, str, userHandle));
                if (permissionInfo.backgroundPermission != null) {
                    appPermissionGroup = appPermissionGroup2;
                    appPermissionGroup.mHasPermissionWithBackgroundMode = true;
                } else {
                    appPermissionGroup = appPermissionGroup2;
                }
                arrayMap.put(str2, permission2);
            } else {
                userHandle2 = userHandle3;
                appPermissionGroup = appPermissionGroup2;
                packageManager2 = packageManager4;
            }
            i++;
            appPermissionGroup2 = appPermissionGroup;
            userHandle3 = userHandle2;
            packageManager4 = packageManager2;
        }
        AppPermissionGroup appPermissionGroup4 = appPermissionGroup2;
        int size = arrayMap.size();
        if (size == 0) {
            return null;
        }
        for (int i2 = 0; i2 < arrayMap.size(); i2++) {
            Permission permission3 = (Permission) arrayMap.valueAt(i2);
            if (permission3.getBackgroundPermissionName() != null && (permission = (Permission) arrayMap.get(permission3.getBackgroundPermissionName())) != null) {
                permission.addForegroundPermissions(permission3);
                permission3.setBackgroundPermission(permission);
                if (((AppOpsManager) context.getSystemService(AppOpsManager.class)).unsafeCheckOpRaw(permission3.getAppOp(), packageInfo.applicationInfo.uid, packageInfo.packageName) == 0) {
                    permission.setAppOpAllowed(true);
                }
            }
        }
        for (int i3 = 0; i3 < size; i3++) {
            Permission permission4 = (Permission) arrayMap.valueAt(i3);
            if (permission4.isBackgroundPermission()) {
                if (appPermissionGroup4.getBackgroundPermissions() == null) {
                    appPermissionGroup4.mBackgroundPermissions = new AppPermissionGroup(appPermissionGroup4.mContext, appPermissionGroup4.getApp(), appPermissionGroup4.getName(), appPermissionGroup4.getDeclaringPackage(), appPermissionGroup4.getLabel(), appPermissionGroup4.getFullLabel(), appPermissionGroup4.getDescription(), appPermissionGroup4.getRequest(), appPermissionGroup4.getRequestDetail(), appPermissionGroup4.getBackgroundRequest(), appPermissionGroup4.getBackgroundRequestDetail(), appPermissionGroup4.getIconPkg(), appPermissionGroup4.getIconResId(), appPermissionGroup4.getUser(), z, appOpsManager3);
                }
                appPermissionGroup4.getBackgroundPermissions().addPermission(permission4);
            } else if ((!permission4.isHardRestricted() || whitelistedRestrictedPermissions.contains(permission4.getName())) && (!permission4.isSoftRestricted() || SoftRestrictedPermissionPolicy.shouldShow(packageInfo, permission4))) {
                appPermissionGroup4.addPermission(permission4);
            }
        }
        if (appPermissionGroup4.getPermissions().isEmpty()) {
            return null;
        }
        return appPermissionGroup4;
    }

    private static int getRequest(PackageItemInfo packageItemInfo) {
        if (packageItemInfo instanceof PermissionGroupInfo) {
            return ((PermissionGroupInfo) packageItemInfo).requestRes;
        }
        if (packageItemInfo instanceof PermissionInfo) {
            return ((PermissionInfo) packageItemInfo).requestRes;
        }
        return 0;
    }

    private static CharSequence loadGroupDescription(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager) {
        CharSequence loadDescription;
        if (packageItemInfo instanceof PermissionGroupInfo) {
            loadDescription = ((PermissionGroupInfo) packageItemInfo).loadDescription(packageManager);
        } else {
            loadDescription = packageItemInfo instanceof PermissionInfo ? ((PermissionInfo) packageItemInfo).loadDescription(packageManager) : null;
        }
        return (loadDescription == null || loadDescription.length() <= 0) ? context.getString(R.string.default_permission_description) : loadDescription;
    }

    private AppPermissionGroup(Context context, PackageInfo packageInfo, String str, String str2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, int i2, int i3, int i4, String str3, int i5, UserHandle userHandle, boolean z, AppOpsManager appOpsManager) {
        int i6 = packageInfo.applicationInfo.targetSdkVersion;
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mPackageInfo = packageInfo;
        this.mAppSupportsRuntimePermissions = i6 > 22;
        this.mIsEphemeralApp = packageInfo.applicationInfo.isInstantApp();
        this.mAppOps = appOpsManager;
        this.mActivityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        this.mDeclaringPackage = str2;
        this.mName = str;
        this.mLabel = charSequence;
        this.mFullLabel = charSequence2;
        this.mDescription = charSequence3;
        this.mCollator = Collator.getInstance(context.getResources().getConfiguration().getLocales().get(0));
        this.mRequest = i;
        this.mRequestDetail = i2;
        this.mBackgroundRequest = i3;
        this.mBackgroundRequestDetail = i4;
        this.mDelayChanges = z;
        if (i5 != 0) {
            this.mIconPkg = str3;
            this.mIconResId = i5;
        } else {
            this.mIconPkg = context.getPackageName();
            this.mIconResId = R.drawable.ic_perm_device_info;
        }
        this.mIsNonIsolatedStorage = this.mAppOps.unsafeCheckOpNoThrow("android:legacy_storage", packageInfo.applicationInfo.uid, packageInfo.packageName) == 0;
    }

    public boolean doesSupportRuntimePermissions() {
        return this.mAppSupportsRuntimePermissions;
    }

    public boolean isGrantingAllowed() {
        return (!this.mIsEphemeralApp || this.mContainsEphemeralPermission) && (this.mAppSupportsRuntimePermissions || this.mContainsPreRuntimePermission);
    }

    public boolean isReviewRequired() {
        if (this.mAppSupportsRuntimePermissions) {
            return false;
        }
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isReviewRequired()) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserSensitive() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isUserSensitive()) {
                return true;
            }
        }
        return false;
    }

    public void unsetReviewRequired() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            Permission valueAt = this.mPermissions.valueAt(i);
            if (valueAt.isReviewRequired()) {
                valueAt.unsetReviewRequired();
            }
        }
        if (this.mDelayChanges) {
            return;
        }
        persistChanges(false);
    }

    public boolean hasGrantedByDefaultPermission() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isGrantedByDefault()) {
                return true;
            }
        }
        return false;
    }

    public PackageInfo getApp() {
        return this.mPackageInfo;
    }

    public String getName() {
        return this.mName;
    }

    public String getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public String getIconPkg() {
        return this.mIconPkg;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public CharSequence getFullLabel() {
        return this.mFullLabel;
    }

    public int getRequest() {
        return this.mRequest;
    }

    private static int getRequestDetail(PackageItemInfo packageItemInfo) {
        if (packageItemInfo instanceof PermissionGroupInfo) {
            return ((PermissionGroupInfo) packageItemInfo).requestDetailResourceId;
        }
        return 0;
    }

    public int getRequestDetail() {
        return this.mRequestDetail;
    }

    private static int getBackgroundRequest(PackageItemInfo packageItemInfo) {
        if (packageItemInfo instanceof PermissionGroupInfo) {
            return ((PermissionGroupInfo) packageItemInfo).backgroundRequestResourceId;
        }
        return 0;
    }

    public int getBackgroundRequest() {
        return this.mBackgroundRequest;
    }

    private static int getBackgroundRequestDetail(PackageItemInfo packageItemInfo) {
        if (packageItemInfo instanceof PermissionGroupInfo) {
            return ((PermissionGroupInfo) packageItemInfo).backgroundRequestDetailResourceId;
        }
        return 0;
    }

    public int getBackgroundRequestDetail() {
        return this.mBackgroundRequestDetail;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public UserHandle getUser() {
        return this.mUserHandle;
    }

    public boolean hasPermission(String str) {
        return this.mPermissions.get(str) != null;
    }

    public Permission getPermission(String str) {
        return this.mPermissions.get(str);
    }

    public boolean areRuntimePermissionsGranted() {
        return areRuntimePermissionsGranted(null);
    }

    public boolean areRuntimePermissionsGranted(String[] strArr) {
        if (LocationUtils.isLocationGroupAndProvider(this.mContext, this.mName, this.mPackageInfo.packageName)) {
            return LocationUtils.isLocationEnabled(this.mContext);
        }
        if (LocationUtils.isLocationGroupAndControllerExtraPackage(this.mContext, this.mName, this.mPackageInfo.packageName)) {
            return LocationUtils.isExtraLocationControllerPackageEnabled(this.mContext);
        }
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            Permission valueAt = this.mPermissions.valueAt(i);
            if ((strArr == null || ArrayUtils.contains(strArr, valueAt.getName())) && valueAt.isGrantedIncludingAppOp()) {
                return true;
            }
        }
        return false;
    }

    public boolean grantRuntimePermissions(boolean z) {
        return grantRuntimePermissions(z, null);
    }

    private boolean setAppOpMode(String str, int i, int i2) {
        if (this.mAppOps.unsafeCheckOpRaw(str, i, this.mPackageInfo.packageName) == i2) {
            return false;
        }
        this.mAppOps.setUidMode(str, i, i2);
        return true;
    }

    private boolean allowAppOp(Permission permission, int i) {
        boolean appOpMode;
        if (permission.isBackgroundPermission()) {
            ArrayList<Permission> foregroundPermissions = permission.getForegroundPermissions();
            int size = foregroundPermissions.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Permission permission2 = foregroundPermissions.get(i2);
                if (permission2.isAppOpAllowed()) {
                    z |= setAppOpMode(permission2.getAppOp(), i, 0);
                }
            }
            return z;
        } else if (permission.hasBackgroundPermission()) {
            Permission backgroundPermission = permission.getBackgroundPermission();
            if (backgroundPermission == null) {
                appOpMode = setAppOpMode(permission.getAppOp(), i, 4);
            } else if (backgroundPermission.isAppOpAllowed()) {
                appOpMode = setAppOpMode(permission.getAppOp(), i, 0);
            } else {
                appOpMode = setAppOpMode(permission.getAppOp(), i, 4);
            }
            return appOpMode;
        } else {
            return setAppOpMode(permission.getAppOp(), i, 0);
        }
    }

    private void killApp(String str) {
        this.mActivityManager.killUid(this.mPackageInfo.applicationInfo.uid, str);
    }

    public boolean grantRuntimePermissions(boolean z, String[] strArr) {
        boolean z2;
        ArrayList<Permission> foregroundPermissions;
        Iterator<Permission> it = this.mPermissions.values().iterator();
        boolean z3 = false;
        while (true) {
            z2 = true;
            if (!it.hasNext()) {
                break;
            }
            Permission next = it.next();
            if (strArr == null || ArrayUtils.contains(strArr, next.getName())) {
                if (next.isGrantingAllowed(this.mIsEphemeralApp, this.mAppSupportsRuntimePermissions)) {
                    boolean isGrantedIncludingAppOp = next.isGrantedIncludingAppOp();
                    if (this.mAppSupportsRuntimePermissions) {
                        if (next.isSystemFixed()) {
                            z2 = false;
                            break;
                        }
                        if (next.affectsAppOp() && !next.isAppOpAllowed()) {
                            next.setAppOpAllowed(true);
                        }
                        if (!next.isGranted()) {
                            next.setGranted(true);
                        }
                        if (!z && (next.isUserFixed() || next.isUserSet())) {
                            next.setUserFixed(false);
                            next.setUserSet(false);
                        }
                    } else if (next.isGranted()) {
                        if (next.affectsAppOp()) {
                            if (!next.isAppOpAllowed()) {
                                next.setAppOpAllowed(true);
                                z3 = true;
                            }
                            if (next.shouldRevokeOnUpgrade()) {
                                next.setRevokeOnUpgrade(false);
                            }
                        }
                        if (next.isReviewRequired()) {
                            next.unsetReviewRequired();
                        }
                    }
                    if (!isGrantedIncludingAppOp && next.isGrantedIncludingAppOp()) {
                        if (next.getName().equals("android.permission.ACCESS_FINE_LOCATION")) {
                            Permission backgroundPermission = next.getBackgroundPermission();
                            if (backgroundPermission != null && backgroundPermission.isGrantedIncludingAppOp()) {
                                this.mTriggerLocationAccessCheckOnPersist = true;
                            }
                        } else if (next.getName().equals("android.permission.ACCESS_BACKGROUND_LOCATION") && (foregroundPermissions = next.getForegroundPermissions()) != null) {
                            int size = foregroundPermissions.size();
                            int i = 0;
                            while (true) {
                                if (i < size) {
                                    Permission permission = foregroundPermissions.get(i);
                                    if (!permission.getName().equals("android.permission.ACCESS_FINE_LOCATION")) {
                                        i++;
                                    } else if (permission.isGrantedIncludingAppOp()) {
                                        this.mTriggerLocationAccessCheckOnPersist = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        if (!this.mDelayChanges) {
            persistChanges(false);
            if (z3) {
                killApp("Permission related app op changed");
            }
        }
        return z2;
    }

    public boolean revokeRuntimePermissions(boolean z) {
        return revokeRuntimePermissions(z, null);
    }

    private boolean disallowAppOp(Permission permission, int i) {
        if (permission.isBackgroundPermission()) {
            ArrayList<Permission> foregroundPermissions = permission.getForegroundPermissions();
            int size = foregroundPermissions.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Permission permission2 = foregroundPermissions.get(i2);
                if (permission2.isAppOpAllowed()) {
                    z |= setAppOpMode(permission2.getAppOp(), i, 4);
                }
            }
            return z;
        }
        return setAppOpMode(permission.getAppOp(), i, 1);
    }

    public boolean revokeRuntimePermissions(boolean z, String[] strArr) {
        boolean z2;
        Iterator<Permission> it = this.mPermissions.values().iterator();
        boolean z3 = false;
        while (true) {
            z2 = true;
            if (!it.hasNext()) {
                break;
            }
            Permission next = it.next();
            if (strArr == null || ArrayUtils.contains(strArr, next.getName())) {
                if (next.isSystemFixed()) {
                    z2 = false;
                    break;
                } else if (this.mAppSupportsRuntimePermissions) {
                    if (next.isGranted()) {
                        next.setGranted(false);
                    }
                    if (z) {
                        if (next.isUserSet() || !next.isUserFixed()) {
                            next.setUserSet(false);
                            next.setUserFixed(true);
                        }
                    } else if (!next.isUserSet() || next.isUserFixed()) {
                        next.setUserSet(true);
                        next.setUserFixed(false);
                    }
                    if (next.affectsAppOp()) {
                        next.setAppOpAllowed(false);
                    }
                } else if (next.isGranted() && next.affectsAppOp()) {
                    if (next.isAppOpAllowed()) {
                        next.setAppOpAllowed(false);
                        z3 = true;
                    }
                    if (!next.shouldRevokeOnUpgrade()) {
                        next.setRevokeOnUpgrade(true);
                    }
                }
            }
        }
        if (!this.mDelayChanges) {
            persistChanges(false);
            if (z3) {
                killApp("Permission related app op changed");
            }
        }
        return z2;
    }

    public void setPolicyFixed(String[] strArr) {
        for (String str : strArr) {
            Permission permission = this.mPermissions.get(str);
            if (permission != null) {
                permission.setPolicyFixed(true);
            }
        }
        if (this.mDelayChanges) {
            return;
        }
        persistChanges(false);
    }

    public void setUserFixed(boolean z) {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            this.mPermissions.valueAt(i).setUserFixed(z);
        }
        if (this.mDelayChanges) {
            return;
        }
        persistChanges(false);
    }

    public ArrayList<Permission> getPermissions() {
        return new ArrayList<>(this.mPermissions.values());
    }

    public AppPermissionGroup getBackgroundPermissions() {
        return this.mBackgroundPermissions;
    }

    public boolean hasPermissionWithBackgroundMode() {
        return this.mHasPermissionWithBackgroundMode;
    }

    public boolean isNonIsolatedStorage() {
        return this.mIsNonIsolatedStorage;
    }

    public boolean isBackgroundGroup() {
        return this.mPermissions.valueAt(0).isBackgroundPermission();
    }

    public boolean isUserFixed() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isUserFixed()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPolicyFixed() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isPolicyFixed()) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserSet() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isUserSet()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSystemFixed() {
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.valueAt(i).isSystemFixed()) {
                return true;
            }
        }
        return false;
    }

    @Override // java.lang.Comparable
    public int compareTo(AppPermissionGroup appPermissionGroup) {
        int compare = this.mCollator.compare(this.mLabel.toString(), appPermissionGroup.mLabel.toString());
        return compare == 0 ? this.mPackageInfo.applicationInfo.uid - appPermissionGroup.mPackageInfo.applicationInfo.uid : compare;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AppPermissionGroup)) {
            return false;
        }
        AppPermissionGroup appPermissionGroup = (AppPermissionGroup) obj;
        return this.mName.equals(appPermissionGroup.mName) && this.mPackageInfo.packageName.equals(appPermissionGroup.mPackageInfo.packageName) && this.mUserHandle.equals(appPermissionGroup.mUserHandle);
    }

    public int hashCode() {
        return this.mName.hashCode() + this.mPackageInfo.packageName.hashCode() + this.mUserHandle.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPermissionGroup.class.getSimpleName());
        sb.append("{name=");
        sb.append(this.mName);
        if (this.mBackgroundPermissions != null) {
            sb.append(", <has background permissions>}");
        }
        if (!this.mPermissions.isEmpty()) {
            sb.append(", <has permissions>}");
        } else {
            sb.append('}');
        }
        return sb.toString();
    }

    private void addPermission(Permission permission) {
        this.mPermissions.put(permission.getName(), permission);
        if (permission.isEphemeral()) {
            this.mContainsEphemeralPermission = true;
        }
        if (permission.isRuntimeOnly()) {
            return;
        }
        this.mContainsPreRuntimePermission = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void persistChanges(boolean z) {
        boolean disallowAppOp;
        int i = this.mPackageInfo.applicationInfo.uid;
        int size = this.mPermissions.size();
        boolean z2 = false;
        for (int i2 = 0; i2 < size; i2++) {
            Permission valueAt = this.mPermissions.valueAt(i2);
            if (!valueAt.isSystemFixed()) {
                if (valueAt.isGranted()) {
                    this.mPackageManager.grantRuntimePermission(this.mPackageInfo.packageName, valueAt.getName(), this.mUserHandle);
                } else if (this.mContext.checkPermission(valueAt.getName(), -1, i) == 0) {
                    this.mPackageManager.revokeRuntimePermission(this.mPackageInfo.packageName, valueAt.getName(), this.mUserHandle);
                }
            }
            this.mPackageManager.updatePermissionFlags(valueAt.getName(), this.mPackageInfo.packageName, 79, valueAt.isUserSet() | (valueAt.isUserFixed() ? 2 : 0) | (valueAt.shouldRevokeOnUpgrade() ? 8 : 0) | (valueAt.isPolicyFixed() ? 4 : 0) | (valueAt.isReviewRequired() ? 64 : 0), this.mUserHandle);
            if (valueAt.affectsAppOp() && !valueAt.isSystemFixed()) {
                if (valueAt.isAppOpAllowed()) {
                    disallowAppOp = allowAppOp(valueAt, i);
                } else {
                    disallowAppOp = disallowAppOp(valueAt, i);
                }
                z2 |= disallowAppOp;
            }
        }
        if (z && z2) {
            killApp("Permission related app op changed");
        }
        if (this.mTriggerLocationAccessCheckOnPersist) {
            new LocationAccessCheck(this.mContext, null).checkLocationAccessSoon();
            this.mTriggerLocationAccessCheckOnPersist = false;
        }
    }

    public boolean hasInstallToRuntimeSplit() {
        PermissionManager permissionManager = (PermissionManager) this.mContext.getSystemService(PermissionManager.class);
        int size = permissionManager.getSplitPermissions().size();
        for (int i = 0; i < size; i++) {
            PermissionManager.SplitPermissionInfo splitPermissionInfo = (PermissionManager.SplitPermissionInfo) permissionManager.getSplitPermissions().get(i);
            String splitPermission = splitPermissionInfo.getSplitPermission();
            try {
                if (this.mPackageManager.getPermissionInfo(splitPermission, 0).getProtection() != 0) {
                    continue;
                } else {
                    List newPermissions = splitPermissionInfo.getNewPermissions();
                    int size2 = newPermissions.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        String str = (String) newPermissions.get(i2);
                        if (hasPermission(str)) {
                            try {
                                if (this.mPackageManager.getPermissionInfo(str, 0).getProtection() == 1 && this.mPackageInfo.applicationInfo.targetSdkVersion < splitPermissionInfo.getTargetSdk()) {
                                    return true;
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.w(LOG_TAG, "No such permission: " + str, e);
                            }
                        }
                    }
                    continue;
                }
            } catch (PackageManager.NameNotFoundException e2) {
                Log.w(LOG_TAG, "No such permission: " + splitPermission, e2);
            }
        }
        return false;
    }
}
