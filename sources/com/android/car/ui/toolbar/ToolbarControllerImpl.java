package com.android.car.ui.toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.util.Consumer;
import com.android.car.ui.R;
import com.android.car.ui.toolbar.MenuItem;
import com.android.car.ui.toolbar.TabLayout;
import com.android.car.ui.toolbar.Toolbar;
import com.android.car.ui.utils.CarUiUtils;
import com.android.car.ui.utils.CarUxRestrictionsUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class ToolbarControllerImpl implements ToolbarController {
    private static final String TAG = "CarUiToolbarController";
    private View mBackground;
    private final Context mContext;
    private ImageView mLogoInNavIconSpace;
    private View[] mMenuItemViews;
    private ViewGroup mMenuItemsContainer;
    private ImageView mNavIcon;
    private ViewGroup mNavIconContainer;
    private AlertDialog mOverflowDialog;
    private ProgressBar mProgressBar;
    private CharSequence mSearchHint;
    private Drawable mSearchIcon;
    private String mSearchQuery;
    private SearchView mSearchView;
    private FrameLayout mSearchViewContainer;
    private boolean mShowMenuItemsWhileSearching;
    private TabLayout mTabLayout;
    private TextView mTitle;
    private ImageView mTitleLogo;
    private ViewGroup mTitleLogoContainer;
    private final Set<Toolbar.OnSearchListener> mOnSearchListeners = new HashSet();
    private final Set<Toolbar.OnSearchCompletedListener> mOnSearchCompletedListeners = new HashSet();
    private final Set<Toolbar.OnBackListener> mOnBackListeners = new HashSet();
    private final Set<Toolbar.OnTabSelectedListener> mOnTabSelectedListeners = new HashSet();
    private final Set<Toolbar.OnHeightChangedListener> mOnHeightChangedListeners = new HashSet();
    private boolean mShowTabsInSubpage = false;
    private boolean mHasLogo = false;
    private Toolbar.State mState = Toolbar.State.HOME;
    private Toolbar.NavButtonMode mNavButtonMode = Toolbar.NavButtonMode.BACK;
    private List<MenuItem> mMenuItems = Collections.emptyList();
    private List<MenuItem> mOverflowItems = new ArrayList();
    private final List<MenuItemRenderer> mMenuItemRenderers = new ArrayList();
    private int mMenuItemsXmlId = 0;
    private MenuItem.Listener mOverflowItemListener = new MenuItem.Listener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$nuHiWU_IwKkEg9E5vj8-Uez9a84
        @Override // com.android.car.ui.toolbar.MenuItem.Listener
        public final void onMenuItemChanged() {
            ToolbarControllerImpl.this.lambda$new$0$ToolbarControllerImpl();
        }
    };
    private final CarUxRestrictionsUtil.OnUxRestrictionsChangedListener mOnUxRestrictionsChangedListener = new CarUxRestrictionsUtil.OnUxRestrictionsChangedListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$XuH-he7uMvOrHcAZyCrw8AehzNg
        @Override // com.android.car.ui.utils.CarUxRestrictionsUtil.OnUxRestrictionsChangedListener
        public final void onRestrictionsChanged(CarUxRestrictions carUxRestrictions) {
            ToolbarControllerImpl.this.lambda$new$1$ToolbarControllerImpl(carUxRestrictions);
        }
    };
    private final MenuItem mOverflowButton = MenuItem.builder(getContext()).setIcon(R.drawable.car_ui_icon_overflow_menu).setTitle(R.string.car_ui_toolbar_menu_item_overflow_title).setOnClickListener(new MenuItem.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$oO5mRdAg3c9VhIPnkrwlE9PbRrU
        @Override // com.android.car.ui.toolbar.MenuItem.OnClickListener
        public final void onClick(MenuItem menuItem) {
            ToolbarControllerImpl.this.lambda$new$2$ToolbarControllerImpl(menuItem);
        }
    }).build();
    private final boolean mIsTabsInSecondRow = getContext().getResources().getBoolean(R.bool.car_ui_toolbar_tabs_on_second_row);
    private boolean mNavIconSpaceReserved = getContext().getResources().getBoolean(R.bool.car_ui_toolbar_nav_icon_reserve_space);
    private boolean mLogoFillsNavIconSpace = getContext().getResources().getBoolean(R.bool.car_ui_toolbar_logo_fills_nav_icon_space);
    private boolean mShowLogo = getContext().getResources().getBoolean(R.bool.car_ui_toolbar_show_logo);

    public /* synthetic */ void lambda$new$0$ToolbarControllerImpl() {
        createOverflowDialog();
        setState(getState());
    }

    public /* synthetic */ void lambda$new$1$ToolbarControllerImpl(CarUxRestrictions carUxRestrictions) {
        for (MenuItemRenderer menuItemRenderer : this.mMenuItemRenderers) {
            menuItemRenderer.setCarUxRestrictions(carUxRestrictions);
        }
    }

    public ToolbarControllerImpl(View view) {
        this.mContext = view.getContext();
        this.mBackground = CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_background);
        this.mTabLayout = (TabLayout) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_tabs);
        this.mNavIcon = (ImageView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_nav_icon);
        this.mLogoInNavIconSpace = (ImageView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_logo);
        this.mNavIconContainer = (ViewGroup) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_nav_icon_container);
        this.mMenuItemsContainer = (ViewGroup) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_menu_items_container);
        this.mTitle = (TextView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_title);
        this.mTitleLogoContainer = (ViewGroup) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_title_logo_container);
        this.mTitleLogo = (ImageView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_title_logo);
        this.mSearchViewContainer = (FrameLayout) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_search_view_container);
        this.mProgressBar = (ProgressBar) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_progress_bar);
        this.mTabLayout.addListener(new TabLayout.Listener() { // from class: com.android.car.ui.toolbar.ToolbarControllerImpl.1
            @Override // com.android.car.ui.toolbar.TabLayout.Listener
            public void onTabSelected(TabLayout.Tab tab) {
                for (Toolbar.OnTabSelectedListener onTabSelectedListener : ToolbarControllerImpl.this.mOnTabSelectedListeners) {
                    onTabSelectedListener.onTabSelected(tab);
                }
            }
        });
        this.mBackground.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$0Shr4D2NlmNQK-0cQlP1vgI1j0c
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                ToolbarControllerImpl.this.lambda$new$3$ToolbarControllerImpl(view2, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        setBackgroundShown(true);
        CarUxRestrictionsUtil.getInstance(getContext()).register(this.mOnUxRestrictionsChangedListener);
    }

    public /* synthetic */ void lambda$new$2$ToolbarControllerImpl(MenuItem menuItem) {
        AlertDialog alertDialog = this.mOverflowDialog;
        if (alertDialog == null) {
            if (Log.isLoggable(TAG, 6)) {
                Log.e(TAG, "Overflow dialog was null when trying to show it!");
                return;
            }
            return;
        }
        alertDialog.show();
    }

    public /* synthetic */ void lambda$new$3$ToolbarControllerImpl(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i8 - i6 != i4 - i2) {
            for (Toolbar.OnHeightChangedListener onHeightChangedListener : this.mOnHeightChangedListeners) {
                onHeightChangedListener.onHeightChanged(this.mBackground.getHeight());
            }
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean isTabsInSecondRow() {
        return this.mIsTabsInSecondRow;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setTitle(int i) {
        this.mTitle.setText(i);
        setState(getState());
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setTitle(CharSequence charSequence) {
        this.mTitle.setText(charSequence);
        setState(getState());
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public CharSequence getTitle() {
        return this.mTitle.getText();
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public TabLayout getTabLayout() {
        return this.mTabLayout;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void addTab(TabLayout.Tab tab) {
        this.mTabLayout.addTab(tab);
        setState(getState());
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void clearAllTabs() {
        this.mTabLayout.clearAllTabs();
        setState(getState());
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public TabLayout.Tab getTab(int i) {
        return this.mTabLayout.get(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void selectTab(int i) {
        this.mTabLayout.selectTab(i);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setShowTabsInSubpage(boolean z) {
        if (z != this.mShowTabsInSubpage) {
            this.mShowTabsInSubpage = z;
            setState(getState());
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getShowTabsInSubpage() {
        return this.mShowTabsInSubpage;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setLogo(int i) {
        setLogo(i != 0 ? getContext().getDrawable(i) : null);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setLogo(Drawable drawable) {
        if (this.mShowLogo) {
            if (drawable != null) {
                this.mLogoInNavIconSpace.setImageDrawable(drawable);
                this.mTitleLogo.setImageDrawable(drawable);
                this.mHasLogo = true;
            } else {
                this.mHasLogo = false;
            }
            setState(this.mState);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchHint(int i) {
        setSearchHint(getContext().getString(i));
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchHint(CharSequence charSequence) {
        this.mSearchHint = charSequence;
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.setHint(this.mSearchHint);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public CharSequence getSearchHint() {
        return this.mSearchHint;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchIcon(int i) {
        setSearchIcon(getContext().getDrawable(i));
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchIcon(Drawable drawable) {
        if (Objects.equals(drawable, this.mSearchIcon)) {
            return;
        }
        this.mSearchIcon = drawable;
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.setIcon(this.mSearchIcon);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setNavButtonMode(Toolbar.NavButtonMode navButtonMode) {
        if (navButtonMode != this.mNavButtonMode) {
            this.mNavButtonMode = navButtonMode;
            setState(this.mState);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public Toolbar.NavButtonMode getNavButtonMode() {
        return this.mNavButtonMode;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setBackgroundShown(boolean z) {
        if (z) {
            this.mBackground.setBackground(getContext().getDrawable(R.drawable.car_ui_toolbar_background));
        } else {
            this.mBackground.setBackground(null);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getBackgroundShown() {
        return this.mBackground.getBackground() != null;
    }

    private void setMenuItemsInternal(List<MenuItem> list) {
        if (list == null) {
            list = Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        synchronized (this) {
            if (list.equals(this.mMenuItems)) {
                return;
            }
            for (MenuItem menuItem : list) {
                if (menuItem.getDisplayBehavior() == MenuItem.DisplayBehavior.NEVER) {
                    arrayList2.add(menuItem);
                    menuItem.setListener(this.mOverflowItemListener);
                } else {
                    arrayList.add(menuItem);
                }
            }
            this.mMenuItems = new ArrayList(list);
            this.mOverflowItems = arrayList2;
            this.mMenuItemRenderers.clear();
            this.mMenuItemsContainer.removeAllViews();
            if (!arrayList2.isEmpty()) {
                arrayList.add(this.mOverflowButton);
                createOverflowDialog();
            }
            final View[] viewArr = new View[arrayList.size()];
            this.mMenuItemViews = viewArr;
            for (final int i = 0; i < arrayList.size(); i++) {
                MenuItemRenderer menuItemRenderer = new MenuItemRenderer((MenuItem) arrayList.get(i), this.mMenuItemsContainer);
                this.mMenuItemRenderers.add(menuItemRenderer);
                menuItemRenderer.createView(new Consumer() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$XM5gsKVhbzu7_zOfUfHY1DZo3yk
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        ToolbarControllerImpl.this.lambda$setMenuItemsInternal$4$ToolbarControllerImpl(viewArr, i, atomicInteger, (View) obj);
                    }
                });
            }
            setState(this.mState);
        }
    }

    public /* synthetic */ void lambda$setMenuItemsInternal$4$ToolbarControllerImpl(View[] viewArr, int i, AtomicInteger atomicInteger, View view) {
        synchronized (this) {
            if (viewArr != this.mMenuItemViews) {
                return;
            }
            viewArr[i] = view;
            if (atomicInteger.addAndGet(1) == viewArr.length) {
                for (View view2 : viewArr) {
                    this.mMenuItemsContainer.addView(view2);
                }
            }
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setMenuItems(List<MenuItem> list) {
        this.mMenuItemsXmlId = 0;
        setMenuItemsInternal(list);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public List<MenuItem> setMenuItems(int i) {
        int i2 = this.mMenuItemsXmlId;
        if (i2 != 0 && i2 == i) {
            return this.mMenuItems;
        }
        this.mMenuItemsXmlId = i;
        List<MenuItem> readMenuItemList = MenuItemRenderer.readMenuItemList(getContext(), i);
        setMenuItemsInternal(readMenuItemList);
        return readMenuItemList;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public List<MenuItem> getMenuItems() {
        return Collections.unmodifiableList(this.mMenuItems);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public MenuItem findMenuItemById(int i) {
        for (MenuItem menuItem : this.mMenuItems) {
            if (menuItem.getId() == i) {
                return menuItem;
            }
        }
        return null;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public MenuItem requireMenuItemById(int i) {
        MenuItem findMenuItemById = findMenuItemById(i);
        if (findMenuItemById != null) {
            return findMenuItemById;
        }
        throw new IllegalArgumentException("ID does not reference a MenuItem on this Toolbar");
    }

    private int countVisibleOverflowItems() {
        int i = 0;
        for (MenuItem menuItem : this.mOverflowItems) {
            if (menuItem.isVisible()) {
                i++;
            }
        }
        return i;
    }

    private void createOverflowDialog() {
        CharSequence[] charSequenceArr = new CharSequence[countVisibleOverflowItems()];
        int i = 0;
        for (MenuItem menuItem : this.mOverflowItems) {
            if (menuItem.isVisible()) {
                charSequenceArr[i] = menuItem.getTitle();
                i++;
            }
        }
        this.mOverflowDialog = new AlertDialog.Builder(getContext()).setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$E9H_20bhQIqvlja1G2XPPIWY0x4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ToolbarControllerImpl.this.lambda$createOverflowDialog$5$ToolbarControllerImpl(dialogInterface, i2);
            }
        }).create();
    }

    public /* synthetic */ void lambda$createOverflowDialog$5$ToolbarControllerImpl(DialogInterface dialogInterface, int i) {
        MenuItem menuItem = this.mOverflowItems.get(i);
        MenuItem.OnClickListener onClickListener = menuItem.getOnClickListener();
        if (onClickListener != null) {
            onClickListener.onClick(menuItem);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setShowMenuItemsWhileSearching(boolean z) {
        this.mShowMenuItemsWhileSearching = z;
        setState(this.mState);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean getShowMenuItemsWhileSearching() {
        return this.mShowMenuItemsWhileSearching;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setSearchQuery(String str) {
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.setSearchQuery(str);
            return;
        }
        this.mSearchQuery = str;
        for (Toolbar.OnSearchListener onSearchListener : this.mOnSearchListeners) {
            onSearchListener.onSearch(str);
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void setState(Toolbar.State state) {
        this.mState = state;
        if (this.mSearchView == null && (state == Toolbar.State.SEARCH || state == Toolbar.State.EDIT)) {
            SearchView searchView = new SearchView(getContext());
            searchView.setHint(this.mSearchHint);
            searchView.setIcon(this.mSearchIcon);
            searchView.setSearchQuery(this.mSearchQuery);
            searchView.setSearchListeners(this.mOnSearchListeners);
            searchView.setSearchCompletedListeners(this.mOnSearchCompletedListeners);
            searchView.setVisibility(8);
            this.mSearchViewContainer.addView(searchView, new FrameLayout.LayoutParams(-1, -1));
            this.mSearchView = searchView;
        }
        for (MenuItemRenderer menuItemRenderer : this.mMenuItemRenderers) {
            menuItemRenderer.setToolbarState(this.mState);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$ToolbarControllerImpl$d5avEHTLVtyukl4Zb-MGGRS3GPY
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ToolbarControllerImpl.this.lambda$setState$6$ToolbarControllerImpl(view);
            }
        };
        boolean z = true;
        if (state == Toolbar.State.SEARCH) {
            this.mNavIcon.setImageResource(R.drawable.car_ui_icon_search_nav_icon);
        } else {
            int i = AnonymousClass2.$SwitchMap$com$android$car$ui$toolbar$Toolbar$NavButtonMode[this.mNavButtonMode.ordinal()];
            if (i == 1) {
                this.mNavIcon.setImageResource(R.drawable.car_ui_icon_close);
            } else if (i == 2) {
                this.mNavIcon.setImageResource(R.drawable.car_ui_icon_down);
            } else {
                this.mNavIcon.setImageResource(R.drawable.car_ui_icon_arrow_back);
            }
        }
        this.mNavIcon.setVisibility(state != Toolbar.State.HOME ? 0 : 8);
        int i2 = 4;
        this.mLogoInNavIconSpace.setVisibility((this.mHasLogo && state == Toolbar.State.HOME && this.mLogoFillsNavIconSpace) ? 0 : 4);
        this.mTitleLogoContainer.setVisibility((!this.mHasLogo || (state != Toolbar.State.SUBPAGE && (state != Toolbar.State.HOME || this.mLogoFillsNavIconSpace))) ? 8 : 0);
        ViewGroup viewGroup = this.mNavIconContainer;
        if (state != Toolbar.State.HOME || (this.mHasLogo && this.mLogoFillsNavIconSpace)) {
            i2 = 0;
        } else if (!this.mNavIconSpaceReserved) {
            i2 = 8;
        }
        viewGroup.setVisibility(i2);
        ViewGroup viewGroup2 = this.mNavIconContainer;
        if (state == Toolbar.State.HOME) {
            onClickListener = null;
        }
        viewGroup2.setOnClickListener(onClickListener);
        this.mNavIconContainer.setClickable(state != Toolbar.State.HOME);
        boolean z2 = this.mTabLayout.getTabCount() > 0 && (state == Toolbar.State.HOME || (state == Toolbar.State.SUBPAGE && this.mShowTabsInSubpage));
        this.mTitle.setVisibility(((state == Toolbar.State.SUBPAGE || state == Toolbar.State.HOME) && (!z2 || this.mIsTabsInSecondRow)) ? 0 : 8);
        this.mTabLayout.setVisibility(z2 ? 0 : 8);
        SearchView searchView2 = this.mSearchView;
        if (searchView2 != null) {
            if (state == Toolbar.State.SEARCH || state == Toolbar.State.EDIT) {
                this.mSearchView.setPlainText(state == Toolbar.State.EDIT);
                this.mSearchView.setVisibility(0);
            } else {
                searchView2.setVisibility(8);
            }
        }
        boolean z3 = !(state == Toolbar.State.SEARCH || state == Toolbar.State.EDIT) || this.mShowMenuItemsWhileSearching;
        this.mMenuItemsContainer.setVisibility(z3 ? 0 : 8);
        this.mOverflowButton.setVisible((!z3 || countVisibleOverflowItems() <= 0) ? false : false);
    }

    public /* synthetic */ void lambda$setState$6$ToolbarControllerImpl(View view) {
        boolean z;
        Activity activity;
        loop0: while (true) {
            for (Toolbar.OnBackListener onBackListener : new ArrayList(this.mOnBackListeners)) {
                z = z || onBackListener.onBack();
            }
        }
        if (z || (activity = CarUiUtils.getActivity(getContext())) == null) {
            return;
        }
        activity.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.car.ui.toolbar.ToolbarControllerImpl$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$car$ui$toolbar$Toolbar$NavButtonMode = new int[Toolbar.NavButtonMode.values().length];

        static {
            try {
                $SwitchMap$com$android$car$ui$toolbar$Toolbar$NavButtonMode[Toolbar.NavButtonMode.CLOSE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$car$ui$toolbar$Toolbar$NavButtonMode[Toolbar.NavButtonMode.DOWN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public Toolbar.State getState() {
        return this.mState;
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerToolbarHeightChangeListener(Toolbar.OnHeightChangedListener onHeightChangedListener) {
        this.mOnHeightChangedListeners.add(onHeightChangedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterToolbarHeightChangeListener(Toolbar.OnHeightChangedListener onHeightChangedListener) {
        return this.mOnHeightChangedListeners.remove(onHeightChangedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnTabSelectedListener(Toolbar.OnTabSelectedListener onTabSelectedListener) {
        this.mOnTabSelectedListeners.add(onTabSelectedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnTabSelectedListener(Toolbar.OnTabSelectedListener onTabSelectedListener) {
        return this.mOnTabSelectedListeners.remove(onTabSelectedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnSearchListener(Toolbar.OnSearchListener onSearchListener) {
        this.mOnSearchListeners.add(onSearchListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnSearchListener(Toolbar.OnSearchListener onSearchListener) {
        return this.mOnSearchListeners.remove(onSearchListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnSearchCompletedListener(Toolbar.OnSearchCompletedListener onSearchCompletedListener) {
        this.mOnSearchCompletedListeners.add(onSearchCompletedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnSearchCompletedListener(Toolbar.OnSearchCompletedListener onSearchCompletedListener) {
        return this.mOnSearchCompletedListeners.remove(onSearchCompletedListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void registerOnBackListener(Toolbar.OnBackListener onBackListener) {
        this.mOnBackListeners.add(onBackListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public boolean unregisterOnBackListener(Toolbar.OnBackListener onBackListener) {
        return this.mOnBackListeners.remove(onBackListener);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void showProgressBar() {
        this.mProgressBar.setVisibility(0);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public void hideProgressBar() {
        this.mProgressBar.setVisibility(8);
    }

    @Override // com.android.car.ui.toolbar.ToolbarController
    public ProgressBar getProgressBar() {
        return this.mProgressBar;
    }
}
