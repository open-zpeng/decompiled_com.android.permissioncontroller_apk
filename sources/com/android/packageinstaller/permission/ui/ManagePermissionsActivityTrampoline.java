package com.android.packageinstaller.permission.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.packageinstaller.permission.service.BaseSearchIndexablesProvider;
/* loaded from: classes.dex */
public class ManagePermissionsActivityTrampoline extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (!BaseSearchIndexablesProvider.isIntentValid(intent, this)) {
            finish();
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            finish();
            return;
        }
        Intent addFlags = new Intent(this, ManagePermissionsActivity.class).addFlags(33554432);
        if (action.equals("com.android.permissioncontroller.settingssearch.action.MANAGE_PERMISSION_APPS")) {
            addFlags.setAction("android.intent.action.MANAGE_PERMISSION_APPS").putExtra("android.intent.extra.PERMISSION_NAME", BaseSearchIndexablesProvider.getOriginalKey(intent));
            startActivity(addFlags);
            finish();
            return;
        }
        finish();
    }
}
