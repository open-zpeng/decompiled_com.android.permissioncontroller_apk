package com.android.packageinstaller.auto;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.car.ui.R;
import com.android.car.ui.preference.PreferenceFragment;
import com.android.car.ui.toolbar.MenuItem;
import com.android.car.ui.toolbar.ToolbarController;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public abstract class AutoSettingsFrameFragment extends PreferenceFragment {
    private CharSequence mActionLabel;
    private View.OnClickListener mActionOnClickListener;
    private boolean mIsLoading;
    private CharSequence mLabel;
    private ToolbarController mToolbar;

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mToolbar = (ToolbarController) onCreateView.findViewById(R.id.toolbar);
        updateHeaderLabel();
        updateLoading();
        updateAction();
        return onCreateView;
    }

    public void setHeaderLabel(CharSequence charSequence) {
        this.mLabel = charSequence;
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().setTitle(this.mLabel);
        }
        updateHeaderLabel();
    }

    public CharSequence getHeaderLabel() {
        return this.mLabel;
    }

    private void updateHeaderLabel() {
        ToolbarController toolbarController = this.mToolbar;
        if (toolbarController != null) {
            toolbarController.setTitle(this.mLabel);
        }
    }

    public void setLoading(boolean z) {
        this.mIsLoading = z;
        updateLoading();
    }

    private void updateLoading() {
        ToolbarController toolbarController = this.mToolbar;
        if (toolbarController != null) {
            if (this.mIsLoading) {
                toolbarController.showProgressBar();
            } else {
                toolbarController.hideProgressBar();
            }
        }
    }

    public void setAction(CharSequence charSequence, View.OnClickListener onClickListener) {
        this.mActionLabel = charSequence;
        this.mActionOnClickListener = onClickListener;
        updateAction();
    }

    private void updateAction() {
        if (this.mToolbar == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.mActionLabel) && this.mActionOnClickListener != null) {
            this.mToolbar.setMenuItems(Collections.singletonList(MenuItem.builder(getContext()).setTitle(this.mActionLabel).setOnClickListener(new MenuItem.OnClickListener() { // from class: com.android.packageinstaller.auto.-$$Lambda$AutoSettingsFrameFragment$Xrh4b46-RE-bgNvQmizP978wo9E
                @Override // com.android.car.ui.toolbar.MenuItem.OnClickListener
                public final void onClick(MenuItem menuItem) {
                    AutoSettingsFrameFragment.this.lambda$updateAction$0$AutoSettingsFrameFragment(menuItem);
                }
            }).build()));
        } else {
            this.mToolbar.setMenuItems((List<MenuItem>) null);
        }
    }

    public /* synthetic */ void lambda$updateAction$0$AutoSettingsFrameFragment(MenuItem menuItem) {
        this.mActionOnClickListener.onClick(null);
    }
}
