package com.android.packageinstaller;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageItemInfo;
import android.util.ArrayMap;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.SpecialAppAccessListActivity;
/* loaded from: classes.dex */
public class PackageInstallerApplication extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        PackageItemInfo.forceSafeLabels();
        updateSpecialAppAccessListActivityEnabledState();
    }

    private void updateSpecialAppAccessListActivityEnabledState() {
        boolean z;
        ArrayMap<String, Role> arrayMap = Roles.get(this);
        int size = arrayMap.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                z = false;
                break;
            }
            Role valueAt = arrayMap.valueAt(i);
            if (valueAt.isAvailable(this) && valueAt.isVisible(this) && !valueAt.isExclusive()) {
                z = true;
                break;
            }
            i++;
        }
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, SpecialAppAccessListActivity.class), z ? 0 : 2, 1);
    }
}
