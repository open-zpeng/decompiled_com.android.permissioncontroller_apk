package com.android.packageinstaller.permission.model;

import android.graphics.drawable.Drawable;
/* loaded from: classes.dex */
public final class PermissionGroup implements Comparable<PermissionGroup> {
    private final String mDeclaringPackage;
    private final int mGranted;
    private final Drawable mIcon;
    private final CharSequence mLabel;
    private final String mName;
    private final PermissionApps mPermApps;
    private final int mTotal;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PermissionGroup(String str, String str2, CharSequence charSequence, Drawable drawable, int i, int i2, PermissionApps permissionApps) {
        this.mDeclaringPackage = str2;
        this.mName = str;
        this.mLabel = charSequence;
        this.mIcon = drawable;
        this.mTotal = i;
        this.mGranted = i2;
        this.mPermApps = permissionApps;
    }

    public String getName() {
        return this.mName;
    }

    public String getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public int getTotal() {
        return this.mTotal;
    }

    public int getGranted() {
        return this.mGranted;
    }

    @Override // java.lang.Comparable
    public int compareTo(PermissionGroup permissionGroup) {
        return this.mLabel.toString().compareTo(permissionGroup.mLabel.toString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && PermissionGroup.class == obj.getClass()) {
            PermissionGroup permissionGroup = (PermissionGroup) obj;
            String str = this.mName;
            if (str == null) {
                if (permissionGroup.mName != null) {
                    return false;
                }
            } else if (!str.equals(permissionGroup.mName)) {
                return false;
            }
            return this.mTotal == permissionGroup.mTotal && this.mGranted == permissionGroup.mGranted;
        }
        return false;
    }

    public int hashCode() {
        String str = this.mName;
        return (str != null ? str.hashCode() + this.mTotal : this.mTotal) + this.mGranted;
    }
}
