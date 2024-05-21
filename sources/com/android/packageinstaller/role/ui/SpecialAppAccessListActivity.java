package com.android.packageinstaller.role.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.role.ui.auto.AutoSpecialAppAccessListFragment;
import com.android.packageinstaller.role.ui.handheld.HandheldSpecialAppAccessListFragment;
/* loaded from: classes.dex */
public class SpecialAppAccessListActivity extends FragmentActivity {
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
                newInstance = AutoSpecialAppAccessListFragment.newInstance();
            } else {
                newInstance = HandheldSpecialAppAccessListFragment.newInstance();
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(16908290, newInstance);
            beginTransaction.commit();
        }
    }
}
