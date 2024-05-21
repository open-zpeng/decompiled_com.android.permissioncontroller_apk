package com.android.car.ui.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.android.car.ui.R;
import com.android.car.ui.toolbar.TabLayout;
import java.util.List;
/* loaded from: classes.dex */
public class Toolbar extends FrameLayout implements ToolbarController {
    private static final String TAG = "CarUiToolbar";
    private ToolbarControllerImpl mController;
    private boolean mEatingHover;
    private boolean mEatingTouch;

    /* loaded from: classes.dex */
    public enum NavButtonMode {
        BACK,
        CLOSE,
        DOWN
    }

    /* loaded from: classes.dex */
    public interface OnBackListener {
        boolean onBack();
    }

    /* loaded from: classes.dex */
    public interface OnHeightChangedListener {
        void onHeightChanged(int i);
    }

    /* loaded from: classes.dex */
    public interface OnSearchCompletedListener {
        void onSearchCompleted();
    }

    /* loaded from: classes.dex */
    public interface OnSearchListener {
        void onSearch(String str);
    }

    /* loaded from: classes.dex */
    public interface OnTabSelectedListener {
        void onTabSelected(TabLayout.Tab tab);
    }

    /* loaded from: classes.dex */
    public enum State {
        HOME,
        SUBPAGE,
        SEARCH,
        EDIT
    }

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.CarUiToolbarStyle);
    }

    public Toolbar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public Toolbar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mEatingTouch = false;
        this.mEatingHover = false;
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(getToolbarLayout(), (ViewGroup) this, true);
        this.mController = new ToolbarControllerImpl(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CarUiToolbar, i, i2);
        try {
            setShowTabsInSubpage(obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbar_showTabsInSubpage, false));
            setTitle(obtainStyledAttributes.getString(R.styleable.CarUiToolbar_title));
            setLogo(obtainStyledAttributes.getResourceId(R.styleable.CarUiToolbar_logo, 0));
            setBackgroundShown(obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbar_showBackground, true));
            setMenuItems(obtainStyledAttributes.getResourceId(R.styleable.CarUiToolbar_menuItems, 0));
            String string = obtainStyledAttributes.getString(R.styleable.CarUiToolbar_searchHint);
            if (string != null) {
                setSearchHint(string);
            }
            int i3 = obtainStyledAttributes.getInt(R.styleable.CarUiToolbar_state, 0);
            if (i3 == 0) {
                setState(State.HOME);
            } else if (i3 == 1) {
                setState(State.SUBPAGE);
            } else if (i3 == 2) {
                setState(State.SEARCH);
            } else if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Unknown initial state");
            }
            int i4 = obtainStyledAttributes.getInt(R.styleable.CarUiToolbar_navButtonMode, 0);
            if (i4 == 0) {
                setNavButtonMode(NavButtonMode.BACK);
            } else if (i4 == 1) {
                setNavButtonMode(NavButtonMode.CLOSE);
            } else if (i4 == 2) {
                setNavButtonMode(NavButtonMode.DOWN);
            } else if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Unknown navigation button style");
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    protected int getToolbarLayout() {
        if (getContext().getResources().getBoolean(R.bool.car_ui_toolbar_tabs_on_second_row)) {
            return R.layout.car_ui_toolbar_two_row;
        }
        return R.layout.car_ui_toolbar;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean isTabsInSecondRow() {
        return this.mController.isTabsInSecondRow();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setTitle(int i) {
        this.mController.setTitle(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setTitle(CharSequence charSequence) {
        this.mController.setTitle(charSequence);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public CharSequence getTitle() {
        return this.mController.getTitle();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public TabLayout getTabLayout() {
        return this.mController.getTabLayout();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void addTab(TabLayout.Tab tab) {
        this.mController.addTab(tab);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void clearAllTabs() {
        this.mController.clearAllTabs();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public TabLayout.Tab getTab(int i) {
        return this.mController.getTab(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void selectTab(int i) {
        this.mController.selectTab(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setShowTabsInSubpage(boolean z) {
        this.mController.setShowTabsInSubpage(z);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getShowTabsInSubpage() {
        return this.mController.getShowTabsInSubpage();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setLogo(int i) {
        this.mController.setLogo(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setLogo(Drawable drawable) {
        this.mController.setLogo(drawable);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchHint(int i) {
        this.mController.setSearchHint(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchHint(CharSequence charSequence) {
        this.mController.setSearchHint(charSequence);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public CharSequence getSearchHint() {
        return this.mController.getSearchHint();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchIcon(int i) {
        this.mController.setSearchIcon(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchIcon(Drawable drawable) {
        this.mController.setSearchIcon(drawable);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setNavButtonMode(NavButtonMode navButtonMode) {
        this.mController.setNavButtonMode(navButtonMode);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public NavButtonMode getNavButtonMode() {
        return this.mController.getNavButtonMode();
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        throw new UnsupportedOperationException("You can not change the background of a CarUi toolbar, use setBackgroundShown(boolean) or an RRO instead.");
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setBackgroundShown(boolean z) {
        this.mController.setBackgroundShown(z);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getBackgroundShown() {
        return this.mController.getBackgroundShown();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setMenuItems(List<MenuItem> list) {
        this.mController.setMenuItems(list);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public List<MenuItem> setMenuItems(int i) {
        return this.mController.setMenuItems(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public List<MenuItem> getMenuItems() {
        return this.mController.getMenuItems();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public MenuItem findMenuItemById(int i) {
        return this.mController.findMenuItemById(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public MenuItem requireMenuItemById(int i) {
        return this.mController.requireMenuItemById(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setShowMenuItemsWhileSearching(boolean z) {
        this.mController.setShowMenuItemsWhileSearching(z);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getShowMenuItemsWhileSearching() {
        return this.mController.getShowMenuItemsWhileSearching();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchQuery(String str) {
        this.mController.setSearchQuery(str);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setState(State state) {
        this.mController.setState(state);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public State getState() {
        return this.mController.getState();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mEatingTouch = false;
        }
        if (!this.mEatingTouch) {
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (actionMasked == 0 && !onTouchEvent) {
                this.mEatingTouch = true;
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mEatingTouch = false;
        }
        return true;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 9) {
            this.mEatingHover = false;
        }
        if (!this.mEatingHover) {
            boolean onHoverEvent = super.onHoverEvent(motionEvent);
            if (actionMasked == 9 && !onHoverEvent) {
                this.mEatingHover = true;
            }
        }
        if (actionMasked == 10 || actionMasked == 3) {
            this.mEatingHover = false;
        }
        return true;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerToolbarHeightChangeListener(OnHeightChangedListener onHeightChangedListener) {
        this.mController.registerToolbarHeightChangeListener(onHeightChangedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterToolbarHeightChangeListener(OnHeightChangedListener onHeightChangedListener) {
        return this.mController.unregisterToolbarHeightChangeListener(onHeightChangedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.mController.registerOnTabSelectedListener(onTabSelectedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        return this.mController.unregisterOnTabSelectedListener(onTabSelectedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnSearchListener(OnSearchListener onSearchListener) {
        this.mController.registerOnSearchListener(onSearchListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnSearchListener(OnSearchListener onSearchListener) {
        return this.mController.unregisterOnSearchListener(onSearchListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnSearchCompletedListener(OnSearchCompletedListener onSearchCompletedListener) {
        this.mController.registerOnSearchCompletedListener(onSearchCompletedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnSearchCompletedListener(OnSearchCompletedListener onSearchCompletedListener) {
        return this.mController.unregisterOnSearchCompletedListener(onSearchCompletedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnBackListener(OnBackListener onBackListener) {
        this.mController.registerOnBackListener(onBackListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnBackListener(OnBackListener onBackListener) {
        return this.mController.unregisterOnBackListener(onBackListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void showProgressBar() {
        this.mController.showProgressBar();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void hideProgressBar() {
        this.mController.hideProgressBar();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public ProgressBar getProgressBar() {
        return this.mController.getProgressBar();
    }
}
