package com.android.car.ui.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import androidx.preference.Preference;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes.dex */
public class CarUiSnapHelper extends LinearSnapHelper {
    private static final float LONG_ITEM_END_VISIBLE_THRESHOLD = 0.3f;
    private static final float VIEW_VISIBLE_THRESHOLD = 0.5f;
    private final Context mContext;
    private OrientationHelper mHorizontalHelper;
    private RecyclerView mRecyclerView;
    private OrientationHelper mVerticalHelper;

    public CarUiSnapHelper(Context context) {
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.LinearSnapHelper, androidx.recyclerview.widget.SnapHelper
    public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager layoutManager, View view) {
        int[] iArr = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            iArr[0] = distanceToTopMargin(view, getHorizontalHelper(layoutManager));
        }
        if (layoutManager.canScrollVertically()) {
            iArr[1] = distanceToTopMargin(view, getVerticalHelper(layoutManager));
        }
        return iArr;
    }

    public void smoothScrollBy(int i) {
        RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        int findTargetSnapPosition = findTargetSnapPosition(layoutManager, i);
        if (findTargetSnapPosition == -1) {
            this.mRecyclerView.smoothScrollBy(0, i);
            return;
        }
        RecyclerView.SmoothScroller createScroller = createScroller(layoutManager);
        if (createScroller == null) {
            return;
        }
        createScroller.setTargetPosition(findTargetSnapPosition);
        layoutManager.startSmoothScroll(createScroller);
    }

    private int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int i) {
        int itemCount;
        View findViewIfScrollable;
        int position;
        int i2;
        PointF computeScrollVectorForPosition;
        int i3;
        int i4;
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) || (itemCount = layoutManager.getItemCount()) == 0 || (findViewIfScrollable = findViewIfScrollable(layoutManager)) == null || (position = layoutManager.getPosition(findViewIfScrollable)) == -1 || (computeScrollVectorForPosition = ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(itemCount - 1)) == null) {
            return -1;
        }
        if (layoutManager.canScrollHorizontally()) {
            i3 = estimateNextPositionDiffForFling(layoutManager, getHorizontalHelper(layoutManager), i);
            if (computeScrollVectorForPosition.x < 0.0f) {
                i3 = -i3;
            }
        } else {
            i3 = 0;
        }
        if (layoutManager.canScrollVertically()) {
            i4 = estimateNextPositionDiffForFling(layoutManager, getVerticalHelper(layoutManager), i);
            if (computeScrollVectorForPosition.y < 0.0f) {
                i4 = -i4;
            }
        } else {
            i4 = 0;
        }
        if (!layoutManager.canScrollVertically()) {
            i4 = i3;
        }
        if (i4 == 0) {
            return -1;
        }
        int i5 = position + i4;
        if (i5 < 0) {
            i5 = 0;
        }
        return i5 >= itemCount ? i2 : i5;
    }

    @Override // androidx.recyclerview.widget.LinearSnapHelper, androidx.recyclerview.widget.SnapHelper
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        OrientationHelper orientationHelper = getOrientationHelper(layoutManager);
        if (childCount == 1) {
            View childAt = layoutManager.getChildAt(0);
            if (isValidSnapView(childAt, orientationHelper)) {
                return childAt;
            }
            return null;
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return null;
        }
        View childAt2 = recyclerView.getChildAt(0);
        if (childAt2.getHeight() <= this.mRecyclerView.getHeight() || orientationHelper.getDecoratedStart(childAt2) >= 0 || orientationHelper.getDecoratedEnd(childAt2) <= this.mRecyclerView.getHeight() * LONG_ITEM_END_VISIBLE_THRESHOLD) {
            View childAt3 = layoutManager.getChildAt(childCount - 1);
            boolean z = layoutManager.getPosition(childAt3) == layoutManager.getItemCount() - 1;
            float percentageVisible = z ? getPercentageVisible(childAt3, orientationHelper) : 0.0f;
            int i = Preference.DEFAULT_ORDER;
            float f = 0.0f;
            View view = null;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt4 = layoutManager.getChildAt(i2);
                int decoratedStart = orientationHelper.getDecoratedStart(childAt4);
                if (Math.abs(decoratedStart) < i) {
                    float percentageVisible2 = getPercentageVisible(childAt4, orientationHelper);
                    if (percentageVisible2 > VIEW_VISIBLE_THRESHOLD && percentageVisible2 > f) {
                        view = childAt4;
                        i = decoratedStart;
                        f = percentageVisible2;
                    }
                }
            }
            if (view != null && (!z || percentageVisible <= f)) {
                childAt3 = view;
            }
            if (isValidSnapView(childAt3, orientationHelper)) {
                return childAt3;
            }
            return null;
        }
        return null;
    }

    private View findViewIfScrollable(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findTopView(layoutManager, getVerticalHelper(layoutManager));
        }
        if (layoutManager.canScrollHorizontally()) {
            return findTopView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private static int distanceToTopMargin(View view, OrientationHelper orientationHelper) {
        return orientationHelper.getDecoratedStart(view) - orientationHelper.getStartAfterPadding();
    }

    private static View findTopView(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper) {
        int abs;
        int childCount = layoutManager.getChildCount();
        View view = null;
        if (childCount == 0) {
            return null;
        }
        int i = Preference.DEFAULT_ORDER;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = layoutManager.getChildAt(i2);
            if (childAt != null && (abs = Math.abs(distanceToTopMargin(childAt, orientationHelper))) < i) {
                view = childAt;
                i = abs;
            }
        }
        return view;
    }

    private boolean isValidSnapView(View view, OrientationHelper orientationHelper) {
        return orientationHelper.getDecoratedMeasurement(view) <= orientationHelper.getTotalSpace();
    }

    private float getPercentageVisible(View view, OrientationHelper orientationHelper) {
        float f;
        int decoratedMeasurement;
        int startAfterPadding = orientationHelper.getStartAfterPadding();
        int endAfterPadding = orientationHelper.getEndAfterPadding();
        int decoratedStart = orientationHelper.getDecoratedStart(view);
        int decoratedEnd = orientationHelper.getDecoratedEnd(view);
        if (decoratedStart < startAfterPadding || decoratedEnd > endAfterPadding) {
            if (decoratedEnd > startAfterPadding && decoratedStart < endAfterPadding) {
                if (decoratedStart <= startAfterPadding && decoratedEnd >= endAfterPadding) {
                    f = endAfterPadding - startAfterPadding;
                    decoratedMeasurement = orientationHelper.getDecoratedMeasurement(view);
                } else if (decoratedStart < startAfterPadding) {
                    f = decoratedEnd - startAfterPadding;
                    decoratedMeasurement = orientationHelper.getDecoratedMeasurement(view);
                } else {
                    return (endAfterPadding - decoratedStart) / orientationHelper.getDecoratedMeasurement(view);
                }
                return f / decoratedMeasurement;
            }
            return 0.0f;
        }
        return 1.0f;
    }

    @Override // androidx.recyclerview.widget.SnapHelper
    public void attachToRecyclerView(RecyclerView recyclerView) {
        super.attachToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @Override // androidx.recyclerview.widget.SnapHelper
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        return new CarUiSmoothScroller(this.mContext);
    }

    @Override // androidx.recyclerview.widget.SnapHelper
    public int[] calculateScrollDistance(int i, int i2) {
        RecyclerView.LayoutManager layoutManager;
        int[] calculateScrollDistance = super.calculateScrollDistance(i, i2);
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null && (layoutManager = recyclerView.getLayoutManager()) != null && layoutManager.getChildCount() != 0) {
            int childCount = isAtEnd(layoutManager) ? 0 : layoutManager.getChildCount() - 1;
            OrientationHelper orientationHelper = getOrientationHelper(layoutManager);
            View childAt = layoutManager.getChildAt(childCount);
            float percentageVisible = getPercentageVisible(childAt, orientationHelper);
            int height = layoutManager.getHeight();
            if (percentageVisible > 0.0f) {
                height -= layoutManager.getDecoratedMeasuredHeight(childAt);
            }
            int i3 = -height;
            calculateScrollDistance[0] = clamp(calculateScrollDistance[0], i3, height);
            calculateScrollDistance[1] = clamp(calculateScrollDistance[1], i3, height);
        }
        return calculateScrollDistance;
    }

    public boolean isAtStart(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null || layoutManager.getChildCount() == 0) {
            return true;
        }
        View childAt = layoutManager.getChildAt(0);
        OrientationHelper verticalHelper = layoutManager.canScrollVertically() ? getVerticalHelper(layoutManager) : getHorizontalHelper(layoutManager);
        return verticalHelper.getDecoratedStart(childAt) >= verticalHelper.getStartAfterPadding() && layoutManager.getPosition(childAt) == 0;
    }

    public boolean isAtEnd(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null || layoutManager.getChildCount() == 0) {
            return true;
        }
        int childCount = layoutManager.getChildCount();
        OrientationHelper verticalHelper = layoutManager.canScrollVertically() ? getVerticalHelper(layoutManager) : getHorizontalHelper(layoutManager);
        View childAt = layoutManager.getChildAt(childCount - 1);
        return layoutManager.getPosition(childAt) == layoutManager.getItemCount() - 1 && layoutManager.getDecoratedBottom(childAt) <= verticalHelper.getEndAfterPadding();
    }

    private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return getVerticalHelper(layoutManager);
        }
        return getHorizontalHelper(layoutManager);
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        OrientationHelper orientationHelper = this.mVerticalHelper;
        if (orientationHelper == null || orientationHelper.getLayoutManager() != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        OrientationHelper orientationHelper = this.mHorizontalHelper;
        if (orientationHelper == null || orientationHelper.getLayoutManager() != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }

    private static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i3, i));
    }

    private static int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper, int i) {
        int[] iArr = {i, i};
        float computeDistancePerChild = computeDistancePerChild(layoutManager, orientationHelper);
        if (computeDistancePerChild <= 0.0f) {
            return 0;
        }
        return Math.round((Math.abs(iArr[0]) > Math.abs(iArr[1]) ? iArr[0] : iArr[1]) / computeDistancePerChild);
    }

    private static float computeDistancePerChild(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return -1.0f;
        }
        View view = null;
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MAX_VALUE;
        View view2 = null;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = layoutManager.getChildAt(i3);
            int position = layoutManager.getPosition(childAt);
            if (position != -1) {
                if (position < i2) {
                    view = childAt;
                    i2 = position;
                }
                if (position > i) {
                    view2 = childAt;
                    i = position;
                }
            }
        }
        if (view == null || view2 == null) {
            return -1.0f;
        }
        int max = Math.max(orientationHelper.getDecoratedEnd(view), orientationHelper.getDecoratedEnd(view2)) - Math.min(orientationHelper.getDecoratedStart(view), orientationHelper.getDecoratedStart(view2));
        if (max == 0) {
            return -1.0f;
        }
        return (max * 1.0f) / ((i - i2) + 1);
    }
}
