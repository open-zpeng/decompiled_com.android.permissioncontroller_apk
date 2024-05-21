package com.xiaopeng.xui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.xiaopeng.xpui.R$id;
import com.xiaopeng.xpui.R$layout;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XCharacterUtils;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes.dex */
public class XToast {
    private XToast() {
    }

    private static Context getApplicationContext() {
        return Xui.getContext();
    }

    public static void show(int i) {
        show(Xui.getContext().getText(i));
    }

    public static void show(CharSequence charSequence) {
        if (charactersSize(charSequence) > 8) {
            showLong(charSequence);
        } else {
            showShort(charSequence);
        }
    }

    public static void showShort(int i) {
        showShort(Xui.getContext().getText(i));
    }

    public static void showShort(CharSequence charSequence) {
        show(charSequence, 0);
    }

    public static void showLong(int i) {
        showLong(Xui.getContext().getText(i));
    }

    public static void showLong(CharSequence charSequence) {
        show(charSequence, 1);
    }

    private static void show(CharSequence charSequence, int i) {
        Toast makeToast = makeToast(R$layout.x_toast);
        makeToast.setDuration(i);
        ((XTextView) makeToast.getView().findViewById(R$id.textView)).setText(charSequence);
        makeToast.show();
    }

    private static Toast makeToast(int i) {
        Context applicationContext = getApplicationContext();
        View inflate = LayoutInflater.from(applicationContext).inflate(i, (ViewGroup) null);
        Toast toast = new Toast(applicationContext);
        toast.setGravity(8388661, 0, 0);
        toast.setView(inflate);
        return toast;
    }

    private static int charactersSize(CharSequence charSequence) {
        String[] split;
        if (charSequence == null) {
            return 0;
        }
        int i = 0;
        for (String str : charSequence.toString().trim().split(" ")) {
            if (str.trim().length() != 0) {
                int i2 = i;
                boolean z = true;
                boolean z2 = false;
                for (int i3 = 0; i3 < str.length(); i3++) {
                    if (XCharacterUtils.isFullAngle(str.charAt(i3))) {
                        if (!z) {
                            i2++;
                        }
                        i2++;
                        z2 = true;
                        z = true;
                    } else {
                        z = false;
                    }
                }
                if (!z2 || !z) {
                    i2++;
                }
                i = i2;
            }
        }
        return i;
    }
}
