package com.android.packageinstaller.role.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.utils.PackageRemovalMonitor;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.model.UserDeniedManager;
import com.android.packageinstaller.role.ui.RequestRoleViewModel;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class RequestRoleFragment extends DialogFragment {
    private static final String LOG_TAG = "RequestRoleFragment";
    private static final String STATE_DONT_ASK_AGAIN = RequestRoleFragment.class.getName() + ".state.DONT_ASK_AGAIN";
    private Adapter mAdapter;
    private CheckBox mDontAskAgainCheck;
    private ListView mListView;
    private String mPackageName;
    private PackageRemovalMonitor mPackageRemovalMonitor;
    private Role mRole;
    private String mRoleName;
    private RequestRoleViewModel mViewModel;

    public static RequestRoleFragment newInstance(String str, String str2) {
        RequestRoleFragment requestRoleFragment = new RequestRoleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        bundle.putString("android.intent.extra.PACKAGE_NAME", str2);
        requestRoleFragment.setArguments(bundle);
        return requestRoleFragment;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mPackageName = arguments.getString("android.intent.extra.PACKAGE_NAME");
        this.mRoleName = arguments.getString("android.intent.extra.ROLE_NAME");
        this.mRole = Roles.get(requireContext()).get(this.mRoleName);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), getTheme());
        Context context = builder.getContext();
        if (((RoleManager) context.getSystemService(RoleManager.class)).getRoleHolders(this.mRoleName).contains(this.mPackageName)) {
            String str = LOG_TAG;
            Log.i(str, "Application is already a role holder, role: " + this.mRoleName + ", package: " + this.mPackageName);
            reportRequestResult(2, null);
            clearDeniedSetResultOkAndFinish();
            return super.onCreateDialog(bundle);
        }
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(this.mPackageName, context);
        if (applicationInfo == null) {
            String str2 = LOG_TAG;
            Log.w(str2, "Unknown application: " + this.mPackageName);
            reportRequestResult(1, null);
            finish();
            return super.onCreateDialog(bundle);
        }
        Drawable badgedIcon = Utils.getBadgedIcon(context, applicationInfo);
        String string = getString(this.mRole.getRequestTitleResource(), Utils.getAppLabel(applicationInfo, context));
        LayoutInflater from = LayoutInflater.from(context);
        View inflate = from.inflate(R.layout.request_role_title, (ViewGroup) null);
        ((ImageView) inflate.requireViewById(R.id.icon)).setImageDrawable(badgedIcon);
        ((TextView) inflate.requireViewById(R.id.title)).setText(string);
        View inflate2 = from.inflate(R.layout.request_role_view, (ViewGroup) null);
        this.mListView = (ListView) inflate2.requireViewById(R.id.list);
        this.mListView.setChoiceMode(1);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$MwTNojc0NQwhU0Ti5fx-4wWrKU8
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                RequestRoleFragment.this.lambda$onCreateDialog$0$RequestRoleFragment(adapterView, view, i, j);
            }
        });
        this.mAdapter = new Adapter(this.mListView, this.mRole);
        if (bundle != null) {
            this.mAdapter.onRestoreInstanceState(bundle);
        }
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        CheckBox checkBox = (CheckBox) inflate2.requireViewById(R.id.dont_ask_again);
        boolean isDeniedOnce = UserDeniedManager.getInstance(context).isDeniedOnce(this.mRoleName, this.mPackageName);
        checkBox.setVisibility(isDeniedOnce ? 0 : 8);
        if (isDeniedOnce) {
            this.mDontAskAgainCheck = checkBox;
            this.mDontAskAgainCheck.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$wW82qQsdAhk9ydNeuj0HvpFhbOw
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RequestRoleFragment.this.lambda$onCreateDialog$1$RequestRoleFragment(view);
                }
            });
            if (bundle != null) {
                boolean z = bundle.getBoolean(STATE_DONT_ASK_AGAIN);
                this.mDontAskAgainCheck.setChecked(z);
                this.mAdapter.setDontAskAgain(z);
            }
        }
        final AlertDialog create = builder.setCustomTitle(inflate).setView(inflate2).setPositiveButton(R.string.request_role_set_as_default, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$Q9Y-2tdj8J8Lpfwl46pJUP2Hrt0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create();
        create.getWindow().addSystemFlags(524288);
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$bbB-jE208eiaS2b21gajz7M8aoY
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                RequestRoleFragment.this.lambda$onCreateDialog$4$RequestRoleFragment(create, dialogInterface);
            }
        });
        return create;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$RequestRoleFragment(AdapterView adapterView, View view, int i, long j) {
        onItemClicked(i);
    }

    public /* synthetic */ void lambda$onCreateDialog$1$RequestRoleFragment(View view) {
        updateUi();
    }

    public /* synthetic */ void lambda$onCreateDialog$4$RequestRoleFragment(AlertDialog alertDialog, DialogInterface dialogInterface) {
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$r2v1riaCmOkiXjFmCN0Zgr3-o8A
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RequestRoleFragment.this.lambda$onCreateDialog$3$RequestRoleFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$onCreateDialog$3$RequestRoleFragment(View view) {
        onSetAsDefault();
    }

    @Override // androidx.fragment.app.DialogFragment
    public AlertDialog getDialog() {
        return (AlertDialog) super.getDialog();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Context requireContext = requireContext();
        if (PackageUtils.getApplicationInfo(this.mPackageName, requireContext) == null) {
            String str = LOG_TAG;
            Log.w(str, "Unknown application: " + this.mPackageName);
            reportRequestResult(1, null);
            finish();
            return;
        }
        this.mPackageRemovalMonitor = new PackageRemovalMonitor(requireContext, this.mPackageName) { // from class: com.android.packageinstaller.role.ui.RequestRoleFragment.1
            @Override // com.android.packageinstaller.permission.utils.PackageRemovalMonitor
            protected void onPackageRemoved() {
                String str2 = RequestRoleFragment.LOG_TAG;
                Log.w(str2, "Application is uninstalled, role: " + RequestRoleFragment.this.mRoleName + ", package: " + RequestRoleFragment.this.mPackageName);
                RequestRoleFragment.this.reportRequestResult(1, null);
                RequestRoleFragment.this.finish();
            }
        };
        this.mPackageRemovalMonitor.register();
        this.mViewModel = (RequestRoleViewModel) ViewModelProviders.of(this, new RequestRoleViewModel.Factory(this.mRole, requireActivity().getApplication())).get(RequestRoleViewModel.class);
        this.mViewModel.getRoleLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$YO4bWG1Ym16rP0Gh8JtFfpJKU6w
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                RequestRoleFragment.this.onRoleDataChanged((List) obj);
            }
        });
        this.mViewModel.getManageRoleHolderStateLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$RequestRoleFragment$p7o6jtz8hhC4aofOzIcYJA85HWw
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                RequestRoleFragment.this.onManageRoleHolderStateChanged(((Integer) obj).intValue());
            }
        });
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mAdapter.onSaveInstanceState(bundle);
        CheckBox checkBox = this.mDontAskAgainCheck;
        if (checkBox != null) {
            bundle.putBoolean(STATE_DONT_ASK_AGAIN, checkBox.isChecked());
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        PackageRemovalMonitor packageRemovalMonitor = this.mPackageRemovalMonitor;
        if (packageRemovalMonitor != null) {
            packageRemovalMonitor.unregister();
            this.mPackageRemovalMonitor = null;
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        String str = LOG_TAG;
        Log.i(str, "Dialog cancelled, role: " + this.mRoleName + ", package: " + this.mPackageName);
        reportRequestResult(6, null);
        setDeniedOnceAndFinish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRoleDataChanged(List<Pair<ApplicationInfo, Boolean>> list) {
        this.mAdapter.replace(list);
        updateUi();
    }

    private void onItemClicked(int i) {
        this.mAdapter.onItemClicked(i);
        updateUi();
    }

    private void onSetAsDefault() {
        CheckBox checkBox = this.mDontAskAgainCheck;
        if (checkBox != null && checkBox.isChecked()) {
            String str = LOG_TAG;
            Log.i(str, "Request denied with don't ask again, role: " + this.mRoleName + ", package: " + this.mPackageName);
            reportRequestResult(8, null);
            setDeniedAlwaysAndFinish();
            return;
        }
        setRoleHolder();
    }

    private void setRoleHolder() {
        String checkedPackageName = this.mAdapter.getCheckedPackageName();
        Context requireContext = requireContext();
        UserHandle myUserHandle = Process.myUserHandle();
        if (checkedPackageName == null) {
            reportRequestResult(7, null);
            this.mRole.onNoneHolderSelectedAsUser(myUserHandle, requireContext);
            this.mViewModel.getManageRoleHolderStateLiveData().clearRoleHoldersAsUser(this.mRoleName, 0, myUserHandle, requireContext);
            return;
        }
        boolean equals = Objects.equals(checkedPackageName, this.mPackageName);
        if (equals) {
            reportRequestResult(5, null);
        } else {
            reportRequestResult(7, checkedPackageName);
        }
        this.mViewModel.getManageRoleHolderStateLiveData().setRoleHolderAsUser(this.mRoleName, checkedPackageName, true, equals ? 1 : 0, myUserHandle, requireContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onManageRoleHolderStateChanged(int i) {
        if (i == 0 || i == 1) {
            updateUi();
        } else if (i != 2) {
            if (i != 3) {
                return;
            }
            finish();
        } else {
            ManageRoleHolderStateLiveData manageRoleHolderStateLiveData = this.mViewModel.getManageRoleHolderStateLiveData();
            String lastPackageName = manageRoleHolderStateLiveData.getLastPackageName();
            if (lastPackageName != null) {
                this.mRole.onHolderSelectedAsUser(lastPackageName, manageRoleHolderStateLiveData.getLastUser(), requireContext());
            }
            if (Objects.equals(lastPackageName, this.mPackageName)) {
                String str = LOG_TAG;
                Log.i(str, "Application added as a role holder, role: " + this.mRoleName + ", package: " + this.mPackageName);
                clearDeniedSetResultOkAndFinish();
                return;
            }
            String str2 = LOG_TAG;
            Log.i(str2, "Request denied with another application added as a role holder, role: " + this.mRoleName + ", package: " + this.mPackageName);
            setDeniedOnceAndFinish();
        }
    }

    private void updateUi() {
        boolean z = true;
        boolean z2 = this.mViewModel.getManageRoleHolderStateLiveData().getValue().intValue() == 0;
        this.mListView.setEnabled(z2);
        CheckBox checkBox = this.mDontAskAgainCheck;
        boolean z3 = checkBox != null && checkBox.isChecked();
        this.mAdapter.setDontAskAgain(z3);
        AlertDialog dialog = getDialog();
        boolean z4 = this.mViewModel.getRoleLiveData().getValue() != null;
        Button button = dialog.getButton(-1);
        if (!z2 || !z4 || (!z3 && this.mAdapter.isHolderApplicationChecked())) {
            z = false;
        }
        button.setEnabled(z);
        dialog.getButton(-2).setEnabled(z2);
    }

    private void clearDeniedSetResultOkAndFinish() {
        UserDeniedManager.getInstance(requireContext()).clearDenied(this.mRoleName, this.mPackageName);
        requireActivity().setResult(-1);
        finish();
    }

    private void setDeniedOnceAndFinish() {
        UserDeniedManager.getInstance(requireContext()).setDeniedOnce(this.mRoleName, this.mPackageName);
        finish();
    }

    private void setDeniedAlwaysAndFinish() {
        UserDeniedManager.getInstance(requireContext()).setDeniedAlways(this.mRoleName, this.mPackageName);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish() {
        requireActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportRequestResult(int i, String str) {
        String holderPackageName = getHolderPackageName();
        reportRequestResult(getApplicationUid(this.mPackageName), this.mPackageName, this.mRoleName, getQualifyingApplicationCount(), getQualifyingApplicationUid(holderPackageName), holderPackageName, getQualifyingApplicationUid(str), str, i);
    }

    private int getApplicationUid(String str) {
        int qualifyingApplicationUid = getQualifyingApplicationUid(str);
        if (qualifyingApplicationUid != -1) {
            return qualifyingApplicationUid;
        }
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, requireActivity());
        if (applicationInfo == null) {
            return -1;
        }
        return applicationInfo.uid;
    }

    private int getQualifyingApplicationUid(String str) {
        Adapter adapter;
        if (str != null && (adapter = this.mAdapter) != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                Pair<ApplicationInfo, Boolean> item = this.mAdapter.getItem(i);
                if (item != null) {
                    ApplicationInfo applicationInfo = (ApplicationInfo) item.first;
                    if (Objects.equals(applicationInfo.packageName, str)) {
                        return applicationInfo.uid;
                    }
                }
            }
        }
        return -1;
    }

    private int getQualifyingApplicationCount() {
        Adapter adapter = this.mAdapter;
        if (adapter == null) {
            return -1;
        }
        int count = adapter.getCount();
        return (count <= 0 || this.mAdapter.getItem(0) != null) ? count : count - 1;
    }

    private String getHolderPackageName() {
        Adapter adapter = this.mAdapter;
        if (adapter == null) {
            return null;
        }
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Pair<ApplicationInfo, Boolean> item = this.mAdapter.getItem(i);
            if (item != null && ((Boolean) item.second).booleanValue()) {
                return ((ApplicationInfo) item.first).packageName;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reportRequestResult(int i, String str, String str2, int i2, int i3, String str3, int i4, String str4, int i5) {
        String str5 = LOG_TAG;
        Log.v(str5, "Role request result requestingUid=" + i + " requestingPackageName=" + str + " roleName=" + str2 + " qualifyingCount=" + i2 + " currentUid=" + i3 + " currentPackageName=" + str3 + " grantedAnotherUid=" + i4 + " grantedAnotherPackageName=" + str4 + " result=" + i5);
        PermissionControllerStatsLog.write(190, i, str, str2, i2, i3, str3, i4, str4, i5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Adapter extends BaseAdapter {
        private static final String STATE_USER_CHECKED = Adapter.class.getName() + ".state.USER_CHECKED";
        private static final String STATE_USER_CHECKED_PACKAGE_NAME = Adapter.class.getName() + ".state.USER_CHECKED_PACKAGE_NAME";
        private boolean mDontAskAgain;
        private boolean mHasHolderApplication;
        private final ListView mListView;
        private boolean mPendingUserChecked;
        private String mPendingUserCheckedPackageName;
        private final List<Pair<ApplicationInfo, Boolean>> mQualifyingApplications = new ArrayList();
        private final Role mRole;
        private boolean mUserChecked;

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        Adapter(ListView listView, Role role) {
            this.mListView = listView;
            this.mRole = role;
        }

        public void onSaveInstanceState(Bundle bundle) {
            bundle.putBoolean(STATE_USER_CHECKED, this.mUserChecked);
            if (this.mUserChecked) {
                bundle.putString(STATE_USER_CHECKED_PACKAGE_NAME, getCheckedPackageName());
            }
        }

        public void onRestoreInstanceState(Bundle bundle) {
            this.mPendingUserChecked = bundle.getBoolean(STATE_USER_CHECKED);
            if (this.mPendingUserChecked) {
                this.mPendingUserCheckedPackageName = bundle.getString(STATE_USER_CHECKED_PACKAGE_NAME);
            }
        }

        public void setDontAskAgain(boolean z) {
            if (this.mDontAskAgain == z) {
                return;
            }
            this.mDontAskAgain = z;
            if (this.mDontAskAgain) {
                this.mUserChecked = false;
                updateItemChecked();
            }
            notifyDataSetChanged();
        }

        public void onItemClicked(int i) {
            this.mUserChecked = true;
            notifyDataSetChanged();
        }

        public void replace(List<Pair<ApplicationInfo, Boolean>> list) {
            this.mQualifyingApplications.clear();
            if (this.mRole.shouldShowNone()) {
                this.mQualifyingApplications.add(0, null);
            }
            this.mQualifyingApplications.addAll(list);
            this.mHasHolderApplication = hasHolderApplication(list);
            notifyDataSetChanged();
            if (this.mPendingUserChecked) {
                restoreItemChecked();
                this.mPendingUserChecked = false;
                this.mPendingUserCheckedPackageName = null;
            }
            if (this.mUserChecked) {
                return;
            }
            updateItemChecked();
        }

        private static boolean hasHolderApplication(List<Pair<ApplicationInfo, Boolean>> list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (((Boolean) list.get(i).second).booleanValue()) {
                    return true;
                }
            }
            return false;
        }

        private void restoreItemChecked() {
            if (this.mPendingUserCheckedPackageName == null) {
                if (this.mRole.shouldShowNone()) {
                    this.mUserChecked = true;
                    this.mListView.setItemChecked(0, true);
                    return;
                }
                return;
            }
            int count = getCount();
            for (int i = 0; i < count; i++) {
                Pair<ApplicationInfo, Boolean> item = getItem(i);
                if (item != null && Objects.equals(((ApplicationInfo) item.first).packageName, this.mPendingUserCheckedPackageName)) {
                    this.mUserChecked = true;
                    this.mListView.setItemChecked(i, true);
                    return;
                }
            }
        }

        private void updateItemChecked() {
            if (!this.mHasHolderApplication) {
                if (this.mRole.shouldShowNone()) {
                    this.mListView.setItemChecked(0, true);
                    return;
                } else {
                    this.mListView.clearChoices();
                    return;
                }
            }
            int count = getCount();
            for (int i = 0; i < count; i++) {
                Pair<ApplicationInfo, Boolean> item = getItem(i);
                if (item != null && ((Boolean) item.second).booleanValue()) {
                    this.mListView.setItemChecked(i, true);
                    return;
                }
            }
        }

        public Pair<ApplicationInfo, Boolean> getCheckedItem() {
            int checkedItemPosition = this.mListView.getCheckedItemPosition();
            if (checkedItemPosition != -1) {
                return getItem(checkedItemPosition);
            }
            return null;
        }

        public String getCheckedPackageName() {
            Pair<ApplicationInfo, Boolean> checkedItem = getCheckedItem();
            if (checkedItem == null) {
                return null;
            }
            return ((ApplicationInfo) checkedItem.first).packageName;
        }

        public boolean isHolderApplicationChecked() {
            Pair<ApplicationInfo, Boolean> checkedItem = getCheckedItem();
            if (checkedItem == null) {
                return !this.mHasHolderApplication;
            }
            return ((Boolean) checkedItem.second).booleanValue();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mQualifyingApplications.size();
        }

        @Override // android.widget.Adapter
        public Pair<ApplicationInfo, Boolean> getItem(int i) {
            return this.mQualifyingApplications.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            Pair<ApplicationInfo, Boolean> item = getItem(i);
            if (item == null) {
                return 0L;
            }
            return ((ApplicationInfo) item.first).packageName.hashCode();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            if (this.mDontAskAgain) {
                Pair<ApplicationInfo, Boolean> item = getItem(i);
                if (item == null) {
                    return !this.mHasHolderApplication;
                }
                return ((Boolean) item.second).booleanValue();
            }
            return true;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            String str;
            Drawable drawable;
            Context context = viewGroup.getContext();
            if (view != null) {
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.request_role_item, viewGroup, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                viewHolder.titleAndSubtitleLayout.getLayoutTransition().setDuration(150L);
            }
            view.setEnabled(isEnabled(i));
            Pair<ApplicationInfo, Boolean> item = getItem(i);
            String str2 = null;
            if (item == null) {
                drawable = AppCompatResources.getDrawable(context, R.drawable.ic_remove_circle);
                str = context.getString(R.string.default_app_none);
                if (!this.mHasHolderApplication) {
                    str2 = context.getString(R.string.request_role_current_default);
                }
            } else {
                ApplicationInfo applicationInfo = (ApplicationInfo) item.first;
                Drawable badgedIcon = Utils.getBadgedIcon(context, applicationInfo);
                String appLabel = Utils.getAppLabel(applicationInfo, context);
                if (((Boolean) item.second).booleanValue()) {
                    str2 = context.getString(R.string.request_role_current_default);
                } else if (this.mListView.isItemChecked(i)) {
                    str2 = context.getString(this.mRole.getRequestDescriptionResource());
                }
                str = appLabel;
                drawable = badgedIcon;
            }
            viewHolder.iconImage.setImageDrawable(drawable);
            viewHolder.titleText.setText(str);
            viewHolder.subtitleText.setVisibility(TextUtils.isEmpty(str2) ? 8 : 0);
            viewHolder.subtitleText.setText(str2);
            return view;
        }

        /* loaded from: classes.dex */
        private static class ViewHolder {
            public final ImageView iconImage;
            public final TextView subtitleText;
            public final ViewGroup titleAndSubtitleLayout;
            public final TextView titleText;

            ViewHolder(View view) {
                this.iconImage = (ImageView) view.requireViewById(R.id.icon);
                this.titleAndSubtitleLayout = (ViewGroup) view.requireViewById(R.id.title_and_subtitle);
                this.titleText = (TextView) view.requireViewById(R.id.title);
                this.subtitleText = (TextView) view.requireViewById(R.id.subtitle);
            }
        }
    }
}
