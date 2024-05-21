package com.android.car.ui.preference;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.DialogPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.android.car.ui.baselayout.Insets;
import com.android.car.ui.baselayout.InsetsChangedListener;
import com.android.car.ui.core.CarUi;
import com.android.car.ui.toolbar.Toolbar;
import com.android.car.ui.toolbar.ToolbarController;
import com.android.car.ui.utils.CarUiUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class PreferenceFragment extends PreferenceFragmentCompat implements InsetsChangedListener {
    private static final String DIALOG_FRAGMENT_TAG = "com.android.car.ui.PreferenceFragment.DIALOG";
    private static final String TAG = "CarUiPreferenceFragment";
    private static final List<Pair<Class<? extends Preference>, Class<? extends Preference>>> sPreferenceMapping = Arrays.asList(new Pair(DropDownPreference.class, CarUiDropDownPreference.class), new Pair(ListPreference.class, CarUiListPreference.class), new Pair(MultiSelectListPreference.class, CarUiMultiSelectListPreference.class), new Pair(EditTextPreference.class, CarUiEditTextPreference.class), new Pair(Preference.class, CarUiPreference.class));

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ToolbarController toolbar = CarUi.getToolbar(getActivity());
        if (toolbar != null) {
            toolbar.setState(Toolbar.State.SUBPAGE);
            if (getPreferenceScreen() != null) {
                toolbar.setTitle(getPreferenceScreen().getTitle());
            }
        }
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        Toolbar toolbar2 = (Toolbar) view.findViewById(R.id.toolbar);
        if (recyclerView == null || toolbar2 == null) {
            return;
        }
        recyclerView.setPadding(0, toolbar2.getHeight(), 0, 0);
        toolbar2.registerToolbarHeightChangeListener(new Toolbar.OnHeightChangedListener() { // from class: com.android.car.ui.preference.-$$Lambda$PreferenceFragment$IGvawGy6TBRTo2-dF7eX0ah1L_4
            @Override // com.android.car.ui.toolbar.Toolbar.OnHeightChangedListener
            public final void onHeightChanged(int i) {
                PreferenceFragment.lambda$onViewCreated$0(RecyclerView.this, i);
            }
        });
        recyclerView.setClipToPadding(false);
        if (getPreferenceScreen() != null) {
            toolbar2.setTitle(getPreferenceScreen().getTitle());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onViewCreated$0(RecyclerView recyclerView, int i) {
        if (recyclerView.getPaddingTop() == i) {
            return;
        }
        int paddingTop = recyclerView.getPaddingTop();
        recyclerView.setPadding(0, i, 0, 0);
        recyclerView.scrollBy(0, paddingTop - i);
    }

    @Override // com.android.car.ui.baselayout.InsetsChangedListener
    public void onCarUiInsetsChanged(Insets insets) {
        View requireView = requireView();
        requireView.requireViewById(R.id.recycler_view).setPadding(0, insets.getTop(), 0, insets.getBottom());
        requireView.getRootView().requireViewById(16908290).setPadding(insets.getLeft(), 0, insets.getRight(), 0);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        Fragment newInstance;
        if (!((getActivity() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) && ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) getActivity()).onPreferenceDisplayDialog(this, preference)) && requireFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
            if (preference instanceof EditTextPreference) {
                newInstance = EditTextPreferenceDialogFragment.newInstance(preference.getKey());
            } else if (preference instanceof ListPreference) {
                newInstance = ListPreferenceFragment.newInstance(preference.getKey());
            } else if (preference instanceof MultiSelectListPreference) {
                newInstance = MultiSelectListPreferenceFragment.newInstance(preference.getKey());
            } else {
                throw new IllegalArgumentException("Cannot display dialog for an unknown Preference type: " + preference.getClass().getSimpleName() + ". Make sure to implement onPreferenceDisplayDialog() to handle displaying a custom dialog for this Preference.");
            }
            newInstance.setTargetFragment(this, 0);
            if (newInstance instanceof DialogFragment) {
                ((DialogFragment) newInstance).show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else if (getActivity() == null) {
                throw new IllegalStateException("Preference fragment is not attached to an Activity.");
            } else {
                if (getView() == null) {
                    throw new IllegalStateException("Preference fragment must have a layout.");
                }
                Context context = getContext();
                FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                beginTransaction.setCustomAnimations(CarUiUtils.getAttrResourceId(context, 16843493), CarUiUtils.getAttrResourceId(context, 16843494), CarUiUtils.getAttrResourceId(context, 16843495), CarUiUtils.getAttrResourceId(context, 16843496));
                beginTransaction.replace(((ViewGroup) getView().getParent()).getId(), newInstance);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        HashMap hashMap = new HashMap();
        ArrayList<Preference> arrayList = new ArrayList();
        ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.addFirst(preferenceScreen);
        while (!arrayDeque.isEmpty()) {
            Preference preference = (Preference) arrayDeque.removeFirst();
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                arrayList.clear();
                for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                    arrayList.add(preferenceGroup.getPreference(i));
                }
                preferenceGroup.removeAll();
                for (Preference preference2 : arrayList) {
                    Preference replacementFor = getReplacementFor(preference2);
                    hashMap.put(replacementFor, preference2.getDependency());
                    preferenceGroup.addPreference(replacementFor);
                    arrayDeque.addFirst(replacementFor);
                }
            }
        }
        super.setPreferenceScreen(preferenceScreen);
        for (Map.Entry entry : hashMap.entrySet()) {
            ((Preference) entry.getKey()).setDependency((String) entry.getValue());
        }
    }

    private static Preference getReplacementFor(Preference preference) {
        Class<?> cls = preference.getClass();
        Iterator<Pair<Class<? extends Preference>, Class<? extends Preference>>> it = sPreferenceMapping.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Pair<Class<? extends Preference>, Class<? extends Preference>> next = it.next();
            Class<?> cls2 = (Class) next.first;
            Class<?> cls3 = (Class) next.second;
            if (cls2.isAssignableFrom(cls)) {
                if (cls == cls2) {
                    try {
                        Preference preference2 = (Preference) cls3.getDeclaredConstructor(Context.class).newInstance(preference.getContext());
                        copyPreference(preference, preference2);
                        return preference2;
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                } else if (cls != cls3 && cls2 != Preference.class) {
                    Log.w(TAG, "Subclass of " + cls2.getSimpleName() + " was used, preventing us from substituting it with " + cls3.getSimpleName());
                }
            }
        }
        return preference;
    }

    private static Preference copyPreference(Preference preference, Preference preference2) {
        preference2.setTitle(preference.getTitle());
        preference2.setOnPreferenceClickListener(preference.getOnPreferenceClickListener());
        preference2.setOnPreferenceChangeListener(preference.getOnPreferenceChangeListener());
        preference2.setIcon(preference.getIcon());
        preference2.setFragment(preference.getFragment());
        preference2.setIntent(preference.getIntent());
        preference2.setKey(preference.getKey());
        preference2.setOrder(preference.getOrder());
        preference2.setSelectable(preference.isSelectable());
        preference2.setPersistent(preference.isPersistent());
        preference2.setIconSpaceReserved(preference.isIconSpaceReserved());
        preference2.setWidgetLayoutResource(preference.getWidgetLayoutResource());
        preference2.setPreferenceDataStore(preference.getPreferenceDataStore());
        preference2.setShouldDisableView(preference.getShouldDisableView());
        preference2.setSingleLineTitle(preference.isSingleLineTitle());
        preference2.setVisible(preference.isVisible());
        preference2.setLayoutResource(preference.getLayoutResource());
        preference2.setCopyingEnabled(preference.isCopyingEnabled());
        if (preference.getSummaryProvider() != null) {
            preference2.setSummaryProvider(preference.getSummaryProvider());
        } else {
            preference2.setSummary(preference.getSummary());
        }
        if (preference.peekExtras() != null) {
            preference2.getExtras().putAll(preference.peekExtras());
        }
        if (preference instanceof DialogPreference) {
            DialogPreference dialogPreference = (DialogPreference) preference;
            DialogPreference dialogPreference2 = (DialogPreference) preference2;
            dialogPreference2.setDialogTitle(dialogPreference.getDialogTitle());
            dialogPreference2.setDialogIcon(dialogPreference.getDialogIcon());
            dialogPreference2.setDialogMessage(dialogPreference.getDialogMessage());
            dialogPreference2.setDialogLayoutResource(dialogPreference.getDialogLayoutResource());
            dialogPreference2.setNegativeButtonText(dialogPreference.getNegativeButtonText());
            dialogPreference2.setPositiveButtonText(dialogPreference.getPositiveButtonText());
        }
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            ListPreference listPreference2 = (ListPreference) preference2;
            listPreference2.setEntries(listPreference.getEntries());
            listPreference2.setEntryValues(listPreference.getEntryValues());
            listPreference2.setValue(listPreference.getValue());
        } else if (preference instanceof EditTextPreference) {
            ((EditTextPreference) preference2).setText(((EditTextPreference) preference).getText());
        } else if (preference instanceof MultiSelectListPreference) {
            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
            MultiSelectListPreference multiSelectListPreference2 = (MultiSelectListPreference) preference2;
            multiSelectListPreference2.setEntries(multiSelectListPreference.getEntries());
            multiSelectListPreference2.setEntryValues(multiSelectListPreference.getEntryValues());
            multiSelectListPreference2.setValues(multiSelectListPreference.getValues());
        }
        return preference2;
    }
}
