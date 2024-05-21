package com.android.car.ui.utils;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.drivingstate.CarUxRestrictions;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.content.Context;
import android.util.Log;
import com.android.car.ui.R;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
/* loaded from: classes.dex */
public class CarUxRestrictionsUtil {
    private static final String TAG = "CarUxRestrictionsUtil";
    private static CarUxRestrictionsUtil sInstance;
    private final Car mCarApi;
    private CarUxRestrictions mCarUxRestrictions = getDefaultRestrictions();
    private CarUxRestrictionsManager mCarUxRestrictionsManager;
    private Set<OnUxRestrictionsChangedListener> mObservers;

    /* loaded from: classes.dex */
    public interface OnUxRestrictionsChangedListener {
        void onRestrictionsChanged(CarUxRestrictions carUxRestrictions);
    }

    private CarUxRestrictionsUtil(Context context) {
        CarUxRestrictionsManager.OnUxRestrictionsChangedListener onUxRestrictionsChangedListener = new CarUxRestrictionsManager.OnUxRestrictionsChangedListener() { // from class: com.android.car.ui.utils.-$$Lambda$CarUxRestrictionsUtil$b4kksuDJ2ImLxn6J1BQQlgUD4Tk
        };
        this.mCarApi = Car.createCar(context.getApplicationContext());
        this.mObservers = Collections.newSetFromMap(new WeakHashMap());
        try {
            this.mCarUxRestrictionsManager = (CarUxRestrictionsManager) this.mCarApi.getCarManager("uxrestriction");
            this.mCarUxRestrictionsManager.registerListener(onUxRestrictionsChangedListener);
            onUxRestrictionsChangedListener.onUxRestrictionsChanged(this.mCarUxRestrictionsManager.getCurrentCarUxRestrictions());
        } catch (NullPointerException | CarNotConnectedException e) {
            Log.e(TAG, "Car not connected", e);
        }
    }

    private static CarUxRestrictions getDefaultRestrictions() {
        return new CarUxRestrictions.Builder(true, 511, 0L).build();
    }

    public static CarUxRestrictionsUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CarUxRestrictionsUtil(context);
        }
        return sInstance;
    }

    public void register(OnUxRestrictionsChangedListener onUxRestrictionsChangedListener) {
        this.mObservers.add(onUxRestrictionsChangedListener);
        onUxRestrictionsChangedListener.onRestrictionsChanged(this.mCarUxRestrictions);
    }

    public void unregister(OnUxRestrictionsChangedListener onUxRestrictionsChangedListener) {
        this.mObservers.remove(onUxRestrictionsChangedListener);
    }

    public CarUxRestrictions getCurrentRestrictions() {
        return this.mCarUxRestrictions;
    }

    public static boolean isRestricted(int i, CarUxRestrictions carUxRestrictions) {
        return carUxRestrictions == null || (i & carUxRestrictions.getActiveRestrictions()) != 0;
    }

    public static String complyString(Context context, String str, CarUxRestrictions carUxRestrictions) {
        int maxRestrictedStringLength;
        if (isRestricted(4, carUxRestrictions)) {
            if (carUxRestrictions == null) {
                maxRestrictedStringLength = context.getResources().getInteger(R.integer.car_ui_default_max_string_length);
            } else {
                maxRestrictedStringLength = carUxRestrictions.getMaxRestrictedStringLength();
            }
            if (str.length() > maxRestrictedStringLength) {
                return str.substring(0, maxRestrictedStringLength) + context.getString(R.string.car_ui_ellipsis);
            }
        }
        return str;
    }

    public void setUxRestrictions(CarUxRestrictions carUxRestrictions) {
        this.mCarUxRestrictions = carUxRestrictions;
    }
}
