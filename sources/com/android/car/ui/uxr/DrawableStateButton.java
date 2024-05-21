package com.android.car.ui.uxr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
/* loaded from: classes.dex */
public class DrawableStateButton extends Button implements DrawableStateView {
    private int[] mState;

    public DrawableStateButton(Context context) {
        super(context);
    }

    public DrawableStateButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DrawableStateButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DrawableStateButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.car.ui.uxr.DrawableStateView
    public void setDrawableState(int[] iArr) {
        this.mState = iArr;
        refreshDrawableState();
    }

    @Override // android.widget.TextView, android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] iArr = this.mState;
        if (iArr == null) {
            return super.onCreateDrawableState(i);
        }
        return Button.mergeDrawableStates(super.onCreateDrawableState(i + iArr.length), this.mState);
    }
}
