package com.android.car.ui.core;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.car.ui.R;
import com.android.car.ui.baselayout.Insets;
import com.android.car.ui.baselayout.InsetsChangedListener;
import com.android.car.ui.core.BaseLayoutController;
import com.android.car.ui.toolbar.ToolbarController;
import com.android.car.ui.toolbar.ToolbarControllerImpl;
import com.android.car.ui.utils.CarUiUtils;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
class BaseLayoutController {
    private static Map<Activity, BaseLayoutController> sBaseLayoutMap = new HashMap();
    private InsetsUpdater mInsetsUpdater;
    private ToolbarController mToolbarController;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BaseLayoutController getBaseLayout(Activity activity) {
        return sBaseLayoutMap.get(activity);
    }

    private BaseLayoutController(Activity activity) {
        installBaseLayout(activity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void build(Activity activity) {
        sBaseLayoutMap.put(activity, new BaseLayoutController(activity));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void destroy(Activity activity) {
        sBaseLayoutMap.remove(activity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToolbarController getToolbarController() {
        return this.mToolbarController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Insets getInsets() {
        return this.mInsetsUpdater.getInsets();
    }

    private void installBaseLayout(Activity activity) {
        int i;
        boolean themeBoolean = getThemeBoolean(activity, R.attr.carUiBaseLayout);
        boolean themeBoolean2 = getThemeBoolean(activity, R.attr.carUiToolbar);
        if (themeBoolean) {
            if (themeBoolean2) {
                i = R.layout.car_ui_base_layout_toolbar;
            } else {
                i = R.layout.car_ui_base_layout;
            }
            View inflate = LayoutInflater.from(activity).inflate(i, (ViewGroup) null, false);
            ViewGroup viewGroup = (ViewGroup) activity.getWindow().findViewById(16908290);
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
            int indexOfChild = viewGroup2.indexOfChild(viewGroup);
            viewGroup2.removeView(viewGroup);
            viewGroup2.addView(inflate, indexOfChild, viewGroup.getLayoutParams());
            ((FrameLayout) CarUiUtils.requireViewByRefId(inflate, R.id.content)).addView(viewGroup, new FrameLayout.LayoutParams(-1, -1));
            if (themeBoolean2) {
                this.mToolbarController = new ToolbarControllerImpl(inflate);
            }
            this.mInsetsUpdater = new InsetsUpdater(activity, inflate, viewGroup);
            this.mInsetsUpdater.installListeners();
        }
    }

    private boolean getThemeBoolean(Activity activity, int i) {
        TypedArray obtainStyledAttributes = activity.getTheme().obtainStyledAttributes(new int[]{i});
        try {
            return obtainStyledAttributes.getBoolean(0, false);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InsetsUpdater implements ViewTreeObserver.OnGlobalLayoutListener {
        private static final String BOTTOM_INSET_TAG = "car_ui_bottom_inset";
        private static final String LEFT_INSET_TAG = "car_ui_left_inset";
        private static final String RIGHT_INSET_TAG = "car_ui_right_inset";
        private static final String TOP_INSET_TAG = "car_ui_top_inset";
        private final Activity mActivity;
        private final View mBottomInsetView;
        private final View mLeftInsetView;
        private final View mRightInsetView;
        private final View mTopInsetView;
        private boolean mInsetsDirty = true;
        private Insets mInsets = new Insets();

        InsetsUpdater(Activity activity, View view, View view2) {
            this.mActivity = activity;
            this.mLeftInsetView = view.findViewWithTag(LEFT_INSET_TAG);
            this.mRightInsetView = view.findViewWithTag(RIGHT_INSET_TAG);
            this.mTopInsetView = view.findViewWithTag(TOP_INSET_TAG);
            this.mBottomInsetView = view.findViewWithTag(BOTTOM_INSET_TAG);
            View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: com.android.car.ui.core.-$$Lambda$BaseLayoutController$InsetsUpdater$zzCIB0038f8j1iFmYePHWvmc4w4
                @Override // android.view.View.OnLayoutChangeListener
                public final void onLayoutChange(View view3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    BaseLayoutController.InsetsUpdater.this.lambda$new$0$BaseLayoutController$InsetsUpdater(view3, i, i2, i3, i4, i5, i6, i7, i8);
                }
            };
            View view3 = this.mLeftInsetView;
            if (view3 != null) {
                view3.addOnLayoutChangeListener(onLayoutChangeListener);
            }
            View view4 = this.mRightInsetView;
            if (view4 != null) {
                view4.addOnLayoutChangeListener(onLayoutChangeListener);
            }
            View view5 = this.mTopInsetView;
            if (view5 != null) {
                view5.addOnLayoutChangeListener(onLayoutChangeListener);
            }
            View view6 = this.mBottomInsetView;
            if (view6 != null) {
                view6.addOnLayoutChangeListener(onLayoutChangeListener);
            }
            view2.addOnLayoutChangeListener(onLayoutChangeListener);
        }

        public /* synthetic */ void lambda$new$0$BaseLayoutController$InsetsUpdater(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            if (i == i5 && i2 == i6 && i3 == i7 && i4 == i8) {
                return;
            }
            this.mInsetsDirty = true;
        }

        void installListeners() {
            this.mActivity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        Insets getInsets() {
            return this.mInsets;
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            if (this.mInsetsDirty) {
                View requireViewById = this.mActivity.requireViewById(16908290);
                View view = this.mTopInsetView;
                int max = view != null ? Math.max(0, getBottomOfView(view) - getTopOfView(requireViewById)) : 0;
                int max2 = this.mBottomInsetView != null ? Math.max(0, getBottomOfView(requireViewById) - getTopOfView(this.mBottomInsetView)) : 0;
                View view2 = this.mLeftInsetView;
                Insets insets = new Insets(view2 != null ? Math.max(0, getRightOfView(view2) - getLeftOfView(requireViewById)) : 0, max, this.mRightInsetView != null ? Math.max(0, getRightOfView(requireViewById) - getLeftOfView(this.mRightInsetView)) : 0, max2);
                this.mInsetsDirty = false;
                if (insets.equals(this.mInsets)) {
                    return;
                }
                this.mInsets = insets;
                dispatchNewInsets(insets);
            }
        }

        private void dispatchNewInsets(Insets insets) {
            boolean z;
            Activity activity = this.mActivity;
            if (activity instanceof InsetsChangedListener) {
                ((InsetsChangedListener) activity).onCarUiInsetsChanged(insets);
                z = true;
            } else {
                z = false;
            }
            Activity activity2 = this.mActivity;
            if (activity2 instanceof FragmentActivity) {
                for (Fragment fragment : ((FragmentActivity) activity2).getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof InsetsChangedListener) {
                        ((InsetsChangedListener) fragment).onCarUiInsetsChanged(insets);
                        z = true;
                    }
                }
            }
            if (z) {
                return;
            }
            this.mActivity.requireViewById(16908290).setPadding(insets.getLeft(), insets.getTop(), insets.getRight(), insets.getBottom());
        }

        private static int getLeftOfView(View view) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            return iArr[0];
        }

        private static int getRightOfView(View view) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            return iArr[0] + view.getWidth();
        }

        private static int getTopOfView(View view) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            return iArr[1];
        }

        private static int getBottomOfView(View view) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            return iArr[1] + view.getHeight();
        }
    }
}
