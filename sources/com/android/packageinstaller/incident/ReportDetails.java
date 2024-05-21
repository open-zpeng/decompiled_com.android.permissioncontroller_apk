package com.android.packageinstaller.incident;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IncidentManager;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ReportDetails {
    private ArrayList<String> mReasons = new ArrayList<>();
    private ArrayList<Drawable> mImages = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class ParseException extends Exception {
        public ParseException(String str) {
            super(str);
        }

        public ParseException(String str, Throwable th) {
            super(str, th);
        }
    }

    private ReportDetails() {
    }

    public static ReportDetails parseIncidentReport(Context context, Uri uri) throws ParseException {
        InputStream inputStream;
        IncidentMinimal parseFrom;
        ReportDetails reportDetails = new ReportDetails();
        try {
            IncidentManager.IncidentReport incidentReport = ((IncidentManager) context.getSystemService(IncidentManager.class)).getIncidentReport(uri);
            if (incidentReport != null && (inputStream = incidentReport.getInputStream()) != null && (parseFrom = IncidentMinimal.parseFrom(inputStream)) != null) {
                parseImages(reportDetails.mImages, parseFrom, context.getResources());
                parseReasons(reportDetails.mReasons, parseFrom);
            }
            return reportDetails;
        } catch (IOException e) {
            throw new ParseException("Error while reading stream.", e);
        } catch (OutOfMemoryError e2) {
            throw new ParseException("Out of memory while loading incident report.", e2);
        }
    }

    private static void parseReasons(ArrayList<String> arrayList, IncidentMinimal incidentMinimal) {
        String reason;
        int headerCount = incidentMinimal.getHeaderCount();
        for (int i = 0; i < headerCount; i++) {
            IncidentHeaderProto header = incidentMinimal.getHeader(i);
            if (header.hasReason() && (reason = header.getReason()) != null && reason.length() > 0) {
                arrayList.add(reason);
            }
        }
    }

    private static void parseImages(ArrayList<Drawable> arrayList, IncidentMinimal incidentMinimal, Resources resources) throws ParseException {
        if (incidentMinimal.hasRestrictedImagesSection()) {
            RestrictedImagesDumpProto restrictedImagesSection = incidentMinimal.getRestrictedImagesSection();
            int setsCount = restrictedImagesSection.getSetsCount();
            int i = 0;
            for (int i2 = 0; i2 < setsCount; i2++) {
                RestrictedImageSetProto sets = restrictedImagesSection.getSets(i2);
                if (sets != null) {
                    int imagesCount = sets.getImagesCount();
                    int i3 = i;
                    for (int i4 = 0; i4 < imagesCount; i4++) {
                        i3++;
                        if (i3 > 200) {
                            throw new ParseException("Image count is greater than the limit of 200");
                        }
                        RestrictedImageProto images = sets.getImages(i4);
                        if (images != null) {
                            String mimeType = images.getMimeType();
                            if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType)) {
                                throw new ParseException("Unsupported image type " + mimeType);
                            }
                            ByteString imageData = images.getImageData();
                            if (imageData != null) {
                                byte[] byteArray = imageData.toByteArray();
                                if (byteArray.length != 0) {
                                    arrayList.add(new BitmapDrawable(resources, new ByteArrayInputStream(byteArray)));
                                }
                            }
                        }
                    }
                    i = i3;
                }
            }
        }
    }

    public ArrayList<String> getReasons() {
        return this.mReasons;
    }

    public ArrayList<Drawable> getImages() {
        return this.mImages;
    }
}
