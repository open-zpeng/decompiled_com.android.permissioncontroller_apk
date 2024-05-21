package com.android.car.ui.recyclerview.decorations.grid;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class GridOffsetItemDecoration extends RecyclerView.ItemDecoration {
    private int mNumColumns;
    private Drawable mOffsetDrawable;
    private final int mOffsetPosition;
    private int mOffsetPx;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface OffsetPosition {
        public static final int END = 1;
        public static final int START = 0;
    }

    public GridOffsetItemDecoration(int i, int i2, int i3) {
        this.mOffsetPx = i;
        this.mNumColumns = i2;
        this.mOffsetPosition = i3;
    }

    public GridOffsetItemDecoration(Drawable drawable, int i, int i2) {
        this.mOffsetDrawable = drawable;
        this.mNumColumns = i;
        this.mOffsetPosition = i2;
    }

    public void setNumOfColumns(int i) {
        this.mNumColumns = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        if (this.mOffsetPosition == 0) {
            if (recyclerView.getChildAdapterPosition(view) < this.mNumColumns) {
                int i = this.mOffsetPx;
                if (i > 0) {
                    rect.top = i;
                    return;
                }
                Drawable drawable = this.mOffsetDrawable;
                if (drawable != null) {
                    rect.top = drawable.getIntrinsicHeight();
                    return;
                }
                return;
            }
            return;
        }
        int itemCount = state.getItemCount();
        if (recyclerView.getChildAdapterPosition(view) >= itemCount - getLastRowChildCount(itemCount)) {
            int i2 = this.mOffsetPx;
            if (i2 > 0) {
                rect.bottom = i2;
                return;
            }
            Drawable drawable2 = this.mOffsetDrawable;
            if (drawable2 != null) {
                rect.bottom = drawable2.getIntrinsicHeight();
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDraw(canvas, recyclerView, state);
        if (this.mOffsetDrawable == null) {
            return;
        }
        int paddingLeft = recyclerView.getPaddingLeft();
        int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
        if (this.mOffsetPosition == 0) {
            int paddingTop = recyclerView.getPaddingTop();
            this.mOffsetDrawable.setBounds(paddingLeft, paddingTop, width, this.mOffsetDrawable.getIntrinsicHeight() + paddingTop);
            this.mOffsetDrawable.draw(canvas);
            return;
        }
        int itemCount = state.getItemCount();
        int i = 0;
        int i2 = 0;
        for (int lastRowChildCount = itemCount - getLastRowChildCount(itemCount); lastRowChildCount < itemCount; lastRowChildCount++) {
            i = recyclerView.getChildAt(lastRowChildCount).getBottom();
            i2 = this.mOffsetDrawable.getIntrinsicHeight() + i;
        }
        this.mOffsetDrawable.setBounds(paddingLeft, i, width, i2);
        this.mOffsetDrawable.draw(canvas);
    }

    private int getLastRowChildCount(int i) {
        int i2 = this.mNumColumns;
        int i3 = i % i2;
        return i3 == 0 ? i2 : i3;
    }
}
