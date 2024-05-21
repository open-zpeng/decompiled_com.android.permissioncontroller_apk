package com.android.packageinstaller.permission.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class AppPermissions {
    private final CharSequence mAppLabel;
    private final Context mContext;
    private final boolean mDelayChanges;
    private final ArrayMap<String, AppPermissionGroup> mGroupNameToGroup;
    private final ArrayList<AppPermissionGroup> mGroups;
    private final Runnable mOnErrorCallback;
    private PackageInfo mPackageInfo;
    private final ArrayMap<String, AppPermissionGroup> mPermissionNameToGroup;
    private final boolean mSortGroups;

    public AppPermissions(Context context, PackageInfo packageInfo, boolean z, Runnable runnable) {
        this(context, packageInfo, z, false, runnable);
    }

    public AppPermissions(Context context, PackageInfo packageInfo, boolean z, boolean z2, Runnable runnable) {
        this.mGroups = new ArrayList<>();
        this.mGroupNameToGroup = new ArrayMap<>();
        this.mPermissionNameToGroup = new ArrayMap<>();
        this.mContext = context;
        this.mPackageInfo = packageInfo;
        this.mAppLabel = Utils.getAppLabel(packageInfo.applicationInfo, context);
        this.mSortGroups = z;
        this.mDelayChanges = z2;
        this.mOnErrorCallback = runnable;
        loadPermissionGroups();
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    public void refresh() {
        loadPackageInfo();
        loadPermissionGroups();
    }

    public CharSequence getAppLabel() {
        return this.mAppLabel;
    }

    public AppPermissionGroup getPermissionGroup(String str) {
        return this.mGroupNameToGroup.get(str);
    }

    public List<AppPermissionGroup> getPermissionGroups() {
        return this.mGroups;
    }

    public boolean isReviewRequired() {
        int size = this.mGroups.size();
        for (int i = 0; i < size; i++) {
            if (this.mGroups.get(i).isReviewRequired()) {
                return true;
            }
        }
        return false;
    }

    private void loadPackageInfo() {
        try {
            this.mPackageInfo = this.mContext.createPackageContextAsUser(this.mPackageInfo.packageName, 0, UserHandle.getUserHandleForUid(this.mPackageInfo.applicationInfo.uid)).getPackageManager().getPackageInfo(this.mPackageInfo.packageName, 4096);
        } catch (PackageManager.NameNotFoundException unused) {
            Runnable runnable = this.mOnErrorCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private void addAllPermissions(AppPermissionGroup appPermissionGroup) {
        ArrayList<Permission> permissions = appPermissionGroup.getPermissions();
        int size = permissions.size();
        for (int i = 0; i < size; i++) {
            this.mPermissionNameToGroup.put(permissions.get(i).getName(), appPermissionGroup);
        }
    }

    private void loadPermissionGroups() {
        AppPermissionGroup create;
        this.mGroups.clear();
        this.mGroupNameToGroup.clear();
        this.mPermissionNameToGroup.clear();
        String[] strArr = this.mPackageInfo.requestedPermissions;
        if (strArr != null) {
            for (String str : strArr) {
                if (getGroupForPermission(str) == null && (create = AppPermissionGroup.create(this.mContext, this.mPackageInfo, str, this.mDelayChanges)) != null) {
                    this.mGroups.add(create);
                    this.mGroupNameToGroup.put(create.getName(), create);
                    addAllPermissions(create);
                    AppPermissionGroup backgroundPermissions = create.getBackgroundPermissions();
                    if (backgroundPermissions != null) {
                        addAllPermissions(backgroundPermissions);
                    }
                }
            }
            if (this.mSortGroups) {
                Collections.sort(this.mGroups);
            }
        }
    }

    public AppPermissionGroup getGroupForPermission(String str) {
        return this.mPermissionNameToGroup.get(str);
    }

    public void persistChanges(boolean z) {
        if (this.mDelayChanges) {
            int size = this.mGroups.size();
            for (int i = 0; i < size; i++) {
                AppPermissionGroup appPermissionGroup = this.mGroups.get(i);
                appPermissionGroup.persistChanges(z);
                AppPermissionGroup backgroundPermissions = appPermissionGroup.getBackgroundPermissions();
                if (backgroundPermissions != null) {
                    backgroundPermissions.persistChanges(z);
                }
            }
        }
    }
}
