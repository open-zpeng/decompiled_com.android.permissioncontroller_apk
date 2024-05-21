package com.android.car.ui.preference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.DialogPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.car.ui.R;
import com.android.car.ui.recyclerview.CarUiContentListItem;
import com.android.car.ui.recyclerview.CarUiListItemAdapter;
import com.android.car.ui.recyclerview.CarUiRecyclerView;
import com.android.car.ui.toolbar.Toolbar;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ListPreferenceFragment extends Fragment {
    private ListPreference mPreference;
    private CarUiContentListItem mSelectedItem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ListPreferenceFragment newInstance(String str) {
        ListPreferenceFragment listPreferenceFragment = new ListPreferenceFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        listPreferenceFragment.setArguments(bundle);
        return listPreferenceFragment;
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
        toolbar.registerToolbarHeightChangeListener(new Toolbar.OnHeightChangedListener() { // from class: com.android.car.ui.preference.-$$Lambda$ListPreferenceFragment$f_SO-fefSO31C9MQPhzIiE35o-M
            @Override // com.android.car.ui.toolbar.Toolbar.OnHeightChangedListener
            public final void onHeightChanged(int i) {
                ListPreferenceFragment.lambda$onViewCreated$0(CarUiRecyclerView.this, i);
            }
        });
        carUiRecyclerView.setClipToPadding(false);
        this.mPreference = getListPreference();
        toolbar.setTitle(this.mPreference.getTitle());
        CharSequence[] entries = this.mPreference.getEntries();
        final CharSequence[] entryValues = this.mPreference.getEntryValues();
        if (entries == null || entryValues == null) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
        }
        if (entries.length != entryValues.length) {
            throw new IllegalStateException("ListPreference entries array length does not match entryValues array length.");
        }
        ListPreference listPreference = this.mPreference;
        int findIndexOfValue = listPreference.findIndexOfValue(listPreference.getValue());
        final ArrayList arrayList = new ArrayList();
        final CarUiListItemAdapter carUiListItemAdapter = new CarUiListItemAdapter(arrayList);
        for (int i = 0; i < entries.length; i++) {
            String charSequence = entries[i].toString();
            CarUiContentListItem carUiContentListItem = new CarUiContentListItem(CarUiContentListItem.Action.RADIO_BUTTON);
            carUiContentListItem.setTitle(charSequence);
            if (i == findIndexOfValue) {
                carUiContentListItem.setChecked(true);
                this.mSelectedItem = carUiContentListItem;
            }
            carUiContentListItem.setOnCheckedChangeListener(new CarUiContentListItem.OnCheckedChangeListener() { // from class: com.android.car.ui.preference.-$$Lambda$ListPreferenceFragment$rJYZvC0kZuHGeJHuQaBshexRSFM
                @Override // com.android.car.ui.recyclerview.CarUiContentListItem.OnCheckedChangeListener
                public final void onCheckedChanged(CarUiContentListItem carUiContentListItem2, boolean z) {
                    ListPreferenceFragment.this.lambda$onViewCreated$1$ListPreferenceFragment(carUiListItemAdapter, arrayList, carUiContentListItem2, z);
                }
            });
            arrayList.add(carUiContentListItem);
        }
        toolbar.registerOnBackListener(new Toolbar.OnBackListener() { // from class: com.android.car.ui.preference.-$$Lambda$ListPreferenceFragment$X8xKVXVwyJtmKMzCsni_CTh5Sgc
            @Override // com.android.car.ui.toolbar.Toolbar.OnBackListener
            public final boolean onBack() {
                return ListPreferenceFragment.this.lambda$onViewCreated$2$ListPreferenceFragment(arrayList, entryValues);
            }
        });
        carUiRecyclerView.setAdapter(carUiListItemAdapter);
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

    public /* synthetic */ void lambda$onViewCreated$1$ListPreferenceFragment(CarUiListItemAdapter carUiListItemAdapter, List list, CarUiContentListItem carUiContentListItem, boolean z) {
        CarUiContentListItem carUiContentListItem2 = this.mSelectedItem;
        if (carUiContentListItem2 != null) {
            carUiContentListItem2.setChecked(false);
            carUiListItemAdapter.notifyItemChanged(list.indexOf(this.mSelectedItem));
        }
        this.mSelectedItem = carUiContentListItem;
    }

    public /* synthetic */ boolean lambda$onViewCreated$2$ListPreferenceFragment(List list, CharSequence[] charSequenceArr) {
        CarUiContentListItem carUiContentListItem = this.mSelectedItem;
        if (carUiContentListItem != null) {
            String charSequence = charSequenceArr[list.indexOf(carUiContentListItem)].toString();
            if (this.mPreference.callChangeListener(charSequence)) {
                this.mPreference.setValue(charSequence);
                return false;
            }
            return false;
        }
        return false;
    }

    private ListPreference getListPreference() {
        if (getArguments() == null) {
            throw new IllegalStateException("Preference arguments cannot be null");
        }
        String string = getArguments().getString("key");
        DialogPreference.TargetFragment targetFragment = (DialogPreference.TargetFragment) getTargetFragment();
        if (string != null) {
            if (targetFragment == null) {
                throw new IllegalStateException("Target fragment must be registered before displaying ListPreference screen.");
            }
            Preference findPreference = targetFragment.findPreference(string);
            if (!(findPreference instanceof ListPreference)) {
                throw new IllegalStateException("Cannot use ListPreferenceFragment with a preference that is not of type ListPreference");
            }
            return (ListPreference) findPreference;
        }
        throw new IllegalStateException("ListPreference key not found in Fragment arguments");
    }
}
