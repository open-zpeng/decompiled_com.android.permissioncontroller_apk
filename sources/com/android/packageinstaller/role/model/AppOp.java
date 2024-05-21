package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.Objects;
/* loaded from: classes.dex */
public class AppOp {
    private final Integer mMaxTargetSdkVersion;
    private final int mMode;
    private final String mName;

    public AppOp(String str, Integer num, int i) {
        this.mName = str;
        this.mMaxTargetSdkVersion = num;
        this.mMode = i;
    }

    public boolean grant(String str, Context context) {
        if (checkTargetSdkVersion(str, context)) {
            return Permissions.setAppOpMode(str, this.mName, this.mMode, context);
        }
        return false;
    }

    public boolean revoke(String str, Context context) {
        if (checkTargetSdkVersion(str, context)) {
            return Permissions.setAppOpMode(str, this.mName, Permissions.getDefaultAppOpMode(this.mName), context);
        }
        return false;
    }

    private boolean checkTargetSdkVersion(String str, Context context) {
        if (this.mMaxTargetSdkVersion == null) {
            return true;
        }
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
        return applicationInfo != null && applicationInfo.targetSdkVersion <= this.mMaxTargetSdkVersion.intValue();
    }

    public String toString() {
        return "AppOp{mName='" + this.mName + "', mMaxTargetSdkVersion=" + this.mMaxTargetSdkVersion + ", mMode=" + this.mMode + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || AppOp.class != obj.getClass()) {
            return false;
        }
        AppOp appOp = (AppOp) obj;
        return this.mMode == appOp.mMode && Objects.equals(this.mName, appOp.mName) && Objects.equals(this.mMaxTargetSdkVersion, appOp.mMaxTargetSdkVersion);
    }

    public int hashCode() {
        return Objects.hash(this.mName, this.mMaxTargetSdkVersion, Integer.valueOf(this.mMode));
    }
}
