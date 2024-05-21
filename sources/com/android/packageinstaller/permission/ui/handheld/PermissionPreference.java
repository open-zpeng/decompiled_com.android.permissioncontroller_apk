package com.android.packageinstaller.permission.ui.handheld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.BidiFormatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.handheld.PermissionPreference;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PermissionPreference extends MultiTargetSwitchPreference {
    private final PermissionPreferenceChangeListener mCallBacks;
    private final PreferenceFragmentCompat mFragment;
    private final AppPermissionGroup mGroup;
    private final int mIconSize;
    private final int mOriginalWidgetLayoutRes;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface PermissionPreferenceChangeListener {
        void hasConfirmDefaultPermissionRevoke();

        void onPreferenceChanged(String str);

        boolean shouldConfirmDefaultPermissionRevoke();
    }

    /* loaded from: classes.dex */
    interface PermissionPreferenceOwnerFragment {
        void onBackgroundAccessChosen(String str, int i);

        void onDenyAnyWay(String str, int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PermissionPreference(PreferenceFragmentCompat preferenceFragmentCompat, AppPermissionGroup appPermissionGroup, PermissionPreferenceChangeListener permissionPreferenceChangeListener, int i) {
        super(preferenceFragmentCompat.getPreferenceManager().getContext());
        this.mFragment = preferenceFragmentCompat;
        this.mGroup = appPermissionGroup;
        this.mCallBacks = permissionPreferenceChangeListener;
        this.mOriginalWidgetLayoutRes = getWidgetLayoutResource();
        this.mIconSize = i;
        setPersistent(false);
        updateUi();
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateUi() {
        boolean areGroupPermissionsIndividuallyControlled = Utils.areGroupPermissionsIndividuallyControlled(getContext(), this.mGroup.getName());
        final RestrictedLockUtils.EnforcedAdmin admin = getAdmin();
        setEnabled(true);
        setWidgetLayoutResource(this.mOriginalWidgetLayoutRes);
        setOnPreferenceClickListener(null);
        setSwitchOnClickListener(null);
        setSummary((CharSequence) null);
        setChecked(this.mGroup.areRuntimePermissionsGranted());
        if (isSystemFixed() || isPolicyFullyFixed() || isForegroundDisabledByPolicy()) {
            if (admin != null) {
                setWidgetLayoutResource(R.layout.restricted_icon);
                setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$bErdSOzJ5Mc0YGj4g1kC5zCZsgY
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return PermissionPreference.this.lambda$updateUi$0$PermissionPreference(admin, preference);
                    }
                });
            } else {
                setEnabled(false);
            }
            updateSummaryForFixedByPolicyPermissionGroup();
        } else if (areGroupPermissionsIndividuallyControlled) {
            setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$_ZJecLjX1JyiLnPgznBcubESAU8
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return PermissionPreference.this.lambda$updateUi$1$PermissionPreference(preference);
                }
            });
            setSwitchOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$dgcdyl9UuvYlrEUC9sDhPYEROkA
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PermissionPreference.this.lambda$updateUi$2$PermissionPreference(view);
                }
            });
            updateSummaryForIndividuallyControlledPermissionGroup();
        } else if (this.mGroup.hasPermissionWithBackgroundMode()) {
            if (this.mGroup.getBackgroundPermissions() == null) {
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$9sTrkzYTlPgZyx84MvZSwveGiVY
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return PermissionPreference.this.lambda$updateUi$3$PermissionPreference(preference, obj);
                    }
                });
                updateSummaryForPermissionGroupWithBackgroundPermission();
            } else if (isBackgroundPolicyFixed()) {
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$hJ4Vj7gkSZj_LcOMkLP1RAtgdiM
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return PermissionPreference.this.lambda$updateUi$4$PermissionPreference(preference, obj);
                    }
                });
                updateSummaryForFixedByPolicyPermissionGroup();
            } else if (isForegroundPolicyFixed()) {
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$WKMGq9_s9bPqrYKpG5SjDN0thEQ
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return PermissionPreference.this.lambda$updateUi$5$PermissionPreference(preference, obj);
                    }
                });
                updateSummaryForFixedByPolicyPermissionGroup();
            } else {
                updateSummaryForPermissionGroupWithBackgroundPermission();
                setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$-56kZyYCMdqHfiYd9fPY-xYZb8s
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return PermissionPreference.this.lambda$updateUi$6$PermissionPreference(preference);
                    }
                });
                setSwitchOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$ZBHSQwUCSCCKfD_MYMSKLa4LbGQ
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PermissionPreference.this.lambda$updateUi$7$PermissionPreference(view);
                    }
                });
            }
        } else {
            setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$jGtdcjJYJTV3Emfq5Ha5Ok7HP2U
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return PermissionPreference.this.lambda$updateUi$8$PermissionPreference(preference, obj);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$updateUi$0$PermissionPreference(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, Preference preference) {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), enforcedAdmin);
        return true;
    }

    public /* synthetic */ boolean lambda$updateUi$1$PermissionPreference(Preference preference) {
        showAllPermissions(this.mGroup.getName());
        return false;
    }

    public /* synthetic */ void lambda$updateUi$2$PermissionPreference(View view) {
        requestChange(((Switch) view).isChecked(), 3);
        updateUi();
    }

    public /* synthetic */ boolean lambda$updateUi$3$PermissionPreference(Preference preference, Object obj) {
        return requestChange(((Boolean) obj).booleanValue(), 1);
    }

    public /* synthetic */ boolean lambda$updateUi$4$PermissionPreference(Preference preference, Object obj) {
        return requestChange(((Boolean) obj).booleanValue(), 1);
    }

    public /* synthetic */ boolean lambda$updateUi$5$PermissionPreference(Preference preference, Object obj) {
        return requestChange(((Boolean) obj).booleanValue(), 2);
    }

    public /* synthetic */ boolean lambda$updateUi$6$PermissionPreference(Preference preference) {
        showBackgroundChooserDialog();
        return true;
    }

    public /* synthetic */ void lambda$updateUi$7$PermissionPreference(View view) {
        if (((Switch) view).isChecked()) {
            showBackgroundChooserDialog();
        } else {
            requestChange(false, 3);
        }
        updateUi();
    }

    public /* synthetic */ boolean lambda$updateUi$8$PermissionPreference(Preference preference, Object obj) {
        return requestChange(((Boolean) obj).booleanValue(), 3);
    }

    private void updateSummaryForIndividuallyControlledPermissionGroup() {
        ArrayList<Permission> permissions = this.mGroup.getPermissions();
        int size = permissions.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (!permissions.get(i2).isGrantedIncludingAppOp()) {
                i++;
            }
        }
        setSummary(getContext().getString(i == 0 ? R.string.permission_revoked_none : i == size ? R.string.permission_revoked_all : R.string.permission_revoked_count, Integer.valueOf(i)));
    }

    private void updateSummaryForPermissionGroupWithBackgroundPermission() {
        AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
        if (!this.mGroup.areRuntimePermissionsGranted()) {
            setSummary(R.string.permission_access_never);
        } else if (backgroundPermissions == null) {
            setSummary(R.string.permission_access_only_foreground);
        } else if (backgroundPermissions.areRuntimePermissionsGranted()) {
            setSummary(R.string.permission_access_always);
        } else {
            setSummary(R.string.permission_access_only_foreground);
        }
    }

    private void updateSummaryForFixedByPolicyPermissionGroup() {
        RestrictedLockUtils.EnforcedAdmin admin = getAdmin();
        AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
        boolean z = admin != null;
        if (isSystemFixed()) {
            setSummary(R.string.permission_summary_enabled_system_fixed);
        } else if (isForegroundDisabledByPolicy()) {
            if (z) {
                setSummary(R.string.disabled_by_admin);
            } else {
                setSummary(R.string.permission_summary_enforced_by_policy);
            }
        } else if (isPolicyFullyFixed()) {
            if (backgroundPermissions == null) {
                if (z) {
                    setSummary(R.string.enabled_by_admin);
                } else {
                    setSummary(R.string.permission_summary_enforced_by_policy);
                }
            } else if (backgroundPermissions.areRuntimePermissionsGranted()) {
                if (z) {
                    setSummary(R.string.enabled_by_admin);
                } else {
                    setSummary(R.string.permission_summary_enforced_by_policy);
                }
            } else if (z) {
                setSummary(R.string.permission_summary_enabled_by_admin_foreground_only);
            } else {
                setSummary(R.string.permission_summary_enabled_by_policy_foreground_only);
            }
        } else if (isBackgroundPolicyFixed()) {
            if (backgroundPermissions.areRuntimePermissionsGranted()) {
                if (z) {
                    setSummary(R.string.permission_summary_enabled_by_admin_background_only);
                } else {
                    setSummary(R.string.permission_summary_enabled_by_policy_background_only);
                }
            } else if (z) {
                setSummary(R.string.permission_summary_disabled_by_admin_background_only);
            } else {
                setSummary(R.string.permission_summary_disabled_by_policy_background_only);
            }
        } else if (isForegroundPolicyFixed()) {
            if (z) {
                setSummary(R.string.permission_summary_enabled_by_admin_foreground_only);
            } else {
                setSummary(R.string.permission_summary_enabled_by_policy_foreground_only);
            }
        }
    }

    private void showAllPermissions(String str) {
        AllAppPermissionsFragment newInstance = AllAppPermissionsFragment.newInstance(this.mGroup.getApp().packageName, str, UserHandle.getUserHandleForUid(this.mGroup.getApp().applicationInfo.uid));
        FragmentTransaction beginTransaction = this.mFragment.getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack("AllPerms");
        beginTransaction.commit();
    }

    private String getAppLabel() {
        return BidiFormatter.getInstance().unicodeWrap(this.mGroup.getApp().applicationInfo.loadSafeLabel(getContext().getPackageManager(), 500.0f, 5).toString());
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.MultiTargetSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mIconSize > 0) {
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
            imageView.setMaxWidth(this.mIconSize);
            imageView.setMaxHeight(this.mIconSize);
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    private boolean requestChange(boolean z, int i) {
        if (LocationUtils.isLocationGroupAndProvider(getContext(), this.mGroup.getName(), this.mGroup.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), getAppLabel());
            return false;
        }
        if (z) {
            this.mCallBacks.onPreferenceChanged(getKey());
            if ((i & 1) != 0) {
                this.mGroup.grantRuntimePermissions(false);
            }
            if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
                this.mGroup.getBackgroundPermissions().grantRuntimePermissions(false);
            }
        } else {
            int i2 = i & 1;
            boolean hasGrantedByDefaultPermission = i2 != 0 ? this.mGroup.hasGrantedByDefaultPermission() : false;
            int i3 = i & 2;
            if (i3 != 0 && this.mGroup.getBackgroundPermissions() != null) {
                hasGrantedByDefaultPermission |= this.mGroup.getBackgroundPermissions().hasGrantedByDefaultPermission();
            }
            if ((hasGrantedByDefaultPermission || !this.mGroup.doesSupportRuntimePermissions()) && this.mCallBacks.shouldConfirmDefaultPermissionRevoke()) {
                showDefaultDenyDialog(i);
                return false;
            }
            this.mCallBacks.onPreferenceChanged(getKey());
            if (i2 != 0) {
                this.mGroup.revokeRuntimePermissions(false);
            }
            if (i3 != 0 && this.mGroup.getBackgroundPermissions() != null) {
                this.mGroup.getBackgroundPermissions().revokeRuntimePermissions(false);
            }
        }
        updateUi();
        return true;
    }

    private void showDefaultDenyDialog(int i) {
        if (this.mFragment.isResumed()) {
            Bundle bundle = new Bundle();
            boolean hasGrantedByDefaultPermission = (i & 1) != 0 ? this.mGroup.hasGrantedByDefaultPermission() : false;
            if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
                hasGrantedByDefaultPermission |= this.mGroup.getBackgroundPermissions().hasGrantedByDefaultPermission();
            }
            bundle.putInt(DefaultDenyDialog.MSG, hasGrantedByDefaultPermission ? R.string.system_warning : R.string.old_sdk_deny_warning);
            bundle.putString(DefaultDenyDialog.KEY, getKey());
            bundle.putInt(DefaultDenyDialog.CHANGE_TARGET, i);
            DefaultDenyDialog defaultDenyDialog = new DefaultDenyDialog();
            defaultDenyDialog.setArguments(bundle);
            defaultDenyDialog.show(this.mFragment.getChildFragmentManager().beginTransaction(), "denyDefault");
        }
    }

    private void showBackgroundChooserDialog() {
        if (this.mFragment.isResumed()) {
            if (LocationUtils.isLocationGroupAndProvider(getContext(), this.mGroup.getName(), this.mGroup.getApp().packageName)) {
                LocationUtils.showLocationDialog(getContext(), getAppLabel());
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence(BackgroundAccessChooser.TITLE, Utils.getRequestMessage(getAppLabel(), this.mGroup, getContext(), this.mGroup.getRequest()));
            bundle.putString(BackgroundAccessChooser.KEY, getKey());
            if (this.mGroup.areRuntimePermissionsGranted()) {
                if (this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                    bundle.putInt(BackgroundAccessChooser.SELECTION, 0);
                } else {
                    bundle.putInt(BackgroundAccessChooser.SELECTION, 1);
                }
            } else {
                bundle.putInt(BackgroundAccessChooser.SELECTION, 2);
            }
            BackgroundAccessChooser backgroundAccessChooser = new BackgroundAccessChooser();
            backgroundAccessChooser.setArguments(bundle);
            backgroundAccessChooser.show(this.mFragment.getChildFragmentManager().beginTransaction(), "backgroundChooser");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDenyAnyWay(int i) {
        boolean z;
        this.mCallBacks.onPreferenceChanged(getKey());
        if ((i & 1) != 0) {
            this.mGroup.revokeRuntimePermissions(false);
            z = this.mGroup.hasGrantedByDefaultPermission();
        } else {
            z = false;
        }
        if ((i & 2) != 0 && this.mGroup.getBackgroundPermissions() != null) {
            this.mGroup.getBackgroundPermissions().revokeRuntimePermissions(false);
            z |= this.mGroup.getBackgroundPermissions().hasGrantedByDefaultPermission();
        }
        if (z || !this.mGroup.doesSupportRuntimePermissions()) {
            this.mCallBacks.hasConfirmDefaultPermissionRevoke();
        }
        updateUi();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBackgroundAccessChosen(int i) {
        AppPermissionGroup backgroundPermissions = this.mGroup.getBackgroundPermissions();
        if (i == 0) {
            requestChange(true, 3);
        } else if (i == 1) {
            if (backgroundPermissions.areRuntimePermissionsGranted()) {
                requestChange(false, 2);
            }
            requestChange(true, 1);
        } else if (i != 2) {
        } else {
            if (this.mGroup.areRuntimePermissionsGranted() || this.mGroup.getBackgroundPermissions().areRuntimePermissionsGranted()) {
                requestChange(false, 3);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DefaultDenyDialog extends DialogFragment {
        private static final String MSG = DefaultDenyDialog.class.getName() + ".arg.msg";
        private static final String CHANGE_TARGET = DefaultDenyDialog.class.getName() + ".arg.changeTarget";
        private static final String KEY = DefaultDenyDialog.class.getName() + ".arg.key";

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getContext()).setMessage(getArguments().getInt(MSG)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$DefaultDenyDialog$9P6Pq5eJmBMH5fDJ-rbny24gJzs
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PermissionPreference.DefaultDenyDialog.this.lambda$onCreateDialog$0$PermissionPreference$DefaultDenyDialog(dialogInterface, i);
                }
            }).create();
        }

        public /* synthetic */ void lambda$onCreateDialog$0$PermissionPreference$DefaultDenyDialog(DialogInterface dialogInterface, int i) {
            ((PermissionPreferenceOwnerFragment) getParentFragment()).onDenyAnyWay(getArguments().getString(KEY), getArguments().getInt(CHANGE_TARGET));
        }
    }

    /* loaded from: classes.dex */
    public static class BackgroundAccessChooser extends DialogFragment {
        private static final String TITLE = BackgroundAccessChooser.class.getName() + ".arg.title";
        private static final String KEY = BackgroundAccessChooser.class.getName() + ".arg.key";
        private static final String SELECTION = BackgroundAccessChooser.class.getName() + ".arg.selection";

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setTitle(getArguments().getCharSequence(TITLE)).setSingleChoiceItems(R.array.background_access_chooser_dialog_choices, getArguments().getInt(SELECTION), new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionPreference$BackgroundAccessChooser$GtN81LlD6qg0QelfJiTNl9KXgFo
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PermissionPreference.BackgroundAccessChooser.this.lambda$onCreateDialog$0$PermissionPreference$BackgroundAccessChooser(dialogInterface, i);
                }
            }).create();
        }

        public /* synthetic */ void lambda$onCreateDialog$0$PermissionPreference$BackgroundAccessChooser(DialogInterface dialogInterface, int i) {
            dismissAllowingStateLoss();
            ((PermissionPreferenceOwnerFragment) getParentFragment()).onBackgroundAccessChosen(getArguments().getString(KEY), i);
        }
    }
}
