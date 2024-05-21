package com.android.packageinstaller.permission.ui;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
/* loaded from: classes.dex */
public interface GrantPermissionsViewHandler {

    /* loaded from: classes.dex */
    public interface ResultListener {
        void onPermissionGrantResult(String str, int i);
    }

    View createView();

    void loadInstanceState(Bundle bundle);

    void saveInstanceState(Bundle bundle);

    void updateUi(String str, int i, int i2, Icon icon, CharSequence charSequence, CharSequence charSequence2, CharSequence[] charSequenceArr);

    void updateWindowAttributes(WindowManager.LayoutParams layoutParams);
}
