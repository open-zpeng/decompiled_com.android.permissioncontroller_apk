package com.android.packageinstaller.role.model;

import android.os.Bundle;
import java.util.Objects;
/* loaded from: classes.dex */
public class RequiredMetaData {
    private final String mName;
    private final boolean mOptional;
    private final Object mValue;

    public RequiredMetaData(String str, Object obj, boolean z) {
        this.mName = str;
        this.mValue = obj;
        this.mOptional = z;
    }

    public boolean isQualified(Bundle bundle) {
        if (bundle.containsKey(this.mName)) {
            return Objects.equals(bundle.get(this.mName), this.mValue);
        }
        return this.mOptional;
    }

    public String toString() {
        return "RequiredMetaData{mName='" + this.mName + "', mValue=" + this.mValue + ", mOptional=" + this.mOptional + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || RequiredMetaData.class != obj.getClass()) {
            return false;
        }
        RequiredMetaData requiredMetaData = (RequiredMetaData) obj;
        return this.mOptional == requiredMetaData.mOptional && Objects.equals(this.mName, requiredMetaData.mName) && Objects.equals(this.mValue, requiredMetaData.mValue);
    }

    public int hashCode() {
        return Objects.hash(this.mName, this.mValue, Boolean.valueOf(this.mOptional));
    }
}
