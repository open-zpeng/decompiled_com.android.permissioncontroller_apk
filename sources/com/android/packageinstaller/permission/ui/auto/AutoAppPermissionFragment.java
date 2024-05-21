package com.android.packageinstaller.permission.ui.auto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import androidx.core.content.res.TypedArrayUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.AlertDialogBuilder;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.auto.AutoAppPermissionFragment;
import com.android.packageinstaller.permission.ui.auto.AutoTwoTargetPreference;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.PackageRemovalMonitor;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AutoAppPermissionFragment extends AutoSettingsFrameFragment {
    private TwoStatePreference mAlwaysPermissionPreference;
    private TwoStatePreference mDenyPermissionPreference;
    private AutoTwoTargetPreference mDetailsPreference;
    private TwoStatePreference mForegroundOnlyPermissionPreference;
    private AppPermissionGroup mGroup;
    private boolean mHasConfirmedRevoke;
    private PackageRemovalMonitor mPackageRemovalMonitor;
    private PackageManager.OnPermissionsChangedListener mPermissionChangeListener;

    public static AutoAppPermissionFragment newInstance(String str, String str2, String str3, UserHandle userHandle) {
        AutoAppPermissionFragment autoAppPermissionFragment = new AutoAppPermissionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        if (str3 == null) {
            bundle.putString("android.intent.extra.PERMISSION_NAME", str2);
        } else {
            bundle.putString("android.intent.extra.PERMISSION_GROUP_NAME", str3);
        }
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        autoAppPermissionFragment.setArguments(bundle);
        return autoAppPermissionFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHasConfirmedRevoke = false;
        this.mGroup = getAppPermissionGroup();
        if (this.mGroup == null) {
            requireActivity().setResult(0);
            requireActivity().finish();
            return;
        }
        setHeaderLabel(getContext().getString(R.string.app_permission_title, this.mGroup.getFullLabel()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppPermissionGroup getAppPermissionGroup() {
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
            return null;
        }
        PackageInfo packageInfo = AutoPermissionsUtils.getPackageInfo(activity, string, (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        if (packageInfo == null) {
            Log.i("AppPermissionFragment", "PackageInfo is null");
            return null;
        }
        AppPermissionGroup create = AppPermissionGroup.create(context, packageInfo, groupInfo, groupPermissionInfos, false);
        if (create == null || !Utils.shouldShowPermission(context, create)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal group: ");
            sb.append(create == null ? "null" : create.getName());
            Log.i("AppPermissionFragment", sb.toString());
            return null;
        }
        return create;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }

    @Override // com.android.car.ui.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.addPreference(AutoPermissionsUtils.createHeaderPreference(getContext(), this.mGroup.getApp().applicationInfo));
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setTitle(getContext().getString(R.string.app_permission_header, this.mGroup.getFullLabel()));
        preferenceScreen.addPreference(preferenceCategory);
        this.mAlwaysPermissionPreference = new SelectedPermissionPreference(getContext());
        this.mAlwaysPermissionPreference.setTitle(R.string.app_permission_button_allow_always);
        preferenceCategory.addPreference(this.mAlwaysPermissionPreference);
        this.mForegroundOnlyPermissionPreference = new SelectedPermissionPreference(getContext());
        this.mForegroundOnlyPermissionPreference.setTitle(R.string.app_permission_button_allow_foreground);
        preferenceCategory.addPreference(this.mForegroundOnlyPermissionPreference);
        this.mDenyPermissionPreference = new SelectedPermissionPreference(getContext());
        this.mDenyPermissionPreference.setTitle(R.string.app_permission_button_deny);
        preferenceCategory.addPreference(this.mDenyPermissionPreference);
        this.mDetailsPreference = new AutoTwoTargetPreference(getContext());
        preferenceScreen.addPreference(this.mDetailsPreference);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        final FragmentActivity requireActivity = requireActivity();
        this.mPermissionChangeListener = new PermissionChangeListener(this.mGroup.getApp().applicationInfo.uid);
        requireActivity.getPackageManager().addOnPermissionsChangeListener(this.mPermissionChangeListener);
        final String str = this.mGroup.getApp().packageName;
        this.mPackageRemovalMonitor = new PackageRemovalMonitor(getContext(), str) { // from class: com.android.packageinstaller.permission.ui.auto.AutoAppPermissionFragment.1
            @Override // com.android.packageinstaller.permission.utils.PackageRemovalMonitor
            public void onPackageRemoved() {
                Log.w("AppPermissionFragment", str + " was uninstalled");
                requireActivity.setResult(0);
                requireActivity.finish();
            }
        };
        this.mPackageRemovalMonitor.register();
        try {
            requireActivity.createPackageContextAsUser(str, 0, this.mGroup.getUser()).getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AppPermissionFragment", str + " was uninstalled while this activity was stopped", e);
            requireActivity.setResult(0);
            requireActivity.finish();
        }
        this.mGroup = getAppPermissionGroup();
        updateUi();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUi() {
        this.mDetailsPreference.setOnSecondTargetClickListener(null);
        this.mDetailsPreference.setVisible(false);
        if (this.mGroup.areRuntimePermissionsGranted()) {
            if (!this.mGroup.hasPermissionWithBackgroundMode() || (this.mGroup.getBackgroundPermissions() != null && this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted())) {
                setSelectedPermissionState(this.mAlwaysPermissionPreference);
            } else {
                setSelectedPermissionState(this.mForegroundOnlyPermissionPreference);
            }
        } else {
            setSelectedPermissionState(this.mDenyPermissionPreference);
        }
        this.mAlwaysPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$s-VmL6g2dDu5NeJ-6uSfR4AVZnI
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return AutoAppPermissionFragment.this.lambda$updateUi$0$AutoAppPermissionFragment(preference);
            }
        });
        this.mForegroundOnlyPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$uMdR_kd9lrEVzQtWLkmJhi0bCao
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return AutoAppPermissionFragment.this.lambda$updateUi$1$AutoAppPermissionFragment(preference);
            }
        });
        this.mDenyPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$QcTTVFN8TOXtzVjPTzsLapC-pqw
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return AutoAppPermissionFragment.this.lambda$updateUi$2$AutoAppPermissionFragment(preference);
            }
        });
        if (this.mGroup.hasPermissionWithBackgroundMode()) {
            if (this.mGroup.getBackgroundPermissions() == null) {
                this.mAlwaysPermissionPreference.setVisible(false);
            } else {
                this.mForegroundOnlyPermissionPreference.setVisible(true);
                this.mAlwaysPermissionPreference.setTitle(R.string.app_permission_button_allow_always);
            }
        } else {
            this.mForegroundOnlyPermissionPreference.setVisible(false);
            this.mAlwaysPermissionPreference.setTitle(R.string.app_permission_button_allow);
        }
        if (isSystemFixed() || isPolicyFullyFixed() || isForegroundDisabledByPolicy()) {
            this.mAlwaysPermissionPreference.setEnabled(false);
            this.mForegroundOnlyPermissionPreference.setEnabled(false);
            this.mDenyPermissionPreference.setEnabled(false);
            final RestrictedLockUtils.EnforcedAdmin admin = getAdmin();
            if (admin != null) {
                this.mDetailsPreference.setWidgetLayoutResource(R.layout.info_preference_widget);
                this.mDetailsPreference.setOnSecondTargetClickListener(new AutoTwoTargetPreference.OnSecondTargetClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$wouAaqKxYlaSmvUWff9nVjc_bNU
                    @Override // com.android.packageinstaller.permission.ui.auto.AutoTwoTargetPreference.OnSecondTargetClickListener
                    public final void onSecondTargetClick(AutoTwoTargetPreference autoTwoTargetPreference) {
                        AutoAppPermissionFragment.this.lambda$updateUi$3$AutoAppPermissionFragment(admin, autoTwoTargetPreference);
                    }
                });
            }
            updateDetailForFixedByPolicyPermissionGroup();
        } else if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), this.mGroup.getName())) {
            this.mDetailsPreference.setWidgetLayoutResource(R.layout.settings_preference_widget);
            this.mDetailsPreference.setOnSecondTargetClickListener(new AutoTwoTargetPreference.OnSecondTargetClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$C41eUz0ii6NsSwwQF79BqMnVPXs
                @Override // com.android.packageinstaller.permission.ui.auto.AutoTwoTargetPreference.OnSecondTargetClickListener
                public final void onSecondTargetClick(AutoTwoTargetPreference autoTwoTargetPreference) {
                    AutoAppPermissionFragment.this.lambda$updateUi$4$AutoAppPermissionFragment(autoTwoTargetPreference);
                }
            });
            updateDetailForIndividuallyControlledPermissionGroup();
        } else if (this.mGroup.hasPermissionWithBackgroundMode()) {
            if (this.mGroup.getBackgroundPermissions() == null) {
                this.mAlwaysPermissionPreference.setEnabled(false);
                this.mDenyPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$VPI_n6kQj85G4UHu0qptgY2AZFE
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return AutoAppPermissionFragment.this.lambda$updateUi$5$AutoAppPermissionFragment(preference);
                    }
                });
            } else if (isBackgroundPolicyFixed()) {
                this.mAlwaysPermissionPreference.setEnabled(false);
                setSelectedPermissionState(this.mForegroundOnlyPermissionPreference);
                this.mDenyPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$XtiNpPIrVeHS-yZ2wJgK7JBh7VM
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return AutoAppPermissionFragment.this.lambda$updateUi$6$AutoAppPermissionFragment(preference);
                    }
                });
                updateDetailForFixedByPolicyPermissionGroup();
            } else if (isForegroundPolicyFixed()) {
                this.mDenyPermissionPreference.setEnabled(false);
                this.mAlwaysPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$BZZLWO7nhbLrzKPbsA9eXzZdNiQ
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return AutoAppPermissionFragment.this.lambda$updateUi$7$AutoAppPermissionFragment(preference);
                    }
                });
                this.mForegroundOnlyPermissionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$l-WhMPHE9WRYTD-RZtQcolNBL88
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return AutoAppPermissionFragment.this.lambda$updateUi$8$AutoAppPermissionFragment(preference);
                    }
                });
                updateDetailForFixedByPolicyPermissionGroup();
            }
        }
    }

    public /* synthetic */ boolean lambda$updateUi$0$AutoAppPermissionFragment(Preference preference) {
        return requestChange(true, 3);
    }

    public /* synthetic */ boolean lambda$updateUi$1$AutoAppPermissionFragment(Preference preference) {
        requestChange(false, 2);
        requestChange(true, 1);
        return true;
    }

    public /* synthetic */ boolean lambda$updateUi$2$AutoAppPermissionFragment(Preference preference) {
        return requestChange(false, 3);
    }

    public /* synthetic */ void lambda$updateUi$3$AutoAppPermissionFragment(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, AutoTwoTargetPreference autoTwoTargetPreference) {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), enforcedAdmin);
    }

    public /* synthetic */ void lambda$updateUi$4$AutoAppPermissionFragment(AutoTwoTargetPreference autoTwoTargetPreference) {
        showAllPermissions(this.mGroup.getName());
    }

    public /* synthetic */ boolean lambda$updateUi$5$AutoAppPermissionFragment(Preference preference) {
        return requestChange(false, 1);
    }

    public /* synthetic */ boolean lambda$updateUi$6$AutoAppPermissionFragment(Preference preference) {
        return requestChange(false, 1);
    }

    public /* synthetic */ boolean lambda$updateUi$7$AutoAppPermissionFragment(Preference preference) {
        return requestChange(true, 2);
    }

    public /* synthetic */ boolean lambda$updateUi$8$AutoAppPermissionFragment(Preference preference) {
        return requestChange(false, 2);
    }

    private void setSelectedPermissionState(TwoStatePreference twoStatePreference) {
        twoStatePreference.setChecked(true);
        TwoStatePreference twoStatePreference2 = this.mAlwaysPermissionPreference;
        if (twoStatePreference != twoStatePreference2) {
            twoStatePreference2.setChecked(false);
        }
        TwoStatePreference twoStatePreference3 = this.mForegroundOnlyPermissionPreference;
        if (twoStatePreference != twoStatePreference3) {
            twoStatePreference3.setChecked(false);
        }
        TwoStatePreference twoStatePreference4 = this.mDenyPermissionPreference;
        if (twoStatePreference != twoStatePreference4) {
            twoStatePreference4.setChecked(false);
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
        this.mDetailsPreference.setSummary(getContext().getString(i == 0 ? R.string.permission_revoked_none : i == size ? R.string.permission_revoked_all : R.string.permission_revoked_count, Integer.valueOf(i)));
        this.mDetailsPreference.setVisible(true);
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
        this.mDetailsPreference.setSummary(i);
        this.mDetailsPreference.setVisible(true);
    }

    private void showAllPermissions(String str) {
        AutoAllAppPermissionsFragment newInstance = AutoAllAppPermissionsFragment.newInstance(this.mGroup.getApp().packageName, str, UserHandle.getUserHandleForUid(this.mGroup.getApp().applicationInfo.uid));
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack("AllPerms");
        beginTransaction.commit();
    }

    private boolean requestChange(boolean z, int i) {
        if (LocationUtils.isLocationGroupAndProvider(getContext(), this.mGroup.getName(), this.mGroup.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), Utils.getAppLabel(this.mGroup.getApp().applicationInfo, getContext()));
            updateUi();
            return false;
        }
        if (z) {
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
                updateUi();
                return false;
            }
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
        }
        updateUi();
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
        if (z || !this.mGroup.doesSupportRuntimePermissions()) {
            this.mHasConfirmedRevoke = true;
        }
        updateUi();
    }

    /* loaded from: classes.dex */
    private static class SelectedPermissionPreference extends TwoStatePreference {
        SelectedPermissionPreference(Context context) {
            super(context, null, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
            setPersistent(false);
        }

        @Override // androidx.preference.TwoStatePreference
        public void setChecked(boolean z) {
            super.setChecked(z);
            setSummary(z ? getContext().getString(R.string.car_permission_selected) : null);
        }
    }

    /* loaded from: classes.dex */
    public static class DefaultDenyDialog extends DialogFragment {
        private static final String MSG = DefaultDenyDialog.class.getName() + ".arg.msg";
        private static final String CHANGE_TARGET = DefaultDenyDialog.class.getName() + ".arg.changeTarget";

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final AutoAppPermissionFragment autoAppPermissionFragment = (AutoAppPermissionFragment) getTargetFragment();
            return new AlertDialogBuilder(getContext()).setMessage(getArguments().getInt(MSG)).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$DefaultDenyDialog$sS_K_YRnsXi_f-XWM5Ud_iN3FMs
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AutoAppPermissionFragment.this.updateUi();
                }
            }).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionFragment$DefaultDenyDialog$1QOgzPDRojuS1ZAGw54LCuRCZ90
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AutoAppPermissionFragment.DefaultDenyDialog.this.lambda$onCreateDialog$1$AutoAppPermissionFragment$DefaultDenyDialog(autoAppPermissionFragment, dialogInterface, i);
                }
            }).create();
        }

        public /* synthetic */ void lambda$onCreateDialog$1$AutoAppPermissionFragment$DefaultDenyDialog(AutoAppPermissionFragment autoAppPermissionFragment, DialogInterface dialogInterface, int i) {
            autoAppPermissionFragment.onDenyAnyWay(getArguments().getInt(CHANGE_TARGET));
        }
    }

    /* loaded from: classes.dex */
    private class PermissionChangeListener implements PackageManager.OnPermissionsChangedListener {
        private final int mUid;

        PermissionChangeListener(int i) {
            this.mUid = i;
        }

        public void onPermissionsChanged(int i) {
            if (i == this.mUid) {
                Log.w("AppPermissionFragment", "Permissions changed.");
                AutoAppPermissionFragment autoAppPermissionFragment = AutoAppPermissionFragment.this;
                autoAppPermissionFragment.mGroup = autoAppPermissionFragment.getAppPermissionGroup();
                AutoAppPermissionFragment.this.updateUi();
            }
        }
    }
}
