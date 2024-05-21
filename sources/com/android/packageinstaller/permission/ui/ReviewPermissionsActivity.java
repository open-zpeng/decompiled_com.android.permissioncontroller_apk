package com.android.packageinstaller.permission.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.permission.ui.handheld.ReviewPermissionsFragment;
import com.android.packageinstaller.permission.ui.wear.ReviewPermissionsWearFragment;
/* loaded from: classes.dex */
public final class ReviewPermissionsActivity extends FragmentActivity implements ConfirmActionDialogFragment$OnActionConfirmedListener {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PackageInfo targetPackageInfo = getTargetPackageInfo();
        if (targetPackageInfo == null) {
            finish();
        } else if (DeviceUtils.isWear(this)) {
            ReviewPermissionsWearFragment newInstance = ReviewPermissionsWearFragment.newInstance(targetPackageInfo);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(16908290, newInstance);
            beginTransaction.commit();
        } else {
            setContentView(R.layout.review_permissions);
            if (getSupportFragmentManager().findFragmentById(R.id.preferences_frame) == null) {
                FragmentTransaction beginTransaction2 = getSupportFragmentManager().beginTransaction();
                beginTransaction2.add(R.id.preferences_frame, ReviewPermissionsFragment.newInstance(targetPackageInfo));
                beginTransaction2.commit();
            }
        }
    }

    private PackageInfo getTargetPackageInfo() {
        String stringExtra = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        if (TextUtils.isEmpty(stringExtra)) {
            return null;
        }
        try {
            return getPackageManager().getPackageInfo(stringExtra, 4096);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }
}
