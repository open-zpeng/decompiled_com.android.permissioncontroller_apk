package com.android.packageinstaller.permission.ui;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.permission.ui.handheld.AdjustUserSensitiveFragment;
/* loaded from: classes.dex */
public class AdjustUserSensitiveActivity extends FragmentActivity {
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, AdjustUserSensitiveFragment.newInstance());
        beginTransaction.commit();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (DeviceUtils.isAuto(this)) {
                onBackPressed();
                return true;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
