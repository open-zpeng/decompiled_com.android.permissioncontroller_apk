package com.xiaopeng.xui.widget.slider;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import com.xiaopeng.xpui.R$style;
import com.xiaopeng.xpui.R$styleable;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.view.XView;
/* loaded from: classes.dex */
class SlideLineView extends XView {
    private static final float BG_DOC_RADIUS = 2.0f;
    private static final long DURATION = 800;
    public static final int LINE_WIDTH = 22;
    private ValueAnimator animator;
    int bgBallColorSelect;
    int bgBallColorUnSelect;
    int bgLineColorSelect;
    int bgLineColorUnSelect;
    private Paint bgLinePaint;
    private Paint blurPaint;
    private final int desireHeight;
    private final int desireWidth;
    private float halfLineHeight;
    private float halfLineWidth;
    private boolean isNight;
    private boolean isSelect;
    private final int lineStrokeWidth;
    private float progress;
    private float slope;

    public SlideLineView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlideLineView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$style.XSliderLine);
    }

    public SlideLineView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.desireWidth = 22;
        this.desireHeight = 40;
        this.lineStrokeWidth = 4;
        this.bgBallColorUnSelect = 671088640;
        this.bgBallColorSelect = -1;
        this.slope = 1.55f;
        this.halfLineHeight = 5.0f;
        this.halfLineWidth = this.halfLineHeight / this.slope;
        this.bgLineColorUnSelect = 671088640;
        this.bgLineColorSelect = -15301639;
        this.isNight = XThemeManager.isNight(getContext());
        this.progress = 1.0f;
        initView(context, attributeSet);
    }

    public SlideLineView(Context context, boolean z, int i) {
        this(context, (AttributeSet) null, i);
        this.isSelect = z;
    }

    public SlideLineView(Context context, boolean z) {
        this(context, (AttributeSet) null);
        this.isSelect = z;
    }

    private void initView(Context context, AttributeSet attributeSet) {
        setLayerType(1, null);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SlideLineView);
            this.bgLineColorUnSelect = obtainStyledAttributes.getColor(R$styleable.SlideLineView_slider_line_un_select, this.bgLineColorUnSelect);
            this.bgLineColorSelect = obtainStyledAttributes.getColor(R$styleable.SlideLineView_slider_line_select, this.bgLineColorSelect);
            obtainStyledAttributes.recycle();
        }
        this.bgLinePaint = new Paint(1);
        this.bgLinePaint.setStyle(Paint.Style.FILL);
        this.bgLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.bgLinePaint.setStrokeWidth(4.0f);
        this.bgLinePaint.setColor(this.bgLineColorSelect);
        this.blurPaint = new Paint(1);
        this.blurPaint.setStyle(Paint.Style.FILL);
        this.blurPaint.setStrokeCap(Paint.Cap.ROUND);
        this.blurPaint.setStrokeWidth(4.0f);
        this.blurPaint.setColor(4);
        this.animator = ValueAnimator.ofFloat(0.0f, BG_DOC_RADIUS, 1.0f);
        this.animator.setDuration(DURATION);
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.slider.SlideLineView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SlideLineView.this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                SlideLineView.this.invalidate();
            }
        });
        this.animator.setInterpolator(new DecelerateInterpolator());
        this.animator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.xui.widget.slider.SlideLineView.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SlideLineView.this.bgLinePaint.setStrokeWidth(4.0f);
                SlideLineView.this.blurPaint.setMaskFilter(null);
            }
        });
        setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.isNight = XThemeManager.isNight(getContext());
        postDelayed(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.SlideLineView.3
            @Override // java.lang.Runnable
            public void run() {
                SlideLineView.this.invalidate();
            }
        }, 500L);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isSelect) {
            if (this.isNight) {
                this.bgLinePaint.setColor(this.bgLineColorSelect);
                this.blurPaint.setColor(this.bgLineColorSelect);
                canvas.drawLine((getWidth() / 2) - (this.halfLineWidth * this.progress), (getHeight() / 2) + (this.halfLineHeight * this.progress), (getWidth() / 2) + (this.halfLineWidth * this.progress), (getHeight() / 2) - (this.halfLineHeight * this.progress), this.bgLinePaint);
                canvas.drawLine((getWidth() / 2) - (this.halfLineWidth * this.progress), (getHeight() / 2) + (this.halfLineHeight * this.progress), (getWidth() / 2) + (this.halfLineWidth * this.progress), (getHeight() / 2) - (this.halfLineHeight * this.progress), this.blurPaint);
                return;
            }
            this.bgLinePaint.setColor(this.bgBallColorSelect);
            this.blurPaint.setColor(this.bgBallColorSelect);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.progress * BG_DOC_RADIUS, this.bgLinePaint);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.progress * BG_DOC_RADIUS, this.blurPaint);
        } else if (this.isNight) {
            this.bgLinePaint.setColor(this.bgLineColorUnSelect);
            canvas.drawLine((getWidth() / 2) - this.halfLineWidth, (getHeight() / 2) + this.halfLineHeight, (getWidth() / 2) + this.halfLineWidth, (getHeight() / 2) - this.halfLineHeight, this.bgLinePaint);
        } else {
            this.bgLinePaint.setColor(this.bgBallColorUnSelect);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, BG_DOC_RADIUS, this.bgLinePaint);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(22, 40);
    }

    public void setSelect(boolean z) {
        this.isSelect = z;
        this.bgLinePaint.setStrokeWidth(z ? BG_DOC_RADIUS : 4.0f);
        if (z) {
            if (this.isNight) {
                this.blurPaint.setColor(this.bgLineColorSelect);
            } else {
                this.blurPaint.setColor(this.bgBallColorSelect);
            }
            this.blurPaint.setMaskFilter(new BlurMaskFilter(4.0f, BlurMaskFilter.Blur.NORMAL));
        }
        if (!z) {
            this.animator.cancel();
        } else {
            this.animator.start();
        }
        invalidate();
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        setAlphaByEnable(z);
        invalidate();
    }

    private void setAlphaByEnable(boolean z) {
        this.bgLineColorUnSelect = z ? 671088640 : 503316480;
        this.bgLineColorSelect = z ? this.bgLineColorSelect | (-1291845632) : this.bgLineColorSelect & 1291845631;
    }
}
