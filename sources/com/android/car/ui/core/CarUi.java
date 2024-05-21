package com.android.car.ui.core;

import android.app.Activity;
import com.android.car.ui.baselayout.Insets;
import com.android.car.ui.toolbar.ToolbarController;
/* loaded from: classes.dex */
public class CarUi {
    public static ToolbarController getToolbar(Activity activity) {
        BaseLayoutController baseLayout = BaseLayoutController.getBaseLayout(activity);
        if (baseLayout != null) {
            return baseLayout.getToolbarController();
        }
        return null;
    }

    public static ToolbarController requireToolbar(Activity activity) {
        ToolbarController toolbar = getToolbar(activity);
        if (toolbar != null) {
            return toolbar;
        }
        throw new IllegalArgumentException("Activity does not have a CarUi Toolbar! Are you using Theme.CarUi.WithToolbar?");
    }

    public static Insets getInsets(Activity activity) {
        BaseLayoutController baseLayout = BaseLayoutController.getBaseLayout(activity);
        if (baseLayout != null) {
            return baseLayout.getInsets();
        }
        return null;
    }

    public static Insets requireInsets(Activity activity) {
        Insets insets = getInsets(activity);
        if (insets != null) {
            return insets;
        }
        throw new IllegalArgumentException("Activity does not have a base layout! Are you using Theme.CarUi.WithToolbar or Theme.CarUi.NoToolbar?");
    }
}
