package com.xiaopeng.xui.vui.floatinglayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import com.xiaopeng.xpui.R$drawable;
import com.xiaopeng.xui.utils.XLogUtils;
import java.io.IOException;
/* loaded from: classes.dex */
public class VuiImageDecoderUtils {
    private static final String TAG = "VuiImageDecoderUtils";
    private static final String TOUCH_DEFAULT_WEBP = "anim/floating_touch.webp";

    public static int getAnimateTimeOut(int i) {
        return i != 1 ? 1500 : 5500;
    }

    public static boolean isSupportAlpha(int i) {
        return false;
    }

    public static boolean isSupportNight(int i) {
        return i == 1;
    }

    @TargetApi(28)
    public static Drawable decoderImage(Context context, int i, boolean z) {
        String str = TAG;
        XLogUtils.d(str, "decoderImage type : " + i + ", isNight : " + z);
        if (i == 1) {
            return new VuiFloatingDrawable(BitmapFactory.decodeResource(context.getResources(), R$drawable.floating_element));
        }
        try {
            return ImageDecoder.decodeDrawable(ImageDecoder.createSource(context.getAssets(), TOUCH_DEFAULT_WEBP));
        } catch (IOException e) {
            XLogUtils.w(TAG, "decodeException:", e);
            return null;
        }
    }

    @TargetApi(28)
    public static Drawable decoderImage(Context context, int i) {
        return decoderImage(context, i, false);
    }
}
