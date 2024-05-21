package com.android.car.ui.recyclerview;
/* loaded from: classes.dex */
public class CarUiHeaderListItem extends CarUiListItem {
    private CharSequence mBody;
    private CharSequence mTitle;

    public CarUiHeaderListItem(CharSequence charSequence) {
        this(charSequence, "");
    }

    public CarUiHeaderListItem(CharSequence charSequence, CharSequence charSequence2) {
        this.mTitle = charSequence;
        this.mBody = charSequence2;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getBody() {
        return this.mBody;
    }
}
