package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment.Parent;
import com.android.packageinstaller.role.ui.SpecialAppAccessViewModel;
import java.util.List;
/* loaded from: classes.dex */
public class SpecialAppAccessChildFragment<PF extends PreferenceFragmentCompat & Parent> extends Fragment implements Preference.OnPreferenceClickListener {
    private static final String PREFERENCE_EXTRA_APPLICATION_INFO = SpecialAppAccessChildFragment.class.getName() + ".extra.APPLICATION_INFO";
    private static final String PREFERENCE_KEY_DESCRIPTION = SpecialAppAccessChildFragment.class.getName() + ".preference.DESCRIPTION";
    private Role mRole;
    private String mRoleName;
    private SpecialAppAccessViewModel mViewModel;

    /* loaded from: classes.dex */
    public interface Parent {
        TwoStatePreference createApplicationPreference(Context context);

        Preference createFooterPreference(Context context);

        void onPreferenceScreenChanged();

        void setTitle(CharSequence charSequence);
    }

    public static /* synthetic */ boolean lambda$onRoleChanged$0(Preference preference, Object obj) {
        return false;
    }

    public static SpecialAppAccessChildFragment newInstance(String str) {
        SpecialAppAccessChildFragment specialAppAccessChildFragment = new SpecialAppAccessChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        specialAppAccessChildFragment.setArguments(bundle);
        return specialAppAccessChildFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mRoleName = getArguments().getString("android.intent.extra.ROLE_NAME");
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        PF requirePreferenceFragment = requirePreferenceFragment();
        FragmentActivity requireActivity = requireActivity();
        this.mRole = Roles.get(requireActivity).get(this.mRoleName);
        requirePreferenceFragment.setTitle(getString(this.mRole.getLabelResource()));
        this.mViewModel = (SpecialAppAccessViewModel) ViewModelProviders.of(this, new SpecialAppAccessViewModel.Factory(this.mRole, requireActivity.getApplication())).get(SpecialAppAccessViewModel.class);
        this.mViewModel.getRoleLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessChildFragment$FLOd-CFXVvpG19Vv3lQHG74xYcY
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SpecialAppAccessChildFragment.this.onRoleChanged((List) obj);
            }
        });
        this.mViewModel.observeManageRoleHolderState(this, new $$Lambda$SpecialAppAccessChildFragment$hWv9qfpy_nOghbeLQxG55hydAls(this));
    }

    public void onRoleChanged(List<Pair<ApplicationInfo, Boolean>> list) {
        Preference findPreference;
        PF requirePreferenceFragment = requirePreferenceFragment();
        PreferenceManager preferenceManager = requirePreferenceFragment.getPreferenceManager();
        Context context = preferenceManager.getContext();
        PreferenceScreen preferenceScreen = requirePreferenceFragment.getPreferenceScreen();
        ArrayMap arrayMap = new ArrayMap();
        if (preferenceScreen == null) {
            preferenceScreen = preferenceManager.createPreferenceScreen(context);
            requirePreferenceFragment.setPreferenceScreen(preferenceScreen);
            findPreference = null;
        } else {
            findPreference = preferenceScreen.findPreference(PREFERENCE_KEY_DESCRIPTION);
            if (findPreference != null) {
                preferenceScreen.removePreference(findPreference);
                findPreference.setOrder(Preference.DEFAULT_ORDER);
            }
            for (int preferenceCount = preferenceScreen.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceScreen.getPreference(preferenceCount);
                preferenceScreen.removePreference(preference);
                preference.setOrder(Preference.DEFAULT_ORDER);
                arrayMap.put(preference.getKey(), preference);
            }
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Pair<ApplicationInfo, Boolean> pair = list.get(i);
            ApplicationInfo applicationInfo = (ApplicationInfo) pair.first;
            boolean booleanValue = ((Boolean) pair.second).booleanValue();
            String str = applicationInfo.packageName + '_' + applicationInfo.uid;
            TwoStatePreference twoStatePreference = (TwoStatePreference) arrayMap.get(str);
            if (twoStatePreference == null) {
                twoStatePreference = requirePreferenceFragment.createApplicationPreference(context);
                twoStatePreference.setKey(str);
                twoStatePreference.setIcon(Utils.getBadgedIcon(context, applicationInfo));
                twoStatePreference.setTitle(Utils.getFullAppLabel(applicationInfo, context));
                twoStatePreference.setPersistent(false);
                twoStatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessChildFragment$qbL6k0b8XzMBE1Sz5UCKEWPU02s
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference2, Object obj) {
                        return SpecialAppAccessChildFragment.lambda$onRoleChanged$0(preference2, obj);
                    }
                });
                twoStatePreference.setOnPreferenceClickListener(this);
                twoStatePreference.getExtras().putParcelable(PREFERENCE_EXTRA_APPLICATION_INFO, applicationInfo);
            }
            twoStatePreference.setChecked(booleanValue);
            this.mRole.prepareApplicationPreferenceAsUser(twoStatePreference, applicationInfo, UserHandle.getUserHandleForUid(applicationInfo.uid), context);
            preferenceScreen.addPreference(twoStatePreference);
        }
        if (findPreference == null) {
            findPreference = requirePreferenceFragment.createFooterPreference(context);
            findPreference.setKey(PREFERENCE_KEY_DESCRIPTION);
            findPreference.setSummary(this.mRole.getDescriptionResource());
        }
        preferenceScreen.addPreference(findPreference);
        requirePreferenceFragment.onPreferenceScreenChanged();
    }

    public void onManageRoleHolderStateChanged(ManageRoleHolderStateLiveData manageRoleHolderStateLiveData, int i) {
        if (i != 2) {
            if (i != 3) {
                return;
            }
            manageRoleHolderStateLiveData.resetState();
            return;
        }
        String lastPackageName = manageRoleHolderStateLiveData.getLastPackageName();
        if (lastPackageName != null && manageRoleHolderStateLiveData.isLastAdd()) {
            this.mRole.onHolderSelectedAsUser(lastPackageName, manageRoleHolderStateLiveData.getLastUser(), requireContext());
        }
        manageRoleHolderStateLiveData.resetState();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        ApplicationInfo applicationInfo = (ApplicationInfo) preference.getExtras().getParcelable(PREFERENCE_EXTRA_APPLICATION_INFO);
        String str = applicationInfo.packageName;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(applicationInfo.uid);
        String key = preference.getKey();
        this.mViewModel.setSpecialAppAccessAsUser(str, !((TwoStatePreference) preference).isChecked(), userHandleForUid, key, this, new $$Lambda$SpecialAppAccessChildFragment$hWv9qfpy_nOghbeLQxG55hydAls(this));
        return true;
    }

    private PF requirePreferenceFragment() {
        return (PF) ((PreferenceFragmentCompat) requireParentFragment());
    }
}
