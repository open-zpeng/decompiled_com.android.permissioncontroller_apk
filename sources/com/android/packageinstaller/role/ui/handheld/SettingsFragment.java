package com.android.packageinstaller.role.ui.handheld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.utils.UiUtils;
import com.android.settingslib.HelpUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class SettingsFragment extends PreferenceFragmentCompat {
    private FrameLayout mContentLayout;
    private TextView mEmptyText;
    private View mLoadingView;
    private LinearLayout mPreferenceLayout;

    protected abstract int getEmptyTextResource();

    protected int getHelpUriResource() {
        return 0;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mContentLayout = (FrameLayout) layoutInflater.inflate(R.layout.settings, viewGroup, false);
        this.mPreferenceLayout = (LinearLayout) super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mContentLayout.addView(this.mPreferenceLayout);
        return this.mContentLayout;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mLoadingView = this.mContentLayout.findViewById(R.id.loading);
        this.mEmptyText = (TextView) this.mContentLayout.findViewById(R.id.empty);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        this.mEmptyText.setText(getEmptyTextResource());
        updateState();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        Utils.prepareSearchMenuItem(menu, requireContext());
        int helpUriResource = getHelpUriResource();
        if (helpUriResource != 0) {
            HelpUtils.prepareHelpMenuItem(requireActivity(), menu, helpUriResource, getClass().getName());
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            requireActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateState() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        boolean z = true;
        UiUtils.setViewShown(this.mLoadingView, preferenceScreen == null);
        if (preferenceScreen == null || preferenceScreen.getPreferenceCount() != 0) {
            z = false;
        }
        UiUtils.setViewShown(this.mEmptyText, z);
    }
}
