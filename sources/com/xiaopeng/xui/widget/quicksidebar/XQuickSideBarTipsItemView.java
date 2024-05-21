package com.xiaopeng.xui.widget.quicksidebar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R$color;
import com.xiaopeng.xpui.R$dimen;
import com.xiaopeng.xpui.R$string;
import com.xiaopeng.xpui.R$styleable;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class XQuickSideBarTipsItemView extends XView {
    private boolean isVisibility;
    private int mBackgroundColor;
    private Paint mBackgroundPaint;
    private RectF mBackgroundRect;
    private int mItemHeight;
    private String mText;
    private int mTextColor;
    private Paint mTextPaint;
    private float mTextSize;
    private int mWidth;

    public XQuickSideBarTipsItemView(Context context) {
        this(context, null);
    }

    public XQuickSideBarTipsItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XQuickSideBarTipsItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBackgroundRect = new RectF();
        this.mText = "";
        this.isVisibility = false;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        XViewDelegate xViewDelegate;
        initDefaultColor();
        this.mTextSize = context.getResources().getDimension(R$dimen.x_sidebar_tips_textsize);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.XQuickSideBarView);
            this.mTextColor = obtainStyledAttributes.getColor(R$styleable.XQuickSideBarView_sidebarTextColor, this.mTextColor);
            this.mBackgroundColor = obtainStyledAttributes.getColor(R$styleable.XQuickSideBarView_sidebarBackgroundColor, this.mBackgroundColor);
            obtainStyledAttributes.recycle();
        }
        final XFontScaleHelper create = XFontScaleHelper.create(getResources(), R$dimen.x_sidebar_tips_textsize);
        if (create != null && (xViewDelegate = this.mXViewDelegate) != null) {
            xViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.quicksidebar.-$$Lambda$XQuickSideBarTipsItemView$51XFEO-0GO0VEvoNZW-VFGL4eW4
                @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                public final void onFontScaleChanged() {
                    XQuickSideBarTipsItemView.this.lambda$init$0$XQuickSideBarTipsItemView(create);
                }
            });
        }
        this.mBackgroundPaint = new Paint(1);
        this.mTextPaint = new Paint(1);
        this.mBackgroundPaint.setColor(this.mBackgroundColor);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setStyle(Paint.Style.FILL);
        this.mTextPaint.setTypeface(Typeface.create(getResources().getString(R$string.x_font_typeface_number_bold), 0));
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public /* synthetic */ void lambda$init$0$XQuickSideBarTipsItemView(XFontScaleHelper xFontScaleHelper) {
        this.mTextSize = xFontScaleHelper.getCurrentFontSize(getResources().getDisplayMetrics());
        this.mTextPaint.setTextSize(this.mTextSize);
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = getMeasuredWidth();
        this.mItemHeight = this.mWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(this.mText)) {
            return;
        }
        canvas.drawColor(getResources().getColor(17170445, null));
        this.mBackgroundRect.set(0.0f, 0.0f, this.mWidth, this.mItemHeight);
        canvas.drawRoundRect(this.mBackgroundRect, 16.0f, 16.0f, this.mBackgroundPaint);
        Paint.FontMetrics fontMetrics = this.mTextPaint.getFontMetrics();
        canvas.drawText(this.mText, this.mBackgroundRect.centerX(), (int) ((this.mBackgroundRect.centerY() - (fontMetrics.top / 2.0f)) - (fontMetrics.bottom / 8.0f)), this.mTextPaint);
    }

    public void setText(String str) {
        this.mText = str;
        Rect rect = new Rect();
        Paint paint = this.mTextPaint;
        String str2 = this.mText;
        paint.getTextBounds(str2, 0, str2.length(), rect);
        invalidate();
    }

    public void setVisibility(boolean z) {
        this.isVisibility = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (XThemeManager.isThemeChanged(configuration)) {
            initDefaultColor();
            this.mBackgroundPaint.setColor(this.mBackgroundColor);
            this.mTextPaint.setColor(this.mTextColor);
            invalidate();
        }
    }

    private void initDefaultColor() {
        this.mTextColor = getColor(R$color.x_side_bar_tips_text_color);
        this.mBackgroundColor = getColor(R$color.x_side_bar_tips_bg_color);
    }

    private int getColor(int i) {
        return getResources().getColor(i, getContext().getTheme());
    }
}
