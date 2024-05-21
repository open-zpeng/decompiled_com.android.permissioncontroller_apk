package com.android.packageinstaller.permission.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public final class LocationProviderInterceptDialog extends FragmentActivity {
    private static final String LOG_TAG = "LocationProviderInterceptDialog";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        if (stringExtra == null) {
            Log.i(LOG_TAG, "Missing mandatory argument EXTRA_PACKAGE_NAME");
            finish();
            return;
        }
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert_material).setTitle(17039380).setMessage(getString(R.string.location_warning, new Object[]{Utils.getAppLabel(getPackageInfo(stringExtra).applicationInfo, this)})).setNegativeButton(R.string.ok, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.-$$Lambda$LocationProviderInterceptDialog$dQSb6NnKOhrYvlViK98bW3DuD84
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LocationProviderInterceptDialog.this.lambda$onCreate$0$LocationProviderInterceptDialog(dialogInterface, i);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.packageinstaller.permission.ui.-$$Lambda$LocationProviderInterceptDialog$KBBmOQ6D_zHfVSjjvKnX2uB1j8g
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                LocationProviderInterceptDialog.this.lambda$onCreate$1$LocationProviderInterceptDialog(dialogInterface);
            }
        }).show();
    }

    public /* synthetic */ void lambda$onCreate$0$LocationProviderInterceptDialog(DialogInterface dialogInterface, int i) {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    public /* synthetic */ void lambda$onCreate$1$LocationProviderInterceptDialog(DialogInterface dialogInterface) {
        finish();
    }

    private PackageInfo getPackageInfo(String str) {
        try {
            return getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = LOG_TAG;
            Log.i(str2, "No package: " + str, e);
            finish();
            return null;
        }
    }
}
