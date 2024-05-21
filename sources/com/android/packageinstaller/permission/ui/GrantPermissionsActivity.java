package com.android.packageinstaller.permission.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.permission.PermissionManager;
import android.text.Html;
import android.text.Spanned;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler;
import com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.PackageRemovalMonitor;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XToast;
import com.xiaopeng.xui.theme.XThemeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/* loaded from: classes.dex */
public class GrantPermissionsActivity extends Activity implements GrantPermissionsViewHandler.ResultListener {
    private AppPermissions mAppPermissions;
    private CharSequence[] mButtonLabels;
    private String mCallingPackage;
    private int mCallingUid;
    private PackageRemovalMonitor mPackageRemovalMonitor;
    private PackageManager.OnPermissionsChangedListener mPermissionChangeListener;
    private ArrayMap<Pair<String, Boolean>, GroupState> mRequestGrantPermissionGroups = new ArrayMap<>();
    private long mRequestId;
    private String[] mRequestedPermissions;
    boolean mResultSet;
    private GrantPermissionsViewHandler mViewHandler;
    private static final String KEY_REQUEST_ID = GrantPermissionsActivity.class.getName() + "_REQUEST_ID";
    public static int NUM_BUTTONS = 5;
    public static int LABEL_ALLOW_BUTTON = 0;
    public static int LABEL_ALLOW_ALWAYS_BUTTON = 1;
    public static int LABEL_ALLOW_FOREGROUND_BUTTON = 2;
    public static int LABEL_DENY_BUTTON = 3;
    public static int LABEL_DENY_AND_DONT_ASK_AGAIN_BUTTON = 4;

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return i == 4;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return i == 4;
    }

    private int getPermissionPolicy() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getPermissionPolicy(null);
    }

    private void addRequestedPermissions(AppPermissionGroup appPermissionGroup, String str, boolean z) {
        boolean z2 = true;
        if (!appPermissionGroup.isGrantingAllowed()) {
            reportRequestResult(str, 1);
            return;
        }
        Permission permission = appPermissionGroup.getPermission(str);
        if (permission == null && ArrayUtils.contains(this.mAppPermissions.getPackageInfo().requestedPermissions, str)) {
            reportRequestResult(str, 9);
        } else if (appPermissionGroup.isUserFixed()) {
            reportRequestResult(str, 2);
        } else if ((appPermissionGroup.isPolicyFixed() && !appPermissionGroup.areRuntimePermissionsGranted()) || permission.isPolicyFixed()) {
            reportRequestResult(str, 3);
        } else {
            Pair<String, Boolean> pair = new Pair<>(appPermissionGroup.getName(), Boolean.valueOf(appPermissionGroup.isBackgroundGroup()));
            GroupState groupState = this.mRequestGrantPermissionGroups.get(pair);
            if (groupState == null) {
                groupState = new GroupState(appPermissionGroup);
                this.mRequestGrantPermissionGroups.put(pair, groupState);
            }
            groupState.affectedPermissions = ArrayUtils.appendString(groupState.affectedPermissions, str);
            int permissionPolicy = getPermissionPolicy();
            if (permissionPolicy == 1) {
                String[] strArr = {str};
                appPermissionGroup.grantRuntimePermissions(false, strArr);
                appPermissionGroup.setPolicyFixed(strArr);
                groupState.mState = 1;
                reportRequestResult(str, 5);
            } else if (permissionPolicy == 2) {
                appPermissionGroup.setPolicyFixed(new String[]{str});
                groupState.mState = 2;
                reportRequestResult(str, 8);
            } else if (appPermissionGroup.areRuntimePermissionsGranted()) {
                appPermissionGroup.grantRuntimePermissions(false, new String[]{str});
                groupState.mState = 1;
                reportRequestResult(str, 5);
            } else {
                z2 = false;
            }
            if (z2 && z) {
                groupState.mState = 3;
            }
        }
    }

    private void reportRequestResult(String str, int i) {
        boolean z = !ArrayUtils.contains(this.mRequestedPermissions, str);
        Log.v("GrantPermissionsActivity", "Permission grant result requestId=" + this.mRequestId + " callingUid=" + this.mCallingUid + " callingPackage=" + this.mCallingPackage + " permission=" + str + " isImplicit=" + z + " result=" + i);
        PermissionControllerStatsLog.write(170, this.mRequestId, this.mCallingUid, this.mCallingPackage, str, z, i);
    }

    private void reportRequestResult(String[] strArr, int i) {
        for (String str : strArr) {
            reportRequestResult(str, i);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        String[] strArr;
        String[] strArr2;
        super.onCreate(bundle);
        if (bundle == null) {
            this.mRequestId = new Random().nextLong();
        } else {
            this.mRequestId = bundle.getLong(KEY_REQUEST_ID);
        }
        this.mCallingPackage = getCallingPackage();
        setFinishOnTouchOutside(false);
        setTitle(R.string.permission_request_title);
        this.mRequestedPermissions = getIntent().getStringArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES");
        if (this.mRequestedPermissions == null) {
            this.mRequestedPermissions = new String[0];
        }
        if (this.mRequestedPermissions.length == 0) {
            setResultAndFinish();
            return;
        }
        PackageInfo callingPackageInfo = getCallingPackageInfo();
        if (callingPackageInfo == null || (strArr = callingPackageInfo.requestedPermissions) == null || strArr.length <= 0) {
            setResultAndFinish();
            return;
        }
        ApplicationInfo applicationInfo = callingPackageInfo.applicationInfo;
        if (applicationInfo.targetSdkVersion < 23) {
            this.mRequestedPermissions = new String[0];
            setResultAndFinish();
            return;
        }
        this.mCallingUid = applicationInfo.uid;
        XpGrantPermissionsViewHandlerImpl xpGrantPermissionsViewHandlerImpl = new XpGrantPermissionsViewHandlerImpl(this, this.mCallingPackage, UserHandle.getUserHandleForUid(this.mCallingUid));
        xpGrantPermissionsViewHandlerImpl.setResultListener(this);
        this.mViewHandler = xpGrantPermissionsViewHandlerImpl;
        this.mAppPermissions = new AppPermissions(this, callingPackageInfo, false, new Runnable() { // from class: com.android.packageinstaller.permission.ui.GrantPermissionsActivity.1
            @Override // java.lang.Runnable
            public void run() {
                GrantPermissionsActivity.this.setResultAndFinish();
            }
        });
        PackageManager packageManager = getPackageManager();
        for (String str : this.mRequestedPermissions) {
            if (str != null) {
                int checkPermission = packageManager.checkPermission(str, this.mCallingPackage);
                Log.i("GrantPermissionsActivity", "pm Result:" + checkPermission + " requestedPermission:" + str + " mCallingPackage:" + this.mCallingPackage);
                if (checkPermission == 2) {
                    Log.i("GrantPermissionsActivity", "pm PERMISSION_DENIED_AND_STOP_CHECK");
                    Xui.init(getApplication());
                    XToast.show((int) R.string.toast_for_function_cannot_use);
                    setResultAndFinish();
                    return;
                }
                ArrayList<String> computeAffectedPermissions = computeAffectedPermissions(str);
                int size = computeAffectedPermissions.size();
                for (int i = 0; i < size; i++) {
                    AppPermissionGroup groupForPermission = this.mAppPermissions.getGroupForPermission(computeAffectedPermissions.get(i));
                    if (groupForPermission == null) {
                        reportRequestResult(computeAffectedPermissions.get(i), 1);
                    } else {
                        addRequestedPermissions(groupForPermission, computeAffectedPermissions.get(i), bundle == null);
                    }
                }
            }
        }
        int size2 = this.mRequestGrantPermissionGroups.size();
        for (int i2 = 0; i2 < size2; i2++) {
            GroupState valueAt = this.mRequestGrantPermissionGroups.valueAt(i2);
            AppPermissionGroup appPermissionGroup = valueAt.mGroup;
            if (bundle != null) {
                valueAt.mState = bundle.getInt(getInstanceStateKey(this.mRequestGrantPermissionGroups.keyAt(i2)), valueAt.mState);
            }
            if (appPermissionGroup.isBackgroundGroup()) {
                boolean areRuntimePermissionsGranted = this.mAppPermissions.getPermissionGroup(appPermissionGroup.getName()).areRuntimePermissionsGranted();
                boolean z = getForegroundGroupState(appPermissionGroup.getName()) != null;
                if (!areRuntimePermissionsGranted && !z) {
                    int length = valueAt.affectedPermissions.length;
                    for (int i3 = 0; i3 < length; i3++) {
                        Log.w("GrantPermissionsActivity", "Cannot grant " + valueAt.affectedPermissions[i3] + " as the matching foreground permission is not already granted.");
                    }
                    valueAt.mState = 3;
                    reportRequestResult(valueAt.affectedPermissions, 1);
                }
            }
        }
        setContentView(this.mViewHandler.createView());
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        this.mViewHandler.updateWindowAttributes(attributes);
        window.setAttributes(attributes);
        if (bundle != null) {
            this.mViewHandler.loadInstanceState(bundle);
        }
        if (showNextPermissionGroupGrantRequest()) {
            return;
        }
        setResultAndFinish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:21:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x004b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateIfPermissionsWereGranted() {
        /*
            r10 = this;
            android.content.pm.PackageManager r0 = r10.getPackageManager()
            android.util.ArrayMap<android.util.Pair<java.lang.String, java.lang.Boolean>, com.android.packageinstaller.permission.ui.GrantPermissionsActivity$GroupState> r1 = r10.mRequestGrantPermissionGroups
            int r1 = r1.size()
            r2 = 0
            r3 = 1
            r4 = r2
            r5 = r3
        Le:
            if (r4 >= r1) goto L4f
            android.util.ArrayMap<android.util.Pair<java.lang.String, java.lang.Boolean>, com.android.packageinstaller.permission.ui.GrantPermissionsActivity$GroupState> r6 = r10.mRequestGrantPermissionGroups
            java.lang.Object r6 = r6.valueAt(r4)
            com.android.packageinstaller.permission.ui.GrantPermissionsActivity$GroupState r6 = (com.android.packageinstaller.permission.ui.GrantPermissionsActivity.GroupState) r6
            if (r6 == 0) goto L4c
            int r7 = r6.mState
            if (r7 == 0) goto L1f
            goto L4c
        L1f:
            java.lang.String[] r7 = r6.affectedPermissions
            if (r7 != 0) goto L25
        L23:
            r7 = r2
            goto L3b
        L25:
            r7 = r2
        L26:
            java.lang.String[] r8 = r6.affectedPermissions
            int r9 = r8.length
            if (r7 >= r9) goto L3a
            r8 = r8[r7]
            java.lang.String r9 = r10.mCallingPackage
            int r8 = r0.checkPermission(r8, r9)
            r9 = -1
            if (r8 != r9) goto L37
            goto L23
        L37:
            int r7 = r7 + 1
            goto L26
        L3a:
            r7 = r3
        L3b:
            if (r7 == 0) goto L4b
            r6.mState = r3
            if (r5 == 0) goto L4c
            boolean r6 = r10.showNextPermissionGroupGrantRequest()
            if (r6 != 0) goto L4c
            r10.setResultAndFinish()
            goto L4c
        L4b:
            r5 = r2
        L4c:
            int r4 = r4 + 1
            goto Le
        L4f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.permission.ui.GrantPermissionsActivity.updateIfPermissionsWereGranted():void");
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        Log.i("GrantPermis", "onStart");
        try {
            this.mPermissionChangeListener = new PermissionChangeListener();
            PackageManager packageManager = getPackageManager();
            packageManager.addOnPermissionsChangeListener(this.mPermissionChangeListener);
            this.mPackageRemovalMonitor = new PackageRemovalMonitor(this, this.mCallingPackage) { // from class: com.android.packageinstaller.permission.ui.GrantPermissionsActivity.2
                @Override // com.android.packageinstaller.permission.utils.PackageRemovalMonitor
                public void onPackageRemoved() {
                    Log.w("GrantPermissionsActivity", GrantPermissionsActivity.this.mCallingPackage + " was uninstalled");
                    GrantPermissionsActivity.this.finish();
                }
            };
            this.mPackageRemovalMonitor.register();
            try {
                packageManager.getPackageInfo(this.mCallingPackage, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("GrantPermissionsActivity", this.mCallingPackage + " was uninstalled while this activity was stopped", e);
                finish();
            }
            updateIfPermissionsWereGranted();
        } catch (PackageManager.NameNotFoundException unused) {
            setResultAndFinish();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.i("GrantPermis", "onStop");
        PackageRemovalMonitor packageRemovalMonitor = this.mPackageRemovalMonitor;
        if (packageRemovalMonitor != null) {
            packageRemovalMonitor.unregister();
            this.mPackageRemovalMonitor = null;
        }
        if (this.mPermissionChangeListener != null) {
            getPackageManager().removeOnPermissionsChangeListener(this.mPermissionChangeListener);
            this.mPermissionChangeListener = null;
        }
        Log.d("GrantPermissionsActivity", "onStop");
        finish();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View decorView = getWindow().getDecorView();
        if (decorView.getTop() != 0) {
            motionEvent.setLocation(motionEvent.getX(), motionEvent.getY() - decorView.getTop());
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private static String getInstanceStateKey(Pair<String, Boolean> pair) {
        return GrantPermissionsActivity.class.getName() + "_" + ((String) pair.first) + "_" + pair.second;
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mViewHandler.saveInstanceState(bundle);
        bundle.putLong(KEY_REQUEST_ID, this.mRequestId);
        int size = this.mRequestGrantPermissionGroups.size();
        for (int i = 0; i < size; i++) {
            int i2 = this.mRequestGrantPermissionGroups.valueAt(i).mState;
            if (i2 != 0) {
                bundle.putInt(getInstanceStateKey(this.mRequestGrantPermissionGroups.keyAt(i)), i2);
            }
        }
    }

    private GroupState getBackgroundGroupState(String str) {
        return this.mRequestGrantPermissionGroups.get(new Pair(str, true));
    }

    private GroupState getForegroundGroupState(String str) {
        return this.mRequestGrantPermissionGroups.get(new Pair(str, false));
    }

    private boolean shouldShowRequestForGroupState(GroupState groupState) {
        if (groupState.mState == 3) {
            return false;
        }
        GroupState foregroundGroupState = getForegroundGroupState(groupState.mGroup.getName());
        return (groupState.mGroup.isBackgroundGroup() && foregroundGroupState != null && shouldShowRequestForGroupState(foregroundGroupState)) ? false : true;
    }

    private boolean showNextPermissionGroupGrantRequest() {
        GroupState backgroundGroupState;
        GroupState groupState;
        Icon icon;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        int backgroundRequest;
        int backgroundRequestDetail;
        Spanned fromHtml;
        int size = this.mRequestGrantPermissionGroups.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (shouldShowRequestForGroupState(this.mRequestGrantPermissionGroups.valueAt(i2))) {
                i++;
            }
        }
        int i3 = 0;
        for (GroupState groupState2 : this.mRequestGrantPermissionGroups.values()) {
            if (shouldShowRequestForGroupState(groupState2)) {
                int i4 = groupState2.mState;
                if (i4 == 0) {
                    if (groupState2.mGroup.isBackgroundGroup()) {
                        groupState = getForegroundGroupState(groupState2.mGroup.getName());
                        backgroundGroupState = groupState2;
                    } else {
                        backgroundGroupState = getBackgroundGroupState(groupState2.mGroup.getName());
                        groupState = groupState2;
                    }
                    CharSequence appLabel = this.mAppPermissions.getAppLabel();
                    try {
                        icon = Icon.createWithResource(groupState2.mGroup.getIconPkg(), groupState2.mGroup.getIconResId());
                    } catch (Resources.NotFoundException e) {
                        Log.e("GrantPermissionsActivity", "Cannot load icon for group" + groupState2.mGroup.getName(), e);
                        icon = null;
                    }
                    if (backgroundGroupState == null || backgroundGroupState.mGroup.areRuntimePermissionsGranted()) {
                        z = false;
                        z2 = false;
                    } else {
                        z = backgroundGroupState.mGroup.isUserSet();
                        z2 = true;
                    }
                    if (groupState == null || groupState.mGroup.areRuntimePermissionsGranted()) {
                        z3 = false;
                        z4 = false;
                    } else {
                        z3 = groupState.mGroup.isUserSet();
                        z4 = true;
                    }
                    this.mButtonLabels = new CharSequence[NUM_BUTTONS];
                    this.mButtonLabels[LABEL_ALLOW_BUTTON] = getString(R.string.grant_dialog_button_allow);
                    CharSequence[] charSequenceArr = this.mButtonLabels;
                    charSequenceArr[LABEL_ALLOW_ALWAYS_BUTTON] = null;
                    charSequenceArr[LABEL_ALLOW_FOREGROUND_BUTTON] = null;
                    charSequenceArr[LABEL_DENY_BUTTON] = getString(R.string.grant_dialog_button_deny);
                    if (z3 || z) {
                        this.mButtonLabels[LABEL_DENY_AND_DONT_ASK_AGAIN_BUTTON] = getString(R.string.grant_dialog_button_deny_and_dont_ask_again);
                    } else {
                        this.mButtonLabels[LABEL_DENY_AND_DONT_ASK_AGAIN_BUTTON] = null;
                    }
                    if (z4) {
                        backgroundRequest = groupState2.mGroup.getRequest();
                        if (groupState2.mGroup.hasPermissionWithBackgroundMode()) {
                            CharSequence[] charSequenceArr2 = this.mButtonLabels;
                            charSequenceArr2[LABEL_ALLOW_BUTTON] = null;
                            charSequenceArr2[LABEL_ALLOW_FOREGROUND_BUTTON] = getString(R.string.grant_dialog_button_allow_foreground);
                            if (z2) {
                                this.mButtonLabels[LABEL_ALLOW_ALWAYS_BUTTON] = getString(R.string.grant_dialog_button_allow_always);
                                if (z3 || z) {
                                    this.mButtonLabels[LABEL_DENY_BUTTON] = null;
                                }
                            }
                            backgroundRequestDetail = 0;
                        } else {
                            backgroundRequestDetail = groupState2.mGroup.getRequestDetail();
                        }
                    } else if (!z2) {
                        return false;
                    } else {
                        backgroundRequest = groupState2.mGroup.getBackgroundRequest();
                        backgroundRequestDetail = groupState2.mGroup.getBackgroundRequestDetail();
                        this.mButtonLabels[LABEL_ALLOW_BUTTON] = getString(R.string.grant_dialog_button_allow_background);
                        this.mButtonLabels[LABEL_DENY_BUTTON] = getString(R.string.grant_dialog_button_deny_background);
                        this.mButtonLabels[LABEL_DENY_AND_DONT_ASK_AGAIN_BUTTON] = getString(R.string.grant_dialog_button_deny_background_and_dont_ask_again);
                    }
                    CharSequence requestMessage = Utils.getRequestMessage(appLabel, groupState2.mGroup, this, backgroundRequest);
                    if (backgroundRequestDetail != 0) {
                        try {
                            fromHtml = Html.fromHtml(getPackageManager().getResourcesForApplication(groupState2.mGroup.getDeclaringPackage()).getString(backgroundRequestDetail), 0);
                        } catch (PackageManager.NameNotFoundException unused) {
                        }
                        setTitle(requestMessage);
                        this.mViewHandler.updateUi(groupState2.mGroup.getName(), i, i3, icon, requestMessage, fromHtml, this.mButtonLabels);
                        return true;
                    }
                    fromHtml = null;
                    setTitle(requestMessage);
                    this.mViewHandler.updateUi(groupState2.mGroup.getName(), i, i3, icon, requestMessage, fromHtml, this.mButtonLabels);
                    return true;
                } else if (i4 != 3) {
                    i3++;
                }
            }
        }
        return false;
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler.ResultListener
    public void onPermissionGrantResult(final String str, final int i) {
        Log.i("GrantPermis", "name " + str + ", result " + i);
        logGrantPermissionActivityButtons(str, i);
        GroupState foregroundGroupState = getForegroundGroupState(str);
        GroupState backgroundGroupState = getBackgroundGroupState(str);
        if (i == 0 || i == 1 || i == 3) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KeyguardManager.class);
            if (keyguardManager.isDeviceLocked()) {
                keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() { // from class: com.android.packageinstaller.permission.ui.GrantPermissionsActivity.3
                    @Override // android.app.KeyguardManager.KeyguardDismissCallback
                    public void onDismissCancelled() {
                    }

                    @Override // android.app.KeyguardManager.KeyguardDismissCallback
                    public void onDismissError() {
                        Log.e("GrantPermissionsActivity", "Cannot dismiss keyguard perm=" + str + " result=" + i);
                    }

                    @Override // android.app.KeyguardManager.KeyguardDismissCallback
                    public void onDismissSucceeded() {
                        GrantPermissionsActivity.this.onPermissionGrantResult(str, i);
                    }
                });
                return;
            }
        }
        if (i == 0) {
            if (foregroundGroupState != null) {
                onPermissionGrantResultSingleState(foregroundGroupState, true, false);
            }
            if (backgroundGroupState != null) {
                onPermissionGrantResultSingleState(backgroundGroupState, true, false);
            }
        } else if (i == 1) {
            if (foregroundGroupState != null) {
                onPermissionGrantResultSingleState(foregroundGroupState, true, false);
            }
            if (backgroundGroupState != null) {
                onPermissionGrantResultSingleState(backgroundGroupState, false, false);
            }
        } else if (i == 2) {
            if (foregroundGroupState != null) {
                onPermissionGrantResultSingleState(foregroundGroupState, false, false);
            }
            if (backgroundGroupState != null) {
                onPermissionGrantResultSingleState(backgroundGroupState, false, false);
            }
        } else if (i == 3) {
            if (foregroundGroupState != null) {
                onPermissionGrantResultSingleState(foregroundGroupState, false, true);
            }
            if (backgroundGroupState != null) {
                onPermissionGrantResultSingleState(backgroundGroupState, false, true);
            }
        }
        if (showNextPermissionGroupGrantRequest()) {
            return;
        }
        setResultAndFinish();
    }

    private void onPermissionGrantResultSingleState(GroupState groupState, boolean z, boolean z2) {
        AppPermissionGroup appPermissionGroup;
        if (groupState == null || (appPermissionGroup = groupState.mGroup) == null || groupState.mState != 0) {
            return;
        }
        if (z) {
            appPermissionGroup.grantRuntimePermissions(z2, groupState.affectedPermissions);
            groupState.mState = 1;
            reportRequestResult(groupState.affectedPermissions, 4);
            return;
        }
        appPermissionGroup.revokeRuntimePermissions(z2, groupState.affectedPermissions);
        groupState.mState = 2;
        reportRequestResult(groupState.affectedPermissions, z2 ? 7 : 6);
    }

    @Override // android.app.Activity
    public void finish() {
        setResultIfNeeded(0);
        super.finish();
    }

    private PackageInfo getCallingPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(this.mCallingPackage, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GrantPermissionsActivity", "No package: " + this.mCallingPackage, e);
            return null;
        }
    }

    private void setResultIfNeeded(int i) {
        if (this.mResultSet) {
            return;
        }
        this.mResultSet = true;
        logRequestedPermissionGroups();
        Intent intent = new Intent("android.content.pm.action.REQUEST_PERMISSIONS");
        intent.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES", this.mRequestedPermissions);
        PackageManager packageManager = getPackageManager();
        int length = this.mRequestedPermissions.length;
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = packageManager.checkPermission(this.mRequestedPermissions[i2], this.mCallingPackage);
        }
        intent.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS", iArr);
        setResult(i, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setResultAndFinish() {
        setResultIfNeeded(-1);
        finish();
    }

    private void logRequestedPermissionGroups() {
        if (this.mRequestGrantPermissionGroups.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.mRequestGrantPermissionGroups.size());
        for (GroupState groupState : this.mRequestGrantPermissionGroups.values()) {
            arrayList.add(groupState.mGroup);
        }
        SafetyNetLogger.logPermissionsRequested(this.mAppPermissions.getPackageInfo(), arrayList);
    }

    private ArrayList<String> computeAffectedPermissions(String str) {
        int i = this.mAppPermissions.getPackageInfo().applicationInfo.targetSdkVersion;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(str);
        List splitPermissions = ((PermissionManager) getSystemService(PermissionManager.class)).getSplitPermissions();
        int size = splitPermissions.size();
        for (int i2 = 0; i2 < size; i2++) {
            PermissionManager.SplitPermissionInfo splitPermissionInfo = (PermissionManager.SplitPermissionInfo) splitPermissions.get(i2);
            if (i < splitPermissionInfo.getTargetSdk() && str.equals(splitPermissionInfo.getSplitPermission())) {
                arrayList.addAll(splitPermissionInfo.getNewPermissions());
            }
        }
        if (i <= 25) {
            ArrayList<String> arrayList2 = new ArrayList<>();
            int size2 = arrayList.size();
            for (int i3 = 0; i3 < size2; i3++) {
                AppPermissionGroup groupForPermission = this.mAppPermissions.getGroupForPermission(arrayList.get(i3));
                if (groupForPermission != null) {
                    ArrayList<Permission> permissions = groupForPermission.getPermissions();
                    int size3 = permissions.size();
                    for (int i4 = 0; i4 < size3; i4++) {
                        arrayList2.add(permissions.get(i4).getName());
                    }
                }
            }
            return arrayList2;
        }
        return arrayList;
    }

    private void logGrantPermissionActivityButtons(String str, int i) {
        int i2;
        int i3;
        int buttonState = getButtonState();
        if (i == 0) {
            i2 = LABEL_ALLOW_BUTTON;
            if (((1 << i2) & buttonState) == 0) {
                i2 = LABEL_ALLOW_ALWAYS_BUTTON;
            }
        } else if (i == 1) {
            i2 = LABEL_ALLOW_FOREGROUND_BUTTON;
        } else if (i == 2) {
            i2 = LABEL_DENY_BUTTON;
        } else if (i == 3) {
            i2 = LABEL_DENY_AND_DONT_ASK_AGAIN_BUTTON;
        } else {
            i3 = 0;
            PermissionControllerStatsLog.write(213, str, this.mCallingUid, this.mCallingPackage, buttonState, i3);
            Log.v("GrantPermissionsActivity", "Logged buttons presented and clicked permissionGroupName=" + str + " uid=" + this.mCallingUid + " package=" + this.mCallingPackage + " presentedButtons=" + buttonState + " clickedButton=" + i3);
        }
        i3 = 1 << i2;
        PermissionControllerStatsLog.write(213, str, this.mCallingUid, this.mCallingPackage, buttonState, i3);
        Log.v("GrantPermissionsActivity", "Logged buttons presented and clicked permissionGroupName=" + str + " uid=" + this.mCallingUid + " package=" + this.mCallingPackage + " presentedButtons=" + buttonState + " clickedButton=" + i3);
    }

    private int getButtonState() {
        int i = 0;
        if (this.mButtonLabels == null) {
            return 0;
        }
        for (int i2 = NUM_BUTTONS - 1; i2 >= 0; i2--) {
            i *= 2;
            if (this.mButtonLabels[i2] != null) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class GroupState {
        String[] affectedPermissions;
        final AppPermissionGroup mGroup;
        int mState = 0;

        GroupState(AppPermissionGroup appPermissionGroup) {
            this.mGroup = appPermissionGroup;
        }
    }

    /* loaded from: classes.dex */
    private class PermissionChangeListener implements PackageManager.OnPermissionsChangedListener {
        final int mCallingPackageUid;

        PermissionChangeListener() throws PackageManager.NameNotFoundException {
            this.mCallingPackageUid = GrantPermissionsActivity.this.getPackageManager().getPackageUid(GrantPermissionsActivity.this.mCallingPackage, 0);
        }

        public void onPermissionsChanged(int i) {
            if (i == this.mCallingPackageUid) {
                GrantPermissionsActivity.this.updateIfPermissionsWereGranted();
            }
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XThemeManager.setWindowBackgroundResource(configuration, getWindow(), R.drawable.x_bg_dialog);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Log.i("GrantPermis", "onDestroy");
    }
}
