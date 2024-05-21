package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.SparseArray;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class UidToSensitivityLiveData extends MediatorLiveData<SparseArray<ArrayMap<String, Integer>>> {
    private static UidToSensitivityLiveData sInstance;
    private final ArrayMap<UserHandle, PerUserUidToSensitivityLiveData> mUsersToLiveData = new ArrayMap<>();
    private final SparseArray<ArrayMap<String, Integer>> mUidToSensitivity = new SparseArray<>();

    public static UidToSensitivityLiveData get(Application application) {
        if (sInstance == null) {
            sInstance = new UidToSensitivityLiveData(application);
        }
        return sInstance;
    }

    private UidToSensitivityLiveData(final Application application) {
        addSource(UsersLiveData.get(application), new Observer() { // from class: com.android.packageinstaller.permission.data.-$$Lambda$UidToSensitivityLiveData$PBgHlCrugjU9jIKsDiH3Khe3RsQ
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                UidToSensitivityLiveData.this.lambda$new$1$UidToSensitivityLiveData(application, (List) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$1$UidToSensitivityLiveData(Application application, List list) {
        for (int size = this.mUsersToLiveData.size() - 1; size >= 0; size--) {
            if (!list.contains(this.mUsersToLiveData.keyAt(size))) {
                removeSource(this.mUsersToLiveData.valueAt(size));
                this.mUsersToLiveData.removeAt(size);
            }
        }
        int size2 = list.size();
        for (int i = 0; i < size2; i++) {
            final UserHandle userHandle = (UserHandle) list.get(i);
            if (!this.mUsersToLiveData.containsKey(userHandle)) {
                PerUserUidToSensitivityLiveData perUserUidToSensitivityLiveData = PerUserUidToSensitivityLiveData.get(userHandle, application);
                this.mUsersToLiveData.put(userHandle, perUserUidToSensitivityLiveData);
                addSource(perUserUidToSensitivityLiveData, new Observer() { // from class: com.android.packageinstaller.permission.data.-$$Lambda$UidToSensitivityLiveData$xMLykWZbJioubGkre6UYfo5rMhc
                    @Override // androidx.lifecycle.Observer
                    public final void onChanged(Object obj) {
                        UidToSensitivityLiveData.this.lambda$new$0$UidToSensitivityLiveData(userHandle, (SparseArray) obj);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$new$0$UidToSensitivityLiveData(UserHandle userHandle, SparseArray sparseArray) {
        for (int size = this.mUidToSensitivity.size() - 1; size >= 0; size--) {
            if (UserHandle.getUserHandleForUid(this.mUidToSensitivity.keyAt(size)).equals(userHandle)) {
                this.mUidToSensitivity.removeAt(size);
            }
        }
        int size2 = sparseArray.size();
        for (int i = 0; i < size2; i++) {
            this.mUidToSensitivity.put(sparseArray.keyAt(i), (ArrayMap) sparseArray.valueAt(i));
        }
        setValue(this.mUidToSensitivity);
    }
}
