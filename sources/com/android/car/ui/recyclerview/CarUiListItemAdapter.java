package com.android.car.ui.recyclerview;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.recyclerview.CarUiContentListItem;
import com.android.car.ui.recyclerview.CarUiListItemAdapter;
import com.android.car.ui.recyclerview.CarUiRecyclerView;
import com.android.car.ui.utils.CarUiUtils;
import java.util.List;
/* loaded from: classes.dex */
public class CarUiListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CarUiRecyclerView.ItemCap {
    static final int VIEW_TYPE_LIST_HEADER = 2;
    static final int VIEW_TYPE_LIST_ITEM = 1;
    private List<? extends CarUiListItem> mItems;
    private int mMaxItems = -1;

    public CarUiListItemAdapter(List<? extends CarUiListItem> list) {
        this.mItems = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i != 1) {
            if (i == 2) {
                return new HeaderViewHolder(from.inflate(R.layout.car_ui_header_list_item, viewGroup, false));
            }
            throw new IllegalStateException("Unknown item type.");
        }
        return new ListItemViewHolder(from.inflate(R.layout.car_ui_list_item, viewGroup, false));
    }

    public List<? extends CarUiListItem> getItems() {
        return this.mItems;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (this.mItems.get(i) instanceof CarUiContentListItem) {
            return 1;
        }
        if (this.mItems.get(i) instanceof CarUiHeaderListItem) {
            return 2;
        }
        throw new IllegalStateException("Unknown view type.");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 1) {
            if (!(viewHolder instanceof ListItemViewHolder)) {
                throw new IllegalStateException("Incorrect view holder type for list item.");
            }
            CarUiListItem carUiListItem = this.mItems.get(i);
            if (!(carUiListItem instanceof CarUiContentListItem)) {
                throw new IllegalStateException("Expected item to be bound to viewholder to be instance of CarUiContentListItem.");
            }
            ((ListItemViewHolder) viewHolder).bind((CarUiContentListItem) carUiListItem);
        } else if (itemViewType == 2) {
            if (!(viewHolder instanceof HeaderViewHolder)) {
                throw new IllegalStateException("Incorrect view holder type for list item.");
            }
            CarUiListItem carUiListItem2 = this.mItems.get(i);
            if (!(carUiListItem2 instanceof CarUiHeaderListItem)) {
                throw new IllegalStateException("Expected item to be bound to viewholder to be instance of CarUiHeaderListItem.");
            }
            ((HeaderViewHolder) viewHolder).bind((CarUiHeaderListItem) carUiListItem2);
        } else {
            throw new IllegalStateException("Unknown item view type.");
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.mMaxItems == -1) {
            return this.mItems.size();
        }
        return Math.min(this.mItems.size(), this.mMaxItems);
    }

    @Override // com.android.car.ui.recyclerview.CarUiRecyclerView.ItemCap
    public void setMaxItems(int i) {
        this.mMaxItems = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ListItemViewHolder extends RecyclerView.ViewHolder {
        final ViewGroup mActionContainer;
        final View mActionContainerTouchInterceptor;
        final View mActionDivider;
        final ImageView mAvatarIcon;
        final TextView mBody;
        final CheckBox mCheckBox;
        final ImageView mContentIcon;
        final ImageView mIcon;
        final ViewGroup mIconContainer;
        final RadioButton mRadioButton;
        final View mReducedTouchInterceptor;
        final ImageView mSupplementalIcon;
        final Switch mSwitch;
        final TextView mTitle;
        final View mTouchInterceptor;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ListItemViewHolder(View view) {
            super(view);
            this.mTitle = (TextView) CarUiUtils.findViewByRefId(view, R.id.title);
            this.mBody = (TextView) CarUiUtils.findViewByRefId(view, R.id.body);
            this.mIcon = (ImageView) CarUiUtils.findViewByRefId(view, R.id.icon);
            this.mContentIcon = (ImageView) CarUiUtils.findViewByRefId(view, R.id.content_icon);
            this.mAvatarIcon = (ImageView) CarUiUtils.findViewByRefId(view, R.id.avatar_icon);
            this.mIconContainer = (ViewGroup) CarUiUtils.findViewByRefId(view, R.id.icon_container);
            this.mActionContainer = (ViewGroup) CarUiUtils.findViewByRefId(view, R.id.action_container);
            this.mActionDivider = CarUiUtils.findViewByRefId(view, R.id.action_divider);
            this.mSwitch = (Switch) CarUiUtils.findViewByRefId(view, R.id.switch_widget);
            this.mCheckBox = (CheckBox) CarUiUtils.findViewByRefId(view, R.id.checkbox_widget);
            this.mRadioButton = (RadioButton) CarUiUtils.findViewByRefId(view, R.id.radio_button_widget);
            this.mSupplementalIcon = (ImageView) CarUiUtils.findViewByRefId(view, R.id.supplemental_icon);
            this.mReducedTouchInterceptor = CarUiUtils.findViewByRefId(view, R.id.reduced_touch_interceptor);
            this.mTouchInterceptor = CarUiUtils.findViewByRefId(view, R.id.touch_interceptor);
            this.mActionContainerTouchInterceptor = CarUiUtils.findViewByRefId(view, R.id.action_container_touch_interceptor);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void bind(final CarUiContentListItem carUiContentListItem) {
            CharSequence title = carUiContentListItem.getTitle();
            CharSequence body = carUiContentListItem.getBody();
            Drawable icon = carUiContentListItem.getIcon();
            if (!TextUtils.isEmpty(title)) {
                this.mTitle.setText(title);
                this.mTitle.setVisibility(0);
            } else {
                this.mTitle.setVisibility(8);
            }
            if (!TextUtils.isEmpty(body)) {
                this.mBody.setText(body);
                this.mBody.setVisibility(0);
            } else {
                this.mBody.setVisibility(8);
            }
            this.mIcon.setVisibility(8);
            this.mContentIcon.setVisibility(8);
            this.mAvatarIcon.setVisibility(8);
            if (icon != null) {
                this.mIconContainer.setVisibility(0);
                int i = AnonymousClass1.$SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType[carUiContentListItem.getPrimaryIconType().ordinal()];
                if (i == 1) {
                    this.mContentIcon.setVisibility(0);
                    this.mContentIcon.setImageDrawable(icon);
                } else if (i == 2) {
                    this.mIcon.setVisibility(0);
                    this.mIcon.setImageDrawable(icon);
                } else if (i == 3) {
                    this.mAvatarIcon.setVisibility(0);
                    this.mAvatarIcon.setImageDrawable(icon);
                    this.mAvatarIcon.setClipToOutline(true);
                }
            } else {
                this.mIconContainer.setVisibility(8);
            }
            this.mActionDivider.setVisibility(carUiContentListItem.isActionDividerVisible() ? 0 : 8);
            this.mSwitch.setVisibility(8);
            this.mCheckBox.setVisibility(8);
            this.mRadioButton.setVisibility(8);
            this.mSupplementalIcon.setVisibility(8);
            final CarUiContentListItem.OnClickListener onClickListener = carUiContentListItem.getOnClickListener();
            int i2 = AnonymousClass1.$SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[carUiContentListItem.getAction().ordinal()];
            if (i2 == 1) {
                this.mActionContainer.setVisibility(8);
                this.mTouchInterceptor.setVisibility(0);
                this.mTouchInterceptor.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$vYmq87MPMF2iCDptoto9aSwoSRI
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        CarUiListItemAdapter.ListItemViewHolder.lambda$bind$0(CarUiContentListItem.OnClickListener.this, carUiContentListItem, view);
                    }
                });
                this.mReducedTouchInterceptor.setVisibility(8);
                this.mActionContainerTouchInterceptor.setVisibility(8);
            } else if (i2 == 2) {
                bindCompoundButton(carUiContentListItem, this.mSwitch, onClickListener);
            } else if (i2 == 3) {
                bindCompoundButton(carUiContentListItem, this.mCheckBox, onClickListener);
            } else if (i2 == 4) {
                bindCompoundButton(carUiContentListItem, this.mRadioButton, onClickListener);
            } else if (i2 == 5) {
                this.mSupplementalIcon.setVisibility(0);
                this.mSupplementalIcon.setImageDrawable(carUiContentListItem.getSupplementalIcon());
                this.mActionContainer.setVisibility(0);
                this.mActionContainerTouchInterceptor.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$3dyg4sLE9sS75QYPkU4fOusPkf4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        CarUiListItemAdapter.ListItemViewHolder.this.lambda$bind$1$CarUiListItemAdapter$ListItemViewHolder(carUiContentListItem, onClickListener, view);
                    }
                });
                if (carUiContentListItem.getSupplementalIconOnClickListener() == null) {
                    this.mTouchInterceptor.setVisibility(0);
                    this.mTouchInterceptor.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$j6Kl0VK2D7maS58g7oclJxfONmk
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CarUiListItemAdapter.ListItemViewHolder.lambda$bind$2(CarUiContentListItem.OnClickListener.this, carUiContentListItem, view);
                        }
                    });
                    this.mReducedTouchInterceptor.setVisibility(8);
                    this.mActionContainerTouchInterceptor.setVisibility(8);
                } else {
                    this.mReducedTouchInterceptor.setVisibility(0);
                    this.mReducedTouchInterceptor.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$vKPFwRo_FVQMiwVwkDs4sXpSKRQ
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CarUiListItemAdapter.ListItemViewHolder.lambda$bind$3(CarUiContentListItem.OnClickListener.this, carUiContentListItem, view);
                        }
                    });
                    this.mActionContainerTouchInterceptor.setVisibility(0);
                    this.mTouchInterceptor.setVisibility(8);
                }
            } else {
                throw new IllegalStateException("Unknown secondary action type.");
            }
            this.itemView.setActivated(carUiContentListItem.isActivated());
            setEnabled(this.itemView, carUiContentListItem.isEnabled());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$bind$0(CarUiContentListItem.OnClickListener onClickListener, CarUiContentListItem carUiContentListItem, View view) {
            if (onClickListener != null) {
                onClickListener.onClick(carUiContentListItem);
            }
        }

        public /* synthetic */ void lambda$bind$1$CarUiListItemAdapter$ListItemViewHolder(CarUiContentListItem carUiContentListItem, CarUiContentListItem.OnClickListener onClickListener, View view) {
            if (carUiContentListItem.getSupplementalIconOnClickListener() != null) {
                carUiContentListItem.getSupplementalIconOnClickListener().onClick(this.mIcon);
            }
            if (onClickListener != null) {
                onClickListener.onClick(carUiContentListItem);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$bind$2(CarUiContentListItem.OnClickListener onClickListener, CarUiContentListItem carUiContentListItem, View view) {
            if (onClickListener != null) {
                onClickListener.onClick(carUiContentListItem);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$bind$3(CarUiContentListItem.OnClickListener onClickListener, CarUiContentListItem carUiContentListItem, View view) {
            if (onClickListener != null) {
                onClickListener.onClick(carUiContentListItem);
            }
        }

        void setEnabled(View view, boolean z) {
            view.setEnabled(z);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setEnabled(viewGroup.getChildAt(i), z);
                }
            }
        }

        void bindCompoundButton(final CarUiContentListItem carUiContentListItem, final CompoundButton compoundButton, final CarUiContentListItem.OnClickListener onClickListener) {
            compoundButton.setVisibility(0);
            compoundButton.setOnCheckedChangeListener(null);
            compoundButton.setChecked(carUiContentListItem.isChecked());
            compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$IC21ErXlxlRrRjdNF-bqr1wvh3s
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton2, boolean z) {
                    CarUiContentListItem.this.setChecked(z);
                }
            });
            this.mTouchInterceptor.setVisibility(0);
            this.mTouchInterceptor.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiListItemAdapter$ListItemViewHolder$OgkGFiHn5JfgBbDAHjk8r7ucHWI
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CarUiListItemAdapter.ListItemViewHolder.lambda$bindCompoundButton$5(compoundButton, onClickListener, carUiContentListItem, view);
                }
            });
            this.mReducedTouchInterceptor.setVisibility(8);
            this.mActionContainerTouchInterceptor.setVisibility(8);
            this.mActionContainer.setVisibility(0);
            this.mActionContainer.setClickable(false);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$bindCompoundButton$5(CompoundButton compoundButton, CarUiContentListItem.OnClickListener onClickListener, CarUiContentListItem carUiContentListItem, View view) {
            compoundButton.toggle();
            if (onClickListener != null) {
                onClickListener.onClick(carUiContentListItem);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.car.ui.recyclerview.CarUiListItemAdapter$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action = new int[CarUiContentListItem.Action.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType;

        static {
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[CarUiContentListItem.Action.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[CarUiContentListItem.Action.SWITCH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[CarUiContentListItem.Action.CHECK_BOX.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[CarUiContentListItem.Action.RADIO_BUTTON.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$Action[CarUiContentListItem.Action.ICON.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType = new int[CarUiContentListItem.IconType.values().length];
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType[CarUiContentListItem.IconType.CONTENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType[CarUiContentListItem.IconType.STANDARD.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$car$ui$recyclerview$CarUiContentListItem$IconType[CarUiContentListItem.IconType.AVATAR.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    /* loaded from: classes.dex */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView mBody;
        private final TextView mTitle;

        HeaderViewHolder(View view) {
            super(view);
            this.mTitle = (TextView) CarUiUtils.findViewByRefId(view, R.id.title);
            this.mBody = (TextView) CarUiUtils.findViewByRefId(view, R.id.body);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void bind(CarUiHeaderListItem carUiHeaderListItem) {
            this.mTitle.setText(carUiHeaderListItem.getTitle());
            CharSequence body = carUiHeaderListItem.getBody();
            if (!TextUtils.isEmpty(body)) {
                this.mBody.setText(body);
            } else {
                this.mBody.setVisibility(8);
            }
        }
    }
}
