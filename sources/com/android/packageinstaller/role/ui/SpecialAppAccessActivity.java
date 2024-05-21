package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.auto.AutoSpecialAppAccessFragment;
import com.android.packageinstaller.role.ui.handheld.HandheldSpecialAppAccessFragment;
/* loaded from: classes.dex */
public class SpecialAppAccessActivity extends FragmentActivity {
    private static final String LOG_TAG = "SpecialAppAccessActivity";

    public static Intent createIntent(String str, Context context) {
        return new Intent(context, SpecialAppAccessActivity.class).putExtra("android.intent.extra.ROLE_NAME", str);
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
        String stringExtra = getIntent().getStringExtra("android.intent.extra.ROLE_NAME");
        Role role = Roles.get(this).get(stringExtra);
        if (role == null) {
            String str = LOG_TAG;
            Log.e(str, "Unknown role: " + stringExtra);
            finish();
        } else if (!role.isAvailable(this)) {
            String str2 = LOG_TAG;
            Log.e(str2, "Role is unavailable: " + stringExtra);
            finish();
        } else if (!role.isVisible(this)) {
            String str3 = LOG_TAG;
            Log.e(str3, "Role is invisible: " + stringExtra);
            finish();
        } else if (bundle == null) {
            if (DeviceUtils.isAuto(this)) {
                newInstance = AutoSpecialAppAccessFragment.newInstance(stringExtra);
            } else {
                newInstance = HandheldSpecialAppAccessFragment.newInstance(stringExtra);
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(16908290, newInstance);
            beginTransaction.commit();
        }
    }
}
