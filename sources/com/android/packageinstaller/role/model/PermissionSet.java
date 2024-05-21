package com.android.packageinstaller.role.model;

import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class PermissionSet {
    private final String mName;
    private final List<String> mPermissions;

    public PermissionSet(String str, List<String> list) {
        this.mName = str;
        this.mPermissions = list;
    }

    public String getName() {
        return this.mName;
    }

    public List<String> getPermissions() {
        return this.mPermissions;
    }

    public String toString() {
        return "PermissionSet{mName='" + this.mName + "', mPermissions=" + this.mPermissions + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || PermissionSet.class != obj.getClass()) {
            return false;
        }
        PermissionSet permissionSet = (PermissionSet) obj;
        return Objects.equals(this.mName, permissionSet.mName) && Objects.equals(this.mPermissions, permissionSet.mPermissions);
    }

    public int hashCode() {
        return Objects.hash(this.mName, this.mPermissions);
    }
}
