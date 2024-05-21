package com.android.packageinstaller.role.ui;

import android.util.ArrayMap;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MergeRoleListLiveData extends MediatorLiveData<List<RoleItem>> {
    private final RoleListLiveData[] mLiveDatas;

    public MergeRoleListLiveData(RoleListLiveData... roleListLiveDataArr) {
        this.mLiveDatas = roleListLiveDataArr;
        int length = this.mLiveDatas.length;
        for (int i = 0; i < length; i++) {
            addSource(this.mLiveDatas[i], new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$MergeRoleListLiveData$wjLTk64YCz3-Emtk5peJQNqCfeI
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    MergeRoleListLiveData.this.lambda$new$0$MergeRoleListLiveData((List) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$MergeRoleListLiveData(List list) {
        onRoleListChanged();
    }

    private void onRoleListChanged() {
        ArrayMap arrayMap = new ArrayMap();
        int length = this.mLiveDatas.length;
        for (int i = 0; i < length; i++) {
            List<RoleItem> value = this.mLiveDatas[i].getValue();
            if (value == null) {
                return;
            }
            int size = value.size();
            for (int i2 = 0; i2 < size; i2++) {
                RoleItem roleItem = value.get(i2);
                String name = roleItem.getRole().getName();
                RoleItem roleItem2 = (RoleItem) arrayMap.get(name);
                if (roleItem2 == null) {
                    arrayMap.put(name, new RoleItem(roleItem.getRole(), new ArrayList(roleItem.getHolderApplicationInfos())));
                } else {
                    roleItem2.getHolderApplicationInfos().addAll(roleItem.getHolderApplicationInfos());
                }
            }
        }
        setValue(new ArrayList(arrayMap.values()));
    }
}
