package com.android.packageinstaller.role.model;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class IntentFilterData {
    private final String mAction;
    private final List<String> mCategories;
    private final String mDataScheme;
    private final String mDataType;

    public IntentFilterData(String str, List<String> list, String str2, String str3) {
        this.mAction = str;
        this.mCategories = list;
        this.mDataScheme = str2;
        this.mDataType = str3;
    }

    public String getDataScheme() {
        return this.mDataScheme;
    }

    public String getDataType() {
        return this.mDataType;
    }

    public Intent createIntent() {
        Intent intent = new Intent(this.mAction);
        String str = this.mDataScheme;
        Uri fromParts = str != null ? Uri.fromParts(str, "", null) : null;
        int size = this.mCategories.size();
        for (int i = 0; i < size; i++) {
            intent.addCategory(this.mCategories.get(i));
        }
        intent.setDataAndType(fromParts, this.mDataType);
        return intent;
    }

    public IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter(this.mAction);
        int size = this.mCategories.size();
        for (int i = 0; i < size; i++) {
            intentFilter.addCategory(this.mCategories.get(i));
        }
        String str = this.mDataScheme;
        if (str != null) {
            intentFilter.addDataScheme(str);
        }
        String str2 = this.mDataType;
        if (str2 != null) {
            try {
                intentFilter.addDataType(str2);
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new IllegalStateException(e);
            }
        }
        return intentFilter;
    }

    public String toString() {
        return "IntentFilterData{mAction='" + this.mAction + "', mCategories='" + this.mCategories + "', mDataScheme='" + this.mDataScheme + "', mDataType='" + this.mDataType + "'}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || IntentFilterData.class != obj.getClass()) {
            return false;
        }
        IntentFilterData intentFilterData = (IntentFilterData) obj;
        return Objects.equals(this.mAction, intentFilterData.mAction) && Objects.equals(this.mCategories, intentFilterData.mCategories) && Objects.equals(this.mDataScheme, intentFilterData.mDataScheme) && Objects.equals(this.mDataType, intentFilterData.mDataType);
    }

    public int hashCode() {
        return Objects.hash(this.mAction, this.mCategories, this.mDataScheme, this.mDataType);
    }
}
