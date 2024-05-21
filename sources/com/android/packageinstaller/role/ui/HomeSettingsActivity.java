package com.android.packageinstaller.role.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
/* loaded from: classes.dex */
public class HomeSettingsActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startActivity(DefaultAppActivity.createIntent("android.app.role.HOME", Process.myUserHandle(), this).addFlags(33554432));
        finish();
    }
}
