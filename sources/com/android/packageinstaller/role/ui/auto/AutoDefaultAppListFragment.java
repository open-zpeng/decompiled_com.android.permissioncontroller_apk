package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.role.ui.DefaultAppListChildFragment;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* loaded from: classes.dex */
public class AutoDefaultAppListFragment extends AutoSettingsFrameFragment implements DefaultAppListChildFragment.Parent {
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppListChildFragment.Parent
    public void onPreferenceScreenChanged() {
    }

    public static AutoDefaultAppListFragment newInstance() {
        return new AutoDefaultAppListFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            DefaultAppListChildFragment newInstance = DefaultAppListChildFragment.newInstance();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, (String) null);
            beginTransaction.commit();
        }
        setHeaderLabel(getString(R.string.default_apps));
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppListChildFragment.Parent
    public TwoTargetPreference createPreference(Context context) {
        return new AutoSettingsPreference(context);
    }
}
