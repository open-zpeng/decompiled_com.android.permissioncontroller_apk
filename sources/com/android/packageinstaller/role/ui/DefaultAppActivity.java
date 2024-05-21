package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.auto.AutoDefaultAppFragment;
import com.android.packageinstaller.role.ui.handheld.HandheldDefaultAppFragment;
/* loaded from: classes.dex */
public class DefaultAppActivity extends FragmentActivity {
    private static final String LOG_TAG = "DefaultAppActivity";

    public static Intent createIntent(String str, UserHandle userHandle, Context context) {
        return new Intent(context, DefaultAppActivity.class).putExtra("android.intent.extra.ROLE_NAME", str).putExtra("android.intent.extra.USER", userHandle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Fragment newInstance;
        if (DeviceUtils.isAuto(this)) {
            setTheme(R.style.CarSettings);
        }
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("android.intent.extra.ROLE_NAME");
        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        if (userHandle == null) {
            userHandle = Process.myUserHandle();
        }
        Role role = Roles.get(this).get(stringExtra);
        if (role == null) {
            String str = LOG_TAG;
            Log.e(str, "Unknown role: " + stringExtra);
            finish();
        } else if (!role.isAvailableAsUser(userHandle, this)) {
            String str2 = LOG_TAG;
            Log.e(str2, "Role is unavailable: " + stringExtra);
            finish();
        } else if (!role.isVisibleAsUser(userHandle, this)) {
            String str3 = LOG_TAG;
            Log.e(str3, "Role is invisible: " + stringExtra);
            finish();
        } else if (bundle == null) {
            if (DeviceUtils.isAuto(this)) {
                newInstance = AutoDefaultAppFragment.newInstance(stringExtra, userHandle);
            } else {
                newInstance = HandheldDefaultAppFragment.newInstance(stringExtra, userHandle);
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(16908290, newInstance);
            beginTransaction.commit();
        }
    }
}
