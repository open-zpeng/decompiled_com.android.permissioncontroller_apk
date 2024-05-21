package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.icu.text.Collator;
import android.os.UserHandle;
import android.util.Pair;
import androidx.arch.core.util.Function;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class RoleSortFunction implements Function<List<Pair<ApplicationInfo, Boolean>>, List<Pair<ApplicationInfo, Boolean>>> {
    private final Comparator<Pair<ApplicationInfo, Boolean>> mComparator;

    public RoleSortFunction(final Context context) {
        this.mComparator = Comparator.comparing(new java.util.function.Function() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RoleSortFunction$Rs1UreJIXaxEyTNHhdBKYQ4GXPQ
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String appLabel;
                appLabel = Utils.getAppLabel((ApplicationInfo) ((Pair) obj).first, context);
                return appLabel;
            }
        }, Collator.getInstance(context.getResources().getConfiguration().getLocales().get(0))).thenComparing(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RoleSortFunction$v_4k5pEcW4KQlYVHK5zMRU0JoLo
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int identifier;
                identifier = UserHandle.getUserHandleForUid(((ApplicationInfo) ((Pair) obj).first).uid).getIdentifier();
                return identifier;
            }
        }));
    }

    @Override // androidx.arch.core.util.Function
    public List<Pair<ApplicationInfo, Boolean>> apply(List<Pair<ApplicationInfo, Boolean>> list) {
        ArrayList arrayList = new ArrayList(list);
        arrayList.sort(this.mComparator);
        return arrayList;
    }
}
