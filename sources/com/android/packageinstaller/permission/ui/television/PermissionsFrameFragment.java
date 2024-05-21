package com.android.packageinstaller.permission.ui.television;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.leanback.widget.VerticalGridView;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public abstract class PermissionsFrameFragment extends PreferenceFragment {
    private RecyclerView mGridView;
    private boolean mIsLoading;
    private View mLoadingView;
    private ViewGroup mPreferencesContainer;
    private ViewGroup mPrefsView;

    protected void onSetEmptyText(TextView textView) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ViewGroup getPreferencesContainer() {
        return this.mPreferencesContainer;
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R.layout.permissions_frame, viewGroup, false);
        this.mPrefsView = (ViewGroup) viewGroup2.findViewById(R.id.prefs_container);
        if (this.mPrefsView == null) {
            this.mPrefsView = viewGroup2;
        }
        this.mLoadingView = viewGroup2.findViewById(R.id.loading_container);
        this.mPreferencesContainer = (ViewGroup) super.onCreateView(layoutInflater, this.mPrefsView, bundle);
        setLoading(this.mIsLoading, false, true);
        this.mPrefsView.addView(this.mPreferencesContainer);
        return viewGroup2;
    }

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        if (getPreferenceScreen() == null) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
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

    private void setViewShown(final View view, boolean z, boolean z2) {
        if (z2) {
            Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), z ? 17432576 : 17432577);
            if (z) {
                view.setVisibility(0);
            } else {
                loadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment.1
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

    @Override // androidx.preference.PreferenceFragment
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        VerticalGridView verticalGridView = (VerticalGridView) layoutInflater.inflate(R.layout.leanback_preferences_list, viewGroup, false);
        verticalGridView.setWindowAlignment(3);
        verticalGridView.setFocusScrollStrategy(0);
        this.mGridView = verticalGridView;
        return this.mGridView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragment
    public RecyclerView.Adapter<?> onCreateAdapter(PreferenceScreen preferenceScreen) {
        RecyclerView recyclerView;
        final RecyclerView.Adapter<?> onCreateAdapter = super.onCreateAdapter(preferenceScreen);
        if (onCreateAdapter != null) {
            final TextView textView = (TextView) getView().findViewById(R.id.no_permissions);
            textView.setText(R.string.no_permissions);
            onSetEmptyText(textView);
            final RecyclerView listView = getListView();
            onCreateAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment.2
                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onChanged() {
                    checkEmpty();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeInserted(int i, int i2) {
                    checkEmpty();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeRemoved(int i, int i2) {
                    checkEmpty();
                }

                private void checkEmpty() {
                    boolean isPreferenceListEmpty = PermissionsFrameFragment.this.isPreferenceListEmpty();
                    int i = 0;
                    textView.setVisibility(isPreferenceListEmpty ? 0 : 8);
                    RecyclerView recyclerView2 = listView;
                    if (isPreferenceListEmpty && onCreateAdapter.getItemCount() == 0) {
                        i = 8;
                    }
                    recyclerView2.setVisibility(i);
                    if (isPreferenceListEmpty || PermissionsFrameFragment.this.mGridView == null) {
                        return;
                    }
                    PermissionsFrameFragment.this.mGridView.requestFocus();
                }
            });
            boolean isPreferenceListEmpty = isPreferenceListEmpty();
            int i = 0;
            textView.setVisibility(isPreferenceListEmpty ? 0 : 8);
            if (isPreferenceListEmpty && onCreateAdapter.getItemCount() == 0) {
                i = 8;
            }
            listView.setVisibility(i);
            if (!isPreferenceListEmpty && (recyclerView = this.mGridView) != null) {
                recyclerView.requestFocus();
            }
        }
        return onCreateAdapter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPreferenceListEmpty() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen.getPreferenceCount() != 0) {
            return preferenceScreen.getPreferenceCount() == 1 && preferenceScreen.findPreference("HeaderPreferenceKey") != null;
        }
        return true;
    }
}
