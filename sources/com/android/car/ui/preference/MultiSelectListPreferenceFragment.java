package com.android.car.ui.preference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import com.android.car.ui.R;
import com.android.car.ui.recyclerview.CarUiContentListItem;
import com.android.car.ui.recyclerview.CarUiListItemAdapter;
import com.android.car.ui.recyclerview.CarUiRecyclerView;
import com.android.car.ui.toolbar.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class MultiSelectListPreferenceFragment extends Fragment {
    private Set<String> mNewValues;
    private CarUiMultiSelectListPreference mPreference;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MultiSelectListPreferenceFragment newInstance(String str) {
        MultiSelectListPreferenceFragment multiSelectListPreferenceFragment = new MultiSelectListPreferenceFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        multiSelectListPreferenceFragment.setArguments(bundle);
        return multiSelectListPreferenceFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.car_ui_list_preference, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        final CarUiRecyclerView carUiRecyclerView = (CarUiRecyclerView) view.requireViewById(R.id.list);
        Toolbar toolbar = (Toolbar) view.requireViewById(R.id.toolbar);
        carUiRecyclerView.setPadding(0, toolbar.getHeight(), 0, 0);
        toolbar.registerToolbarHeightChangeListener(new Toolbar.OnHeightChangedListener() { // from class: com.android.car.ui.preference.-$$Lambda$MultiSelectListPreferenceFragment$WB_s31a2ruUSMORHw8iLsf9W2gE
            @Override // com.android.car.ui.toolbar.Toolbar.OnHeightChangedListener
            public final void onHeightChanged(int i) {
                MultiSelectListPreferenceFragment.lambda$onViewCreated$0(CarUiRecyclerView.this, i);
            }
        });
        this.mPreference = getPreference();
        carUiRecyclerView.setClipToPadding(false);
        toolbar.setTitle(this.mPreference.getTitle());
        this.mNewValues = new HashSet(this.mPreference.getValues());
        CharSequence[] entries = this.mPreference.getEntries();
        CharSequence[] entryValues = this.mPreference.getEntryValues();
        if (entries == null || entryValues == null) {
            throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
        }
        if (entries.length != entryValues.length) {
            throw new IllegalStateException("MultiSelectListPreference entries array length does not match entryValues array length.");
        }
        ArrayList arrayList = new ArrayList();
        boolean[] selectedItems = this.mPreference.getSelectedItems();
        for (int i = 0; i < entries.length; i++) {
            String charSequence = entries[i].toString();
            final String charSequence2 = entryValues[i].toString();
            CarUiContentListItem carUiContentListItem = new CarUiContentListItem(CarUiContentListItem.Action.CHECK_BOX);
            carUiContentListItem.setTitle(charSequence);
            carUiContentListItem.setChecked(selectedItems[i]);
            carUiContentListItem.setOnCheckedChangeListener(new CarUiContentListItem.OnCheckedChangeListener() { // from class: com.android.car.ui.preference.-$$Lambda$MultiSelectListPreferenceFragment$YnepC-0onrBWA20eSGEJPNKYYWk
                @Override // com.android.car.ui.recyclerview.CarUiContentListItem.OnCheckedChangeListener
                public final void onCheckedChanged(CarUiContentListItem carUiContentListItem2, boolean z) {
                    MultiSelectListPreferenceFragment.this.lambda$onViewCreated$1$MultiSelectListPreferenceFragment(charSequence2, carUiContentListItem2, z);
                }
            });
            arrayList.add(carUiContentListItem);
        }
        carUiRecyclerView.setAdapter(new CarUiListItemAdapter(arrayList));
        toolbar.registerOnBackListener(new Toolbar.OnBackListener() { // from class: com.android.car.ui.preference.-$$Lambda$MultiSelectListPreferenceFragment$9G_4FDcnD6zaD4H-r-BDer3cUys
            @Override // com.android.car.ui.toolbar.Toolbar.OnBackListener
            public final boolean onBack() {
                return MultiSelectListPreferenceFragment.this.lambda$onViewCreated$2$MultiSelectListPreferenceFragment();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onViewCreated$0(CarUiRecyclerView carUiRecyclerView, int i) {
        if (carUiRecyclerView.getPaddingTop() == i) {
            return;
        }
        int paddingTop = carUiRecyclerView.getPaddingTop();
        carUiRecyclerView.setPadding(0, i, 0, 0);
        carUiRecyclerView.scrollBy(0, paddingTop - i);
    }

    public /* synthetic */ void lambda$onViewCreated$1$MultiSelectListPreferenceFragment(String str, CarUiContentListItem carUiContentListItem, boolean z) {
        if (z) {
            this.mNewValues.add(str);
        } else {
            this.mNewValues.remove(str);
        }
    }

    public /* synthetic */ boolean lambda$onViewCreated$2$MultiSelectListPreferenceFragment() {
        if (this.mPreference.callChangeListener(this.mNewValues)) {
            this.mPreference.setValues(this.mNewValues);
            return false;
        }
        return false;
    }

    private CarUiMultiSelectListPreference getPreference() {
        if (getArguments() == null) {
            throw new IllegalStateException("Preference arguments cannot be null");
        }
        String string = getArguments().getString("key");
        DialogPreference.TargetFragment targetFragment = (DialogPreference.TargetFragment) getTargetFragment();
        if (string != null) {
            if (targetFragment == null) {
                throw new IllegalStateException("Target fragment must be registered before displaying MultiSelectListPreference screen.");
            }
            Preference findPreference = targetFragment.findPreference(string);
            if (!(findPreference instanceof CarUiMultiSelectListPreference)) {
                throw new IllegalStateException("Cannot use MultiSelectListPreferenceFragment with a preference that is not of type CarUiMultiSelectListPreference");
            }
            return (CarUiMultiSelectListPreference) findPreference;
        }
        throw new IllegalStateException("MultiSelectListPreference key not found in Fragment arguments");
    }
}
