package com.xiaopeng.xui.utils;
/* loaded from: classes.dex */
public class XKeyEventUtils {
    private static final int KEYCODE_CAR_ENTER = 1015;

    public static boolean isCancel(int i) {
        return i == 4 || i == 111;
    }

    public static boolean isEnter(int i) {
        return i == KEYCODE_CAR_ENTER || i == 66;
    }
}