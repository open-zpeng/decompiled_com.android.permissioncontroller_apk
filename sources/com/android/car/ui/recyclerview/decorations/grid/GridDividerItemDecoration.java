package com.android.car.ui.recyclerview.decorations.grid;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mHorizontalDivider;
    private int mNumColumns;
    private final Drawable mVerticalDivider;

    public GridDividerItemDecoration(Drawable drawable, Drawable drawable2, int i) {
        this.mHorizontalDivider = drawable;
        this.mVerticalDivider = drawable2;
        this.mNumColumns = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        drawVerticalDividers(canvas, recyclerView);
        drawHorizontalDividers(canvas, recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        rect.set(0, 0, this.mHorizontalDivider.getIntrinsicWidth(), this.mHorizontalDivider.getIntrinsicHeight());
    }

    private void drawHorizontalDividers(Canvas canvas, RecyclerView recyclerView) {
        int i;
        int childCount = recyclerView.getChildCount();
        int i2 = this.mNumColumns;
        int i3 = childCount / i2;
        int i4 = childCount % i2;
        int min = Math.min(childCount, i2);
        for (int i5 = 1; i5 < min; i5++) {
            if (i5 < i4) {
                i = this.mNumColumns * i3;
            } else {
                i = (i3 - 1) * this.mNumColumns;
            }
            View childAt = recyclerView.getChildAt(i5);
            View childAt2 = recyclerView.getChildAt(i + i5);
            int top = childAt.getTop() + ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_top_margin));
            int left = childAt.getLeft();
            int bottom = childAt2.getBottom() - ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_bottom_margin));
            this.mHorizontalDivider.setBounds(left - this.mHorizontalDivider.getIntrinsicWidth(), top, left, bottom);
            this.mHorizontalDivider.draw(canvas);
        }
    }

    public void setNumOfColumns(int i) {
        this.mNumColumns = i;
    }

    private void drawVerticalDividers(Canvas canvas, RecyclerView recyclerView) {
        int i;
        double ceil = Math.ceil(recyclerView.getChildCount() / this.mNumColumns);
        int i2 = 1;
        while (true) {
            double d = i2;
            if (d > ceil) {
                return;
            }
            if (i2 != 1) {
                if (d == ceil) {
                    i = (i2 - 1) * this.mNumColumns;
                } else {
                    i = this.mNumColumns * i2;
                }
                View childAt = recyclerView.getChildAt(this.mNumColumns * (i2 - 1));
                View childAt2 = recyclerView.getChildAt(i - 1);
                int top = childAt.getTop();
                this.mVerticalDivider.setBounds(childAt.getLeft() + ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_start_margin)), top - this.mVerticalDivider.getIntrinsicHeight(), childAt2.getRight() - ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_end_margin)), top);
                this.mVerticalDivider.draw(canvas);
            }
            i2++;
        }
    }
}
