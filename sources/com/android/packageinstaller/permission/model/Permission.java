package com.android.packageinstaller.permission.model;

import android.content.pm.PermissionInfo;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class Permission {
    private final String mAppOp;
    private boolean mAppOpAllowed;
    private Permission mBackgroundPermission;
    private final String mBackgroundPermissionName;
    private int mFlags;
    private ArrayList<Permission> mForegroundPermissions;
    private boolean mGranted;
    private boolean mIsEphemeral;
    private boolean mIsRuntimeOnly;
    private final String mName;
    private final PermissionInfo mPermissionInfo;

    public Permission(String str, PermissionInfo permissionInfo, boolean z, String str2, boolean z2, int i) {
        this.mPermissionInfo = permissionInfo;
        this.mName = str;
        this.mBackgroundPermissionName = permissionInfo.backgroundPermission;
        this.mGranted = z;
        this.mAppOp = str2;
        this.mAppOpAllowed = z2;
        this.mFlags = i;
        this.mIsEphemeral = (permissionInfo.protectionLevel & 4096) != 0;
        this.mIsRuntimeOnly = (permissionInfo.protectionLevel & 8192) != 0;
    }

    public void addForegroundPermissions(Permission permission) {
        if (this.mForegroundPermissions == null) {
            this.mForegroundPermissions = new ArrayList<>(1);
        }
        this.mForegroundPermissions.add(permission);
    }

    public void setBackgroundPermission(Permission permission) {
        this.mBackgroundPermission = permission;
    }

    public String getName() {
        return this.mName;
    }

    public String getAppOp() {
        return this.mAppOp;
    }

    public int getFlags() {
        return this.mFlags;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHardRestricted() {
        return (this.mPermissionInfo.flags & 4) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSoftRestricted() {
        return (this.mPermissionInfo.flags & 8) != 0;
    }

    public boolean affectsAppOp() {
        return this.mAppOp != null || isBackgroundPermission();
    }

    public boolean isGranted() {
        return this.mGranted;
    }

    public boolean isGrantedIncludingAppOp() {
        return this.mGranted && (!affectsAppOp() || isAppOpAllowed()) && !isReviewRequired();
    }

    public boolean isReviewRequired() {
        return (this.mFlags & 64) != 0;
    }

    public void unsetReviewRequired() {
        this.mFlags &= -65;
    }

    public void setGranted(boolean z) {
        this.mGranted = z;
    }

    public boolean isAppOpAllowed() {
        return this.mAppOpAllowed;
    }

    public boolean isUserFixed() {
        return (this.mFlags & 2) != 0;
    }

    public void setUserFixed(boolean z) {
        if (z) {
            this.mFlags |= 2;
        } else {
            this.mFlags &= -3;
        }
    }

    public boolean isSystemFixed() {
        return (this.mFlags & 16) != 0;
    }

    public boolean isPolicyFixed() {
        return (this.mFlags & 4) != 0;
    }

    public boolean isUserSet() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isGrantedByDefault() {
        return (this.mFlags & 32) != 0;
    }

    public boolean isUserSensitive() {
        return isGrantedIncludingAppOp() ? (this.mFlags & 256) != 0 : (this.mFlags & 512) != 0;
    }

    public String getBackgroundPermissionName() {
        return this.mBackgroundPermissionName;
    }

    public Permission getBackgroundPermission() {
        return this.mBackgroundPermission;
    }

    public ArrayList<Permission> getForegroundPermissions() {
        return this.mForegroundPermissions;
    }

    public boolean hasBackgroundPermission() {
        return this.mBackgroundPermissionName != null;
    }

    public boolean isBackgroundPermission() {
        return this.mForegroundPermissions != null;
    }

    public void setUserSet(boolean z) {
        if (z) {
            this.mFlags |= 1;
        } else {
            this.mFlags &= -2;
        }
    }

    public void setPolicyFixed(boolean z) {
        if (z) {
            this.mFlags |= 4;
        } else {
            this.mFlags &= -5;
        }
    }

    public boolean shouldRevokeOnUpgrade() {
        return (this.mFlags & 8) != 0;
    }

    public void setRevokeOnUpgrade(boolean z) {
        if (z) {
            this.mFlags |= 8;
        } else {
            this.mFlags &= -9;
        }
    }

    public void setAppOpAllowed(boolean z) {
        this.mAppOpAllowed = z;
    }

    public boolean isEphemeral() {
        return this.mIsEphemeral;
    }

    public boolean isRuntimeOnly() {
        return this.mIsRuntimeOnly;
    }

    public boolean isGrantingAllowed(boolean z, boolean z2) {
        return (!z || isEphemeral()) && (z2 || !isRuntimeOnly());
    }
}
