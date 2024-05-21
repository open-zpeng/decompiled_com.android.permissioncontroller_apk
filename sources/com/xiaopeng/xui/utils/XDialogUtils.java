package com.xiaopeng.xui.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import com.xiaopeng.xpui.R$style;
/* loaded from: classes.dex */
public class XDialogUtils {
    @Deprecated
    public static void setHorizontalCenter(Dialog dialog) {
        Window window;
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 1;
        attributes.x = 0;
        window.setAttributes(attributes);
    }

    @Deprecated
    public static void setHorizontal(Dialog dialog, int i) {
        Window window;
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 8388611;
        attributes.x = i;
        window.setAttributes(attributes);
    }

    @Deprecated
    public static void setVerticalCenter(Dialog dialog, int i) {
        Window window;
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 16;
        attributes.y = 0;
        window.setAttributes(attributes);
    }

    @Deprecated
    public static void setVertical(Dialog dialog, int i) {
        Window window;
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 48;
        attributes.y = i;
        window.setAttributes(attributes);
    }

    @Deprecated
    public static void setVerticalHorizontal(Dialog dialog, int i, int i2) {
        Window window;
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 8388659;
        attributes.x = i;
        attributes.y = i2;
        window.setAttributes(attributes);
    }

    public static Dialog createThemeDialog(Context context) {
        return new Dialog(context, R$style.XAppTheme_XDialog);
    }

    public static void setSystemDialog(Dialog dialog, int i) {
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().setType(i);
    }

    public static void handleSoftInput(Dialog dialog) {
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().setSoftInputMode(16);
    }

    public static void requestFullScreen(Dialog dialog) {
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().requestFeature(15);
    }
}
