package com.xiaopeng.xui.utils;

import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.View;
import com.xiaopeng.xpui.R$styleable;
/* loaded from: classes.dex */
public class XBackgroundPaddingUtils {
    public static Rect backgroundPadding(View view, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = view.getContext().obtainStyledAttributes(attributeSet, R$styleable.XBackgroundPadding);
        if (obtainStyledAttributes.getBoolean(R$styleable.XBackgroundPadding_x_bg_padding_enable, false)) {
            int dimensionPixelSize = obtainStyledAttributes.hasValue(R$styleable.XBackgroundPadding_x_bg_padding) ? obtainStyledAttributes.getDimensionPixelSize(R$styleable.XBackgroundPadding_x_bg_padding, -1) : -1;
            int i = dimensionPixelSize;
            int i2 = i;
            int i3 = i2;
            if (obtainStyledAttributes.hasValue(R$styleable.XBackgroundPadding_x_bg_padding_left)) {
                i = obtainStyledAttributes.getDimensionPixelSize(R$styleable.XBackgroundPadding_x_bg_padding_left, -1);
            }
            if (obtainStyledAttributes.hasValue(R$styleable.XBackgroundPadding_x_bg_padding_right)) {
                i2 = obtainStyledAttributes.getDimensionPixelSize(R$styleable.XBackgroundPadding_x_bg_padding_right, -1);
            }
            if (obtainStyledAttributes.hasValue(R$styleable.XBackgroundPadding_x_bg_padding_top)) {
                i3 = obtainStyledAttributes.getDimensionPixelSize(R$styleable.XBackgroundPadding_x_bg_padding_top, -1);
            }
            if (obtainStyledAttributes.hasValue(R$styleable.XBackgroundPadding_x_bg_padding_bottom)) {
                dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.XBackgroundPadding_x_bg_padding_bottom, -1);
            }
            obtainStyledAttributes.recycle();
            return backgroundPadding(view, i, i3, i2, dimensionPixelSize);
        }
        obtainStyledAttributes.recycle();
        return null;
    }

    public static Rect backgroundPadding(View view, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        Drawable drawable;
        int i9;
        int i10;
        int i11;
        int i12;
        log(String.format("insetLeft %s, insetTop %s, insetRight %s, insetBottom %s, ", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)));
        Drawable background = view.getBackground();
        int paddingLeft = view.getPaddingLeft();
        int paddingRight = view.getPaddingRight();
        int paddingTop = view.getPaddingTop();
        int paddingBottom = view.getPaddingBottom();
        log(String.format("paddingLeft %s, paddingRight %s, paddingTop %s, paddingBottom %s, ", Integer.valueOf(paddingLeft), Integer.valueOf(paddingRight), Integer.valueOf(paddingTop), Integer.valueOf(paddingBottom)));
        if (background != null) {
            if (background instanceof InsetDrawable) {
                InsetDrawable insetDrawable = (InsetDrawable) background;
                drawable = insetDrawable.getDrawable();
                Rect rect = new Rect();
                insetDrawable.getPadding(rect);
                i5 = i;
                if (i5 == -1) {
                    i5 = rect.left;
                }
                int i13 = i2;
                if (i13 == -1) {
                    i13 = rect.top;
                }
                int i14 = i13;
                int i15 = i3;
                if (i15 == -1) {
                    i15 = rect.right;
                }
                int i16 = i15;
                int i17 = i4;
                if (i17 == -1) {
                    i17 = rect.bottom;
                }
                i9 = i5 - rect.left;
                i11 = i14 - rect.top;
                i10 = i16 - rect.right;
                i12 = i17 - rect.bottom;
                i8 = i17;
                i6 = i14;
                i7 = i16;
            } else {
                i5 = i;
                i6 = i2;
                i7 = i3;
                i8 = i4;
                drawable = background;
                i9 = 0;
                i10 = 0;
                i11 = 0;
                i12 = 0;
            }
            log(String.format("paddingLeftOffset %s, paddingTopOffset %s, paddingRightOffset %s, paddingBottomOffset %s, ", Integer.valueOf(i9), Integer.valueOf(i11), Integer.valueOf(i10), Integer.valueOf(i12)));
            InsetDrawable insetDrawable2 = new InsetDrawable(drawable, i5, i6, i7, i8);
            view.setBackground(insetDrawable2);
            insetDrawable2.setDrawable(drawable);
            view.setPadding(paddingLeft + i9, paddingTop + i11, paddingRight + i10, paddingBottom + i12);
            log(String.format("paddingLeft %s, paddingRight %s, paddingTop %s, paddingBottom %s, ", Integer.valueOf(view.getPaddingLeft()), Integer.valueOf(view.getPaddingRight()), Integer.valueOf(view.getPaddingTop()), Integer.valueOf(view.getPaddingBottom())));
            return new Rect(i5, i6, i7, i8);
        }
        return null;
    }

    private static void log(String str) {
        XLogUtils.d("xpui-bgPadding", str);
    }
}
