package com.android.car.ui.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.utils.CarUiUtils;
/* loaded from: classes.dex */
public class CarUiSmoothScroller extends LinearSmoothScroller {
    float mDecelerationTimeDivisor;
    int mDensityDpi;
    Interpolator mInterpolator;
    float mMillisecondsPerInch;
    float mMillisecondsPerPixel;

    @Override // androidx.recyclerview.widget.LinearSmoothScroller
    protected int getVerticalSnapPreference() {
        return -1;
    }

    public CarUiSmoothScroller(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mMillisecondsPerInch = CarUiUtils.getFloat(context.getResources(), R.dimen.car_ui_scrollbar_milliseconds_per_inch);
        this.mDecelerationTimeDivisor = CarUiUtils.getFloat(context.getResources(), R.dimen.car_ui_scrollbar_deceleration_times_divisor);
        this.mInterpolator = new DecelerateInterpolator(CarUiUtils.getFloat(context.getResources(), R.dimen.car_ui_scrollbar_decelerate_interpolator_factor));
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
        this.mMillisecondsPerPixel = this.mMillisecondsPerInch / this.mDensityDpi;
    }

    @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        int calculateTimeForDeceleration;
        int calculateDyToMakeVisible = calculateDyToMakeVisible(view, -1);
        if (calculateDyToMakeVisible != 0 && (calculateTimeForDeceleration = calculateTimeForDeceleration(calculateDyToMakeVisible)) > 0) {
            action.update(0, -calculateDyToMakeVisible, calculateTimeForDeceleration, this.mInterpolator);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.LinearSmoothScroller
    public int calculateTimeForScrolling(int i) {
        return (int) Math.ceil(Math.abs(i) * this.mMillisecondsPerPixel);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.LinearSmoothScroller
    public int calculateTimeForDeceleration(int i) {
        return (int) Math.ceil(calculateTimeForScrolling(i) / this.mDecelerationTimeDivisor);
    }
}
