package com.android.car.ui.recyclerview.decorations.linear;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class LinearOffsetItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mOffsetDrawable;
    private int mOffsetPosition;
    private int mOffsetPx;
    private int mOrientation;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface OffsetPosition {
        public static final int END = 1;
        public static final int START = 0;
    }

    public LinearOffsetItemDecoration(int i, int i2) {
        this.mOffsetPx = i;
        this.mOffsetPosition = i2;
    }

    public LinearOffsetItemDecoration(Drawable drawable) {
        this.mOffsetDrawable = drawable;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        if (this.mOffsetPosition != 0 || recyclerView.getChildAdapterPosition(view) <= 0) {
            int itemCount = state.getItemCount();
            if (this.mOffsetPosition != 1 || recyclerView.getChildAdapterPosition(view) == itemCount - 1) {
                this.mOrientation = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
                int i = this.mOrientation;
                if (i == 0) {
                    int i2 = this.mOffsetPx;
                    if (i2 > 0) {
                        if (this.mOffsetPosition == 0) {
                            rect.left = i2;
                            return;
                        } else {
                            rect.right = i2;
                            return;
                        }
                    }
                    Drawable drawable = this.mOffsetDrawable;
                    if (drawable != null) {
                        if (this.mOffsetPosition == 0) {
                            rect.left = drawable.getIntrinsicWidth();
                        } else {
                            rect.right = drawable.getIntrinsicWidth();
                        }
                    }
                } else if (i == 1) {
                    int i3 = this.mOffsetPx;
                    if (i3 > 0) {
                        if (this.mOffsetPosition == 0) {
                            rect.top = i3;
                            return;
                        } else {
                            rect.bottom = i3;
                            return;
                        }
                    }
                    Drawable drawable2 = this.mOffsetDrawable;
                    if (drawable2 != null) {
                        if (this.mOffsetPosition == 0) {
                            rect.top = drawable2.getIntrinsicHeight();
                        } else {
                            rect.bottom = drawable2.getIntrinsicHeight();
                        }
                    }
                }
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDraw(canvas, recyclerView, state);
        if (this.mOffsetDrawable == null) {
            return;
        }
        int i = this.mOrientation;
        if (i == 0) {
            drawOffsetHorizontal(canvas, recyclerView);
        } else if (i == 1) {
            drawOffsetVertical(canvas, recyclerView);
        }
    }

    private void drawOffsetHorizontal(Canvas canvas, RecyclerView recyclerView) {
        int right;
        int intrinsicWidth;
        int paddingTop = recyclerView.getPaddingTop();
        int height = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        if (this.mOffsetPosition == 0) {
            right = recyclerView.getPaddingLeft();
            intrinsicWidth = this.mOffsetDrawable.getIntrinsicWidth();
        } else {
            View childAt = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
            right = childAt.getRight() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt.getLayoutParams())).rightMargin;
            intrinsicWidth = this.mOffsetDrawable.getIntrinsicWidth();
        }
        this.mOffsetDrawable.setBounds(right, paddingTop, intrinsicWidth + right, height);
        this.mOffsetDrawable.draw(canvas);
    }

    private void drawOffsetVertical(Canvas canvas, RecyclerView recyclerView) {
        int bottom;
        int intrinsicHeight;
        int paddingLeft = recyclerView.getPaddingLeft();
        int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
        if (this.mOffsetPosition == 0) {
            bottom = recyclerView.getPaddingTop();
            intrinsicHeight = this.mOffsetDrawable.getIntrinsicHeight();
        } else {
            View childAt = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
            bottom = childAt.getBottom() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt.getLayoutParams())).bottomMargin;
            intrinsicHeight = this.mOffsetDrawable.getIntrinsicHeight();
        }
        this.mOffsetDrawable.setBounds(paddingLeft, bottom, width, intrinsicHeight + bottom);
        this.mOffsetDrawable.draw(canvas);
    }
}
