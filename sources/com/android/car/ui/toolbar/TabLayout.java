package com.android.car.ui.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.car.ui.R;
import com.android.car.ui.toolbar.TabLayout;
import com.android.car.ui.utils.CarUiUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class TabLayout extends LinearLayout {
    private final Set<Listener> mListeners;
    private final TabAdapter mTabAdapter;

    /* loaded from: classes.dex */
    public interface Listener {
        default void onTabReselected(Tab tab) {
        }

        default void onTabSelected(Tab tab) {
        }

        default void onTabUnselected(Tab tab) {
        }
    }

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TabLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2;
        this.mListeners = new ArraySet();
        if (context.getResources().getBoolean(R.bool.car_ui_toolbar_tab_flexible_layout)) {
            i2 = R.layout.car_ui_toolbar_tab_item_layout_flexible;
        } else {
            i2 = R.layout.car_ui_toolbar_tab_item_layout;
        }
        this.mTabAdapter = new TabAdapter(context, i2, this);
    }

    public void addTab(Tab tab) {
        this.mTabAdapter.add(tab);
        if (this.mTabAdapter.getCount() == 1) {
            this.mTabAdapter.selectTab(0);
        }
    }

    public void selectTab(Tab tab) {
        this.mTabAdapter.selectTab(tab);
    }

    public void selectTab(int i) {
        this.mTabAdapter.selectTab(i);
    }

    public int getTabCount() {
        return this.mTabAdapter.getCount();
    }

    public int getTabPosition(Tab tab) {
        return this.mTabAdapter.getPosition(tab);
    }

    public Tab get(int i) {
        return this.mTabAdapter.getItem(i);
    }

    public void clearAllTabs() {
        this.mTabAdapter.clear();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnTabSelected(Tab tab) {
        for (Listener listener : this.mListeners) {
            listener.onTabSelected(tab);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnTabUnselected(Tab tab) {
        for (Listener listener : this.mListeners) {
            listener.onTabUnselected(tab);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnTabReselected(Tab tab) {
        for (Listener listener : this.mListeners) {
            listener.onTabReselected(tab);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addTabView(View view, int i) {
        addView(view, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TabAdapter extends BaseAdapter {
        private final Context mContext;
        private final Typeface mSelectedTypeface;
        private final int mTabItemLayoutRes;
        private final TabLayout mTabLayout;
        private final List<Tab> mTabList;
        private final Typeface mUnselectedTypeface;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        private TabAdapter(Context context, int i, TabLayout tabLayout) {
            this.mTabList = new ArrayList();
            this.mContext = context;
            this.mTabItemLayoutRes = i;
            this.mTabLayout = tabLayout;
            this.mUnselectedTypeface = createStyledTypeface(context, R.style.TextAppearance_CarUi_Widget_Toolbar_Tab);
            this.mSelectedTypeface = createStyledTypeface(context, R.style.TextAppearance_CarUi_Widget_Toolbar_Tab_Selected);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void add(Tab tab) {
            this.mTabList.add(tab);
            notifyItemInserted(this.mTabList.size() - 1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clear() {
            this.mTabList.clear();
            this.mTabLayout.removeAllViews();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getPosition(Tab tab) {
            return this.mTabList.indexOf(tab);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mTabList.size();
        }

        @Override // android.widget.Adapter
        public Tab getItem(int i) {
            return this.mTabList.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = LayoutInflater.from(this.mContext).inflate(this.mTabItemLayoutRes, viewGroup, false);
            presentTabItemView(i, inflate);
            return inflate;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void selectTab(Tab tab) {
            selectTab(getPosition(tab));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void selectTab(int i) {
            if (i < 0 || i >= getCount()) {
                throw new IndexOutOfBoundsException("Invalid position");
            }
            int i2 = 0;
            while (i2 < getCount()) {
                Tab tab = this.mTabList.get(i2);
                boolean z = i == i2;
                if (tab.mIsSelected != z) {
                    tab.mIsSelected = z;
                    notifyItemChanged(i2);
                    if (tab.mIsSelected) {
                        this.mTabLayout.dispatchOnTabSelected(tab);
                    } else {
                        this.mTabLayout.dispatchOnTabUnselected(tab);
                    }
                } else if (tab.mIsSelected) {
                    this.mTabLayout.dispatchOnTabReselected(tab);
                }
                i2++;
            }
        }

        private void notifyItemChanged(int i) {
            presentTabItemView(i, this.mTabLayout.getChildAt(i));
        }

        private void notifyItemInserted(int i) {
            this.mTabLayout.addTabView(getView(i, null, this.mTabLayout), i);
        }

        private void presentTabItemView(int i, View view) {
            final Tab tab = this.mTabList.get(i);
            TextView textView = (TextView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_tab_item_text);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$TabLayout$TabAdapter$0Vjm7Veshp6Oh0zTkPnpEz-k18k
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    TabLayout.TabAdapter.this.lambda$presentTabItemView$0$TabLayout$TabAdapter(tab, view2);
                }
            });
            tab.bindText(textView);
            tab.bindIcon((ImageView) CarUiUtils.requireViewByRefId(view, R.id.car_ui_toolbar_tab_item_icon));
            view.setActivated(tab.mIsSelected);
            textView.setTypeface(tab.mIsSelected ? this.mSelectedTypeface : this.mUnselectedTypeface);
        }

        public /* synthetic */ void lambda$presentTabItemView$0$TabLayout$TabAdapter(Tab tab, View view) {
            selectTab(tab);
        }

        private static Typeface createStyledTypeface(Context context, int i) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, new int[]{16842903, 16844165});
            try {
                int integer = obtainStyledAttributes.getInteger(0, 0);
                return Typeface.create(Typeface.defaultFromStyle(integer), obtainStyledAttributes.getInteger(1, 0), (2 & integer) != 0);
            } finally {
                obtainStyledAttributes.recycle();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Tab {
        private final Drawable mIcon;
        private boolean mIsSelected;
        private final CharSequence mText;

        public Tab(Drawable drawable, CharSequence charSequence) {
            this.mIcon = drawable;
            this.mText = charSequence;
        }

        protected void bindText(TextView textView) {
            textView.setText(this.mText);
        }

        protected void bindIcon(ImageView imageView) {
            imageView.setImageDrawable(this.mIcon);
        }
    }
}
