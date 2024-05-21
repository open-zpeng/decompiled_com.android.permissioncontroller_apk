package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
/* loaded from: classes.dex */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private boolean mChecked;

    public CheckableLinearLayout(Context context) {
        super(context);
        this.mChecked = false;
    }

    public CheckableLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mChecked = false;
    }

    public CheckableLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChecked = false;
    }

    public CheckableLinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mChecked = false;
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        if (this.mChecked == z) {
            return;
        }
        this.mChecked = z;
        refreshDrawableState();
        updateChildrenChecked();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }

    @Override // android.view.ViewGroup, android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isChecked()) {
            LinearLayout.mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    private void updateChildrenChecked() {
        updateChildrenChecked(this, this.mChecked);
    }

    private static void updateChildrenChecked(ViewGroup viewGroup, boolean z) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.isDuplicateParentStateEnabled()) {
                if (childAt instanceof Checkable) {
                    ((Checkable) childAt).setChecked(z);
                } else if (childAt instanceof ViewGroup) {
                    updateChildrenChecked((ViewGroup) childAt, z);
                }
            }
        }
    }
}
