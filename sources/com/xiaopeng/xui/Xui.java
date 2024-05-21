package com.xiaopeng.xui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.xiaopeng.xui.drawable.shimmer.XShimmer;
import com.xiaopeng.xui.utils.XLogUtils;
/* loaded from: classes.dex */
public class Xui {
    private static Application mApp;
    private static boolean sDialogFullScreen;
    private static boolean sFontScaleDynamicChangeEnable;
    private static boolean sVuiEnable;

    public static void clear() {
    }

    public static void release() {
    }

    public static void init(Application application) {
        mApp = application;
        Log.i("xpui", "0.7.9-merge-SNAPSHOT_release_0.7_5f94aa58_2021/07/12 20:00:05");
        XShimmer.msGlobalEnable = false;
    }

    public static Context getContext() {
        Application application = mApp;
        if (application != null) {
            return application;
        }
        throw new RuntimeException("Xui must be call Xui#init()!");
    }

    public static boolean isVuiEnable() {
        return sVuiEnable;
    }

    public static void setVuiEnable(boolean z) {
        sVuiEnable = z;
    }

    public static boolean isFontScaleDynamicChangeEnable() {
        return sFontScaleDynamicChangeEnable;
    }

    public static void setFontScaleDynamicChangeEnable(boolean z) {
        sFontScaleDynamicChangeEnable = z;
    }

    public static boolean isDialogFullScreen() {
        return sDialogFullScreen;
    }

    public static void setDialogFullScreen(boolean z) {
        sDialogFullScreen = z;
    }

    public static void setLogLevel(int i) {
        XLogUtils.setLogLevel(i);
    }
}
