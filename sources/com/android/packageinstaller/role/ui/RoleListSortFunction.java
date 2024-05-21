package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.icu.text.Collator;
import androidx.arch.core.util.Function;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class RoleListSortFunction implements Function<List<RoleItem>, List<RoleItem>> {
    private final Comparator<RoleItem> mComparator;

    public RoleListSortFunction(final Context context) {
        this.mComparator = Comparator.comparing(new java.util.function.Function() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RoleListSortFunction$TS-l3HyoXOf86cCkBt-wRX9c0Us
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String string;
                string = context.getString(((RoleItem) obj).getRole().getShortLabelResource());
                return string;
            }
        }, Collator.getInstance(context.getResources().getConfiguration().getLocales().get(0)));
    }

    @Override // androidx.arch.core.util.Function
    public List<RoleItem> apply(List<RoleItem> list) {
        ArrayList arrayList = new ArrayList(list);
        arrayList.sort(this.mComparator);
        return arrayList;
    }
}
