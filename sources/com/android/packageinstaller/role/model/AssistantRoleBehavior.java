package com.android.packageinstaller.role.model;

import android.app.ActivityManager;
import android.app.Application;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.utils.UserUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AssistantRoleBehavior implements RoleBehavior {
    private static final String LOG_TAG = "AssistantRoleBehavior";
    private static final Intent ASSIST_SERVICE_PROBE = new Intent("android.service.voice.VoiceInteractionService");
    private static final Intent ASSIST_ACTIVITY_PROBE = new Intent("android.intent.action.ASSIST");

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void onRoleAdded(Role role, Context context) {
        if (context.getPackageManager().isDeviceUpgrading() && ((RoleManager) context.getSystemService(RoleManager.class)).getRoleHolders(role.getName()).isEmpty()) {
            role.onNoneHolderSelectedAsUser(Process.myUserHandle(), context);
        }
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        return !UserUtils.isWorkProfile(userHandle, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        return ExclusiveDefaultHolderMixin.getDefaultHolder(role, "config_defaultAssistant", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return VisibilityMixin.isVisible("config_showDefaultAssistant", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public Intent getManageIntentAsUser(Role role, UserHandle userHandle, Context context) {
        return new Intent("android.settings.VOICE_INPUT_SETTINGS");
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public CharSequence getConfirmationMessage(Role role, String str, Context context) {
        return context.getString(R.string.assistant_confirmation_message);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public List<String> getQualifyingPackagesAsUser(Role role, UserHandle userHandle, Context context) {
        Context userContext = UserUtils.getUserContext(context, userHandle);
        PackageManager packageManager = userContext.getPackageManager();
        ArraySet arraySet = new ArraySet();
        if (!((ActivityManager) userContext.getSystemService(ActivityManager.class)).isLowRamDevice()) {
            List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(ASSIST_SERVICE_PROBE, 786560);
            int size = queryIntentServices.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = queryIntentServices.get(i);
                if (isAssistantVoiceInteractionService(packageManager, resolveInfo.serviceInfo)) {
                    arraySet.add(resolveInfo.serviceInfo.packageName);
                }
            }
        }
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(ASSIST_ACTIVITY_PROBE, 851968);
        int size2 = queryIntentActivities.size();
        for (int i2 = 0; i2 < size2; i2++) {
            arraySet.add(queryIntentActivities.get(i2).activityInfo.packageName);
        }
        return new ArrayList(arraySet);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public Boolean isPackageQualified(Role role, String str, Context context) {
        PackageManager packageManager = context.getPackageManager();
        int i = 0;
        if (!((ActivityManager) context.getSystemService(ActivityManager.class)).isLowRamDevice()) {
            List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent(ASSIST_SERVICE_PROBE).setPackage(str), 786560);
            int i2 = !queryIntentServices.isEmpty() ? 1 : 0;
            int size = queryIntentServices.size();
            while (i < size) {
                if (isAssistantVoiceInteractionService(packageManager, queryIntentServices.get(i).serviceInfo)) {
                    return true;
                }
                i++;
            }
            i = i2;
        }
        boolean z = !packageManager.queryIntentActivities(new Intent(ASSIST_ACTIVITY_PROBE).setPackage(str), 851968).isEmpty();
        if (!z) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Package ");
            sb.append(str);
            sb.append(" not qualified for ");
            sb.append(role.getName());
            sb.append(" due to ");
            sb.append(i != 0 ? "unqualified" : "missing");
            sb.append(" service and missing activity");
            Log.w(str2, sb.toString());
        }
        return Boolean.valueOf(z);
    }

    private boolean isAssistantVoiceInteractionService(PackageManager packageManager, ServiceInfo serviceInfo) {
        int next;
        if ("android.permission.BIND_VOICE_INTERACTION".equals(serviceInfo.permission)) {
            try {
                XmlResourceParser loadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, "android.voice_interaction");
                if (loadXmlMetaData == null) {
                    if (loadXmlMetaData != null) {
                        loadXmlMetaData.close();
                    }
                    return false;
                }
                do {
                    next = loadXmlMetaData.next();
                    if (next == 1) {
                        break;
                    }
                } while (next != 2);
                AttributeSet asAttributeSet = Xml.asAttributeSet(loadXmlMetaData);
                int attributeCount = asAttributeSet.getAttributeCount();
                boolean z = false;
                String str = null;
                String str2 = null;
                for (int i = 0; i < attributeCount; i++) {
                    int attributeNameResource = asAttributeSet.getAttributeNameResource(i);
                    if (attributeNameResource == 16843837) {
                        str = asAttributeSet.getAttributeValue(i);
                    } else if (attributeNameResource == 16843932) {
                        str2 = asAttributeSet.getAttributeValue(i);
                    } else if (attributeNameResource == 16844016) {
                        z = asAttributeSet.getAttributeBooleanValue(i, false);
                    }
                }
                if (str == null || str2 == null || !z) {
                    if (loadXmlMetaData != null) {
                        loadXmlMetaData.close();
                    }
                    return false;
                }
                if (loadXmlMetaData != null) {
                    loadXmlMetaData.close();
                }
                return true;
            } catch (Resources.NotFoundException | IOException | XmlPullParserException unused) {
                return false;
            }
        }
        return false;
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void onHolderChangedAsUser(Role role, UserHandle userHandle, Context context) {
        Utils.updateUserSensitive((Application) context.getApplicationContext(), userHandle);
    }
}
