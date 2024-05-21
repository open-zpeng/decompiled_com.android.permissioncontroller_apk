package com.android.car.ui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.recyclerview.CarUiListItemAdapter;
import com.android.car.ui.recyclerview.CarUiRadioButtonListItemAdapter;
import java.util.List;
/* loaded from: classes.dex */
public class CarUiRadioButtonListItemAdapter extends CarUiListItemAdapter {
    private int mSelectedIndex;

    public CarUiRadioButtonListItemAdapter(List<CarUiRadioButtonListItem> list) {
        super(list);
        this.mSelectedIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            CarUiRadioButtonListItem carUiRadioButtonListItem = list.get(i);
            if (carUiRadioButtonListItem.isChecked() && this.mSelectedIndex >= 0) {
                throw new IllegalStateException("At most one item in a CarUiRadioButtonListItemAdapter can be checked");
            }
            if (carUiRadioButtonListItem.isChecked()) {
                this.mSelectedIndex = i;
            }
        }
    }

    @Override // com.android.car.ui.recyclerview.CarUiListItemAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 1) {
            return new RadioButtonListItemViewHolder(from.inflate(R.layout.car_ui_list_item, viewGroup, false));
        }
        return super.onCreateViewHolder(viewGroup, i);
    }

    @Override // com.android.car.ui.recyclerview.CarUiListItemAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder.getItemViewType() == 1) {
            if (!(viewHolder instanceof RadioButtonListItemViewHolder)) {
                throw new IllegalStateException("Incorrect view holder type for list item.");
            }
            CarUiListItem carUiListItem = getItems().get(i);
            if (!(carUiListItem instanceof CarUiRadioButtonListItem)) {
                throw new IllegalStateException("Expected item to be bound to viewholder to be instance of CarUiRadioButtonListItem.");
            }
            RadioButtonListItemViewHolder radioButtonListItemViewHolder = (RadioButtonListItemViewHolder) viewHolder;
            radioButtonListItemViewHolder.bind((CarUiRadioButtonListItem) carUiListItem);
            radioButtonListItemViewHolder.setOnCheckedChangeListener(new RadioButtonListItemViewHolder.OnCheckedChangeListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiRadioButtonListItemAdapter$mvviQERdRkFrTDqGYXRdYLzEYfU
                @Override // com.android.car.ui.recyclerview.CarUiRadioButtonListItemAdapter.RadioButtonListItemViewHolder.OnCheckedChangeListener
                public final void onCheckedChanged(boolean z) {
                    CarUiRadioButtonListItemAdapter.this.lambda$onBindViewHolder$0$CarUiRadioButtonListItemAdapter(i, z);
                }
            });
            return;
        }
        super.onBindViewHolder(viewHolder, i);
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$CarUiRadioButtonListItemAdapter(int i, boolean z) {
        if (z && this.mSelectedIndex >= 0) {
            ((CarUiRadioButtonListItem) getItems().get(this.mSelectedIndex)).setChecked(false);
            notifyItemChanged(this.mSelectedIndex);
        }
        if (z) {
            this.mSelectedIndex = i;
            ((CarUiRadioButtonListItem) getItems().get(this.mSelectedIndex)).setChecked(true);
            notifyItemChanged(this.mSelectedIndex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RadioButtonListItemViewHolder extends CarUiListItemAdapter.ListItemViewHolder {
        private OnCheckedChangeListener mListener;

        /* loaded from: classes.dex */
        public interface OnCheckedChangeListener {
            void onCheckedChanged(boolean z);
        }

        RadioButtonListItemViewHolder(View view) {
            super(view);
        }

        void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
            this.mListener = onCheckedChangeListener;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.android.car.ui.recyclerview.CarUiListItemAdapter.ListItemViewHolder
        public void bind(final CarUiContentListItem carUiContentListItem) {
            super.bind(carUiContentListItem);
            this.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.car.ui.recyclerview.-$$Lambda$CarUiRadioButtonListItemAdapter$RadioButtonListItemViewHolder$vf4rZnq1exIlQuEk85FaP9k-ad0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    CarUiRadioButtonListItemAdapter.RadioButtonListItemViewHolder.this.lambda$bind$0$CarUiRadioButtonListItemAdapter$RadioButtonListItemViewHolder(carUiContentListItem, compoundButton, z);
                }
            });
        }

        public /* synthetic */ void lambda$bind$0$CarUiRadioButtonListItemAdapter$RadioButtonListItemViewHolder(CarUiContentListItem carUiContentListItem, CompoundButton compoundButton, boolean z) {
            carUiContentListItem.setChecked(z);
            OnCheckedChangeListener onCheckedChangeListener = this.mListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(z);
            }
        }
    }
}
