package com.android.car.ui.recyclerview;

import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.utils.CarUiUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DefaultScrollBar implements ScrollBar {
    private float mButtonDisabledAlpha;
    private ImageView mDownButton;
    private OrientationHelper mOrientationHelper;
    int mPaddingEnd;
    int mPaddingStart;
    private RecyclerView mRecyclerView;
    private View mScrollThumb;
    private int mScrollThumbTrackHeight;
    private View mScrollView;
    private int mSeparatingMargin;
    private CarUiSnapHelper mSnapHelper;
    private ImageView mUpButton;
    private final Interpolator mPaginationInterpolator = new AccelerateDecelerateInterpolator();
    private final int mRowsPerPage = -1;
    private final Handler mHandler = new Handler();
    private final RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.android.car.ui.recyclerview.DefaultScrollBar.1
        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            DefaultScrollBar.this.updatePaginationButtons(false);
        }
    };

    /* loaded from: classes.dex */
    interface PaginationListener {
        public static final int PAGE_DOWN = 1;
        public static final int PAGE_UP = 0;

        void onPaginate(int i);
    }

    int getPage(int i) {
        return -1;
    }

    DefaultScrollBar() {
    }

    @Override // com.android.car.ui.recyclerview.ScrollBar
    public void initialize(RecyclerView recyclerView, View view) {
        this.mRecyclerView = recyclerView;
        this.mScrollView = view;
        Resources resources = recyclerView.getContext().getResources();
        this.mButtonDisabledAlpha = CarUiUtils.getFloat(resources, R.dimen.car_ui_button_disabled_alpha);
        getRecyclerView().addOnScrollListener(this.mRecyclerViewOnScrollListener);
        getRecyclerView().getRecycledViewPool().setMaxRecycledViews(0, 12);
        this.mSeparatingMargin = resources.getDimensionPixelSize(R.dimen.car_ui_scrollbar_separator_margin);
        this.mUpButton = (ImageView) CarUiUtils.requireViewByRefId(this.mScrollView, R.id.page_up);
        this.mUpButton.setOnClickListener(new PaginateButtonClickListener(0));
        this.mDownButton = (ImageView) CarUiUtils.requireViewByRefId(this.mScrollView, R.id.page_down);
        this.mDownButton.setOnClickListener(new PaginateButtonClickListener(1));
        this.mScrollThumb = CarUiUtils.requireViewByRefId(this.mScrollView, R.id.scrollbar_thumb);
        this.mSnapHelper = new CarUiSnapHelper(recyclerView.getContext());
        getRecyclerView().setOnFlingListener(null);
        this.mSnapHelper.attachToRecyclerView(getRecyclerView());
        this.mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$DefaultScrollBar$q6W8NNjXNWGK046-dsj7h2KAO-w
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                DefaultScrollBar.this.lambda$initialize$1$DefaultScrollBar(view2, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
    }

    public /* synthetic */ void lambda$initialize$1$DefaultScrollBar(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9 = i3 - i;
        OrientationHelper orientationHelper = getOrientationHelper(getRecyclerView().getLayoutManager());
        layoutViewCenteredFromTop(this.mUpButton, orientationHelper.getStartAfterPadding() + this.mPaddingStart, i9);
        layoutViewCenteredFromTop(this.mScrollThumb, this.mUpButton.getBottom() + this.mSeparatingMargin, i9);
        layoutViewCenteredFromBottom(this.mDownButton, orientationHelper.getEndAfterPadding() - this.mPaddingEnd, i9);
        this.mHandler.post(new Runnable() { // from class: com.android.car.ui.recyclerview.-$$Lambda$DefaultScrollBar$SSnK1ohxAIvAk4GwF6DIw7kMfXk
            @Override // java.lang.Runnable
            public final void run() {
                DefaultScrollBar.this.calculateScrollThumbTrackHeight();
            }
        });
        this.mHandler.post(new Runnable() { // from class: com.android.car.ui.recyclerview.-$$Lambda$DefaultScrollBar$FiJuIRSCHuDvt2lOiqwFkkTgMe8
            @Override // java.lang.Runnable
            public final void run() {
                DefaultScrollBar.this.lambda$initialize$0$DefaultScrollBar();
            }
        });
    }

    public /* synthetic */ void lambda$initialize$0$DefaultScrollBar() {
        updatePaginationButtons(false);
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    @Override // com.android.car.ui.recyclerview.ScrollBar
    public void requestLayout() {
        this.mScrollView.requestLayout();
    }

    @Override // com.android.car.ui.recyclerview.ScrollBar
    public void setPadding(int i, int i2) {
        this.mPaddingStart = i;
        this.mPaddingEnd = i2;
        requestLayout();
    }

    private void setUpEnabled(boolean z) {
        this.mUpButton.setEnabled(z);
        this.mUpButton.setAlpha(z ? 1.0f : this.mButtonDisabledAlpha);
    }

    private void setDownEnabled(boolean z) {
        this.mDownButton.setEnabled(z);
        this.mDownButton.setAlpha(z ? 1.0f : this.mButtonDisabledAlpha);
    }

    private boolean isDownEnabled() {
        return this.mDownButton.isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateScrollThumbTrackHeight() {
        this.mScrollThumbTrackHeight = this.mDownButton.getTop() - (this.mSeparatingMargin * 2);
        this.mScrollThumbTrackHeight -= this.mUpButton.getBottom();
    }

    private static void layoutViewCenteredFromTop(View view, int i, int i2) {
        int measuredWidth = view.getMeasuredWidth();
        int i3 = (i2 - measuredWidth) / 2;
        view.layout(i3, i, measuredWidth + i3, view.getMeasuredHeight() + i);
    }

    private static void layoutViewCenteredFromBottom(View view, int i, int i2) {
        int measuredWidth = view.getMeasuredWidth();
        int i3 = (i2 - measuredWidth) / 2;
        view.layout(i3, i - view.getMeasuredHeight(), measuredWidth + i3, i);
    }

    private void setParameters(int i, int i2, int i3, boolean z) {
        if (!this.mScrollView.isLaidOut() || this.mScrollView.getVisibility() == 8 || i == 0) {
            return;
        }
        int calculateScrollThumbLength = calculateScrollThumbLength(i, i3);
        int calculateScrollThumbOffset = calculateScrollThumbOffset(i, i2, calculateScrollThumbLength);
        ViewGroup.LayoutParams layoutParams = this.mScrollThumb.getLayoutParams();
        if (layoutParams.height != calculateScrollThumbLength) {
            layoutParams.height = calculateScrollThumbLength;
            this.mScrollThumb.requestLayout();
        }
        moveY(this.mScrollThumb, calculateScrollThumbOffset, z);
    }

    private int calculateScrollThumbLength(int i, int i2) {
        return Math.round((i2 / i) * this.mScrollThumbTrackHeight);
    }

    private int calculateScrollThumbOffset(int i, int i2, int i3) {
        int i4;
        int top = this.mScrollThumb.getTop();
        if (isDownEnabled()) {
            i4 = Math.round((i2 / i) * this.mScrollThumbTrackHeight);
        } else {
            i4 = this.mScrollThumbTrackHeight - i3;
        }
        return top + i4;
    }

    private void moveY(View view, float f, boolean z) {
        view.animate().y(f).setDuration(z ? 200 : 0).setInterpolator(this.mPaginationInterpolator).start();
    }

    /* loaded from: classes.dex */
    private class PaginateButtonClickListener implements View.OnClickListener {
        private final int mPaginateDirection;
        private PaginationListener mPaginationListener;

        PaginateButtonClickListener(int i) {
            this.mPaginateDirection = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PaginationListener paginationListener = this.mPaginationListener;
            if (paginationListener != null) {
                paginationListener.onPaginate(this.mPaginateDirection);
            }
            int i = this.mPaginateDirection;
            if (i == 1) {
                DefaultScrollBar.this.pageDown();
            } else if (i == 0) {
                DefaultScrollBar.this.pageUp();
            }
        }
    }

    private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager layoutManager) {
        OrientationHelper orientationHelper = this.mOrientationHelper;
        if (orientationHelper == null || orientationHelper.getLayoutManager() != layoutManager) {
            this.mOrientationHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mOrientationHelper;
    }

    void pageUp() {
        int computeVerticalScrollOffset = getRecyclerView().computeVerticalScrollOffset();
        if (getRecyclerView().getLayoutManager() == null || getRecyclerView().getChildCount() == 0 || computeVerticalScrollOffset == 0) {
            return;
        }
        OrientationHelper orientationHelper = getOrientationHelper(getRecyclerView().getLayoutManager());
        int totalSpace = orientationHelper.getTotalSpace();
        int i = 0;
        while (true) {
            if (i >= getRecyclerView().getChildCount()) {
                break;
            }
            View childAt = getRecyclerView().getChildAt(i);
            if (childAt.getHeight() <= totalSpace) {
                i++;
            } else if (orientationHelper.getDecoratedEnd(childAt) < totalSpace) {
                totalSpace -= orientationHelper.getDecoratedEnd(childAt);
            } else if ((-totalSpace) < orientationHelper.getDecoratedStart(childAt) && orientationHelper.getDecoratedStart(childAt) < 0) {
                totalSpace = Math.abs(orientationHelper.getDecoratedStart(childAt));
            }
        }
        this.mSnapHelper.smoothScrollBy(-totalSpace);
    }

    void pageDown() {
        int i;
        if (getRecyclerView().getLayoutManager() == null || getRecyclerView().getChildCount() == 0) {
            return;
        }
        OrientationHelper orientationHelper = getOrientationHelper(getRecyclerView().getLayoutManager());
        int totalSpace = orientationHelper.getTotalSpace();
        View childAt = getRecyclerView().getChildAt(getRecyclerView().getChildCount() - 1);
        if (!getRecyclerView().getLayoutManager().isViewPartiallyVisible(childAt, false, false) || (i = orientationHelper.getDecoratedStart(childAt)) <= 0) {
            i = totalSpace;
        }
        int childCount = getRecyclerView().getChildCount() - 1;
        while (true) {
            if (childCount < 0) {
                break;
            }
            View childAt2 = getRecyclerView().getChildAt(childCount);
            if (childAt2.getHeight() <= totalSpace) {
                childCount--;
            } else if (orientationHelper.getDecoratedStart(childAt2) > 0) {
                i = orientationHelper.getDecoratedStart(childAt2);
            } else if (totalSpace < orientationHelper.getDecoratedEnd(childAt2) && orientationHelper.getDecoratedEnd(childAt2) < totalSpace * 2) {
                i = orientationHelper.getDecoratedEnd(childAt2) - totalSpace;
            }
        }
        this.mSnapHelper.smoothScrollBy(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePaginationButtons(boolean z) {
        boolean isAtStart = isAtStart();
        boolean isAtEnd = isAtEnd();
        RecyclerView.LayoutManager layoutManager = getRecyclerView().getLayoutManager();
        if ((isAtStart && isAtEnd) || layoutManager == null || layoutManager.getItemCount() == 0) {
            this.mScrollView.setVisibility(4);
        } else {
            this.mScrollView.setVisibility(0);
        }
        setUpEnabled(!isAtStart);
        setDownEnabled(!isAtEnd);
        if (layoutManager == null) {
            return;
        }
        if (layoutManager.canScrollVertically()) {
            setParameters(getRecyclerView().computeVerticalScrollRange(), getRecyclerView().computeVerticalScrollOffset(), getRecyclerView().computeVerticalScrollExtent(), z);
        } else {
            setParameters(getRecyclerView().computeHorizontalScrollRange(), getRecyclerView().computeHorizontalScrollOffset(), getRecyclerView().computeHorizontalScrollExtent(), z);
        }
        this.mScrollView.invalidate();
    }

    boolean isAtStart() {
        return this.mSnapHelper.isAtStart(getRecyclerView().getLayoutManager());
    }

    boolean isAtEnd() {
        return this.mSnapHelper.isAtEnd(getRecyclerView().getLayoutManager());
    }
}
