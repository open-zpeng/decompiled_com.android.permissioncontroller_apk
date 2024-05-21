package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R$style;
import com.xiaopeng.xui.view.XViewDelegate;
import xiaopeng.widget.SimpleSlider;
/* loaded from: classes.dex */
public class XSimpleSlider extends SimpleSlider {
    private XViewDelegate mXViewDelegate;

    public XSimpleSlider(Context context) {
        this(context, null);
    }

    public XSimpleSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$style.XSimpleSlider);
    }

    public XSimpleSlider(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R$style.XSimpleSlider);
    }

    public XSimpleSlider(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, i2);
        this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$1nSZItY1LxLjhgUMuj4enwCbqSg
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XSimpleSlider.this.refreshVisual();
            }
        });
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }
}
