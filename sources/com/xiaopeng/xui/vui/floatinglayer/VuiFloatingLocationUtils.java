package com.xiaopeng.xui.vui.floatinglayer;

import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayer;
/* loaded from: classes.dex */
class VuiFloatingLocationUtils {
    private static final String TAG = "VuiFloatingLocation";

    VuiFloatingLocationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] getLocation(int i, VuiFloatingLayer.LayerInfo layerInfo, int i2, int i3) {
        int[] iArr = new int[2];
        if (layerInfo == null) {
            return iArr;
        }
        int[] iArr2 = layerInfo.location;
        int i4 = iArr2[0];
        if (i == 0) {
            int i5 = layerInfo.targetWidth;
            int i6 = ((i5 / 2) + i4) - (i2 / 2);
            int i7 = (iArr2[1] + (layerInfo.targetHeight / 2)) - (i3 / 2);
            int i8 = i6 + layerInfo.mCenterOffsetX;
            int i9 = i7 + layerInfo.mCenterOffsetY;
            if (i8 < i4 || i8 > i4 + i5) {
                log("offset more or less than current view width");
            }
            iArr[0] = i8;
            iArr[1] = i9;
        } else if (i == 1) {
            int i10 = layerInfo.targetWidth;
            int i11 = (((i10 / 2) + i4) - (i2 / 2)) + layerInfo.mCenterOffsetX;
            int i12 = (iArr2[1] - i3) + 35 + layerInfo.mCenterOffsetY;
            if (i11 < i4 || i11 > i4 + i10) {
                log("offset more or less than current view width");
            }
            iArr[0] = i11;
            iArr[1] = i12;
        }
        return iArr;
    }

    private static void log(String str) {
        XLogUtils.v(TAG, str);
    }
}
