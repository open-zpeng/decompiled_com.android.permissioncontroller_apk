package com.android.packageinstaller.permission.ui;

import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.permission.ui.auto.AutoAllAppPermissionsFragment;
import com.android.packageinstaller.permission.ui.auto.AutoAppPermissionsFragment;
import com.android.packageinstaller.permission.ui.auto.AutoManageStandardPermissionsFragment;
import com.android.packageinstaller.permission.ui.auto.AutoPermissionAppsFragment;
import com.android.packageinstaller.permission.ui.handheld.AllAppPermissionsFragment;
import com.android.packageinstaller.permission.ui.handheld.ManageStandardPermissionsFragment;
import com.android.packageinstaller.permission.ui.television.AppPermissionsFragment;
import com.android.packageinstaller.permission.ui.television.ManagePermissionsFragment;
import com.android.packageinstaller.permission.ui.television.PermissionAppsFragment;
import com.android.packageinstaller.permission.ui.wear.AppPermissionsFragmentWear;
import java.util.Random;
/* loaded from: classes.dex */
public final class ManagePermissionsActivity extends FragmentActivity {
    private static final String LOG_TAG = "ManagePermissionsActivity";

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Fragment newInstance;
        android.app.Fragment newInstance2;
        if (DeviceUtils.isAuto(this)) {
            setTheme(R.style.CarSettings);
        }
        super.onCreate(bundle);
        if (bundle != null) {
            return;
        }
        String action = getIntent().getAction();
        getWindow().addSystemFlags(524288);
        long longExtra = getIntent().getLongExtra("com.android.packageinstaller.extra.SESSION_ID", 0L);
        while (longExtra == 0) {
            longExtra = new Random().nextLong();
        }
        char c = 65535;
        switch (action.hashCode()) {
            case -1168603379:
                if (action.equals("android.intent.action.MANAGE_PERMISSION_APPS")) {
                    c = 3;
                    break;
                }
                break;
            case -140132685:
                if (action.equals("android.intent.action.REVIEW_PERMISSION_USAGE")) {
                    c = 1;
                    break;
                }
                break;
            case 1685512017:
                if (action.equals("android.intent.action.MANAGE_APP_PERMISSIONS")) {
                    c = 2;
                    break;
                }
                break;
            case 1861372431:
                if (action.equals("android.intent.action.MANAGE_PERMISSIONS")) {
                    c = 0;
                    break;
                }
                break;
        }
        android.app.Fragment fragment = null;
        if (c != 0) {
            if (c == 1) {
                finish();
                return;
            } else if (c == 2) {
                String stringExtra = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
                if (stringExtra == null) {
                    Log.i(LOG_TAG, "Missing mandatory argument EXTRA_PACKAGE_NAME");
                    finish();
                    return;
                }
                boolean booleanExtra = getIntent().getBooleanExtra("com.android.packageinstaller.extra.ALL_PERMISSIONS", false);
                UserHandle userHandle = (UserHandle) getIntent().getParcelableExtra("android.intent.extra.USER");
                if (userHandle == null) {
                    userHandle = UserHandle.of(UserHandle.myUserId());
                }
                if (DeviceUtils.isAuto(this)) {
                    if (booleanExtra) {
                        newInstance = AutoAllAppPermissionsFragment.newInstance(stringExtra, userHandle);
                    } else {
                        newInstance = AutoAppPermissionsFragment.newInstance(stringExtra, userHandle);
                    }
                } else if (DeviceUtils.isWear(this)) {
                    newInstance = AppPermissionsFragmentWear.newInstance(stringExtra);
                } else if (DeviceUtils.isTelevision(this)) {
                    newInstance2 = AppPermissionsFragment.newInstance(stringExtra);
                    fragment = newInstance2;
                    newInstance = null;
                } else if (booleanExtra) {
                    newInstance = AllAppPermissionsFragment.newInstance(stringExtra, userHandle);
                } else {
                    newInstance = com.android.packageinstaller.permission.ui.handheld.AppPermissionsFragment.newInstance(stringExtra, userHandle, longExtra);
                }
            } else if (c == 3) {
                String stringExtra2 = getIntent().getStringExtra("android.intent.extra.PERMISSION_NAME");
                if (stringExtra2 == null) {
                    Log.i(LOG_TAG, "Missing mandatory argument EXTRA_PERMISSION_NAME");
                    finish();
                    return;
                } else if (DeviceUtils.isAuto(this)) {
                    newInstance = AutoPermissionAppsFragment.newInstance(stringExtra2);
                } else if (DeviceUtils.isTelevision(this)) {
                    newInstance2 = PermissionAppsFragment.newInstance(stringExtra2);
                    fragment = newInstance2;
                    newInstance = null;
                } else {
                    newInstance = com.android.packageinstaller.permission.ui.handheld.PermissionAppsFragment.newInstance(stringExtra2, longExtra);
                }
            } else {
                Log.w(LOG_TAG, "Unrecognized action " + action);
                finish();
                return;
            }
        } else if (DeviceUtils.isAuto(this)) {
            newInstance = AutoManageStandardPermissionsFragment.newInstance();
        } else if (DeviceUtils.isTelevision(this)) {
            newInstance2 = ManagePermissionsFragment.newInstance();
            fragment = newInstance2;
            newInstance = null;
        } else {
            newInstance = ManageStandardPermissionsFragment.newInstance(longExtra);
        }
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(16908290, fragment).commit();
            return;
        }
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.commit();
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
