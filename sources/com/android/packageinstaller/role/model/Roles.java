package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.R;
import com.xiaopeng.libtheme.ThemeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Roles {
    private static final String LOG_TAG = "Roles";
    private static final Object sLock;
    private static final ArrayMap<String, Integer> sModeNameToMode = new ArrayMap<>();
    private static ArrayMap<String, Role> sRoles;

    static {
        sModeNameToMode.put("allowed", 0);
        sModeNameToMode.put("ignored", 1);
        sModeNameToMode.put("errored", 2);
        sModeNameToMode.put("default", 3);
        sModeNameToMode.put(ThemeManager.AttributeSet.FOREGROUND, 4);
        sLock = new Object();
    }

    private Roles() {
    }

    public static ArrayMap<String, Role> get(Context context) {
        ArrayMap<String, Role> arrayMap;
        synchronized (sLock) {
            if (sRoles == null) {
                sRoles = load(context);
            }
            arrayMap = sRoles;
        }
        return arrayMap;
    }

    private static ArrayMap<String, Role> load(Context context) {
        try {
            XmlResourceParser xml = context.getResources().getXml(R.xml.roles);
            try {
                Pair<ArrayMap<String, PermissionSet>, ArrayMap<String, Role>> parseXml = parseXml(xml);
                if (parseXml == null) {
                    ArrayMap<String, Role> arrayMap = new ArrayMap<>();
                    if (xml != null) {
                        xml.close();
                    }
                    return arrayMap;
                }
                ArrayMap arrayMap2 = (ArrayMap) parseXml.first;
                ArrayMap<String, Role> arrayMap3 = (ArrayMap) parseXml.second;
                if (xml != null) {
                    xml.close();
                }
                return arrayMap3;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (xml != null) {
                        try {
                            xml.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        } catch (IOException | XmlPullParserException e) {
            throwOrLogMessage("Unable to parse roles.xml", e);
            return new ArrayMap<>();
        }
    }

    private static Pair<ArrayMap<String, PermissionSet>, ArrayMap<String, Role>> parseXml(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        int depth2 = xmlResourceParser.getDepth() + 1;
        Pair<ArrayMap<String, PermissionSet>, ArrayMap<String, Role>> pair = null;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                if (!xmlResourceParser.getName().equals("roles")) {
                    throwOrLogForUnknownTag(xmlResourceParser);
                    skipCurrentTag(xmlResourceParser);
                } else if (pair != null) {
                    throwOrLogMessage("Duplicate <roles>");
                    skipCurrentTag(xmlResourceParser);
                } else {
                    pair = parseRoles(xmlResourceParser);
                }
            }
        }
        if (pair == null) {
            throwOrLogMessage("Missing <roles>");
        }
        return pair;
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x006e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0050 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.util.Pair<android.util.ArrayMap<java.lang.String, com.android.packageinstaller.role.model.PermissionSet>, android.util.ArrayMap<java.lang.String, com.android.packageinstaller.role.model.Role>> parseRoles(android.content.res.XmlResourceParser r8) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            android.util.ArrayMap r0 = new android.util.ArrayMap
            r0.<init>()
            android.util.ArrayMap r1 = new android.util.ArrayMap
            r1.<init>()
            int r2 = r8.getDepth()
            r3 = 1
            int r2 = r2 + r3
        L10:
            int r4 = r8.next()
            if (r4 == r3) goto L83
            int r5 = r8.getDepth()
            if (r5 >= r2) goto L1f
            r6 = 3
            if (r4 == r6) goto L83
        L1f:
            if (r5 > r2) goto L10
            r5 = 2
            if (r4 == r5) goto L25
            goto L10
        L25:
            java.lang.String r4 = r8.getName()
            r5 = -1
            int r6 = r4.hashCode()
            r7 = -1439271068(0xffffffffaa367764, float:-1.6206269E-13)
            if (r6 == r7) goto L43
            r7 = 3506294(0x358076, float:4.913364E-39)
            if (r6 == r7) goto L39
            goto L4d
        L39:
            java.lang.String r6 = "role"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L4d
            r4 = r3
            goto L4e
        L43:
            java.lang.String r6 = "permission-set"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L4d
            r4 = 0
            goto L4e
        L4d:
            r4 = r5
        L4e:
            if (r4 == 0) goto L6e
            if (r4 == r3) goto L59
            throwOrLogForUnknownTag(r8)
            skipCurrentTag(r8)
            goto L10
        L59:
            com.android.packageinstaller.role.model.Role r4 = parseRole(r8, r0)
            if (r4 != 0) goto L60
            goto L10
        L60:
            r4.getName()
            r1.keySet()
            java.lang.String r5 = r4.getName()
            r1.put(r5, r4)
            goto L10
        L6e:
            com.android.packageinstaller.role.model.PermissionSet r4 = parsePermissionSet(r8)
            if (r4 != 0) goto L75
            goto L10
        L75:
            r4.getName()
            r0.keySet()
            java.lang.String r5 = r4.getName()
            r0.put(r5, r4)
            goto L10
        L83:
            android.util.Pair r8 = new android.util.Pair
            r8.<init>(r0, r1)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.role.model.Roles.parseRoles(android.content.res.XmlResourceParser):android.util.Pair");
    }

    private static PermissionSet parsePermissionSet(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        String requireAttributeValue = requireAttributeValue(xmlResourceParser, "name", "permission-set");
        if (requireAttributeValue == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                if (xmlResourceParser.getName().equals("permission")) {
                    String requireAttributeValue2 = requireAttributeValue(xmlResourceParser, "name", "permission");
                    if (requireAttributeValue2 != null) {
                        arrayList.add(requireAttributeValue2);
                    }
                } else {
                    throwOrLogForUnknownTag(xmlResourceParser);
                    skipCurrentTag(xmlResourceParser);
                }
            }
        }
        return new PermissionSet(requireAttributeValue, arrayList);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Role parseRole(XmlResourceParser xmlResourceParser, ArrayMap<String, PermissionSet> arrayMap) throws IOException, XmlPullParserException {
        RoleBehavior roleBehavior;
        Integer num;
        Integer num2;
        int depth;
        int i;
        char c;
        String requireAttributeValue = requireAttributeValue(xmlResourceParser, "name", "role");
        List<AppOp> list = null;
        if (requireAttributeValue == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        String attributeValue = getAttributeValue(xmlResourceParser, "behavior");
        if (attributeValue != null) {
            String str = Roles.class.getPackage().getName() + '.' + attributeValue;
            try {
                roleBehavior = (RoleBehavior) Class.forName(str).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throwOrLogMessage("Unable to instantiate behavior: " + str, e);
                skipCurrentTag(xmlResourceParser);
                return null;
            }
        } else {
            roleBehavior = null;
        }
        Integer requireAttributeResourceValue = requireAttributeResourceValue(xmlResourceParser, "description", 0, "role");
        if (requireAttributeResourceValue == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        int i2 = 1;
        Boolean requireAttributeBooleanValue = requireAttributeBooleanValue(xmlResourceParser, "exclusive", true, "role");
        if (requireAttributeBooleanValue == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        Integer requireAttributeResourceValue2 = requireAttributeResourceValue(xmlResourceParser, "label", 0, "role");
        if (requireAttributeResourceValue2 == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        boolean attributeBooleanValue = getAttributeBooleanValue(xmlResourceParser, "requestable", true);
        if (attributeBooleanValue) {
            num = requireAttributeResourceValue(xmlResourceParser, "requestDescription", 0, "role");
            if (num == null) {
                skipCurrentTag(xmlResourceParser);
                return null;
            }
            num2 = requireAttributeResourceValue(xmlResourceParser, "requestTitle", 0, "role");
            if (num2 == null) {
                skipCurrentTag(xmlResourceParser);
                return null;
            }
        } else {
            num = 0;
            num2 = 0;
        }
        Integer requireAttributeResourceValue3 = requireAttributeResourceValue(xmlResourceParser, "shortLabel", 0, "role");
        if (requireAttributeResourceValue3 == null) {
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        boolean attributeBooleanValue2 = getAttributeBooleanValue(xmlResourceParser, "showNone", false);
        if (attributeBooleanValue2 && !requireAttributeBooleanValue.booleanValue()) {
            throwOrLogMessage("showNone=\"true\" is invalid for a non-exclusive role: " + requireAttributeValue);
            skipCurrentTag(xmlResourceParser);
            return null;
        }
        boolean attributeBooleanValue3 = getAttributeBooleanValue(xmlResourceParser, "systemOnly", false);
        int depth2 = xmlResourceParser.getDepth() + 1;
        List<RequiredComponent> list2 = null;
        List<String> list3 = null;
        List<PreferredActivity> list4 = null;
        while (true) {
            int next = xmlResourceParser.next();
            if (next != i2 && ((depth = xmlResourceParser.getDepth()) >= depth2 || next != 3)) {
                if (depth > depth2 || next != 2) {
                    i = 1;
                } else {
                    String name = xmlResourceParser.getName();
                    switch (name.hashCode()) {
                        case -795106042:
                            if (name.equals("app-ops")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case -162238279:
                            if (name.equals("preferred-activities")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1133704324:
                            if (name.equals("permissions")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1920178692:
                            if (name.equals("required-components")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    if (c != 0) {
                        i = 1;
                        if (c != 1) {
                            if (c != 2) {
                                if (c != 3) {
                                    throwOrLogForUnknownTag(xmlResourceParser);
                                    skipCurrentTag(xmlResourceParser);
                                } else if (list4 != null) {
                                    throwOrLogMessage("Duplicate <preferred-activities> in role: " + requireAttributeValue);
                                    skipCurrentTag(xmlResourceParser);
                                } else {
                                    list4 = parsePreferredActivities(xmlResourceParser);
                                }
                            } else if (list != null) {
                                throwOrLogMessage("Duplicate <app-ops> in role: " + requireAttributeValue);
                                skipCurrentTag(xmlResourceParser);
                            } else {
                                list = parseAppOps(xmlResourceParser);
                            }
                        } else if (list3 != null) {
                            throwOrLogMessage("Duplicate <permissions> in role: " + requireAttributeValue);
                            skipCurrentTag(xmlResourceParser);
                        } else {
                            list3 = parsePermissions(xmlResourceParser, arrayMap);
                        }
                    } else {
                        i = 1;
                        if (list2 != null) {
                            throwOrLogMessage("Duplicate <required-components> in role: " + requireAttributeValue);
                            skipCurrentTag(xmlResourceParser);
                        } else {
                            list2 = parseRequiredComponents(xmlResourceParser);
                        }
                    }
                }
                i2 = i;
            }
        }
        List<RequiredComponent> emptyList = list2 == null ? Collections.emptyList() : list2;
        List<String> emptyList2 = list3 == null ? Collections.emptyList() : list3;
        List<AppOp> emptyList3 = list == null ? Collections.emptyList() : list;
        if (list4 == null) {
            list4 = Collections.emptyList();
        }
        return new Role(requireAttributeValue, roleBehavior, requireAttributeResourceValue.intValue(), requireAttributeBooleanValue.booleanValue(), requireAttributeResourceValue2.intValue(), num.intValue(), num2.intValue(), attributeBooleanValue, requireAttributeResourceValue3.intValue(), attributeBooleanValue2, attributeBooleanValue3, emptyList, emptyList2, emptyList3, list4);
    }

    private static List<RequiredComponent> parseRequiredComponents(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        ArrayList arrayList = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        while (true) {
            int next = xmlResourceParser.next();
            if (next != 1 && ((depth = xmlResourceParser.getDepth()) >= depth2 || next != 3)) {
                if (depth <= depth2 && next == 2) {
                    String name = xmlResourceParser.getName();
                    char c = 65535;
                    switch (name.hashCode()) {
                        case -1655966961:
                            if (name.equals("activity")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -987494927:
                            if (name.equals("provider")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -808719889:
                            if (name.equals("receiver")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1984153269:
                            if (name.equals("service")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    if (c == 0 || c == 1 || c == 2 || c == 3) {
                        RequiredComponent parseRequiredComponent = parseRequiredComponent(xmlResourceParser, name);
                        if (parseRequiredComponent != null) {
                            arrayList.add(parseRequiredComponent);
                        }
                    } else {
                        throwOrLogForUnknownTag(xmlResourceParser);
                        skipCurrentTag(xmlResourceParser);
                    }
                }
            }
        }
        return arrayList;
    }

    private static RequiredComponent parseRequiredComponent(XmlResourceParser xmlResourceParser, String str) throws IOException, XmlPullParserException {
        char c;
        int depth;
        Boolean requireAttributeBooleanValue;
        String attributeValue = getAttributeValue(xmlResourceParser, "permission");
        ArrayList arrayList = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        IntentFilterData intentFilterData = null;
        while (true) {
            int next = xmlResourceParser.next();
            c = 65535;
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                String name = xmlResourceParser.getName();
                int hashCode = name.hashCode();
                if (hashCode != -1115949454) {
                    if (hashCode == -1029793847 && name.equals("intent-filter")) {
                        c = 0;
                    }
                } else if (name.equals("meta-data")) {
                    c = 1;
                }
                if (c != 0) {
                    if (c == 1) {
                        String requireAttributeValue = requireAttributeValue(xmlResourceParser, "name", "meta-data");
                        if (requireAttributeValue != null && (requireAttributeBooleanValue = requireAttributeBooleanValue(xmlResourceParser, "value", false, "meta-data")) != null) {
                            arrayList.add(new RequiredMetaData(requireAttributeValue, requireAttributeBooleanValue, getAttributeBooleanValue(xmlResourceParser, "optional", false)));
                        }
                    } else {
                        throwOrLogForUnknownTag(xmlResourceParser);
                        skipCurrentTag(xmlResourceParser);
                    }
                } else if (intentFilterData != null) {
                    throwOrLogMessage("Duplicate <intent-filter> in <" + str + ">");
                    skipCurrentTag(xmlResourceParser);
                } else {
                    intentFilterData = parseIntentFilterData(xmlResourceParser);
                }
            }
        }
        if (intentFilterData == null) {
            throwOrLogMessage("Missing <intent-filter> in <" + str + ">");
            return null;
        }
        switch (str.hashCode()) {
            case -1655966961:
                if (str.equals("activity")) {
                    c = 0;
                    break;
                }
                break;
            case -987494927:
                if (str.equals("provider")) {
                    c = 1;
                    break;
                }
                break;
            case -808719889:
                if (str.equals("receiver")) {
                    c = 2;
                    break;
                }
                break;
            case 1984153269:
                if (str.equals("service")) {
                    c = 3;
                    break;
                }
                break;
        }
        if (c != 0) {
            if (c != 1) {
                if (c != 2) {
                    if (c == 3) {
                        return new RequiredService(intentFilterData, attributeValue, arrayList);
                    }
                    throwOrLogMessage("Unknown tag <" + str + ">");
                    return null;
                }
                return new RequiredBroadcastReceiver(intentFilterData, attributeValue, arrayList);
            }
            return new RequiredContentProvider(intentFilterData, attributeValue, arrayList);
        }
        return new RequiredActivity(intentFilterData, attributeValue, arrayList);
    }

    private static IntentFilterData parseIntentFilterData(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        ArrayList arrayList = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        String str = null;
        String str2 = null;
        String str3 = null;
        boolean z = false;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                String name = xmlResourceParser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -1422950858) {
                    if (hashCode != 3076010) {
                        if (hashCode == 50511102 && name.equals("category")) {
                            c = 1;
                        }
                    } else if (name.equals("data")) {
                        c = 2;
                    }
                } else if (name.equals("action")) {
                    c = 0;
                }
                if (c != 0) {
                    if (c == 1) {
                        String requireAttributeValue = requireAttributeValue(xmlResourceParser, "name", "category");
                        if (requireAttributeValue != null) {
                            validateIntentFilterCategory(requireAttributeValue);
                            arrayList.add(requireAttributeValue);
                        }
                    } else if (c != 2) {
                        throwOrLogForUnknownTag(xmlResourceParser);
                        skipCurrentTag(xmlResourceParser);
                    } else if (z) {
                        throwOrLogMessage("Duplicate <data> in <intent-filter>");
                        skipCurrentTag(xmlResourceParser);
                    } else {
                        String attributeValue = getAttributeValue(xmlResourceParser, "scheme");
                        String attributeValue2 = getAttributeValue(xmlResourceParser, "mimeType");
                        if (attributeValue2 != null) {
                            validateIntentFilterDataType(attributeValue2);
                        }
                        z = true;
                        str3 = attributeValue2;
                        str2 = attributeValue;
                    }
                } else if (str != null) {
                    throwOrLogMessage("Duplicate <action> in <intent-filter>");
                    skipCurrentTag(xmlResourceParser);
                } else {
                    str = requireAttributeValue(xmlResourceParser, "name", "action");
                }
            }
        }
        if (str == null) {
            throwOrLogMessage("Missing <action> in <intent-filter>");
            return null;
        }
        return new IntentFilterData(str, arrayList, str2, str3);
    }

    private static void validateIntentFilterCategory(String str) {
        if (Objects.equals(str, "android.intent.category.DEFAULT")) {
            throwOrLogMessage("<category> should not include android.intent.category.DEFAULT");
        }
    }

    private static void validateIntentFilterDataType(String str) {
        int indexOf = str.indexOf(47);
        if (indexOf <= 0 || str.length() < indexOf + 2) {
            throwOrLogMessage("Invalid attribute \"mimeType\" value on <data>: " + str);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0061 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x004d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.util.List<java.lang.String> parsePermissions(android.content.res.XmlResourceParser r9, android.util.ArrayMap<java.lang.String, com.android.packageinstaller.role.model.PermissionSet> r10) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = r9.getDepth()
            r2 = 1
            int r1 = r1 + r2
        Lb:
            int r3 = r9.next()
            if (r3 == r2) goto L92
            int r4 = r9.getDepth()
            if (r4 >= r1) goto L1a
            r5 = 3
            if (r3 == r5) goto L92
        L1a:
            if (r4 > r1) goto Lb
            r4 = 2
            if (r3 == r4) goto L20
            goto Lb
        L20:
            java.lang.String r3 = r9.getName()
            r4 = -1
            int r5 = r3.hashCode()
            r6 = -1439271068(0xffffffffaa367764, float:-1.6206269E-13)
            java.lang.String r7 = "permission"
            java.lang.String r8 = "permission-set"
            if (r5 == r6) goto L40
            r6 = -517618225(0xffffffffe125c5cf, float:-1.911229E20)
            if (r5 == r6) goto L38
            goto L48
        L38:
            boolean r3 = r3.equals(r7)
            if (r3 == 0) goto L48
            r3 = r2
            goto L49
        L40:
            boolean r3 = r3.equals(r8)
            if (r3 == 0) goto L48
            r3 = 0
            goto L49
        L48:
            r3 = r4
        L49:
            java.lang.String r4 = "name"
            if (r3 == 0) goto L61
            if (r3 == r2) goto L56
            throwOrLogForUnknownTag(r9)
            skipCurrentTag(r9)
            goto Lb
        L56:
            java.lang.String r3 = requireAttributeValue(r9, r4, r7)
            if (r3 != 0) goto L5d
            goto Lb
        L5d:
            r0.add(r3)
            goto Lb
        L61:
            java.lang.String r3 = requireAttributeValue(r9, r4, r8)
            if (r3 != 0) goto L68
            goto Lb
        L68:
            boolean r4 = r10.containsKey(r3)
            if (r4 != 0) goto L83
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unknown permission set:"
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            throwOrLogMessage(r3)
            goto Lb
        L83:
            java.lang.Object r3 = r10.get(r3)
            com.android.packageinstaller.role.model.PermissionSet r3 = (com.android.packageinstaller.role.model.PermissionSet) r3
            java.util.List r3 = r3.getPermissions()
            r0.addAll(r3)
            goto Lb
        L92:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.role.model.Roles.parsePermissions(android.content.res.XmlResourceParser, android.util.ArrayMap):java.util.List");
    }

    private static List<AppOp> parseAppOps(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                if (xmlResourceParser.getName().equals("app-op")) {
                    String requireAttributeValue = requireAttributeValue(xmlResourceParser, "name", "app-op");
                    if (requireAttributeValue != null) {
                        arrayList.add(requireAttributeValue);
                        Integer valueOf = Integer.valueOf(getAttributeIntValue(xmlResourceParser, "maxTargetSdkVersion", RecyclerView.UNDEFINED_DURATION));
                        if (valueOf.intValue() == Integer.MIN_VALUE) {
                            valueOf = null;
                        }
                        if (valueOf != null && valueOf.intValue() < 1) {
                            throwOrLogMessage("Invalid value for \"maxTargetSdkVersion\": " + valueOf);
                        }
                        String requireAttributeValue2 = requireAttributeValue(xmlResourceParser, "mode", "app-op");
                        if (requireAttributeValue2 != null) {
                            int indexOfKey = sModeNameToMode.indexOfKey(requireAttributeValue2);
                            if (indexOfKey < 0) {
                                throwOrLogMessage("Unknown value for \"mode\" on <app-op>: " + requireAttributeValue2);
                            } else {
                                arrayList2.add(new AppOp(requireAttributeValue, valueOf, sModeNameToMode.valueAt(indexOfKey).intValue()));
                            }
                        }
                    }
                } else {
                    throwOrLogForUnknownTag(xmlResourceParser);
                    skipCurrentTag(xmlResourceParser);
                }
            }
        }
        return arrayList2;
    }

    private static List<PreferredActivity> parsePreferredActivities(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        int depth;
        ArrayList arrayList = new ArrayList();
        int depth2 = xmlResourceParser.getDepth() + 1;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || ((depth = xmlResourceParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2) {
                if (xmlResourceParser.getName().equals("preferred-activity")) {
                    PreferredActivity parsePreferredActivity = parsePreferredActivity(xmlResourceParser);
                    if (parsePreferredActivity != null) {
                        arrayList.add(parsePreferredActivity);
                    }
                } else {
                    throwOrLogForUnknownTag(xmlResourceParser);
                    skipCurrentTag(xmlResourceParser);
                }
            }
        }
        return arrayList;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x006c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x004d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.android.packageinstaller.role.model.PreferredActivity parsePreferredActivity(android.content.res.XmlResourceParser r10) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = r10.getDepth()
            r2 = 1
            int r1 = r1 + r2
            r3 = 0
            r4 = r3
        Ld:
            int r5 = r10.next()
            if (r5 == r2) goto L7e
            int r6 = r10.getDepth()
            if (r6 >= r1) goto L1c
            r7 = 3
            if (r5 == r7) goto L7e
        L1c:
            if (r6 > r1) goto Ld
            r6 = 2
            if (r5 == r6) goto L22
            goto Ld
        L22:
            java.lang.String r5 = r10.getName()
            r6 = -1
            int r7 = r5.hashCode()
            r8 = -1655966961(0xffffffff9d4bf30f, float:-2.6992485E-21)
            java.lang.String r9 = "activity"
            if (r7 == r8) goto L42
            r8 = -1029793847(0xffffffffc29e97c9, float:-79.296455)
            if (r7 == r8) goto L38
            goto L4a
        L38:
            java.lang.String r7 = "intent-filter"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L4a
            r5 = r2
            goto L4b
        L42:
            boolean r5 = r5.equals(r9)
            if (r5 == 0) goto L4a
            r5 = 0
            goto L4b
        L4a:
            r5 = r6
        L4b:
            if (r5 == 0) goto L6c
            if (r5 == r2) goto L56
            throwOrLogForUnknownTag(r10)
            skipCurrentTag(r10)
            goto Ld
        L56:
            com.android.packageinstaller.role.model.IntentFilterData r5 = parseIntentFilterData(r10)
            if (r5 != 0) goto L5d
            goto Ld
        L5d:
            java.lang.String r6 = r5.getDataType()
            if (r6 == 0) goto L68
            java.lang.String r6 = "mimeType in <data> is not supported when setting a preferred activity"
            throwOrLogMessage(r6)
        L68:
            r0.add(r5)
            goto Ld
        L6c:
            if (r4 == 0) goto L77
            java.lang.String r5 = "Duplicate <activity> in <preferred-activity>"
            throwOrLogMessage(r5)
            skipCurrentTag(r10)
            goto Ld
        L77:
            com.android.packageinstaller.role.model.RequiredComponent r4 = parseRequiredComponent(r10, r9)
            com.android.packageinstaller.role.model.RequiredActivity r4 = (com.android.packageinstaller.role.model.RequiredActivity) r4
            goto Ld
        L7e:
            if (r4 != 0) goto L86
            java.lang.String r10 = "Missing <activity> in <preferred-activity>"
            throwOrLogMessage(r10)
            return r3
        L86:
            boolean r10 = r0.isEmpty()
            if (r10 == 0) goto L92
            java.lang.String r10 = "Missing <intent-filter> in <preferred-activity>"
            throwOrLogMessage(r10)
            return r3
        L92:
            com.android.packageinstaller.role.model.PreferredActivity r10 = new com.android.packageinstaller.role.model.PreferredActivity
            r10.<init>(r4, r0)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.role.model.Roles.parsePreferredActivity(android.content.res.XmlResourceParser):com.android.packageinstaller.role.model.PreferredActivity");
    }

    private static void skipCurrentTag(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
        int depth = xmlResourceParser.getDepth() + 1;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1) {
                return;
            }
            if (xmlResourceParser.getDepth() < depth && next == 3) {
                return;
            }
        }
    }

    private static String getAttributeValue(XmlResourceParser xmlResourceParser, String str) {
        return xmlResourceParser.getAttributeValue(null, str);
    }

    private static String requireAttributeValue(XmlResourceParser xmlResourceParser, String str, String str2) {
        String attributeValue = getAttributeValue(xmlResourceParser, str);
        if (attributeValue == null) {
            throwOrLogMessage("Missing attribute \"" + str + "\" on <" + str2 + ">");
        }
        return attributeValue;
    }

    private static boolean getAttributeBooleanValue(XmlResourceParser xmlResourceParser, String str, boolean z) {
        return xmlResourceParser.getAttributeBooleanValue(null, str, z);
    }

    private static Boolean requireAttributeBooleanValue(XmlResourceParser xmlResourceParser, String str, boolean z, String str2) {
        if (requireAttributeValue(xmlResourceParser, str, str2) == null) {
            return null;
        }
        return Boolean.valueOf(getAttributeBooleanValue(xmlResourceParser, str, z));
    }

    private static int getAttributeIntValue(XmlResourceParser xmlResourceParser, String str, int i) {
        return xmlResourceParser.getAttributeIntValue(null, str, i);
    }

    private static int getAttributeResourceValue(XmlResourceParser xmlResourceParser, String str, int i) {
        return xmlResourceParser.getAttributeResourceValue(null, str, i);
    }

    private static Integer requireAttributeResourceValue(XmlResourceParser xmlResourceParser, String str, int i, String str2) {
        if (requireAttributeValue(xmlResourceParser, str, str2) == null) {
            return null;
        }
        return Integer.valueOf(getAttributeResourceValue(xmlResourceParser, str, i));
    }

    private static void throwOrLogMessage(String str) {
        Log.wtf(LOG_TAG, str);
    }

    private static void throwOrLogMessage(String str, Throwable th) {
        Log.wtf(LOG_TAG, str, th);
    }

    private static void throwOrLogForUnknownTag(XmlResourceParser xmlResourceParser) {
        throwOrLogMessage("Unknown tag: " + xmlResourceParser.getName());
    }
}
