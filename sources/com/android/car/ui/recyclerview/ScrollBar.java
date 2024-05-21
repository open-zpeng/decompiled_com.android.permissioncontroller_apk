package com.android.car.ui.recyclerview;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes.dex */
public interface ScrollBar {
    void initialize(RecyclerView recyclerView, View view);

    void requestLayout();

    void setPadding(int i, int i2);
}
