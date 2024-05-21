package com.android.packageinstaller.role.ui;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.model.UserDeniedManager;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.Objects;
/* loaded from: classes.dex */
public class RequestRoleActivity extends FragmentActivity {
    private static final String LOG_TAG = "RequestRoleActivity";
    private String mPackageName;
    private String mRoleName;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        this.mRoleName = getIntent().getStringExtra("android.intent.extra.ROLE_NAME");
        this.mPackageName = getCallingPackage();
        if (!handleChangeDefaultDialerDialogCompatibility()) {
            reportRequestResult(1);
            finish();
        } else if (!handleSmsDefaultDialogCompatibility()) {
            reportRequestResult(1);
            finish();
        } else if (TextUtils.isEmpty(this.mRoleName)) {
            String str = LOG_TAG;
            Log.w(str, "Role name cannot be null or empty: " + this.mRoleName);
            reportRequestResult(1);
            finish();
        } else if (TextUtils.isEmpty(this.mPackageName)) {
            String str2 = LOG_TAG;
            Log.w(str2, "Package name cannot be null or empty: " + this.mPackageName);
            reportRequestResult(1);
            finish();
        } else {
            Role role = Roles.get(this).get(this.mRoleName);
            if (role == null) {
                String str3 = LOG_TAG;
                Log.w(str3, "Unknown role: " + this.mRoleName);
                reportRequestResult(1);
                finish();
            } else if (!role.isAvailable(this)) {
                String str4 = LOG_TAG;
                Log.e(str4, "Role is unavailable: " + this.mRoleName);
                reportRequestResult(1);
                finish();
            } else if (!role.isVisible(this)) {
                String str5 = LOG_TAG;
                Log.e(str5, "Role is invisible: " + this.mRoleName);
                reportRequestResult(1);
                finish();
            } else if (!role.isRequestable()) {
                String str6 = LOG_TAG;
                Log.e(str6, "Role is not requestable: " + this.mRoleName);
                reportRequestResult(1);
                finish();
            } else if (!role.isExclusive()) {
                String str7 = LOG_TAG;
                Log.e(str7, "Role is not exclusive: " + this.mRoleName);
                reportRequestResult(1);
                finish();
            } else if (PackageUtils.getApplicationInfo(this.mPackageName, this) == null) {
                String str8 = LOG_TAG;
                Log.w(str8, "Unknown application: " + this.mPackageName);
                reportRequestResult(1);
                finish();
            } else if (((RoleManager) getSystemService(RoleManager.class)).getRoleHolders(this.mRoleName).contains(this.mPackageName)) {
                String str9 = LOG_TAG;
                Log.i(str9, "Application is already a role holder, role: " + this.mRoleName + ", package: " + this.mPackageName);
                reportRequestResult(2);
                setResult(-1);
                finish();
            } else if (!role.isPackageQualified(this.mPackageName, this)) {
                String str10 = LOG_TAG;
                Log.w(str10, "Application doesn't qualify for role, role: " + this.mRoleName + ", package: " + this.mPackageName);
                reportRequestResult(3);
                finish();
            } else if (!UserDeniedManager.getInstance(this).isDeniedAlways(this.mRoleName, this.mPackageName)) {
                if (bundle == null) {
                    RequestRoleFragment newInstance = RequestRoleFragment.newInstance(this.mRoleName, this.mPackageName);
                    FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                    beginTransaction.add(newInstance, (String) null);
                    beginTransaction.commit();
                }
            } else {
                String str11 = LOG_TAG;
                Log.w(str11, "Application is denied always for role, role: " + this.mRoleName + ", package: " + this.mPackageName);
                reportRequestResult(4);
                finish();
            }
        }
    }

    private boolean handleChangeDefaultDialerDialogCompatibility() {
        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), "android.telecom.action.CHANGE_DEFAULT_DIALER")) {
            Log.w(LOG_TAG, "TelecomManager.ACTION_CHANGE_DEFAULT_DIALER is deprecated; please use RoleManager.createRequestRoleIntent() and Activity.startActivityForResult() instead");
            this.mRoleName = "android.app.role.DIALER";
            this.mPackageName = null;
            String stringExtra = intent.getStringExtra("android.intent.extra.CALLING_PACKAGE");
            String stringExtra2 = intent.getStringExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME");
            if (Objects.equals(stringExtra2, stringExtra)) {
                this.mPackageName = stringExtra2;
                return true;
            } else if (Objects.equals(stringExtra, (String) CollectionUtils.firstOrNull(((RoleManager) getSystemService(RoleManager.class)).getRoleHolders("android.app.role.DIALER")))) {
                this.mPackageName = stringExtra2;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean handleSmsDefaultDialogCompatibility() {
        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), "android.provider.Telephony.ACTION_CHANGE_DEFAULT")) {
            Log.w(LOG_TAG, "Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT is deprecated; please use RoleManager.createRequestRoleIntent() and Activity.startActivityForResult() instead");
            this.mRoleName = "android.app.role.SMS";
            this.mPackageName = null;
            String stringExtra = intent.getStringExtra("android.intent.extra.CALLING_PACKAGE");
            String stringExtra2 = intent.getStringExtra("package");
            if (stringExtra2 == null) {
                startActivity(DefaultAppActivity.createIntent("android.app.role.SMS", Process.myUserHandle(), this).addFlags(33554432));
                return false;
            } else if (Objects.equals(stringExtra2, stringExtra)) {
                this.mPackageName = stringExtra2;
                return true;
            } else if (Objects.equals(stringExtra, (String) CollectionUtils.firstOrNull(((RoleManager) getSystemService(RoleManager.class)).getRoleHolders("android.app.role.SMS")))) {
                this.mPackageName = stringExtra2;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void reportRequestResult(int i) {
        RequestRoleFragment.reportRequestResult(getApplicationUid(this.mPackageName, this), this.mPackageName, this.mRoleName, -1, -1, null, -1, null, i);
    }

    private static int getApplicationUid(String str, Context context) {
        ApplicationInfo applicationInfo;
        if (str == null || (applicationInfo = PackageUtils.getApplicationInfo(str, context)) == null) {
            return -1;
        }
        return applicationInfo.uid;
    }
}
