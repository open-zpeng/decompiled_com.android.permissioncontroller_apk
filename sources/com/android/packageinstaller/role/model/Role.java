package com.android.packageinstaller.role.model;

import android.app.ActivityManager;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import androidx.preference.Preference;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
import com.android.packageinstaller.role.utils.PackageUtils;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class Role {
    private static final String LOG_TAG = "Role";
    private final List<AppOp> mAppOps;
    private final RoleBehavior mBehavior;
    private final int mDescriptionResource;
    private final boolean mExclusive;
    private final int mLabelResource;
    private final String mName;
    private final List<String> mPermissions;
    private final List<PreferredActivity> mPreferredActivities;
    private final int mRequestDescriptionResource;
    private final int mRequestTitleResource;
    private final boolean mRequestable;
    private final List<RequiredComponent> mRequiredComponents;
    private final int mShortLabelResource;
    private final boolean mShowNone;
    private final boolean mSystemOnly;

    public Role(String str, RoleBehavior roleBehavior, int i, boolean z, int i2, int i3, int i4, boolean z2, int i5, boolean z3, boolean z4, List<RequiredComponent> list, List<String> list2, List<AppOp> list3, List<PreferredActivity> list4) {
        this.mName = str;
        this.mBehavior = roleBehavior;
        this.mDescriptionResource = i;
        this.mExclusive = z;
        this.mLabelResource = i2;
        this.mRequestDescriptionResource = i3;
        this.mRequestTitleResource = i4;
        this.mRequestable = z2;
        this.mShortLabelResource = i5;
        this.mShowNone = z3;
        this.mSystemOnly = z4;
        this.mRequiredComponents = list;
        this.mPermissions = list2;
        this.mAppOps = list3;
        this.mPreferredActivities = list4;
    }

    public String getName() {
        return this.mName;
    }

    public int getDescriptionResource() {
        return this.mDescriptionResource;
    }

    public boolean isExclusive() {
        return this.mExclusive;
    }

    public int getLabelResource() {
        return this.mLabelResource;
    }

    public int getRequestDescriptionResource() {
        return this.mRequestDescriptionResource;
    }

    public int getRequestTitleResource() {
        return this.mRequestTitleResource;
    }

    public boolean isRequestable() {
        return this.mRequestable;
    }

    public int getShortLabelResource() {
        return this.mShortLabelResource;
    }

    public boolean shouldShowNone() {
        return this.mShowNone;
    }

    public List<RequiredComponent> getRequiredComponents() {
        return this.mRequiredComponents;
    }

    public List<String> getPermissions() {
        return this.mPermissions;
    }

    public List<AppOp> getAppOps() {
        return this.mAppOps;
    }

    public void onRoleAdded(Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.onRoleAdded(this, context);
        }
    }

    public boolean isAvailableAsUser(UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.isAvailableAsUser(this, userHandle, context);
        }
        return true;
    }

    public boolean isAvailable(Context context) {
        return isAvailableAsUser(Process.myUserHandle(), context);
    }

    public List<String> getDefaultHolders(Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.getDefaultHolders(this, context);
        }
        return Collections.emptyList();
    }

    public String getFallbackHolder(Context context) {
        if (this.mBehavior == null || isNoneHolderSelected(context)) {
            return null;
        }
        return this.mBehavior.getFallbackHolder(this, context);
    }

    public boolean isVisibleAsUser(UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.isVisibleAsUser(this, userHandle, context);
        }
        return true;
    }

    public boolean isVisible(Context context) {
        return isVisibleAsUser(Process.myUserHandle(), context);
    }

    public Intent getManageIntentAsUser(UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.getManageIntentAsUser(this, userHandle, context);
        }
        return null;
    }

    public void preparePreferenceAsUser(TwoTargetPreference twoTargetPreference, UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.preparePreferenceAsUser(this, twoTargetPreference, userHandle, context);
        }
    }

    public boolean isApplicationVisibleAsUser(ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.isApplicationVisibleAsUser(this, applicationInfo, userHandle, context);
        }
        return true;
    }

    public void prepareApplicationPreferenceAsUser(Preference preference, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.prepareApplicationPreferenceAsUser(this, preference, applicationInfo, userHandle, context);
        }
    }

    public CharSequence getConfirmationMessage(String str, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            return roleBehavior.getConfirmationMessage(this, str, context);
        }
        return null;
    }

    public boolean isPackageQualified(String str, Context context) {
        Boolean isPackageQualified;
        if (isPackageMinimallyQualifiedAsUser(str, Process.myUserHandle(), context)) {
            RoleBehavior roleBehavior = this.mBehavior;
            if (roleBehavior != null && (isPackageQualified = roleBehavior.isPackageQualified(this, str, context)) != null) {
                return isPackageQualified.booleanValue();
            }
            int size = this.mRequiredComponents.size();
            for (int i = 0; i < size; i++) {
                RequiredComponent requiredComponent = this.mRequiredComponents.get(i);
                if (requiredComponent.getQualifyingComponentForPackage(str, context) == null) {
                    Log.w(LOG_TAG, str + " not qualified for " + this.mName + " due to missing " + requiredComponent);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public List<String> getQualifyingPackagesAsUser(UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        ArrayList qualifyingPackagesAsUser = roleBehavior != null ? roleBehavior.getQualifyingPackagesAsUser(this, userHandle, context) : null;
        int i = 0;
        if (qualifyingPackagesAsUser == null) {
            ArrayMap arrayMap = new ArrayMap();
            int size = this.mRequiredComponents.size();
            for (int i2 = 0; i2 < size; i2++) {
                List<ComponentName> qualifyingComponentsAsUser = this.mRequiredComponents.get(i2).getQualifyingComponentsAsUser(userHandle, context);
                int size2 = qualifyingComponentsAsUser.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    String packageName = qualifyingComponentsAsUser.get(i3).getPackageName();
                    Integer num = (Integer) arrayMap.get(packageName);
                    int i4 = 1;
                    if (num != null) {
                        i4 = 1 + num.intValue();
                    }
                    arrayMap.put(packageName, Integer.valueOf(i4));
                }
            }
            ArrayList arrayList = new ArrayList();
            int size3 = arrayMap.size();
            for (int i5 = 0; i5 < size3; i5++) {
                if (((Integer) arrayMap.valueAt(i5)).intValue() == size) {
                    arrayList.add((String) arrayMap.keyAt(i5));
                }
            }
            qualifyingPackagesAsUser = arrayList;
        }
        int size4 = qualifyingPackagesAsUser.size();
        while (i < size4) {
            if (isPackageMinimallyQualifiedAsUser(qualifyingPackagesAsUser.get(i), userHandle, context)) {
                i++;
            } else {
                qualifyingPackagesAsUser.remove(i);
                size4--;
            }
        }
        return qualifyingPackagesAsUser;
    }

    private boolean isPackageMinimallyQualifiedAsUser(String str, UserHandle userHandle, Context context) {
        if (Objects.equals(str, "android")) {
            return false;
        }
        ApplicationInfo applicationInfoAsUser = PackageUtils.getApplicationInfoAsUser(str, userHandle, context);
        if (applicationInfoAsUser != null) {
            return !(this.mSystemOnly && (applicationInfoAsUser.flags & 1) == 0) && applicationInfoAsUser.enabled && !applicationInfoAsUser.isInstantApp() && UserUtils.getUserContext(context, userHandle).getPackageManager().getDeclaredSharedLibraries(str, 0).isEmpty();
        }
        String str2 = LOG_TAG;
        Log.w(str2, "Cannot get ApplicationInfo for package: " + str + ", user: " + userHandle.getIdentifier());
        return false;
    }

    public void grant(String str, boolean z, boolean z2, Context context) {
        boolean grant = Permissions.grant(str, this.mPermissions, true, z2, true, false, false, context);
        int size = this.mAppOps.size();
        for (int i = 0; i < size; i++) {
            this.mAppOps.get(i).grant(str, context);
        }
        int size2 = this.mPreferredActivities.size();
        for (int i2 = 0; i2 < size2; i2++) {
            this.mPreferredActivities.get(i2).configure(str, context);
        }
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.grant(this, str, context);
        }
        if (z || !grant || Permissions.isRuntimePermissionsSupported(str, context)) {
            return;
        }
        killApp(str, context);
    }

    public void revoke(String str, boolean z, boolean z2, Context context) {
        List heldRolesFromController = ((RoleManager) context.getSystemService(RoleManager.class)).getHeldRolesFromController(str);
        heldRolesFromController.remove(this.mName);
        ArrayList arrayList = new ArrayList(this.mPermissions);
        ArrayMap<String, Role> arrayMap = Roles.get(context);
        int size = heldRolesFromController.size();
        for (int i = 0; i < size; i++) {
            arrayList.removeAll(arrayMap.get((String) heldRolesFromController.get(i)).getPermissions());
        }
        boolean revoke = Permissions.revoke(str, arrayList, true, false, z2, context);
        ArrayList arrayList2 = new ArrayList(this.mAppOps);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList2.removeAll(arrayMap.get((String) heldRolesFromController.get(i2)).getAppOps());
        }
        int size2 = arrayList2.size();
        for (int i3 = 0; i3 < size2; i3++) {
            ((AppOp) arrayList2.get(i3)).revoke(str, context);
        }
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.revoke(this, str, context);
        }
        if (z || !revoke) {
            return;
        }
        killApp(str, context);
    }

    private void killApp(String str, Context context) {
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
        if (applicationInfo == null) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot get ApplicationInfo for package: " + str);
            return;
        }
        ((ActivityManager) context.getSystemService(ActivityManager.class)).killUid(applicationInfo.uid, "Permission or app op changed");
    }

    private boolean isNoneHolderSelected(Context context) {
        SharedPreferences deviceProtectedSharedPreferences = Utils.getDeviceProtectedSharedPreferences(context);
        return deviceProtectedSharedPreferences.getBoolean("is_none_role_holder_selected:" + this.mName, false);
    }

    public void onHolderAddedAsUser(String str, UserHandle userHandle, Context context) {
        SharedPreferences.Editor edit = Utils.getDeviceProtectedSharedPreferences(UserUtils.getUserContext(context, userHandle)).edit();
        edit.remove("is_none_role_holder_selected:" + this.mName).apply();
    }

    public void onHolderSelectedAsUser(String str, UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.onHolderSelectedAsUser(this, str, userHandle, context);
        }
    }

    public void onHolderChangedAsUser(UserHandle userHandle, Context context) {
        RoleBehavior roleBehavior = this.mBehavior;
        if (roleBehavior != null) {
            roleBehavior.onHolderChangedAsUser(this, userHandle, context);
        }
    }

    public void onNoneHolderSelectedAsUser(UserHandle userHandle, Context context) {
        SharedPreferences.Editor edit = Utils.getDeviceProtectedSharedPreferences(UserUtils.getUserContext(context, userHandle)).edit();
        edit.putBoolean("is_none_role_holder_selected:" + this.mName, true).apply();
    }

    public String toString() {
        return "Role{mName='" + this.mName + "', mBehavior=" + this.mBehavior + ", mExclusive=" + this.mExclusive + ", mLabelResource=" + this.mLabelResource + ", mShowNone=" + this.mShowNone + ", mSystemOnly=" + this.mSystemOnly + ", mRequiredComponents=" + this.mRequiredComponents + ", mPermissions=" + this.mPermissions + ", mAppOps=" + this.mAppOps + ", mPreferredActivities=" + this.mPreferredActivities + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || Role.class != obj.getClass()) {
            return false;
        }
        Role role = (Role) obj;
        return this.mExclusive == role.mExclusive && this.mLabelResource == role.mLabelResource && this.mShowNone == role.mShowNone && this.mSystemOnly == role.mSystemOnly && this.mName.equals(role.mName) && Objects.equals(this.mBehavior, role.mBehavior) && this.mRequiredComponents.equals(role.mRequiredComponents) && this.mPermissions.equals(role.mPermissions) && this.mAppOps.equals(role.mAppOps) && this.mPreferredActivities.equals(role.mPreferredActivities);
    }

    public int hashCode() {
        return Objects.hash(this.mName, this.mBehavior, Boolean.valueOf(this.mExclusive), Integer.valueOf(this.mLabelResource), Boolean.valueOf(this.mShowNone), Boolean.valueOf(this.mSystemOnly), this.mRequiredComponents, this.mPermissions, this.mAppOps, this.mPreferredActivities);
    }
}
