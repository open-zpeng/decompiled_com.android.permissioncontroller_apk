package com.android.packageinstaller.permission.utils;

import android.util.ArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> T firstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @SafeVarargs
    public static <T> boolean retainAll(ArraySet<T> arraySet, T... tArr) {
        boolean z = false;
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            if (!ArrayUtils.contains(tArr, arraySet.valueAt(size))) {
                arraySet.removeAt(size);
                z = true;
            }
        }
        return z;
    }

    public static <T> List<T> singletonOrEmpty(T t) {
        return t != null ? Collections.singletonList(t) : Collections.emptyList();
    }
}
