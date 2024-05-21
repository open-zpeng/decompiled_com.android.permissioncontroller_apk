package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.SparseIntArray;
import androidx.lifecycle.LiveData;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.Set;
/* loaded from: classes.dex */
public class ForcedUserSensitiveUidsLiveData extends LiveData<SparseIntArray> implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static ForcedUserSensitiveUidsLiveData sInstance;
    private final SharedPreferences mPrefs;

    public static ForcedUserSensitiveUidsLiveData get(Application application) {
        if (sInstance == null) {
            sInstance = new ForcedUserSensitiveUidsLiveData(application);
        }
        return sInstance;
    }

    private ForcedUserSensitiveUidsLiveData(Application application) {
        this.mPrefs = Utils.getParentUserContext(application).getSharedPreferences("preferences", 0);
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        onSharedPreferenceChanged(this.mPrefs, "forced_user_sensitive_uids_key");
        this.mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        this.mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals("forced_user_sensitive_uids_key")) {
            Set<String> stringSet = sharedPreferences.getStringSet("forced_user_sensitive_uids_key", null);
            if (stringSet == null) {
                setValue(new SparseIntArray(0));
                return;
            }
            SparseIntArray sparseIntArray = new SparseIntArray(stringSet.size());
            for (String str2 : stringSet) {
                sparseIntArray.put(Integer.valueOf(str2).intValue(), 0);
            }
            setValue(sparseIntArray);
        }
    }
}
