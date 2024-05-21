package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ColorExtractor {
    private final int NUM_SAMPLES = 20;
    private final float[] mTmpHsv = new float[3];
    private final float[] mTmpHueScoreHistogram = new float[360];
    private final int[] mTmpPixels = new int[20];
    private final SparseArray<Float> mTmpRgbScores = new SparseArray<>();

    public int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public int findDominantColorByHue(Bitmap bitmap, int i) {
        int i2;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int sqrt = (int) Math.sqrt((height * width) / i);
        if (sqrt < 1) {
            sqrt = 1;
        }
        float[] fArr = this.mTmpHsv;
        Arrays.fill(fArr, 0.0f);
        float[] fArr2 = this.mTmpHueScoreHistogram;
        Arrays.fill(fArr2, 0.0f);
        int[] iArr = this.mTmpPixels;
        int i3 = 0;
        Arrays.fill(iArr, 0);
        int i4 = -1;
        int i5 = 0;
        int i6 = 0;
        float f = -1.0f;
        while (true) {
            i2 = -16777216;
            if (i5 >= height) {
                break;
            }
            float f2 = f;
            int i7 = i6;
            int i8 = i4;
            int i9 = i3;
            while (i9 < width) {
                int pixel = bitmap.getPixel(i9, i5);
                if (((pixel >> 24) & 255) >= 128) {
                    int i10 = pixel | (-16777216);
                    Color.colorToHSV(i10, fArr);
                    int i11 = (int) fArr[i3];
                    if (i11 >= 0 && i11 < fArr2.length) {
                        if (i7 < i) {
                            iArr[i7] = i10;
                            i7++;
                        }
                        fArr2[i11] = fArr2[i11] + (fArr[1] * fArr[2]);
                        if (fArr2[i11] > f2) {
                            f2 = fArr2[i11];
                            i8 = i11;
                        }
                    }
                }
                i9 += sqrt;
                i3 = 0;
            }
            i5 += sqrt;
            i4 = i8;
            i6 = i7;
            f = f2;
            i3 = 0;
        }
        SparseArray<Float> sparseArray = this.mTmpRgbScores;
        sparseArray.clear();
        float f3 = -1.0f;
        for (int i12 = 0; i12 < i6; i12++) {
            int i13 = iArr[i12];
            Color.colorToHSV(i13, fArr);
            if (((int) fArr[0]) == i4) {
                float f4 = fArr[1];
                float f5 = fArr[2];
                int i14 = ((int) (100.0f * f4)) + ((int) (10000.0f * f5));
                float f6 = f4 * f5;
                Float f7 = sparseArray.get(i14);
                if (f7 != null) {
                    f6 += f7.floatValue();
                }
                sparseArray.put(i14, Float.valueOf(f6));
                if (f6 > f3) {
                    i2 = i13;
                    f3 = f6;
                }
            }
        }
        return i2;
    }
}
