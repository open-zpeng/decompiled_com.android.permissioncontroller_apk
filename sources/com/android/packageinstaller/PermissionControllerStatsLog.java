package com.android.packageinstaller;

import android.os.SystemClock;
import android.util.StatsLog;
import java.nio.charset.StandardCharsets;
/* loaded from: classes.dex */
public class PermissionControllerStatsLog {
    public static void write(int i, int i2, String str, String str2, int i3, int i4, String str3, int i5, String str4, int i6) {
        byte[] bytes = (str == null ? "" : str).getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 21;
        byte[] bytes2 = (str2 == null ? "" : str2).getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5 + 5;
        byte[] bytes3 = (str3 == null ? "" : str3).getBytes(StandardCharsets.UTF_8);
        int length3 = length2 + bytes3.length + 5 + 5;
        byte[] bytes4 = (str4 != null ? str4 : "").getBytes(StandardCharsets.UTF_8);
        int length4 = length3 + bytes4.length + 5 + 5;
        if (length4 > 4064) {
            return;
        }
        byte[] bArr = new byte[length4];
        bArr[0] = 3;
        bArr[1] = 11;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 0;
        copyInt(bArr, 17, i2);
        bArr[21] = 2;
        copyInt(bArr, 22, bytes.length);
        System.arraycopy(bytes, 0, bArr, 26, bytes.length);
        int length5 = 21 + bytes.length + 5;
        bArr[length5] = 2;
        copyInt(bArr, length5 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, length5 + 5, bytes2.length);
        int length6 = length5 + bytes2.length + 5;
        bArr[length6] = 0;
        copyInt(bArr, length6 + 1, i3);
        int i7 = length6 + 5;
        bArr[i7] = 0;
        copyInt(bArr, i7 + 1, i4);
        int i8 = i7 + 5;
        bArr[i8] = 2;
        copyInt(bArr, i8 + 1, bytes3.length);
        System.arraycopy(bytes3, 0, bArr, i8 + 5, bytes3.length);
        int length7 = i8 + bytes3.length + 5;
        bArr[length7] = 0;
        copyInt(bArr, length7 + 1, i5);
        int i9 = length7 + 5;
        bArr[i9] = 2;
        copyInt(bArr, i9 + 1, bytes4.length);
        System.arraycopy(bytes4, 0, bArr, i9 + 5, bytes4.length);
        int length8 = i9 + bytes4.length + 5;
        bArr[length8] = 0;
        copyInt(bArr, length8 + 1, i6);
        StatsLog.writeRaw(bArr, length8 + 5);
    }

    public static void write(int i, long j, int i2, String str, int i3) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 30 + 5;
        if (length > 4064) {
            return;
        }
        byte[] bArr = new byte[length];
        bArr[0] = 3;
        bArr[1] = 6;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 0;
        copyInt(bArr, 26, i2);
        bArr[30] = 2;
        copyInt(bArr, 31, bytes.length);
        System.arraycopy(bytes, 0, bArr, 35, bytes.length);
        int length2 = 30 + bytes.length + 5;
        bArr[length2] = 0;
        copyInt(bArr, length2 + 1, i3);
        StatsLog.writeRaw(bArr, length2 + 5);
    }

    public static void write(int i, long j, int i2, String str, String str2) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 30;
        if (str2 == null) {
            str2 = "";
        }
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 6;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 0;
        copyInt(bArr, 26, i2);
        bArr[30] = 2;
        copyInt(bArr, 31, bytes.length);
        System.arraycopy(bytes, 0, bArr, 35, bytes.length);
        int length3 = 30 + bytes.length + 5;
        bArr[length3] = 2;
        copyInt(bArr, length3 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, length3 + 5, bytes2.length);
        StatsLog.writeRaw(bArr, length3 + bytes2.length + 5);
    }

    public static void write(int i, long j, int i2, String str, String str2, boolean z) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 30;
        if (str2 == null) {
            str2 = "";
        }
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 7;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 0;
        copyInt(bArr, 26, i2);
        bArr[30] = 2;
        copyInt(bArr, 31, bytes.length);
        System.arraycopy(bytes, 0, bArr, 35, bytes.length);
        int length3 = 30 + bytes.length + 5;
        bArr[length3] = 2;
        copyInt(bArr, length3 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, length3 + 5, bytes2.length);
        int length4 = length3 + bytes2.length + 5;
        bArr[length4] = 0;
        copyInt(bArr, length4 + 1, z ? 1 : 0);
        StatsLog.writeRaw(bArr, length4 + 5);
    }

    public static void write(int i, long j, int i2, String str, String str2, boolean z, int i3) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 30;
        if (str2 == null) {
            str2 = "";
        }
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5 + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 8;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 0;
        copyInt(bArr, 26, i2);
        bArr[30] = 2;
        copyInt(bArr, 31, bytes.length);
        System.arraycopy(bytes, 0, bArr, 35, bytes.length);
        int length3 = 30 + bytes.length + 5;
        bArr[length3] = 2;
        copyInt(bArr, length3 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, length3 + 5, bytes2.length);
        int length4 = length3 + bytes2.length + 5;
        bArr[length4] = 0;
        copyInt(bArr, length4 + 1, z ? 1 : 0);
        int i4 = length4 + 5;
        bArr[i4] = 0;
        copyInt(bArr, i4 + 1, i3);
        StatsLog.writeRaw(bArr, i4 + 5);
    }

    public static void write(int i, long j, long j2, int i2, String str, String str2, boolean z) {
        byte[] bytes = (str == null ? "" : str).getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 39;
        byte[] bytes2 = (str2 != null ? str2 : "").getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 8;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 1;
        copyLong(bArr, 26, j2);
        bArr[34] = 0;
        copyInt(bArr, 35, i2);
        bArr[39] = 2;
        copyInt(bArr, 40, bytes.length);
        System.arraycopy(bytes, 0, bArr, 44, bytes.length);
        int length3 = 39 + bytes.length + 5;
        bArr[length3] = 2;
        copyInt(bArr, length3 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, length3 + 5, bytes2.length);
        int length4 = length3 + bytes2.length + 5;
        bArr[length4] = 0;
        copyInt(bArr, length4 + 1, z ? 1 : 0);
        StatsLog.writeRaw(bArr, length4 + 5);
    }

    public static void write(int i, long j, long j2, String str, int i2, String str2, int i3) {
        byte[] bytes = (str == null ? "" : str).getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 34 + 5;
        byte[] bytes2 = (str2 != null ? str2 : "").getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 8;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 1;
        copyLong(bArr, 17, j);
        bArr[25] = 1;
        copyLong(bArr, 26, j2);
        bArr[34] = 2;
        copyInt(bArr, 35, bytes.length);
        System.arraycopy(bytes, 0, bArr, 39, bytes.length);
        int length3 = 34 + bytes.length + 5;
        bArr[length3] = 0;
        copyInt(bArr, length3 + 1, i2);
        int i4 = length3 + 5;
        bArr[i4] = 2;
        copyInt(bArr, i4 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, i4 + 5, bytes2.length);
        int length4 = i4 + bytes2.length + 5;
        bArr[length4] = 0;
        copyInt(bArr, length4 + 1, i3);
        StatsLog.writeRaw(bArr, length4 + 5);
    }

    public static void write(int i, String str, int i2, String str2) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 16 + 5;
        if (str2 == null) {
            str2 = "";
        }
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 5;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 2;
        copyInt(bArr, 17, bytes.length);
        System.arraycopy(bytes, 0, bArr, 21, bytes.length);
        int length3 = 16 + bytes.length + 5;
        bArr[length3] = 0;
        copyInt(bArr, length3 + 1, i2);
        int i3 = length3 + 5;
        bArr[i3] = 2;
        copyInt(bArr, i3 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, i3 + 5, bytes2.length);
        StatsLog.writeRaw(bArr, i3 + bytes2.length + 5);
    }

    public static void write(int i, String str, int i2, String str2, int i3, int i4) {
        if (str == null) {
            str = "";
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length + 5 + 16 + 5;
        if (str2 == null) {
            str2 = "";
        }
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        int length2 = length + bytes2.length + 5 + 5 + 5;
        if (length2 > 4064) {
            return;
        }
        byte[] bArr = new byte[length2];
        bArr[0] = 3;
        bArr[1] = 7;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        bArr[2] = 1;
        copyLong(bArr, 3, elapsedRealtimeNanos);
        bArr[11] = 0;
        copyInt(bArr, 12, i);
        bArr[16] = 2;
        copyInt(bArr, 17, bytes.length);
        System.arraycopy(bytes, 0, bArr, 21, bytes.length);
        int length3 = 16 + bytes.length + 5;
        bArr[length3] = 0;
        copyInt(bArr, length3 + 1, i2);
        int i5 = length3 + 5;
        bArr[i5] = 2;
        copyInt(bArr, i5 + 1, bytes2.length);
        System.arraycopy(bytes2, 0, bArr, i5 + 5, bytes2.length);
        int length4 = i5 + bytes2.length + 5;
        bArr[length4] = 0;
        copyInt(bArr, length4 + 1, i3);
        int i6 = length4 + 5;
        bArr[i6] = 0;
        copyInt(bArr, i6 + 1, i4);
        StatsLog.writeRaw(bArr, i6 + 5);
    }

    private static void copyInt(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) i2;
        bArr[i + 1] = (byte) (i2 >> 8);
        bArr[i + 2] = (byte) (i2 >> 16);
        bArr[i + 3] = (byte) (i2 >> 24);
    }

    private static void copyLong(byte[] bArr, int i, long j) {
        bArr[i] = (byte) j;
        bArr[i + 1] = (byte) (j >> 8);
        bArr[i + 2] = (byte) (j >> 16);
        bArr[i + 3] = (byte) (j >> 24);
        bArr[i + 4] = (byte) (j >> 32);
        bArr[i + 5] = (byte) (j >> 40);
        bArr[i + 6] = (byte) (j >> 48);
        bArr[i + 7] = (byte) (j >> 56);
    }
}
