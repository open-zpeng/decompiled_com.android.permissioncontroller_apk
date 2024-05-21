package com.android.car.ui.recyclerview;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.recyclerview.decorations.grid.GridDividerItemDecoration;
import com.android.car.ui.recyclerview.decorations.grid.GridOffsetItemDecoration;
import com.android.car.ui.recyclerview.decorations.linear.LinearDividerItemDecoration;
import com.android.car.ui.recyclerview.decorations.linear.LinearOffsetItemDecoration;
import com.android.car.ui.toolbar.Toolbar;
import com.android.car.ui.utils.CarUiUtils;
import com.android.car.ui.utils.CarUxRestrictionsUtil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class CarUiRecyclerView extends RecyclerView implements Toolbar.OnHeightChangedListener {
    private static final String TAG = "CarUiRecyclerView";
    private int mCarUiRecyclerViewLayout;
    private CarUxRestrictionsUtil mCarUxRestrictionsUtil;
    private LinearLayout mContainer;
    private int mContainerVisibility;
    private GridDividerItemDecoration mDividerItemDecoration;
    private boolean mFullyInitialized;
    private int mInitialTopPadding;
    private boolean mInstallingExtScrollBar;
    private final UxRestrictionChangedListener mListener;
    private int mNumOfColumns;
    private GridOffsetItemDecoration mOffsetItemDecoration;
    private ScrollBar mScrollBar;
    private String mScrollBarClass;
    private boolean mScrollBarEnabled;
    private float mScrollBarPaddingEnd;
    private float mScrollBarPaddingStart;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CarUiRecyclerViewLayout {
        public static final int GRID = 2;
        public static final int LINEAR = 0;
    }

    /* loaded from: classes.dex */
    public interface ItemCap {
        public static final int UNLIMITED = -1;

        void setMaxItems(int i);
    }

    public CarUiRecyclerView(Context context) {
        this(context, null);
    }

    public CarUiRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.carUiRecyclerViewStyle);
    }

    public CarUiRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = new UxRestrictionChangedListener();
        this.mInstallingExtScrollBar = false;
        this.mContainerVisibility = 0;
        init(context, attributeSet, i);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        this.mCarUxRestrictionsUtil = CarUxRestrictionsUtil.getInstance(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CarUiRecyclerView, i, R.style.Widget_CarUi_CarUiRecyclerView);
        this.mScrollBarEnabled = context.getResources().getBoolean(R.bool.car_ui_scrollbar_enable);
        this.mFullyInitialized = false;
        this.mScrollBarPaddingStart = context.getResources().getDimension(R.dimen.car_ui_scrollbar_padding_start);
        this.mScrollBarPaddingEnd = context.getResources().getDimension(R.dimen.car_ui_scrollbar_padding_end);
        this.mCarUiRecyclerViewLayout = obtainStyledAttributes.getInt(R.styleable.CarUiRecyclerView_layoutStyle, 0);
        this.mNumOfColumns = obtainStyledAttributes.getInt(R.styleable.CarUiRecyclerView_numOfColumns, 2);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.CarUiRecyclerView_enableDivider, false);
        if (this.mCarUiRecyclerViewLayout == 0) {
            int integer = obtainStyledAttributes.getInteger(R.styleable.CarUiRecyclerView_startOffset, 0);
            int integer2 = obtainStyledAttributes.getInteger(R.styleable.CarUiRecyclerView_endOffset, 0);
            if (z) {
                addItemDecoration(new LinearDividerItemDecoration(context.getDrawable(R.drawable.car_ui_recyclerview_divider)));
            }
            LinearOffsetItemDecoration linearOffsetItemDecoration = new LinearOffsetItemDecoration(integer, 0);
            LinearOffsetItemDecoration linearOffsetItemDecoration2 = new LinearOffsetItemDecoration(integer2, 1);
            addItemDecoration(linearOffsetItemDecoration);
            addItemDecoration(linearOffsetItemDecoration2);
            setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            int integer3 = obtainStyledAttributes.getInteger(R.styleable.CarUiRecyclerView_startOffset, 0);
            int integer4 = obtainStyledAttributes.getInteger(R.styleable.CarUiRecyclerView_endOffset, 0);
            if (z) {
                this.mDividerItemDecoration = new GridDividerItemDecoration(context.getDrawable(R.drawable.car_ui_divider), context.getDrawable(R.drawable.car_ui_divider), this.mNumOfColumns);
                addItemDecoration(this.mDividerItemDecoration);
            }
            this.mOffsetItemDecoration = new GridOffsetItemDecoration(integer3, this.mNumOfColumns, 0);
            GridOffsetItemDecoration gridOffsetItemDecoration = new GridOffsetItemDecoration(integer4, this.mNumOfColumns, 1);
            addItemDecoration(this.mOffsetItemDecoration);
            addItemDecoration(gridOffsetItemDecoration);
            setLayoutManager(new GridLayoutManager(getContext(), this.mNumOfColumns));
            setNumOfColumns(this.mNumOfColumns);
        }
        if (!this.mScrollBarEnabled) {
            obtainStyledAttributes.recycle();
            this.mFullyInitialized = true;
            return;
        }
        this.mScrollBarClass = context.getResources().getString(R.string.car_ui_scrollbar_component);
        obtainStyledAttributes.recycle();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiRecyclerView$FDDZqaI-dTS95KaTq9SwnaQL-so
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                CarUiRecyclerView.this.lambda$init$0$CarUiRecyclerView();
            }
        });
    }

    public /* synthetic */ void lambda$init$0$CarUiRecyclerView() {
        if (this.mInitialTopPadding == 0) {
            this.mInitialTopPadding = getPaddingTop();
        }
        this.mFullyInitialized = true;
    }

    @Override // com.android.car.ui.toolbar.Toolbar.OnHeightChangedListener
    public void onHeightChanged(int i) {
        setPaddingRelative(getPaddingStart(), this.mInitialTopPadding + i, getPaddingEnd(), getPaddingBottom());
    }

    public boolean fullyInitialized() {
        return this.mFullyInitialized;
    }

    public void setNumOfColumns(int i) {
        this.mNumOfColumns = i;
        GridOffsetItemDecoration gridOffsetItemDecoration = this.mOffsetItemDecoration;
        if (gridOffsetItemDecoration != null) {
            gridOffsetItemDecoration.setNumOfColumns(this.mNumOfColumns);
        }
        GridDividerItemDecoration gridDividerItemDecoration = this.mDividerItemDecoration;
        if (gridDividerItemDecoration != null) {
            gridDividerItemDecoration.setNumOfColumns(this.mNumOfColumns);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        this.mContainerVisibility = i;
        LinearLayout linearLayout = this.mContainer;
        if (linearLayout != null) {
            linearLayout.setVisibility(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCarUxRestrictionsUtil.register(this.mListener);
        if (this.mInstallingExtScrollBar || !this.mScrollBarEnabled) {
            return;
        }
        this.mInstallingExtScrollBar = true;
        installExternalScrollBar();
        this.mInstallingExtScrollBar = false;
    }

    private void installExternalScrollBar() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        this.mContainer = new LinearLayout(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.car_ui_recycler_view, (ViewGroup) this.mContainer, true);
        this.mContainer.setLayoutParams(getLayoutParams());
        this.mContainer.setVisibility(this.mContainerVisibility);
        int indexOfChild = viewGroup.indexOfChild(this);
        viewGroup.removeView(this);
        ((FrameLayout) CarUiUtils.requireViewByRefId(this.mContainer, R.id.car_ui_recycler_view)).addView(this, new FrameLayout.LayoutParams(-1, -1));
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        viewGroup.addView(this.mContainer, indexOfChild);
        createScrollBarFromConfig(CarUiUtils.requireViewByRefId(this.mContainer, R.id.car_ui_scroll_bar));
    }

    private void createScrollBarFromConfig(View view) {
        Class<?> cls;
        try {
            if (!TextUtils.isEmpty(this.mScrollBarClass)) {
                cls = getContext().getClassLoader().loadClass(this.mScrollBarClass);
            } else {
                cls = DefaultScrollBar.class;
            }
            try {
                this.mScrollBar = (ScrollBar) cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                this.mScrollBar.initialize(this, view);
                this.mScrollBar.setPadding((int) this.mScrollBarPaddingStart, (int) this.mScrollBarPaddingEnd);
            } catch (Throwable th) {
                andLog("Error creating scroll bar component: " + this.mScrollBarClass, th);
                throw null;
            }
        } catch (Throwable th2) {
            andLog("Error loading scroll bar component: " + this.mScrollBarClass, th2);
            throw null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCarUxRestrictionsUtil.unregister(this.mListener);
    }

    public void setScrollBarPadding(int i, int i2) {
        if (this.mScrollBarEnabled) {
            this.mScrollBarPaddingStart = i;
            this.mScrollBarPaddingEnd = i2;
            ScrollBar scrollBar = this.mScrollBar;
            if (scrollBar != null) {
                scrollBar.setPadding(i, i2);
            }
        }
    }

    @Deprecated
    public RecyclerView.LayoutManager getEffectiveLayoutManager() {
        return super.getLayoutManager();
    }

    private static RuntimeException andLog(String str, Throwable th) {
        Log.e(TAG, str, th);
        throw new RuntimeException(str, th);
    }

    /* loaded from: classes.dex */
    private class UxRestrictionChangedListener implements CarUxRestrictionsUtil.OnUxRestrictionsChangedListener {
        private UxRestrictionChangedListener() {
        }

        @Override // com.android.car.ui.utils.CarUxRestrictionsUtil.OnUxRestrictionsChangedListener
        public void onRestrictionsChanged(CarUxRestrictions carUxRestrictions) {
            RecyclerView.Adapter adapter = CarUiRecyclerView.this.getAdapter();
            if (adapter instanceof ItemCap) {
                int maxCumulativeContentItems = (carUxRestrictions.getActiveRestrictions() & 32) != 0 ? carUxRestrictions.getMaxCumulativeContentItems() : -1;
                int itemCount = adapter.getItemCount();
                ((ItemCap) adapter).setMaxItems(maxCumulativeContentItems);
                int itemCount2 = adapter.getItemCount();
                if (itemCount2 == itemCount) {
                    return;
                }
                if (itemCount2 < itemCount) {
                    adapter.notifyItemRangeRemoved(itemCount2, itemCount - itemCount2);
                } else {
                    adapter.notifyItemRangeInserted(itemCount, itemCount2 - itemCount);
                }
            }
        }
    }
}
