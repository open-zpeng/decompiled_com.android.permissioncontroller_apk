package com.android.car.ui.recyclerview.decorations.linear;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class LinearDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;
    private int mOrientation;

    public LinearDividerItemDecoration(Drawable drawable) {
        this.mDivider = drawable;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        int i = this.mOrientation;
        if (i == 0) {
            drawHorizontalDividers(canvas, recyclerView);
        } else if (i == 1) {
            drawVerticalDividers(canvas, recyclerView);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        if (recyclerView.getChildAdapterPosition(view) == 0) {
            return;
        }
        this.mOrientation = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
        int i = this.mOrientation;
        if (i == 0) {
            rect.left = this.mDivider.getIntrinsicWidth();
        } else if (i == 1) {
            rect.top = this.mDivider.getIntrinsicHeight();
        }
    }

    private void drawHorizontalDividers(Canvas canvas, RecyclerView recyclerView) {
        int paddingTop = recyclerView.getPaddingTop() + ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_top_margin));
        int height = (recyclerView.getHeight() - recyclerView.getPaddingBottom()) - ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_bottom_margin));
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View childAt = recyclerView.getChildAt(i);
            int right = childAt.getRight() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt.getLayoutParams())).rightMargin;
            this.mDivider.setBounds(right, paddingTop, this.mDivider.getIntrinsicWidth() + right, height);
            this.mDivider.draw(canvas);
        }
    }

    private void drawVerticalDividers(Canvas canvas, RecyclerView recyclerView) {
        int paddingLeft = recyclerView.getPaddingLeft() + ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_start_margin));
        int width = (recyclerView.getWidth() - recyclerView.getPaddingRight()) - ((int) recyclerView.getContext().getResources().getDimension(R.dimen.car_ui_recyclerview_divider_end_margin));
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View childAt = recyclerView.getChildAt(i);
            int bottom = childAt.getBottom() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt.getLayoutParams())).bottomMargin;
            this.mDivider.setBounds(paddingLeft, bottom, width, this.mDivider.getIntrinsicHeight() + bottom);
            this.mDivider.draw(canvas);
        }
    }
}
