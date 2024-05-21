package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* loaded from: classes.dex */
public class AutoSpecialAppAccessListFragment extends AutoSettingsFrameFragment implements SpecialAppAccessListChildFragment.Parent {
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment.Parent
    public void onPreferenceScreenChanged() {
    }

    public static AutoSpecialAppAccessListFragment newInstance() {
        return new AutoSpecialAppAccessListFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            SpecialAppAccessListChildFragment newInstance = SpecialAppAccessListChildFragment.newInstance();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, (String) null);
            beginTransaction.commit();
        }
        setHeaderLabel(getString(R.string.special_app_access));
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment.Parent
    public TwoTargetPreference createPreference(Context context) {
        return new AutoSettingsPreference(context);
    }
}
