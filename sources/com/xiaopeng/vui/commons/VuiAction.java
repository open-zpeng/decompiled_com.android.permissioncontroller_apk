package com.xiaopeng.vui.commons;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public enum VuiAction {
    CLICK("Click"),
    SETVALUE("SetValue"),
    SCROLLBYX("ScrollByX"),
    SCROLLBYY("ScrollByY"),
    SETCHECK("SetCheck"),
    ITEMCLICK("ItemClick"),
    SELECTTAB("SelectTab"),
    SCROLLTO("ScrollTo"),
    SETSELECTED("SetSelected");
    
    private String name;

    VuiAction(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public static List<String> getVuiActionList() {
        ArrayList arrayList = new ArrayList();
        for (VuiAction vuiAction : values()) {
            arrayList.add(vuiAction.getName());
        }
        return arrayList;
    }
}
