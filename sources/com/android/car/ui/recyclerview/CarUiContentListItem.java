package com.android.car.ui.recyclerview;

import android.graphics.drawable.Drawable;
import android.view.View;
/* loaded from: classes.dex */
public class CarUiContentListItem extends CarUiListItem {
    private Action mAction;
    private CharSequence mBody;
    private Drawable mIcon;
    private boolean mIsActionDividerVisible;
    private boolean mIsActivated;
    private boolean mIsChecked;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnClickListener mOnClickListener;
    private Drawable mSupplementalIcon;
    private View.OnClickListener mSupplementalIconOnClickListener;
    private CharSequence mTitle;
    private boolean mIsEnabled = true;
    private IconType mPrimaryIconType = IconType.STANDARD;

    /* loaded from: classes.dex */
    public enum Action {
        NONE,
        SWITCH,
        CHECK_BOX,
        RADIO_BUTTON,
        ICON
    }

    /* loaded from: classes.dex */
    public enum IconType {
        CONTENT,
        STANDARD,
        AVATAR
    }

    /* loaded from: classes.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CarUiContentListItem carUiContentListItem, boolean z);
    }

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void onClick(CarUiContentListItem carUiContentListItem);
    }

    public CarUiContentListItem(Action action) {
        this.mAction = action;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
    }

    public CharSequence getBody() {
        return this.mBody;
    }

    public void setBody(CharSequence charSequence) {
        this.mBody = charSequence;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
    }

    public IconType getPrimaryIconType() {
        return this.mPrimaryIconType;
    }

    public void setPrimaryIconType(IconType iconType) {
        this.mPrimaryIconType = iconType;
    }

    public boolean isActivated() {
        return this.mIsActivated;
    }

    public void setActivated(boolean z) {
        this.mIsActivated = z;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void setEnabled(boolean z) {
        this.mIsEnabled = z;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public void setChecked(boolean z) {
        if (z == this.mIsChecked) {
            return;
        }
        Action action = this.mAction;
        if (action == Action.CHECK_BOX || action == Action.SWITCH || action == Action.RADIO_BUTTON) {
            this.mIsChecked = z;
            OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, this.mIsChecked);
            }
        }
    }

    public void setActionDividerVisible(boolean z) {
        this.mIsActionDividerVisible = z;
    }

    public boolean isActionDividerVisible() {
        return this.mIsActionDividerVisible;
    }

    public Action getAction() {
        return this.mAction;
    }

    public Drawable getSupplementalIcon() {
        if (this.mAction != Action.ICON) {
            return null;
        }
        return this.mSupplementalIcon;
    }

    public void setSupplementalIcon(Drawable drawable) {
        setSupplementalIcon(drawable, null);
    }

    public void setSupplementalIcon(Drawable drawable, View.OnClickListener onClickListener) {
        if (this.mAction != Action.ICON) {
            throw new IllegalStateException("Cannot set supplemental icon on list item that does not have an action of type ICON");
        }
        this.mSupplementalIcon = drawable;
        this.mSupplementalIconOnClickListener = onClickListener;
    }

    public View.OnClickListener getSupplementalIconOnClickListener() {
        return this.mSupplementalIconOnClickListener;
    }

    public void setOnItemClickedListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return this.mOnCheckedChangeListener;
    }
}
