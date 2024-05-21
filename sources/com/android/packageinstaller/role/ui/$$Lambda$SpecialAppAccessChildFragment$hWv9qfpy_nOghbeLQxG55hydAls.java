package com.android.packageinstaller.role.ui;

import com.android.packageinstaller.role.ui.SpecialAppAccessViewModel;
/* compiled from: lambda */
/* renamed from: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessChildFragment$hWv9qfpy_nOghbeLQxG55hydAls  reason: invalid class name */
/* loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$SpecialAppAccessChildFragment$hWv9qfpy_nOghbeLQxG55hydAls implements SpecialAppAccessViewModel.ManageRoleHolderStateObserver {
    private final /* synthetic */ SpecialAppAccessChildFragment f$0;

    public /* synthetic */ $$Lambda$SpecialAppAccessChildFragment$hWv9qfpy_nOghbeLQxG55hydAls(SpecialAppAccessChildFragment specialAppAccessChildFragment) {
        this.f$0 = specialAppAccessChildFragment;
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessViewModel.ManageRoleHolderStateObserver
    public final void onManageRoleHolderStateChanged(ManageRoleHolderStateLiveData manageRoleHolderStateLiveData, int i) {
        SpecialAppAccessChildFragment.lambda$hWv9qfpy_nOghbeLQxG55hydAls(this.f$0, manageRoleHolderStateLiveData, i);
    }
}
