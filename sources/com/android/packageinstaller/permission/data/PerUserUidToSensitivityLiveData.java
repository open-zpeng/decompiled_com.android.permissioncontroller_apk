package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.packageinstaller.AsyncTaskLiveData;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class PerUserUidToSensitivityLiveData extends AsyncTaskLiveData<SparseArray<ArrayMap<String, Integer>>> {
    private static final SparseArray<PerUserUidToSensitivityLiveData> sInstances = new SparseArray<>();
    private final Context mContext;
    private final BroadcastReceiver mPackageMonitor = new BroadcastReceiver() { // from class: com.android.packageinstaller.permission.data.PerUserUidToSensitivityLiveData.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PerUserUidToSensitivityLiveData.this.loadValue();
        }
    };
    private final UserHandle mUser;

    public static PerUserUidToSensitivityLiveData get(UserHandle userHandle, Application application) {
        PerUserUidToSensitivityLiveData perUserUidToSensitivityLiveData = sInstances.get(userHandle.getIdentifier());
        if (perUserUidToSensitivityLiveData == null) {
            PerUserUidToSensitivityLiveData perUserUidToSensitivityLiveData2 = new PerUserUidToSensitivityLiveData(userHandle, application);
            sInstances.put(userHandle.getIdentifier(), perUserUidToSensitivityLiveData2);
            return perUserUidToSensitivityLiveData2;
        }
        return perUserUidToSensitivityLiveData;
    }

    private PerUserUidToSensitivityLiveData(UserHandle userHandle, Application application) {
        this.mUser = userHandle;
        try {
            this.mContext = application.createPackageContextAsUser(application.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        loadValue();
        this.mContext.registerReceiver(this.mPackageMonitor, new IntentFilter("android.intent.action.PACKAGE_CHANGED"));
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        this.mContext.unregisterReceiver(this.mPackageMonitor);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.packageinstaller.AsyncTaskLiveData
    public SparseArray<ArrayMap<String, Integer>> loadValueInBackground() {
        int intValue;
        PackageManager packageManager = this.mContext.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(4096);
        Set<String> platformPermissions = Utils.getPlatformPermissions();
        ArraySet<String> launcherPackages = Utils.getLauncherPackages(this.mContext);
        SparseArray<ArrayMap<String, Integer>> sparseArray = new SparseArray<>();
        int size = installedPackages.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            boolean contains = launcherPackages.contains(packageInfo.packageName);
            boolean z = (packageInfo.applicationInfo.flags & 1) != 0;
            ArrayMap<String, Integer> arrayMap = sparseArray.get(packageInfo.applicationInfo.uid);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                sparseArray.put(packageInfo.applicationInfo.uid, arrayMap);
            }
            for (String str : platformPermissions) {
                if (ArrayUtils.contains(packageInfo.requestedPermissions, str)) {
                    Integer num = arrayMap.get(str);
                    int i2 = 768;
                    List<PackageInfo> list = installedPackages;
                    if (packageInfo.applicationInfo.uid < 10000) {
                        intValue = num == null ? 768 : num.intValue();
                    } else {
                        intValue = num == null ? 0 : num.intValue();
                    }
                    if (z && !contains) {
                        i2 = (packageManager.getPermissionFlags(str, packageInfo.packageName, this.mUser) & 32) != 0 ? 0 : 256;
                    }
                    arrayMap.put(str, Integer.valueOf(packageInfo.applicationInfo.uid < 10000 ? i2 & intValue : i2 | intValue));
                    installedPackages = list;
                }
            }
        }
        return sparseArray;
    }
}
