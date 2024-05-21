package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.SparseArray;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class NonSensitivePackagesLiveData extends MediatorLiveData<ArrayList<ApplicationInfo>> {
    private static NonSensitivePackagesLiveData sInstance;

    public static NonSensitivePackagesLiveData get(Application application) {
        if (sInstance == null) {
            sInstance = new NonSensitivePackagesLiveData(application);
        }
        return sInstance;
    }

    private NonSensitivePackagesLiveData(final Application application) {
        addSource(UidToSensitivityLiveData.get(application), new Observer() { // from class: com.android.packageinstaller.permission.data.-$$Lambda$NonSensitivePackagesLiveData$E7yg44nuAd3PtzJTLiSl_4G3QWg
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NonSensitivePackagesLiveData.this.lambda$new$1$NonSensitivePackagesLiveData(application, (SparseArray) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$1$NonSensitivePackagesLiveData(final Application application, final SparseArray sparseArray) {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.data.-$$Lambda$NonSensitivePackagesLiveData$eE2QJ_gwfPCcpyL--xw8gqkqkTY
            @Override // java.lang.Runnable
            public final void run() {
                NonSensitivePackagesLiveData.this.lambda$new$0$NonSensitivePackagesLiveData(application, sparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$NonSensitivePackagesLiveData(Application application, SparseArray sparseArray) {
        PackageManager packageManager = application.getPackageManager();
        ArrayList arrayList = new ArrayList();
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseArray.keyAt(i);
            UserHandle userHandleForUid = UserHandle.getUserHandleForUid(keyAt);
            ArrayMap arrayMap = (ArrayMap) sparseArray.valueAt(i);
            int size2 = arrayMap.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size2) {
                    break;
                } else if (((Integer) arrayMap.valueAt(i2)).intValue() != 768) {
                    String[] packagesForUid = packageManager.getPackagesForUid(keyAt);
                    if (packagesForUid != null) {
                        for (String str : packagesForUid) {
                            try {
                                arrayList.add(packageManager.getApplicationInfoAsUser(str, 0, userHandleForUid));
                            } catch (PackageManager.NameNotFoundException unused) {
                            }
                        }
                    }
                } else {
                    i2++;
                }
            }
        }
        postValue(arrayList);
    }
}
