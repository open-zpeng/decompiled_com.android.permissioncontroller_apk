package com.android.car.ui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.utils.CarUiUtils;
/* loaded from: classes.dex */
final class CarUiRecyclerViewAdapter extends RecyclerView.Adapter<NestedRowViewHolder> {
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(NestedRowViewHolder nestedRowViewHolder, int i) {
    }

    CarUiRecyclerViewAdapter() {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public NestedRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NestedRowViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.car_ui_recycler_view_item, viewGroup, false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class NestedRowViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout frameLayout;

        NestedRowViewHolder(View view) {
            super(view);
            this.frameLayout = (FrameLayout) CarUiUtils.requireViewByRefId(view, R.id.nested_recycler_view_layout);
        }
    }
}
