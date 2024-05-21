package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment.Parent;
import java.util.List;
/* loaded from: classes.dex */
public class SpecialAppAccessListChildFragment<PF extends PreferenceFragmentCompat & Parent> extends Fragment implements Preference.OnPreferenceClickListener {
    private SpecialAppAccessListViewModel mViewModel;

    /* loaded from: classes.dex */
    public interface Parent {
        TwoTargetPreference createPreference(Context context);

        void onPreferenceScreenChanged();
    }

    public static SpecialAppAccessListChildFragment newInstance() {
        return new SpecialAppAccessListChildFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mViewModel = (SpecialAppAccessListViewModel) ViewModelProviders.of(this).get(SpecialAppAccessListViewModel.class);
        this.mViewModel.getLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessListChildFragment$q-Zw2y2ZGVzrv7S7xZ8Rxas2lnk
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SpecialAppAccessListChildFragment.this.lambda$onActivityCreated$0$SpecialAppAccessListChildFragment((List) obj);
            }
        });
    }

    public /* synthetic */ void lambda$onActivityCreated$0$SpecialAppAccessListChildFragment(List list) {
        onRoleListChanged();
    }

    private void onRoleListChanged() {
        List<RoleItem> value = this.mViewModel.getLiveData().getValue();
        if (value == null) {
            return;
        }
        PF requirePreferenceFragment = requirePreferenceFragment();
        PreferenceManager preferenceManager = requirePreferenceFragment.getPreferenceManager();
        Context context = preferenceManager.getContext();
        PreferenceScreen preferenceScreen = requirePreferenceFragment.getPreferenceScreen();
        ArrayMap arrayMap = new ArrayMap();
        if (preferenceScreen == null) {
            preferenceScreen = preferenceManager.createPreferenceScreen(context);
            requirePreferenceFragment.setPreferenceScreen(preferenceScreen);
        } else {
            for (int preferenceCount = preferenceScreen.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceScreen.getPreference(preferenceCount);
                preferenceScreen.removePreference(preference);
                preference.setOrder(Preference.DEFAULT_ORDER);
                arrayMap.put(preference.getKey(), preference);
            }
        }
        int size = value.size();
        for (int i = 0; i < size; i++) {
            Role role = value.get(i).getRole();
            TwoTargetPreference twoTargetPreference = (TwoTargetPreference) arrayMap.get(role.getName());
            if (twoTargetPreference == null) {
                twoTargetPreference = requirePreferenceFragment.createPreference(context);
                twoTargetPreference.setKey(role.getName());
                twoTargetPreference.setIconSpaceReserved(true);
                twoTargetPreference.setTitle(role.getShortLabelResource());
                twoTargetPreference.setPersistent(false);
                twoTargetPreference.setOnPreferenceClickListener(this);
            }
            role.preparePreferenceAsUser(twoTargetPreference, Process.myUserHandle(), context);
            preferenceScreen.addPreference(twoTargetPreference);
        }
        requirePreferenceFragment.onPreferenceScreenChanged();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Context requireContext = requireContext();
        Intent manageIntentAsUser = Roles.get(requireContext).get(key).getManageIntentAsUser(Process.myUserHandle(), requireContext);
        if (manageIntentAsUser == null) {
            manageIntentAsUser = SpecialAppAccessActivity.createIntent(key, requireContext);
        }
        startActivity(manageIntentAsUser);
        return true;
    }

    private PF requirePreferenceFragment() {
        return (PF) ((PreferenceFragmentCompat) requireParentFragment());
    }
}
