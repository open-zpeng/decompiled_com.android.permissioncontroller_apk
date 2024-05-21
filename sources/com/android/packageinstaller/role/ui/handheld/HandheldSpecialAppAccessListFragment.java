package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentTransaction;
import com.android.car.ui.R;
import com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* loaded from: classes.dex */
public class HandheldSpecialAppAccessListFragment extends SettingsFragment implements SpecialAppAccessListChildFragment.Parent {
    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment
    protected int getEmptyTextResource() {
        return R.string.no_special_app_access;
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment
    protected int getHelpUriResource() {
        return R.string.help_uri_special_app_access;
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat
    public /* bridge */ /* synthetic */ void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public static HandheldSpecialAppAccessListFragment newInstance() {
        return new HandheldSpecialAppAccessListFragment();
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            SpecialAppAccessListChildFragment newInstance = SpecialAppAccessListChildFragment.newInstance();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, (String) null);
            beginTransaction.commit();
        }
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment.Parent
    public TwoTargetPreference createPreference(Context context) {
        return new AppIconSettingsButtonPreference(context);
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessListChildFragment.Parent
    public void onPreferenceScreenChanged() {
        updateState();
    }
}