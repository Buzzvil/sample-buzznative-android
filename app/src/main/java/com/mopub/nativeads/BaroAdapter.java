package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.BuzzSDK;
import com.buzzvil.baro.UserProfile;
import com.buzzvil.baro.nativead.Ad;
import com.buzzvil.baro.nativead.AdListener;
import com.buzzvil.baro.nativead.AssetBinder;
import com.buzzvil.baro.nativead.NativeAd;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

public class BaroAdapter extends CustomEventNative {
    private static final String TAG = "BaroAdapter";
    private static final String PLACEMENT_ID_KEY = "placementId";

    private static Location location = null;
    private static String birthday = null;
    private static String gender = null;
    private static AssetBinder assetBinder = null;


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

        initializeUserProfile(context);
        final NativeAd baroNativeAd = new NativeAd.Builder(context, placementId, new BaroForMopub())
                .setAssetBinder(assetBinder)
                .build();
        if (location != null) {
            baroNativeAd.setLocation(location.latitude, location.longitude);
        }
        baroNativeAd.loadAd();
    }

    private static void logD(@NonNull final String tag, @NonNull final String message) {
        MoPubLog.d(tag + ": " + message);
    }

    private static void initializeUserProfile(final Context context) {
        final UserProfile.Builder profileBuilder = new UserProfile.Builder();
        if (!TextUtils.isEmpty(birthday)) {
            profileBuilder.setBirthday(birthday);
        }
        if (!TextUtils.isEmpty(gender)) {
            profileBuilder.setGender(gender);
        }
        final UserProfile userProfile = profileBuilder.build();

        BuzzSDK.setUserProfile(context, userProfile);
    }

    public static void setLocation(final double latitude, final double longitude) {
        BaroAdapter.location = new Location(latitude, longitude);
    }

    /**
     * Set the birthday of the user
     *
     * @param birthday A String with 'yyyy-MM-dd' format
     */
    public static void setUserBirthday(final String birthday) {
        BaroAdapter.birthday = birthday;
    }

    /**
     * Set the gender of the user
     *
     * @param gender UserProfile.USER_GENDER_FEMALE or UserProfile.USER_GENDER_MALE
     */
    public static void setUserGender(final String gender) {
        BaroAdapter.gender = gender;
    }

    public static void setAssetBinder(final AssetBinder assetBinder) {
        BaroAdapter.assetBinder = assetBinder;
    }

    static class Location {
        final double latitude;
        final double longitude;

        Location(final double latitude, final double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    protected class BaroForMopub extends StaticNativeAd implements AdListener {
        static final String TAG = "BaroForMopub";

        private Ad ad;

        @Override
        public void onError(final BuzzAdError buzzAdError) {
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
        public void onAdLoaded(final Ad ad) {
            logD(TAG, "onAdLoaded");
            this.ad = ad;
            if (customEventNativeListener != null) {
                customEventNativeListener.onNativeAdLoaded(this);
            }
        }

        @Override
        public void onImpressed(final Ad ad) {
            logD(TAG, "onImpression");
            notifyAdImpressed();
        }

        @Override
        public void onClick(final Ad ad) {
            logD(TAG, "onClick");
            notifyAdClicked();
        }

        Ad getAd() {
            return ad;
        }
    }
}
