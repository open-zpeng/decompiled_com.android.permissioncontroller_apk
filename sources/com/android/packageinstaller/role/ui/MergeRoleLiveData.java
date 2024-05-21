package com.android.packageinstaller.role.ui;

import android.content.pm.ApplicationInfo;
import android.util.Pair;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MergeRoleLiveData extends MediatorLiveData<List<Pair<ApplicationInfo, Boolean>>> {
    private final RoleLiveData[] mLiveDatas;

    public MergeRoleLiveData(RoleLiveData... roleLiveDataArr) {
        this.mLiveDatas = roleLiveDataArr;
        int length = this.mLiveDatas.length;
        for (int i = 0; i < length; i++) {
            addSource(this.mLiveDatas[i], new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$MergeRoleLiveData$dyFBsfsbEltKpIQItwgVGnUs4qE
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    MergeRoleLiveData.this.lambda$new$0$MergeRoleLiveData((List) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$MergeRoleLiveData(List list) {
        onRoleChanged();
    }

    private void onRoleChanged() {
        ArrayList arrayList = new ArrayList();
        int length = this.mLiveDatas.length;
        for (int i = 0; i < length; i++) {
            List<Pair<ApplicationInfo, Boolean>> value = this.mLiveDatas[i].getValue();
            if (value == null) {
                return;
            }
            arrayList.addAll(value);
        }
        setValue(arrayList);
    }
}
