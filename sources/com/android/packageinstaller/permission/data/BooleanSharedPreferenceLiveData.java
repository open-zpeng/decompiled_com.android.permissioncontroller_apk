package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import androidx.lifecycle.LiveData;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public class BooleanSharedPreferenceLiveData extends LiveData<Boolean> implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static ArrayMap<String, BooleanSharedPreferenceLiveData> sInstances = new ArrayMap<>();
    private final String mKey;
    private final SharedPreferences mPrefs;

    public static BooleanSharedPreferenceLiveData get(String str, Application application) {
        if (sInstances.get(str) == null) {
            sInstances.put(str, new BooleanSharedPreferenceLiveData(str, application));
        }
        return sInstances.get(str);
    }

    private BooleanSharedPreferenceLiveData(String str, Application application) {
        this.mPrefs = Utils.getParentUserContext(application).getSharedPreferences("preferences", 0);
        this.mKey = str;
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        onSharedPreferenceChanged(this.mPrefs, this.mKey);
        this.mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        this.mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (this.mKey.equals(str)) {
            setValue(Boolean.valueOf(sharedPreferences.getBoolean(this.mKey, false)));
        }
    }
}
