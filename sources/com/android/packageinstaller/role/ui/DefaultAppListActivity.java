package com.android.packageinstaller.role.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.role.ui.auto.AutoDefaultAppListFragment;
import com.android.packageinstaller.role.ui.handheld.HandheldDefaultAppListFragment;
/* loaded from: classes.dex */
public class DefaultAppListActivity extends FragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Fragment newInstance;
        if (DeviceUtils.isAuto(this)) {
            setTheme(R.style.CarSettings);
        }
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        if (bundle == null) {
            if (DeviceUtils.isAuto(this)) {
                newInstance = AutoDefaultAppListFragment.newInstance();
            } else {
                newInstance = HandheldDefaultAppListFragment.newInstance();
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(16908290, newInstance);
            beginTransaction.commit();
        }
    }
}
