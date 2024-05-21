package com.android.car.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
/* loaded from: classes.dex */
public final class CarUiUtils {
    private CarUiUtils() {
    }

    public static float getFloat(Resources resources, int i) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        return typedValue.getFloat();
    }

    public static int getAttrResourceId(Context context, int i) {
        return getAttrResourceId(context, 0, i);
    }

    public static int getAttrResourceId(Context context, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, new int[]{i2});
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        return resourceId;
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static <T extends View> T findViewByRefId(View view, int i) {
        if (i == -1) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        view.getResources().getValue(i, typedValue, true);
        return (T) view.findViewById(typedValue.resourceId);
    }

    public static <T extends View> T requireViewByRefId(View view, int i) {
        T t = (T) findViewByRefId(view, i);
        if (t != null) {
            return t;
        }
        throw new IllegalArgumentException("ID does not reference a View inside this View");
    }
}
