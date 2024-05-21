package com.xiaopeng.xui.view.touch;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
/* loaded from: classes.dex */
public class XTouchDelegate extends TouchDelegate {
    private View mDelegateViewHold;

    public XTouchDelegate(Rect rect, View view) {
        super(rect, view);
        this.mDelegateViewHold = view;
    }

    public View getDelegateViewHold() {
        return this.mDelegateViewHold;
    }
}
