package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.handheld.AppPermissionFragment;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.PackageRemovalMonitor;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.widget.ActionBarShadowController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/* loaded from: classes.dex */
public class AppPermissionFragment extends SettingsWithLargeHeader {
    private RadioButton mAlwaysButton;
    private RadioButton mDenyButton;
    private View mDivider;
    private RadioButton mForegroundOnlyButton;
    private AppPermissionGroup mGroup;
    private boolean mHasConfirmedRevoke;
    private NestedScrollView mNestedScrollView;
    private PackageRemovalMonitor mPackageRemovalMonitor;
    private PackageManager.OnPermissionsChangedListener mPermissionChangeListener;
    private TextView mPermissionDetails;
    private RadioGroup mRadioGroup;
    private ViewGroup mWidgetFrame;

    public static AppPermissionFragment newInstance(String str, String str2, String str3, UserHandle userHandle, String str4, long j) {
        AppPermissionFragment appPermissionFragment = new AppPermissionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        if (str3 == null) {
            bundle.putString("android.intent.extra.PERMISSION_NAME", str2);
        } else {
            bundle.putString("android.intent.extra.PERMISSION_GROUP_NAME", str3);
        }
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        bundle.putString("com.android.packageinstaller.extra.CALLER_NAME", str4);
        bundle.putLong("com.android.packageinstaller.extra.SESSION_ID", j);
        appPermissionFragment.setArguments(bundle);
        return appPermissionFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.mHasConfirmedRevoke = false;
        createAppPermissionGroup();
        if (this.mGroup != null) {
            getActivity().setTitle(getPreferenceManager().getContext().getString(R.string.app_permission_title, this.mGroup.getFullLabel()));
            logAppPermissionFragmentViewed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createAppPermissionGroup() {
        FragmentActivity activity = getActivity();
        Context context = getPreferenceManager().getContext();
        String string = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        String string2 = getArguments().getString("android.intent.extra.PERMISSION_GROUP_NAME");
        if (string2 == null) {
            string2 = getArguments().getString("android.intent.extra.PERMISSION_NAME");
        }
        PackageItemInfo groupInfo = Utils.getGroupInfo(string2, context);
        List<PermissionInfo> groupPermissionInfos = Utils.getGroupPermissionInfos(string2, context);
        if (groupInfo == null || groupPermissionInfos == null) {
            Log.i("AppPermissionFragment", "Illegal group: " + string2);
            activity.setResult(0);
            activity.finish();
            return;
        }
        this.mGroup = AppPermissionGroup.create(context, getPackageInfo(activity, string, (UserHandle) getArguments().getParcelable("android.intent.extra.USER")), groupInfo, groupPermissionInfos, false);
        AppPermissionGroup appPermissionGroup = this.mGroup;
        if (appPermissionGroup == null || !Utils.shouldShowPermission(context, appPermissionGroup)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal group: ");
            AppPermissionGroup appPermissionGroup2 = this.mGroup;
            sb.append(appPermissionGroup2 == null ? "null" : appPermissionGroup2.getName());
            Log.i("AppPermissionFragment", sb.toString());
            activity.setResult(0);
            activity.finish();
        }
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.SettingsWithLargeHeader, com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        final Context context = getPreferenceManager().getContext();
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R.layout.app_permission, viewGroup, false);
        AppPermissionGroup appPermissionGroup = this.mGroup;
        if (appPermissionGroup == null) {
            return viewGroup2;
        }
        String fullAppLabel = Utils.getFullAppLabel(appPermissionGroup.getApp().applicationInfo, context);
        setHeader(getAppIcon(), fullAppLabel, null, null, false);
        updateHeader(viewGroup2.requireViewById(R.id.large_header));
        ((TextView) viewGroup2.requireViewById(R.id.permission_message)).setText(context.getString(R.string.app_permission_header, this.mGroup.getFullLabel()));
        viewGroup2.requireViewById(R.id.usage_summary).setVisibility(8);
        final long j = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID");
        TextView textView = (TextView) viewGroup2.requireViewById(R.id.footer_link_1);
        textView.setText(context.getString(R.string.app_permission_footer_app_permissions_link, fullAppLabel));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$8MbyLAsWQ1zTRcJXYbwxObe1UTM
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppPermissionFragment.this.lambda$onCreateView$0$AppPermissionFragment(j, context, view);
            }
        });
        TextView textView2 = (TextView) viewGroup2.requireViewById(R.id.footer_link_2);
        textView2.setText(context.getString(R.string.app_permission_footer_permission_apps_link));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$1JiURd6jqKBdOSTQ14SN1JjfaJc
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppPermissionFragment.this.lambda$onCreateView$1$AppPermissionFragment(j, context, view);
            }
        });
        String string = getArguments().getString("com.android.packageinstaller.extra.CALLER_NAME");
        if (AppPermissionsFragment.class.getName().equals(string)) {
            textView.setVisibility(8);
        } else if (PermissionAppsFragment.class.getName().equals(string)) {
            textView2.setVisibility(8);
        }
        this.mRadioGroup = (RadioGroup) viewGroup2.requireViewById(R.id.radiogroup);
        this.mAlwaysButton = (RadioButton) viewGroup2.requireViewById(R.id.allow_radio_button);
        this.mForegroundOnlyButton = (RadioButton) viewGroup2.requireViewById(R.id.foreground_only_radio_button);
        this.mDenyButton = (RadioButton) viewGroup2.requireViewById(R.id.deny_radio_button);
        this.mDivider = viewGroup2.requireViewById(R.id.two_target_divider);
        this.mWidgetFrame = (ViewGroup) viewGroup2.requireViewById(R.id.widget_frame);
        this.mPermissionDetails = (TextView) viewGroup2.requireViewById(R.id.permission_details);
        this.mNestedScrollView = (NestedScrollView) viewGroup2.requireViewById(R.id.nested_scroll_view);
        return viewGroup2;
    }

    public /* synthetic */ void lambda$onCreateView$0$AppPermissionFragment(long j, Context context, View view) {
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(this.mGroup.getApp().applicationInfo.uid);
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mGroup.getApp().packageName);
        intent.putExtra("com.android.packageinstaller.extra.SESSION_ID", j);
        intent.putExtra("android.intent.extra.USER", userHandleForUid);
        context.startActivity(intent);
    }

    public /* synthetic */ void lambda$onCreateView$1$AppPermissionFragment(long j, Context context, View view) {
        Intent intent = new Intent("android.intent.action.MANAGE_PERMISSION_APPS");
        intent.putExtra("android.intent.extra.PERMISSION_NAME", this.mGroup.getName());
        intent.putExtra("com.android.packageinstaller.extra.SESSION_ID", j);
        context.startActivity(intent);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mGroup == null) {
            return;
        }
        final String string = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        UserHandle userHandle = (UserHandle) getArguments().getParcelable("android.intent.extra.USER");
        final FragmentActivity activity = getActivity();
        try {
            this.mPermissionChangeListener = new PermissionChangeListener(this.mGroup.getApp().applicationInfo.uid);
            activity.getPackageManager().addOnPermissionsChangeListener(this.mPermissionChangeListener);
            this.mPackageRemovalMonitor = new PackageRemovalMonitor(getContext(), string) { // from class: com.android.packageinstaller.permission.ui.handheld.AppPermissionFragment.1
                @Override // com.android.packageinstaller.permission.utils.PackageRemovalMonitor
                public void onPackageRemoved() {
                    Log.w("AppPermissionFragment", string + " was uninstalled");
                    activity.setResult(0);
                    activity.finish();
                }
            };
            this.mPackageRemovalMonitor.register();
            try {
                activity.createPackageContextAsUser(string, 0, userHandle).getPackageManager().getPackageInfo(string, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AppPermissionFragment", string + " was uninstalled while this activity was stopped", e);
                activity.setResult(0);
                activity.finish();
            }
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setElevation(0.0f);
            }
            ActionBarShadowController.attachToView(activity, getLifecycle(), this.mNestedScrollView);
            createAppPermissionGroup();
            updateButtons();
        } catch (PackageManager.NameNotFoundException unused) {
            activity.setResult(0);
            activity.finish();
        }
    }

    void logAppPermissionFragmentViewed() {
        long j = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        PermissionControllerStatsLog.write(216, j, this.mGroup.getApp().applicationInfo.uid, this.mGroup.getApp().packageName, this.mGroup.getName());
        Log.v("AppPermissionFragment", "AppPermission fragment viewed with sessionId=" + j + " uid=" + this.mGroup.getApp().applicationInfo.uid + " packageName=" + this.mGroup.getApp().packageName + " permissionGroupName=" + this.mGroup.getName());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        PackageRemovalMonitor packageRemovalMonitor = this.mPackageRemovalMonitor;
        if (packageRemovalMonitor != null) {
            packageRemovalMonitor.unregister();
            this.mPackageRemovalMonitor = null;
        }
        if (this.mPermissionChangeListener != null) {
            getActivity().getPackageManager().removeOnPermissionsChangeListener(this.mPermissionChangeListener);
            this.mPermissionChangeListener = null;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private ArrayList<PermissionState> createPermissionSnapshot() {
        ArrayList<PermissionState> arrayList = new ArrayList<>();
        ArrayList<Permission> permissions = this.mGroup.getPermissions();
        int size = permissions.size();
        for (int i = 0; i < size; i++) {
            Permission permission = permissions.get(i);
            arrayList.add(new PermissionState(permission.getName(), permission.isGrantedIncludingAppOp()));
        }
        AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
        if (backgroundPermissions == null) {
            return arrayList;
        }
        ArrayList<Permission> permissions2 = backgroundPermissions.getPermissions();
        int size2 = permissions2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Permission permission2 = permissions2.get(i2);
            arrayList.add(new PermissionState(permission2.getName(), permission2.isGrantedIncludingAppOp()));
        }
        return arrayList;
    }

    private void logPermissionChanges(ArrayList<PermissionState> arrayList) {
        long nextLong = new Random().nextLong();
        int size = arrayList.size();
        long j = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        for (int i = 0; i < size; i++) {
            PermissionState permissionState = arrayList.get(i);
            boolean z = permissionState.permissionGranted;
            Permission permission = this.mGroup.getPermission(permissionState.permissionName);
            if (permission == null) {
                if (this.mGroup.getBackgroundPermissions() != null) {
                    permission = this.mGroup.getBackgroundPermissions().getPermission(permissionState.permissionName);
                }
            }
            boolean isGrantedIncludingAppOp = permission.isGrantedIncludingAppOp();
            if (z != isGrantedIncludingAppOp) {
                logAppPermissionFragmentActionReported(j, nextLong, permissionState.permissionName, isGrantedIncludingAppOp);
            }
        }
    }

    private void logAppPermissionFragmentActionReported(long j, long j2, String str, boolean z) {
        PermissionControllerStatsLog.write(215, j, j2, this.mGroup.getApp().applicationInfo.uid, this.mGroup.getApp().packageName, str, z);
        Log.v("AppPermissionFragment", "Permission changed via UI with sessionId=" + j + " changeId=" + j2 + " uid=" + this.mGroup.getApp().applicationInfo.uid + " packageName=" + this.mGroup.getApp().packageName + " permission=" + str + " isGranted=" + z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtons() {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        this.mDivider.setVisibility(8);
        this.mWidgetFrame.setVisibility(8);
        this.mPermissionDetails.setVisibility(8);
        if (this.mGroup.areRuntimePermissionsGranted()) {
            if (!this.mGroup.hasPermissionWithBackgroundMode() || (this.mGroup.getBackgroundPermissions() != null && this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted())) {
                setCheckedButton(this.mAlwaysButton);
            } else {
                setCheckedButton(this.mForegroundOnlyButton);
            }
        } else {
            setCheckedButton(this.mDenyButton);
        }
        this.mAlwaysButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$2g2WGx1PbArEm6IEr8u5UNVvg9c
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppPermissionFragment.this.lambda$updateButtons$2$AppPermissionFragment(view);
            }
        });
        this.mForegroundOnlyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$_-I2phmMTJYg_1arzuPHWqboUe0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppPermissionFragment.this.lambda$updateButtons$3$AppPermissionFragment(view);
            }
        });
        this.mDenyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$tk2whXLVpVuVs924F43awcfLtI4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppPermissionFragment.this.lambda$updateButtons$4$AppPermissionFragment(view);
            }
        });
        if (this.mGroup.hasPermissionWithBackgroundMode()) {
            if (this.mGroup.getBackgroundPermissions() == null) {
                this.mAlwaysButton.setVisibility(8);
            } else {
                this.mForegroundOnlyButton.setVisibility(0);
                this.mAlwaysButton.setText(context.getString(R.string.app_permission_button_allow_always));
            }
        } else {
            this.mForegroundOnlyButton.setVisibility(8);
            this.mAlwaysButton.setText(context.getString(R.string.app_permission_button_allow));
        }
        if (isSystemFixed() || isPolicyFullyFixed() || isForegroundDisabledByPolicy()) {
            this.mAlwaysButton.setEnabled(false);
            this.mForegroundOnlyButton.setEnabled(false);
            this.mDenyButton.setEnabled(false);
            final RestrictedLockUtils.EnforcedAdmin admin = getAdmin();
            if (admin != null) {
                showRightIcon(R.drawable.ic_info);
                this.mWidgetFrame.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$6rG_VpXskar6Bxhswe1-mhRTDEc
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(context, admin);
                    }
                });
            }
            updateDetailForFixedByPolicyPermissionGroup();
        } else if (Utils.areGroupPermissionsIndividuallyControlled(context, this.mGroup.getName())) {
            this.mDivider.setVisibility(0);
            showRightIcon(R.drawable.ic_settings);
            this.mWidgetFrame.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$XmAgosK-mupZryPDyVNNTnwSZ00
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AppPermissionFragment.this.lambda$updateButtons$6$AppPermissionFragment(view);
                }
            });
            updateDetailForIndividuallyControlledPermissionGroup();
        } else if (this.mGroup.hasPermissionWithBackgroundMode()) {
            if (this.mGroup.getBackgroundPermissions() == null) {
                this.mAlwaysButton.setEnabled(false);
                this.mDenyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$-v-V0rgLZG4raqdW_cr2XTYSHgQ
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppPermissionFragment.this.lambda$updateButtons$7$AppPermissionFragment(view);
                    }
                });
            } else if (isBackgroundPolicyFixed()) {
                this.mAlwaysButton.setEnabled(false);
                setCheckedButton(this.mForegroundOnlyButton);
                this.mDenyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$qeK7tf6Op7aM5ynOtyVaKp9yESc
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppPermissionFragment.this.lambda$updateButtons$8$AppPermissionFragment(view);
                    }
                });
                updateDetailForFixedByPolicyPermissionGroup();
            } else if (isForegroundPolicyFixed()) {
                this.mDenyButton.setEnabled(false);
                this.mAlwaysButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$JaJgLXMGM8z4lFMC33rR8HswKZ8
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppPermissionFragment.this.lambda$updateButtons$9$AppPermissionFragment(view);
                    }
                });
                this.mForegroundOnlyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$bZ0YkpAIzZcVhEB5ou8bbTs-SMg
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppPermissionFragment.this.lambda$updateButtons$10$AppPermissionFragment(view);
                    }
                });
                updateDetailForFixedByPolicyPermissionGroup();
            }
        }
    }

    public /* synthetic */ void lambda$updateButtons$2$AppPermissionFragment(View view) {
        requestChange(true, 3);
    }

    public /* synthetic */ void lambda$updateButtons$3$AppPermissionFragment(View view) {
        requestChange(false, 2);
        requestChange(true, 1);
    }

    public /* synthetic */ void lambda$updateButtons$4$AppPermissionFragment(View view) {
        requestChange(false, 3);
    }

    public /* synthetic */ void lambda$updateButtons$6$AppPermissionFragment(View view) {
        showAllPermissions(this.mGroup.getName());
    }

    public /* synthetic */ void lambda$updateButtons$7$AppPermissionFragment(View view) {
        requestChange(false, 1);
    }

    public /* synthetic */ void lambda$updateButtons$8$AppPermissionFragment(View view) {
        requestChange(false, 1);
    }

    public /* synthetic */ void lambda$updateButtons$9$AppPermissionFragment(View view) {
        requestChange(true, 2);
    }

    public /* synthetic */ void lambda$updateButtons$10$AppPermissionFragment(View view) {
        requestChange(false, 2);
    }

    private void setCheckedButton(RadioButton radioButton) {
        this.mRadioGroup.clearCheck();
        radioButton.setChecked(true);
        RadioButton radioButton2 = this.mAlwaysButton;
        if (radioButton != radioButton2) {
            radioButton2.setChecked(false);
        }
        RadioButton radioButton3 = this.mForegroundOnlyButton;
        if (radioButton != radioButton3) {
            radioButton3.setChecked(false);
        }
        RadioButton radioButton4 = this.mDenyButton;
        if (radioButton != radioButton4) {
            radioButton4.setChecked(false);
        }
    }

    private void showRightIcon(int i) {
        this.mWidgetFrame.removeAllViews();
        ImageView imageView = new ImageView(getPreferenceManager().getContext());
        imageView.setImageResource(i);
        this.mWidgetFrame.addView(imageView);
        this.mWidgetFrame.setVisibility(0);
    }

    private static PackageInfo getPackageInfo(Activity activity, String str, UserHandle userHandle) {
        try {
            return activity.createPackageContextAsUser(str, 0, userHandle).getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("AppPermissionFragment", "No package: " + activity.getCallingPackage(), e);
            activity.setResult(0);
            activity.finish();
            return null;
        }
    }

    private boolean isSystemFixed() {
        return this.mGroup.isSystemFixed();
    }

    private boolean isForegroundPolicyFixed() {
        return this.mGroup.isPolicyFixed();
    }

    private boolean isBackgroundPolicyFixed() {
        return this.mGroup.getBackgroundPermissions() != null && this.mGroup.getBackgroundPermissions().isPolicyFixed();
    }

    private boolean isPolicyFullyFixed() {
        return isForegroundPolicyFixed() && (this.mGroup.getBackgroundPermissions() == null || isBackgroundPolicyFixed());
    }

    private boolean isForegroundDisabledByPolicy() {
        return isForegroundPolicyFixed() && !this.mGroup.areRuntimePermissionsGranted();
    }

    private RestrictedLockUtils.EnforcedAdmin getAdmin() {
        return RestrictedLockUtils.getProfileOrDeviceOwner(getContext(), this.mGroup.getUser());
    }

    private void updateDetailForIndividuallyControlledPermissionGroup() {
        ArrayList<Permission> permissions = this.mGroup.getPermissions();
        int size = permissions.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (!permissions.get(i2).isGrantedIncludingAppOp()) {
                i++;
            }
        }
        this.mPermissionDetails.setText(getContext().getString(i == 0 ? R.string.permission_revoked_none : i == size ? R.string.permission_revoked_all : R.string.permission_revoked_count, Integer.valueOf(i)));
        this.mPermissionDetails.setVisibility(0);
    }

    private void updateDetailForFixedByPolicyPermissionGroup() {
        RestrictedLockUtils.EnforcedAdmin admin = getAdmin();
        AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
        boolean z = admin != null;
        if (isSystemFixed()) {
            setDetail(R.string.permission_summary_enabled_system_fixed);
        } else if (isForegroundDisabledByPolicy()) {
            if (z) {
                setDetail(R.string.disabled_by_admin);
            } else {
                setDetail(R.string.permission_summary_enforced_by_policy);
            }
        } else if (isPolicyFullyFixed()) {
            if (backgroundPermissions == null) {
                if (z) {
                    setDetail(R.string.enabled_by_admin);
                } else {
                    setDetail(R.string.permission_summary_enforced_by_policy);
                }
            } else if (backgroundPermissions.areRuntimePermissionsGranted()) {
                if (z) {
                    setDetail(R.string.enabled_by_admin);
                } else {
                    setDetail(R.string.permission_summary_enforced_by_policy);
                }
            } else if (z) {
                setDetail(R.string.permission_summary_enabled_by_admin_foreground_only);
            } else {
                setDetail(R.string.permission_summary_enabled_by_policy_foreground_only);
            }
        } else if (isBackgroundPolicyFixed()) {
            if (backgroundPermissions.areRuntimePermissionsGranted()) {
                if (z) {
                    setDetail(R.string.permission_summary_enabled_by_admin_background_only);
                } else {
                    setDetail(R.string.permission_summary_enabled_by_policy_background_only);
                }
            } else if (z) {
                setDetail(R.string.permission_summary_disabled_by_admin_background_only);
            } else {
                setDetail(R.string.permission_summary_disabled_by_policy_background_only);
            }
        } else if (isForegroundPolicyFixed()) {
            if (z) {
                setDetail(R.string.permission_summary_enabled_by_admin_foreground_only);
            } else {
                setDetail(R.string.permission_summary_enabled_by_policy_foreground_only);
            }
        }
    }

    private void setDetail(int i) {
        this.mPermissionDetails.setText(getPreferenceManager().getContext().getString(i));
        this.mPermissionDetails.setVisibility(0);
    }

    private void showAllPermissions(String str) {
        AllAppPermissionsFragment newInstance = AllAppPermissionsFragment.newInstance(this.mGroup.getApp().packageName, str, UserHandle.getUserHandleForUid(this.mGroup.getApp().applicationInfo.uid));
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack("AllPerms");
        beginTransaction.commit();
    }

    private Drawable getAppIcon() {
        return Utils.getBadgedIcon(getActivity(), this.mGroup.getApp().applicationInfo);
    }

    private boolean requestChange(boolean z, int i) {
        if (LocationUtils.isLocationGroupAndProvider(getContext(), this.mGroup.getName(), this.mGroup.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), Utils.getAppLabel(this.mGroup.getApp().applicationInfo, getContext()));
            updateButtons();
            return false;
        }
        if (z) {
            ArrayList<PermissionState> createPermissionSnapshot = createPermissionSnapshot();
            if ((i & 1) != 0) {
                if (!this.mGroup.areRuntimePermissionsGranted()) {
                    SafetyNetLogger.logPermissionToggled(this.mGroup);
                }
                this.mGroup.grantRuntimePermissions(false);
            }
            if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
                if (!this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                    SafetyNetLogger.logPermissionToggled(this.mGroup.getBackgroundPermissions());
                }
                this.mGroup.getBackgroundPermissions().grantRuntimePermissions(false);
            }
            logPermissionChanges(createPermissionSnapshot);
        } else {
            int i2 = i & 1;
            boolean z2 = i2 != 0 && this.mGroup.areRuntimePermissionsGranted() && (this.mGroup.hasGrantedByDefaultPermission() || !this.mGroup.doesSupportRuntimePermissions() || this.mGroup.hasInstallToRuntimeSplit());
            int i3 = i & 2;
            if (i3 != 0 && this.mGroup.getBackgroundPermissions() != null && this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
                z2 |= backgroundPermissions.hasGrantedByDefaultPermission() || !backgroundPermissions.doesSupportRuntimePermissions() || backgroundPermissions.hasInstallToRuntimeSplit();
            }
            if (z2 && !this.mHasConfirmedRevoke) {
                showDefaultDenyDialog(i);
                updateButtons();
                return false;
            }
            ArrayList<PermissionState> createPermissionSnapshot2 = createPermissionSnapshot();
            if (i2 != 0 && this.mGroup.areRuntimePermissionsGranted()) {
                if (this.mGroup.areRuntimePermissionsGranted()) {
                    SafetyNetLogger.logPermissionToggled(this.mGroup);
                }
                this.mGroup.revokeRuntimePermissions(false);
            }
            if (i3 != 0 && this.mGroup.getBackgroundPermissions() != null && this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                if (this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                    SafetyNetLogger.logPermissionToggled(this.mGroup.getBackgroundPermissions());
                }
                this.mGroup.getBackgroundPermissions().revokeRuntimePermissions(false);
            }
            logPermissionChanges(createPermissionSnapshot2);
        }
        updateButtons();
        return true;
    }

    private void showDefaultDenyDialog(int i) {
        Bundle bundle = new Bundle();
        boolean hasGrantedByDefaultPermission = (i & 1) != 0 ? this.mGroup.hasGrantedByDefaultPermission() : false;
        if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
            hasGrantedByDefaultPermission |= this.mGroup.getBackgroundPermissions().hasGrantedByDefaultPermission();
        }
        bundle.putInt(DefaultDenyDialog.MSG, hasGrantedByDefaultPermission ? R.string.system_warning : R.string.old_sdk_deny_warning);
        bundle.putInt(DefaultDenyDialog.CHANGE_TARGET, i);
        DefaultDenyDialog defaultDenyDialog = new DefaultDenyDialog();
        defaultDenyDialog.setArguments(bundle);
        defaultDenyDialog.setTargetFragment(this, 0);
        defaultDenyDialog.show(getFragmentManager().beginTransaction(), DefaultDenyDialog.class.getName());
    }

    void onDenyAnyWay(int i) {
        boolean z;
        ArrayList<PermissionState> createPermissionSnapshot = createPermissionSnapshot();
        if ((i & 1) != 0) {
            if (this.mGroup.areRuntimePermissionsGranted()) {
                SafetyNetLogger.logPermissionToggled(this.mGroup);
            }
            this.mGroup.revokeRuntimePermissions(false);
            z = this.mGroup.hasGrantedByDefaultPermission();
        } else {
            z = false;
        }
        if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
            if (this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                SafetyNetLogger.logPermissionToggled(this.mGroup.getBackgroundPermissions());
            }
            this.mGroup.getBackgroundPermissions().revokeRuntimePermissions(false);
            z |= this.mGroup.getBackgroundPermissions().hasGrantedByDefaultPermission();
        }
        logPermissionChanges(createPermissionSnapshot);
        if (z || !this.mGroup.doesSupportRuntimePermissions()) {
            this.mHasConfirmedRevoke = true;
        }
        updateButtons();
    }

    /* loaded from: classes.dex */
    public static class DefaultDenyDialog extends DialogFragment {
        private static final String MSG = DefaultDenyDialog.class.getName() + ".arg.msg";
        private static final String CHANGE_TARGET = DefaultDenyDialog.class.getName() + ".arg.changeTarget";
        private static final String KEY = DefaultDenyDialog.class.getName() + ".arg.key";

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final AppPermissionFragment appPermissionFragment = (AppPermissionFragment) getTargetFragment();
            return new AlertDialog.Builder(getContext()).setMessage(getArguments().getInt(MSG)).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$DefaultDenyDialog$MOZAq9rLwY9I9dnLo7cw972FS8w
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AppPermissionFragment.this.updateButtons();
                }
            }).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionFragment$DefaultDenyDialog$RimFKDqydrPn8Ykyk7hK2lZnHSM
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AppPermissionFragment.DefaultDenyDialog.this.lambda$onCreateDialog$1$AppPermissionFragment$DefaultDenyDialog(appPermissionFragment, dialogInterface, i);
                }
            }).create();
        }

        public /* synthetic */ void lambda$onCreateDialog$1$AppPermissionFragment$DefaultDenyDialog(AppPermissionFragment appPermissionFragment, DialogInterface dialogInterface, int i) {
            appPermissionFragment.onDenyAnyWay(getArguments().getInt(CHANGE_TARGET));
        }
    }

    /* loaded from: classes.dex */
    private class PermissionChangeListener implements PackageManager.OnPermissionsChangedListener {
        private final int mUid;

        PermissionChangeListener(int i) throws PackageManager.NameNotFoundException {
            this.mUid = i;
        }

        public void onPermissionsChanged(int i) {
            if (i == this.mUid) {
                Log.w("AppPermissionFragment", "Permissions changed.");
                AppPermissionFragment.this.createAppPermissionGroup();
                AppPermissionFragment.this.updateButtons();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PermissionState {
        public final boolean permissionGranted;
        public final String permissionName;

        PermissionState(String str, boolean z) {
            this.permissionName = str;
            this.permissionGranted = z;
        }
    }
}
