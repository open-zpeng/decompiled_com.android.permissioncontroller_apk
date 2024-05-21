package com.android.car.ui.toolbar;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.android.car.ui.R;
import com.android.car.ui.utils.CarUxRestrictionsUtil;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class MenuItem {
    private final Context mContext;
    private CarUxRestrictions mCurrentRestrictions;
    private DisplayBehavior mDisplayBehavior;
    private Drawable mIcon;
    private int mId;
    private final boolean mIsActivatable;
    private boolean mIsActivated;
    private final boolean mIsCheckable;
    private boolean mIsChecked;
    private boolean mIsEnabled;
    private final boolean mIsSearch;
    private final boolean mIsTinted;
    private boolean mIsVisible;
    private WeakReference<Listener> mListener;
    private OnClickListener mOnClickListener;
    private final boolean mShowIconAndTitle;
    private CharSequence mTitle;
    private int mUxRestrictions;

    /* loaded from: classes.dex */
    public enum DisplayBehavior {
        ALWAYS,
        NEVER
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Listener {
        void onMenuItemChanged();
    }

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void onClick(MenuItem menuItem);
    }

    private MenuItem(Builder builder) {
        this.mListener = new WeakReference<>(null);
        this.mContext = builder.mContext;
        this.mId = builder.mId;
        this.mIsCheckable = builder.mIsCheckable;
        this.mIsActivatable = builder.mIsActivatable;
        this.mTitle = builder.mTitle;
        this.mIcon = builder.mIcon;
        this.mOnClickListener = builder.mOnClickListener;
        this.mDisplayBehavior = builder.mDisplayBehavior;
        this.mIsEnabled = builder.mIsEnabled;
        this.mIsChecked = builder.mIsChecked;
        this.mIsVisible = builder.mIsVisible;
        this.mIsActivated = builder.mIsActivated;
        this.mIsSearch = builder.mIsSearch;
        this.mShowIconAndTitle = builder.mShowIconAndTitle;
        this.mIsTinted = builder.mIsTinted;
        this.mUxRestrictions = builder.mUxRestrictions;
        this.mCurrentRestrictions = CarUxRestrictionsUtil.getInstance(this.mContext).getCurrentRestrictions();
    }

    private void update() {
        Listener listener = this.mListener.get();
        if (listener != null) {
            listener.onMenuItemChanged();
        }
    }

    public void setId(int i) {
        this.mId = i;
        update();
    }

    public int getId() {
        return this.mId;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void setEnabled(boolean z) {
        this.mIsEnabled = z;
        update();
    }

    public boolean isCheckable() {
        return this.mIsCheckable;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public void setChecked(boolean z) {
        if (!isCheckable()) {
            throw new IllegalStateException("Cannot call setChecked() on a non-checkable MenuItem");
        }
        this.mIsChecked = z;
        update();
    }

    public boolean isTinted() {
        return this.mIsTinted;
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    public void setVisible(boolean z) {
        this.mIsVisible = z;
        update();
    }

    public boolean isActivatable() {
        return this.mIsActivatable;
    }

    public boolean isActivated() {
        return this.mIsActivated;
    }

    public void setActivated(boolean z) {
        if (!isActivatable()) {
            throw new IllegalStateException("Cannot call setActivated() on a non-activatable MenuItem");
        }
        this.mIsActivated = z;
        update();
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        update();
    }

    public void setTitle(int i) {
        setTitle(this.mContext.getString(i));
    }

    public void setUxRestrictions(int i) {
        if (this.mUxRestrictions != i) {
            this.mUxRestrictions = i;
            update();
        }
    }

    public int getUxRestrictions() {
        return this.mUxRestrictions;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }

    public boolean isShowingIconAndTitle() {
        return this.mShowIconAndTitle;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
        update();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCarUxRestrictions(CarUxRestrictions carUxRestrictions) {
        boolean isRestricted = isRestricted();
        this.mCurrentRestrictions = carUxRestrictions;
        if (isRestricted() != isRestricted) {
            update();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRestricted() {
        return CarUxRestrictionsUtil.isRestricted(this.mUxRestrictions, this.mCurrentRestrictions);
    }

    public void performClick() {
        if (isEnabled() && isVisible()) {
            if (isRestricted()) {
                Toast.makeText(this.mContext, R.string.car_ui_restricted_while_driving, 1).show();
                return;
            }
            if (isActivatable()) {
                setActivated(!isActivated());
            }
            if (isCheckable()) {
                setChecked(!isChecked());
            }
            OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }
    }

    public DisplayBehavior getDisplayBehavior() {
        return this.mDisplayBehavior;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        update();
    }

    public void setIcon(int i) {
        setIcon(i == 0 ? null : this.mContext.getDrawable(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSearch() {
        return this.mIsSearch;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final Context mContext;
        private Drawable mIcon;
        private OnClickListener mOnClickListener;
        private Drawable mSearchIcon;
        private String mSearchTitle;
        private Drawable mSettingsIcon;
        private String mSettingsTitle;
        private CharSequence mTitle;
        private int mId = -1;
        private DisplayBehavior mDisplayBehavior = DisplayBehavior.ALWAYS;
        private boolean mIsTinted = true;
        private boolean mShowIconAndTitle = false;
        private boolean mIsEnabled = true;
        private boolean mIsCheckable = false;
        private boolean mIsChecked = false;
        private boolean mIsVisible = true;
        private boolean mIsActivatable = false;
        private boolean mIsActivated = false;
        private boolean mIsSearch = false;
        private boolean mIsSettings = false;
        private int mUxRestrictions = 0;

        public Builder(Context context) {
            this.mContext = context.getApplicationContext();
        }

        public MenuItem build() {
            if (this.mIsActivatable && (this.mShowIconAndTitle || this.mIcon == null)) {
                throw new IllegalStateException("Only simple icons can be activatable");
            }
            if (this.mIsCheckable && (this.mDisplayBehavior == DisplayBehavior.NEVER || this.mShowIconAndTitle || this.mIsActivatable)) {
                throw new IllegalStateException("Unsupported options for a checkable MenuItem");
            }
            if (this.mIsSearch && this.mIsSettings) {
                throw new IllegalStateException("Can't have both a search and settings MenuItem");
            }
            if (this.mIsSearch && (!this.mSearchTitle.contentEquals(this.mTitle) || !this.mSearchIcon.equals(this.mIcon) || this.mIsCheckable || this.mIsActivatable || !this.mIsTinted || this.mShowIconAndTitle || this.mDisplayBehavior != DisplayBehavior.ALWAYS)) {
                throw new IllegalStateException("Invalid search MenuItem");
            }
            if (this.mIsSettings && (!this.mSettingsTitle.contentEquals(this.mTitle) || !this.mSettingsIcon.equals(this.mIcon) || this.mIsCheckable || this.mIsActivatable || !this.mIsTinted || this.mShowIconAndTitle || this.mDisplayBehavior != DisplayBehavior.ALWAYS)) {
                throw new IllegalStateException("Invalid settings MenuItem");
            }
            return new MenuItem(this);
        }

        public Builder setId(int i) {
            this.mId = i;
            return this;
        }

        public Builder setTitle(int i) {
            setTitle(this.mContext.getString(i));
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public Builder setIcon(int i) {
            this.mIcon = i == 0 ? null : this.mContext.getDrawable(i);
            return this;
        }

        public Builder setIcon(Drawable drawable) {
            this.mIcon = drawable;
            return this;
        }

        public Builder setTinted(boolean z) {
            this.mIsTinted = z;
            return this;
        }

        public Builder setVisible(boolean z) {
            this.mIsVisible = z;
            return this;
        }

        public Builder setActivatable() {
            this.mIsActivatable = true;
            return this;
        }

        public Builder setActivated(boolean z) {
            setActivatable();
            this.mIsActivated = z;
            return this;
        }

        public Builder setOnClickListener(OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
            return this;
        }

        public Builder setShowIconAndTitle(boolean z) {
            this.mShowIconAndTitle = z;
            return this;
        }

        public Builder setDisplayBehavior(DisplayBehavior displayBehavior) {
            this.mDisplayBehavior = displayBehavior;
            return this;
        }

        public Builder setEnabled(boolean z) {
            this.mIsEnabled = z;
            return this;
        }

        public Builder setCheckable() {
            this.mIsCheckable = true;
            return this;
        }

        public Builder setChecked(boolean z) {
            setCheckable();
            this.mIsChecked = z;
            return this;
        }

        public Builder setUxRestrictions(int i) {
            this.mUxRestrictions = i;
            return this;
        }

        public Builder setToSearch() {
            this.mSearchTitle = this.mContext.getString(R.string.car_ui_toolbar_menu_item_search_title);
            this.mSearchIcon = this.mContext.getDrawable(R.drawable.car_ui_icon_search);
            this.mIsSearch = true;
            setTitle(this.mSearchTitle);
            setIcon(this.mSearchIcon);
            return this;
        }

        public Builder setToSettings() {
            this.mSettingsTitle = this.mContext.getString(R.string.car_ui_toolbar_menu_item_settings_title);
            this.mSettingsIcon = this.mContext.getDrawable(R.drawable.car_ui_icon_settings);
            this.mIsSettings = true;
            setTitle(this.mSettingsTitle);
            setIcon(this.mSettingsIcon);
            setUxRestrictions(64);
            return this;
        }

        @Deprecated
        public static MenuItem createSearch(Context context, OnClickListener onClickListener) {
            return MenuItem.builder(context).setToSearch().setOnClickListener(onClickListener).build();
        }

        @Deprecated
        public static MenuItem createSettings(Context context, OnClickListener onClickListener) {
            return MenuItem.builder(context).setToSettings().setOnClickListener(onClickListener).build();
        }
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListener(Listener listener) {
        this.mListener = new WeakReference<>(listener);
    }
}
