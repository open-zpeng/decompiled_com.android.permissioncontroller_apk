package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.widget.ActionBarShadowController;
/* loaded from: classes.dex */
public abstract class PermissionsFrameFragment extends PreferenceFragmentCompat {
    private TextView mEmptyView;
    private boolean mIsLoading;
    private View mLoadingView;
    private NestedScrollView mNestedScrollView;
    private ViewGroup mPreferencesContainer;
    private ViewGroup mPrefsView;
    private View mProgressHeader;
    private View mProgressView;

    public int getEmptyViewString() {
        return R.string.no_permissions;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ViewGroup getPreferencesContainer() {
        return this.mPreferencesContainer;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        Utils.prepareSearchMenuItem(menu, requireContext());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R.layout.permissions_frame, viewGroup, false);
        this.mPrefsView = (ViewGroup) viewGroup2.findViewById(R.id.prefs_container);
        if (this.mPrefsView == null) {
            this.mPrefsView = viewGroup2;
        }
        this.mEmptyView = (TextView) this.mPrefsView.findViewById(R.id.no_permissions);
        this.mEmptyView.setText(getEmptyViewString());
        this.mLoadingView = viewGroup2.findViewById(R.id.loading_container);
        this.mPreferencesContainer = (ViewGroup) super.onCreateView(layoutInflater, this.mPrefsView, bundle);
        setLoading(this.mIsLoading, false, true);
        this.mPrefsView.addView(this.mPreferencesContainer, 0);
        this.mNestedScrollView = (NestedScrollView) viewGroup2.requireViewById(R.id.nested_scroll_view);
        this.mProgressHeader = viewGroup2.requireViewById(R.id.progress_bar_animation);
        this.mProgressView = viewGroup2.requireViewById(R.id.progress_bar_background);
        setProgressBarVisible(false);
        getListView().setFocusable(false);
        return viewGroup2;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mNestedScrollView != null) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setElevation(0.0f);
            }
            ActionBarShadowController.attachToView(getActivity(), getLifecycle(), this.mNestedScrollView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setLoading(boolean z, boolean z2) {
        setLoading(z, z2, false);
    }

    private void setLoading(boolean z, boolean z2, boolean z3) {
        if (this.mIsLoading != z || z3) {
            this.mIsLoading = z;
            if (getView() == null) {
                z2 = false;
            }
            ViewGroup viewGroup = this.mPrefsView;
            if (viewGroup != null) {
                setViewShown(viewGroup, !z, z2);
            }
            View view = this.mLoadingView;
            if (view != null) {
                setViewShown(view, z, z2);
            }
        }
    }

    protected void setProgressBarVisible(boolean z) {
        this.mProgressHeader.setVisibility(z ? 0 : 8);
        this.mProgressView.setVisibility(z ? 0 : 8);
    }

    void updateEmptyState() {
        RecyclerView listView = getListView();
        if (this.mEmptyView == null || listView == null) {
            return;
        }
        if (listView.getAdapter() != null && listView.getAdapter().getItemCount() != 0) {
            this.mEmptyView.setVisibility(8);
            listView.setVisibility(0);
            return;
        }
        this.mEmptyView.setVisibility(0);
        listView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
        RecyclerView.Adapter adapter = getListView().getAdapter();
        if (adapter != null) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment.1
                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onChanged() {
                    PermissionsFrameFragment.this.updateEmptyState();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeInserted(int i, int i2) {
                    PermissionsFrameFragment.this.updateEmptyState();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeRemoved(int i, int i2) {
                    PermissionsFrameFragment.this.updateEmptyState();
                }
            });
        }
        updateEmptyState();
    }

    private void setViewShown(final View view, boolean z, boolean z2) {
        if (z2) {
            Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), z ? 17432576 : 17432577);
            if (z) {
                view.setVisibility(0);
            } else {
                loadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment.2
                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(4);
                    }
                });
            }
            view.startAnimation(loadAnimation);
            return;
        }
        view.clearAnimation();
        view.setVisibility(z ? 0 : 4);
    }
}
