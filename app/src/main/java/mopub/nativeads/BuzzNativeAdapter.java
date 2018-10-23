package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.buzzvil.buzzad.BuzzAdError;
import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.AdListener;
import com.buzzvil.buzzad.nativead.NativeAd;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

public class BuzzNativeAdapter extends CustomEventNative {
    private static final String TAG = "BuzzNativeAdapter";
    private static final String PLACEMENT_ID_KEY = "placementId";

    private static Location location = null;

    private CustomEventNativeListener customEventNativeListener;

    @Override
    protected void loadNativeAd(@NonNull final Context context,
                                @NonNull final CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        logD(TAG, "loadNativeAd");

        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        if (TextUtils.isEmpty(placementId)) {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
        }

        this.customEventNativeListener = customEventNativeListener;

        final NativeAd nativeAd = new NativeAd(context, placementId);
        nativeAd.setAdListener(new BuzzNativeForMoPub());
        if (location != null) {
            nativeAd.setLocation(location.latitude, location.longitude);
        }
        nativeAd.loadAd();
    }

    private static void logD(@NonNull final String tag, @NonNull final String message) {
        MoPubLog.d(tag + ": " + message);
    }

    public static void setLocation(final double latitude, final double longitude) {
        BuzzNativeAdapter.location = new Location(latitude, longitude);
    }

    static class Location {
        final double latitude;
        final double longitude;

        Location(final double latitude, final double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    class BuzzNativeForMoPub extends StaticNativeAd implements AdListener {
        static final String TAG = "BuzzNativeForMoPub";

        private Ad ad;

        @Override
        public void onError(BuzzAdError buzzAdError) {
            logD(TAG, "onError");
            if (customEventNativeListener != null) {
                switch (buzzAdError) {
                    case NOFILL:
                        customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
                        break;
                    case NETWORK_ERROR:
                        customEventNativeListener.onNativeAdFailed(NativeErrorCode.CONNECTION_ERROR);
                        break;
                }
            }

        }

        @Override
        public void onAdLoaded(Ad ad) {
            logD(TAG, "onAdLoaded");
            this.ad = ad;
            if (customEventNativeListener != null) {
                customEventNativeListener.onNativeAdLoaded(this);
            }
        }

        @Override
        public void onImpressed(Ad ad) {
            logD(TAG, "onImpression");
            notifyAdImpressed();
        }

        @Override
        public void onClick(Ad ad) {
            logD(TAG, "onClick");
            notifyAdClicked();
        }

        Ad getAd() {
            return ad;
        }
    }
}
