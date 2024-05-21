package com.android.packageinstaller.permission.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.permission.ui.auto.AutoAppPermissionFragment;
import com.android.packageinstaller.permission.ui.handheld.AppPermissionFragment;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public final class AppPermissionActivity extends FragmentActivity {
    private static final String LOG_TAG = "AppPermissionActivity";

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Fragment newInstance;
        if (DeviceUtils.isAuto(this)) {
            setTheme(R.style.CarSettings);
        }
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        String stringExtra = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        if (stringExtra == null) {
            Log.i(LOG_TAG, "Missing mandatory argument EXTRA_PACKAGE_NAME");
            finish();
            return;
        }
        String stringExtra2 = getIntent().getStringExtra("android.intent.extra.PERMISSION_NAME");
        if (stringExtra2 == null) {
            Log.i(LOG_TAG, "Missing mandatory argument EXTRA_PERMISSION_NAME");
            finish();
            return;
        }
        String groupOfPlatformPermission = Utils.getGroupOfPlatformPermission(stringExtra2);
        UserHandle userHandle = (UserHandle) getIntent().getParcelableExtra("android.intent.extra.USER");
        if (userHandle == null) {
            Log.i(LOG_TAG, "Missing mandatory argument EXTRA_USER");
            finish();
        } else if (LocationUtils.isLocationGroupAndProvider(this, groupOfPlatformPermission, stringExtra)) {
            Intent intent = new Intent(this, LocationProviderInterceptDialog.class);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", stringExtra);
            startActivity(intent);
            finish();
        } else if (LocationUtils.isLocationGroupAndControllerExtraPackage(this, groupOfPlatformPermission, stringExtra)) {
            LocationUtils.startLocationControllerExtraPackageSettings(this);
            finish();
        } else {
            String stringExtra3 = getIntent().getStringExtra("com.android.packageinstaller.extra.CALLER_NAME");
            if (DeviceUtils.isAuto(this)) {
                newInstance = AutoAppPermissionFragment.newInstance(stringExtra, stringExtra2, groupOfPlatformPermission, userHandle);
            } else {
                newInstance = AppPermissionFragment.newInstance(stringExtra, stringExtra2, groupOfPlatformPermission, userHandle, stringExtra3, getIntent().getLongExtra("com.android.packageinstaller.extra.SESSION_ID", 0L));
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(16908290, newInstance);
            beginTransaction.commit();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (DeviceUtils.isAuto(this)) {
            if (menuItem.getItemId() == 16908332) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
