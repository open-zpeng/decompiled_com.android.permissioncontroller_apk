package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class IconNormalizer {
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IconNormalizer(Context context, int i, boolean z) {
        this.mMaxSize = i * 2;
        int i2 = this.mMaxSize;
        this.mBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ALPHA_8);
        this.mCanvas = new Canvas(this.mBitmap);
        int i3 = this.mMaxSize;
        this.mPixels = new byte[i3 * i3];
        this.mLeftBorder = new float[i3];
        this.mRightBorder = new float[i3];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        this.mPaintMaskShape = new Paint();
        this.mPaintMaskShape.setColor(-65536);
        this.mPaintMaskShape.setStyle(Paint.Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        this.mPaintMaskShapeOutline = new Paint();
        this.mPaintMaskShapeOutline.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Paint.Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = z;
    }

    private static float getScale(float f, float f2, float f3) {
        float f4 = f / f2;
        float f5 = f4 < 0.7853982f ? 0.6597222f : ((1.0f - f4) * 0.040449437f) + 0.6510417f;
        float f6 = f / f3;
        if (f6 > f5) {
            return (float) Math.sqrt(f5 / f6);
        }
        return 1.0f;
    }

    @TargetApi(26)
    public static float normalizeAdaptiveIcon(Drawable drawable, int i, RectF rectF) {
        Rect rect = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, i, i);
        Path iconMask = ((AdaptiveIconDrawable) drawable).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, i, i));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            float f = i;
            rectF.set(bounds.left / f, bounds.top / f, 1.0f - (bounds.right / f), 1.0f - (bounds.bottom / f));
        }
        drawable.setBounds(rect);
        float f2 = area;
        return getScale(f2, f2, i * i);
    }

    private boolean isShape(Path path) {
        if (Math.abs((this.mBounds.width() / this.mBounds.height()) - 1.0f) > 0.05f) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale(this.mBounds.width(), this.mBounds.height());
        Matrix matrix = this.mMatrix;
        Rect rect = this.mBounds;
        matrix.postTranslate(rect.left, rect.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        Rect rect;
        ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer(wrap);
        Rect rect2 = this.mBounds;
        int i = rect2.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - rect2.right;
        int i5 = i3;
        int i6 = 0;
        while (true) {
            rect = this.mBounds;
            if (i >= rect.bottom) {
                break;
            }
            int i7 = rect.left;
            int i8 = i5 + i7;
            while (i7 < this.mBounds.right) {
                if ((this.mPixels[i8] & 255) > 40) {
                    i6++;
                }
                i8++;
                i7++;
            }
            i5 = i8 + i4;
            i++;
        }
        return ((float) i6) / ((float) (rect.width() * this.mBounds.height())) < 0.005f;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0055, code lost:
        if (r4 <= r17.mMaxSize) goto L80;
     */
    /* JADX WARN: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00e5 A[Catch: all -> 0x0155, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000e, B:8:0x0012, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:22:0x003e, B:33:0x0061, B:37:0x0094, B:42:0x00a2, B:43:0x00aa, B:47:0x00bb, B:48:0x00c6, B:53:0x00d6, B:55:0x00e5, B:59:0x00fb, B:58:0x00f0, B:60:0x00fe, B:62:0x0110, B:65:0x0134, B:67:0x0138, B:69:0x013b, B:70:0x0144, B:24:0x0042, B:26:0x0053, B:30:0x005b, B:32:0x005f, B:28:0x0057), top: B:78:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0110 A[Catch: all -> 0x0155, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000e, B:8:0x0012, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:22:0x003e, B:33:0x0061, B:37:0x0094, B:42:0x00a2, B:43:0x00aa, B:47:0x00bb, B:48:0x00c6, B:53:0x00d6, B:55:0x00e5, B:59:0x00fb, B:58:0x00f0, B:60:0x00fe, B:62:0x0110, B:65:0x0134, B:67:0x0138, B:69:0x013b, B:70:0x0144, B:24:0x0042, B:26:0x0053, B:30:0x005b, B:32:0x005f, B:28:0x0057), top: B:78:0x0009 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized float getScale(android.graphics.drawable.Drawable r18, android.graphics.RectF r19, android.graphics.Path r20, boolean[] r21) {
        /*
            Method dump skipped, instructions count: 344
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] fArr, int i, int i2, int i3) {
        float[] fArr2 = new float[fArr.length - 1];
        int i4 = -1;
        float f = Float.MAX_VALUE;
        for (int i5 = i2 + 1; i5 <= i3; i5++) {
            if (fArr[i5] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i4 = i2;
                } else {
                    float f2 = ((fArr[i5] - fArr[i4]) / (i5 - i4)) - f;
                    float f3 = i;
                    if (f2 * f3 < 0.0f) {
                        while (i4 > i2) {
                            i4--;
                            if ((((fArr[i5] - fArr[i4]) / (i5 - i4)) - fArr2[i4]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (fArr[i5] - fArr[i4]) / (i5 - i4);
                for (int i6 = i4; i6 < i5; i6++) {
                    fArr2[i6] = f;
                    fArr[i6] = fArr[i4] + ((i6 - i4) * f);
                }
                i4 = i5;
            }
        }
    }
}
