package com.android.car.ui.toolbar;

import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;
import com.android.car.ui.toolbar.TabLayout;
import com.android.car.ui.toolbar.Toolbar;
import java.util.List;
/* loaded from: classes.dex */
public interface ToolbarController {
    void addTab(TabLayout.Tab tab);

    void clearAllTabs();

    MenuItem findMenuItemById(int i);

    boolean getBackgroundShown();

    List<MenuItem> getMenuItems();

    Toolbar.NavButtonMode getNavButtonMode();

    ProgressBar getProgressBar();

    CharSequence getSearchHint();

    boolean getShowMenuItemsWhileSearching();

    boolean getShowTabsInSubpage();

    Toolbar.State getState();

    TabLayout.Tab getTab(int i);

    TabLayout getTabLayout();

    CharSequence getTitle();

    void hideProgressBar();

    boolean isTabsInSecondRow();

    void registerOnBackListener(Toolbar.OnBackListener onBackListener);

    void registerOnSearchCompletedListener(Toolbar.OnSearchCompletedListener onSearchCompletedListener);

    void registerOnSearchListener(Toolbar.OnSearchListener onSearchListener);

    void registerOnTabSelectedListener(Toolbar.OnTabSelectedListener onTabSelectedListener);

    void registerToolbarHeightChangeListener(Toolbar.OnHeightChangedListener onHeightChangedListener);

    MenuItem requireMenuItemById(int i);

    void selectTab(int i);

    void setBackgroundShown(boolean z);

    void setLogo(int i);

    void setLogo(Drawable drawable);

    List<MenuItem> setMenuItems(int i);

    void setMenuItems(List<MenuItem> list);

    void setNavButtonMode(Toolbar.NavButtonMode navButtonMode);

    void setSearchHint(int i);

    void setSearchHint(CharSequence charSequence);

    void setSearchIcon(int i);

    void setSearchIcon(Drawable drawable);

    void setSearchQuery(String str);

    void setShowMenuItemsWhileSearching(boolean z);

    void setShowTabsInSubpage(boolean z);

    void setState(Toolbar.State state);

    void setTitle(int i);

    void setTitle(CharSequence charSequence);

    void showProgressBar();

    boolean unregisterOnBackListener(Toolbar.OnBackListener onBackListener);

    boolean unregisterOnSearchCompletedListener(Toolbar.OnSearchCompletedListener onSearchCompletedListener);

    boolean unregisterOnSearchListener(Toolbar.OnSearchListener onSearchListener);

    boolean unregisterOnTabSelectedListener(Toolbar.OnTabSelectedListener onTabSelectedListener);

    boolean unregisterToolbarHeightChangeListener(Toolbar.OnHeightChangedListener onHeightChangedListener);
}
