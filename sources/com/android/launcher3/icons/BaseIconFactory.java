package com.android.launcher3.icons;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.UserHandle;
/* loaded from: classes.dex */
public class BaseIconFactory implements AutoCloseable {
    static final boolean ATLEAST_OREO;
    static final boolean ATLEAST_P;
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;

    public static int getBadgeSizeForIconSize(int i) {
        return (int) (i * 0.444f);
    }

    static {
        ATLEAST_OREO = Build.VERSION.SDK_INT >= 26;
        ATLEAST_P = Build.VERSION.SDK_INT >= 28;
    }

    protected BaseIconFactory(Context context, int i, int i2, boolean z) {
        this.mOldBounds = new Rect();
        this.mWrapperBackgroundColor = -1;
        this.mContext = context.getApplicationContext();
        this.mShapeDetection = z;
        this.mFillResIconDpi = i;
        this.mIconBitmapSize = i2;
        this.mPm = this.mContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        this.mCanvas = new Canvas();
        this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseIconFactory(Context context, int i, int i2) {
        this(context, i, i2, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }

    public BitmapInfo createBadgedIconBitmap(Drawable drawable, UserHandle userHandle, boolean z) {
        return createBadgedIconBitmap(drawable, userHandle, z, false, null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable drawable, UserHandle userHandle, boolean z, boolean z2, float[] fArr) {
        if (fArr == null) {
            fArr = new float[1];
        }
        Drawable normalizeAndWrapToAdaptiveIcon = normalizeAndWrapToAdaptiveIcon(drawable, z, null, fArr);
        Bitmap createIconBitmap = createIconBitmap(normalizeAndWrapToAdaptiveIcon, fArr[0]);
        if (ATLEAST_OREO && (normalizeAndWrapToAdaptiveIcon instanceof AdaptiveIconDrawable)) {
            this.mCanvas.setBitmap(createIconBitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(createIconBitmap), this.mCanvas);
            this.mCanvas.setBitmap(null);
        }
        if (z2) {
            badgeWithDrawable(createIconBitmap, this.mContext.getDrawable(R$drawable.ic_instant_app_badge));
        }
        if (userHandle != null) {
            Drawable userBadgedIcon = this.mPm.getUserBadgedIcon(new FixedSizeBitmapDrawable(createIconBitmap), userHandle);
            if (userBadgedIcon instanceof BitmapDrawable) {
                createIconBitmap = ((BitmapDrawable) userBadgedIcon).getBitmap();
            } else {
                createIconBitmap = createIconBitmap(userBadgedIcon, 1.0f);
            }
        }
        return BitmapInfo.fromBitmap(createIconBitmap, this.mDisableColorExtractor ? null : this.mColorExtractor);
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable drawable, boolean z, RectF rectF, float[] fArr) {
        float scale;
        if (drawable == null) {
            return null;
        }
        if (z && ATLEAST_OREO) {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R$drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) this.mWrapperIcon;
            adaptiveIconDrawable.setBounds(0, 0, 1, 1);
            boolean[] zArr = new boolean[1];
            scale = getNormalizer().getScale(drawable, rectF, adaptiveIconDrawable.getIconMask(), zArr);
            if (!(drawable instanceof AdaptiveIconDrawable) && !zArr[0]) {
                FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable) adaptiveIconDrawable.getForeground();
                fixedScaleDrawable.setDrawable(drawable);
                fixedScaleDrawable.setScale(scale);
                scale = getNormalizer().getScale(adaptiveIconDrawable, rectF, null, null);
                ((ColorDrawable) adaptiveIconDrawable.getBackground()).setColor(this.mWrapperBackgroundColor);
                drawable = adaptiveIconDrawable;
            }
        } else {
            scale = getNormalizer().getScale(drawable, rectF, null, null);
        }
        fArr[0] = scale;
        return drawable;
    }

    public void badgeWithDrawable(Bitmap bitmap, Drawable drawable) {
        this.mCanvas.setBitmap(bitmap);
        badgeWithDrawable(this.mCanvas, drawable);
        this.mCanvas.setBitmap(null);
    }

    public void badgeWithDrawable(Canvas canvas, Drawable drawable) {
        int badgeSizeForIconSize = getBadgeSizeForIconSize(this.mIconBitmapSize);
        int i = this.mIconBitmapSize;
        drawable.setBounds(i - badgeSizeForIconSize, i - badgeSizeForIconSize, i, i);
        drawable.draw(canvas);
    }

    private Bitmap createIconBitmap(Drawable drawable, float f) {
        return createIconBitmap(drawable, f, this.mIconBitmapSize);
    }

    public Bitmap createIconBitmap(Drawable drawable, float f, int i) {
        int i2;
        int i3;
        float f2;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        if (drawable == null) {
            return createBitmap;
        }
        this.mCanvas.setBitmap(createBitmap);
        this.mOldBounds.set(drawable.getBounds());
        if (ATLEAST_OREO && (drawable instanceof AdaptiveIconDrawable)) {
            int max = Math.max((int) Math.ceil(0.010416667f * f2), Math.round((i * (1.0f - f)) / 2.0f));
            int i4 = i - max;
            drawable.setBounds(max, max, i4, i4);
            drawable.draw(this.mCanvas);
        } else {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (createBitmap != null && bitmap.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f3 = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    i3 = (int) (i / f3);
                    i2 = i;
                } else if (intrinsicHeight > intrinsicWidth) {
                    i2 = (int) (i * f3);
                    i3 = i;
                }
                int i5 = (i - i2) / 2;
                int i6 = (i - i3) / 2;
                drawable.setBounds(i5, i6, i2 + i5, i3 + i6);
                this.mCanvas.save();
                float f4 = i / 2;
                this.mCanvas.scale(f, f, f4, f4);
                drawable.draw(this.mCanvas);
                this.mCanvas.restore();
            }
            i2 = i;
            i3 = i2;
            int i52 = (i - i2) / 2;
            int i62 = (i - i3) / 2;
            drawable.setBounds(i52, i62, i2 + i52, i3 + i62);
            this.mCanvas.save();
            float f42 = i / 2;
            this.mCanvas.scale(f, f, f42, f42);
            drawable.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        drawable.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap(null);
        return createBitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }
}
