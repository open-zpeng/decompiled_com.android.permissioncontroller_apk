package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.xiaopeng.xpui.R$style;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes.dex */
public class XToggleButton extends ToggleButton implements VuiView {
    protected XViewDelegate mXViewDelegate;

    public XToggleButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, i2);
        initVui(this, attributeSet);
    }

    public XToggleButton(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R$style.XButton_CompoundButton_ToggleButton_Fill);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public XToggleButton(android.content.Context r2, android.util.AttributeSet r3) {
        /*
            r1 = this;
            int r0 = com.xiaopeng.xpui.R$style.XButton_CompoundButton_ToggleButton_Fill
            r1.<init>(r2, r3, r0, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XToggleButton.<init>(android.content.Context, android.util.AttributeSet):void");
    }

    public XToggleButton(Context context) {
        this(context, null);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.widget.TextView, android.view.View
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

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        setVuiVisibility(this, i);
    }

    @Override // android.widget.TextView, android.view.View
    public void setSelected(boolean z) {
        super.setSelected(z);
        setVuiSelected(this, z);
    }

    @Override // android.widget.ToggleButton, android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        boolean isChecked = isChecked();
        super.setChecked(z);
        if (isChecked != z) {
            updateVui(this);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }
}
