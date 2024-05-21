package com.android.packageinstaller.role.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import com.android.packageinstaller.permission.service.BaseSearchIndexablesProvider;
/* loaded from: classes.dex */
public class RoleSearchTrampolineActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Intent createIntent;
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
        char c = 65535;
        int hashCode = action.hashCode();
        if (hashCode != 263796501) {
            if (hashCode == 853022806 && action.equals("com.android.permissioncontroller.settingssearch.action.MANAGE_SPECIAL_APP_ACCESS")) {
                c = 1;
            }
        } else if (action.equals("com.android.permissioncontroller.settingssearch.action.MANAGE_DEFAULT_APP")) {
            c = 0;
        }
        if (c == 0) {
            createIntent = DefaultAppActivity.createIntent(BaseSearchIndexablesProvider.getOriginalKey(intent), Process.myUserHandle(), this);
        } else if (c == 1) {
            createIntent = SpecialAppAccessActivity.createIntent(BaseSearchIndexablesProvider.getOriginalKey(intent), this);
        } else {
            finish();
            return;
        }
        createIntent.addFlags(33554432);
        startActivity(createIntent);
        finish();
    }
}
