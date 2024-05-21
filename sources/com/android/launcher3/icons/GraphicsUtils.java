package com.android.launcher3.icons;

import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
/* loaded from: classes.dex */
public class GraphicsUtils {
    public static int getArea(Region region) {
        RegionIterator regionIterator = new RegionIterator(region);
        Rect rect = new Rect();
        int i = 0;
        while (regionIterator.next(rect)) {
            i += rect.width() * rect.height();
        }
        return i;
    }
}
