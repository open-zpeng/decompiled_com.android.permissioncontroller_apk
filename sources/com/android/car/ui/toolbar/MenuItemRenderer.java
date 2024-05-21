package com.android.car.ui.toolbar;

import android.app.Activity;
import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.util.Consumer;
import com.android.car.ui.R;
import com.android.car.ui.toolbar.MenuItem;
import com.android.car.ui.toolbar.Toolbar;
import com.android.car.ui.utils.CarUiUtils;
import com.android.car.ui.uxr.DrawableStateView;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MenuItemRenderer implements MenuItem.Listener {
    private static final int[] RESTRICTED_STATE = {R.attr.state_ux_restricted};
    private View mIconContainer;
    private ImageView mIconView;
    private final MenuItem mMenuItem;
    private final int mMenuItemIconSize;
    private final ViewGroup mParentView;
    private Switch mSwitch;
    private TextView mTextView;
    private TextView mTextWithIconView;
    private Toolbar.State mToolbarState;
    private View mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemRenderer(MenuItem menuItem, ViewGroup viewGroup) {
        this.mMenuItem = menuItem;
        this.mParentView = viewGroup;
        this.mMenuItem.setListener(this);
        this.mMenuItemIconSize = viewGroup.getContext().getResources().getDimensionPixelSize(R.dimen.car_ui_toolbar_menu_item_icon_size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setToolbarState(Toolbar.State state) {
        this.mToolbarState = state;
        if (this.mMenuItem.isSearch()) {
            updateView();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCarUxRestrictions(CarUxRestrictions carUxRestrictions) {
        this.mMenuItem.setCarUxRestrictions(carUxRestrictions);
    }

    @Override // com.android.car.ui.toolbar.MenuItem.Listener
    public void onMenuItemChanged() {
        updateView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createView(final Consumer<View> consumer) {
        new AsyncLayoutInflater(this.mParentView.getContext()).inflate(R.layout.car_ui_toolbar_menu_item, this.mParentView, new AsyncLayoutInflater.OnInflateFinishedListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$MenuItemRenderer$p92KjVA5uetSnb4q9D1Fz51yLs0
            @Override // androidx.asynclayoutinflater.view.AsyncLayoutInflater.OnInflateFinishedListener
            public final void onInflateFinished(View view, int i, ViewGroup viewGroup) {
                MenuItemRenderer.this.lambda$createView$0$MenuItemRenderer(consumer, view, i, viewGroup);
            }
        });
    }

    public /* synthetic */ void lambda$createView$0$MenuItemRenderer(Consumer consumer, View view, int i, ViewGroup viewGroup) {
        this.mView = view;
        this.mIconContainer = CarUiUtils.requireViewByRefId(this.mView, R.id.car_ui_toolbar_menu_item_icon_container);
        this.mIconView = (ImageView) CarUiUtils.requireViewByRefId(this.mView, R.id.car_ui_toolbar_menu_item_icon);
        this.mSwitch = (Switch) CarUiUtils.requireViewByRefId(this.mView, R.id.car_ui_toolbar_menu_item_switch);
        this.mTextView = (TextView) CarUiUtils.requireViewByRefId(this.mView, R.id.car_ui_toolbar_menu_item_text);
        this.mTextWithIconView = (TextView) CarUiUtils.requireViewByRefId(this.mView, R.id.car_ui_toolbar_menu_item_text_with_icon);
        updateView();
        consumer.accept(this.mView);
    }

    private void updateView() {
        View view = this.mView;
        if (view == null) {
            return;
        }
        view.setId(this.mMenuItem.getId());
        boolean z = this.mMenuItem.getIcon() != null;
        boolean isEmpty = true ^ TextUtils.isEmpty(this.mMenuItem.getTitle());
        boolean isShowingIconAndTitle = this.mMenuItem.isShowingIconAndTitle();
        boolean isCheckable = this.mMenuItem.isCheckable();
        if (!this.mMenuItem.isVisible() || ((this.mMenuItem.isSearch() && this.mToolbarState == Toolbar.State.SEARCH) || (!isCheckable && !z && !isEmpty))) {
            this.mView.setVisibility(8);
            return;
        }
        this.mView.setVisibility(0);
        this.mView.setContentDescription(this.mMenuItem.getTitle());
        this.mIconContainer.setVisibility(8);
        this.mTextView.setVisibility(8);
        this.mTextWithIconView.setVisibility(8);
        this.mSwitch.setVisibility(8);
        if (isCheckable) {
            this.mSwitch.setChecked(this.mMenuItem.isChecked());
            this.mSwitch.setVisibility(0);
        } else if (isEmpty && z && isShowingIconAndTitle) {
            Drawable icon = this.mMenuItem.getIcon();
            int i = this.mMenuItemIconSize;
            icon.setBounds(0, 0, i, i);
            this.mTextWithIconView.setCompoundDrawables(this.mMenuItem.getIcon(), null, null, null);
            this.mTextWithIconView.setText(this.mMenuItem.getTitle());
            this.mTextWithIconView.setVisibility(0);
        } else if (z) {
            this.mIconView.setImageDrawable(this.mMenuItem.getIcon());
            this.mIconContainer.setVisibility(0);
        } else {
            this.mTextView.setText(this.mMenuItem.getTitle());
            this.mTextView.setVisibility(0);
        }
        if (!this.mMenuItem.isTinted() && z) {
            this.mMenuItem.getIcon().setTintList(null);
        }
        recursiveSetEnabledAndDrawableState(this.mView);
        this.mView.setActivated(this.mMenuItem.isActivated());
        if (this.mMenuItem.getOnClickListener() != null || this.mMenuItem.isCheckable() || this.mMenuItem.isActivatable()) {
            this.mView.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$MenuItemRenderer$ql8WLabAg_CiY9HcQTI4ML8FsgY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    MenuItemRenderer.this.lambda$updateView$1$MenuItemRenderer(view2);
                }
            });
            return;
        }
        this.mView.setOnClickListener(null);
        this.mView.setClickable(false);
    }

    public /* synthetic */ void lambda$updateView$1$MenuItemRenderer(View view) {
        this.mMenuItem.performClick();
    }

    private void recursiveSetEnabledAndDrawableState(View view) {
        view.setEnabled(this.mMenuItem.isEnabled());
        int[] iArr = this.mMenuItem.isRestricted() ? RESTRICTED_STATE : null;
        if (view instanceof ImageView) {
            ((ImageView) view).setImageState(iArr, true);
        } else if (view instanceof DrawableStateView) {
            ((DrawableStateView) view).setDrawableState(iArr);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                recursiveSetEnabledAndDrawableState(viewGroup.getChildAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<MenuItem> readMenuItemList(Context context, int i) {
        if (i == 0) {
            return new ArrayList();
        }
        try {
            XmlResourceParser xml = context.getResources().getXml(i);
            AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
            ArrayList arrayList = new ArrayList();
            xml.next();
            xml.next();
            xml.require(2, null, "MenuItems");
            while (xml.next() != 3) {
                arrayList.add(readMenuItem(context, xml, asAttributeSet));
            }
            if (xml != null) {
                xml.close();
            }
            return arrayList;
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Unable to parse Menu Items", e);
        }
    }

    private static MenuItem readMenuItem(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        TypedArray typedArray;
        boolean z;
        boolean z2;
        MenuItem.OnClickListener onClickListener;
        MenuItem.DisplayBehavior displayBehavior;
        xmlResourceParser.require(2, null, "MenuItem");
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CarUiToolbarMenuItem);
        try {
            int resourceId = obtainStyledAttributes.getResourceId(R.styleable.CarUiToolbarMenuItem_id, -1);
            String string = obtainStyledAttributes.getString(R.styleable.CarUiToolbarMenuItem_title);
            Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.CarUiToolbarMenuItem_icon);
            boolean z3 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_search, false);
            boolean z4 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_settings, false);
            boolean z5 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_tinted, true);
            boolean z6 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_visible, true);
            boolean z7 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_showIconAndTitle, false);
            boolean z8 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_checkable, false);
            boolean z9 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_checked, false);
            boolean hasValue = obtainStyledAttributes.hasValue(R.styleable.CarUiToolbarMenuItem_checked);
            boolean z10 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_activatable, false);
            boolean z11 = obtainStyledAttributes.getBoolean(R.styleable.CarUiToolbarMenuItem_activated, false);
            boolean hasValue2 = obtainStyledAttributes.hasValue(R.styleable.CarUiToolbarMenuItem_activated);
            int i = obtainStyledAttributes.getInt(R.styleable.CarUiToolbarMenuItem_displayBehavior, 0);
            int i2 = obtainStyledAttributes.getInt(R.styleable.CarUiToolbarMenuItem_uxRestrictions, 0);
            String string2 = obtainStyledAttributes.getString(R.styleable.CarUiToolbarMenuItem_onClick);
            if (string2 != null) {
                typedArray = obtainStyledAttributes;
                try {
                    final Activity activity = CarUiUtils.getActivity(context);
                    if (activity == null) {
                        throw new RuntimeException("Couldn't find an activity for the MenuItem");
                    }
                    z = hasValue;
                    try {
                        z2 = z8;
                        final Method method = activity.getClass().getMethod(string2, MenuItem.class);
                        onClickListener = new MenuItem.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$MenuItemRenderer$o4Q9ccZfugknqOARB2tvDnC1XDw
                            @Override // com.android.car.ui.toolbar.MenuItem.OnClickListener
                            public final void onClick(MenuItem menuItem) {
                                MenuItemRenderer.lambda$readMenuItem$2(method, activity, menuItem);
                            }
                        };
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("OnClick method " + string2 + "(MenuItem) not found in your activity", e);
                    }
                } catch (Throwable th) {
                    th = th;
                    typedArray.recycle();
                    throw th;
                }
            } else {
                typedArray = obtainStyledAttributes;
                z = hasValue;
                z2 = z8;
                onClickListener = null;
            }
            if (i == 0) {
                displayBehavior = MenuItem.DisplayBehavior.ALWAYS;
            } else {
                displayBehavior = MenuItem.DisplayBehavior.NEVER;
            }
            xmlResourceParser.next();
            xmlResourceParser.require(3, null, "MenuItem");
            MenuItem.Builder displayBehavior2 = MenuItem.builder(context).setId(resourceId).setTitle(string).setIcon(drawable).setOnClickListener(onClickListener).setUxRestrictions(i2).setTinted(z5).setVisible(z6).setShowIconAndTitle(z7).setDisplayBehavior(displayBehavior);
            if (z3) {
                displayBehavior2.setToSearch();
            }
            if (z4) {
                displayBehavior2.setToSettings();
            }
            if (z2 || z) {
                displayBehavior2.setChecked(z9);
            }
            if (z10 || hasValue2) {
                displayBehavior2.setActivated(z11);
            }
            MenuItem build = displayBehavior2.build();
            typedArray.recycle();
            return build;
        } catch (Throwable th2) {
            th = th2;
            typedArray = obtainStyledAttributes;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$readMenuItem$2(Method method, Activity activity, MenuItem menuItem) {
        try {
            method.invoke(activity, menuItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't call the MenuItem's listener", e);
        }
    }
}
