package com.mopub.mobileads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.bannerad.BannerAdListener;
import com.buzzvil.baro.bannerad.BannerAdView;
import com.buzzvil.baro.bannerad.BannerSize;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

public class BaroAdapter extends CustomEventBanner {
    private static final String TAG = "BaroAdapter";

    private static final String PLACEMENT_ID_KEY = "placementId";
    private static final String BANNER_SIZE_KEY = "size";

    private BannerAdView bannerAdView;

    private CustomEventBannerListener customEventBannerListener;

    @Override
    protected void loadBanner(@NonNull final Context context,
                              @NonNull final CustomEventBannerListener customEventBannerListener,
                              @NonNull final Map<String, Object> localExtras,
                              @NonNull final Map<String, String> serverExtras) {
        logD(TAG, "loadBanner");

        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        if (TextUtils.isEmpty(placementId)) {
            customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }

        BannerSize bannerSize;
        try {
            bannerSize = BannerSize.valueOf(serverExtras.get(BANNER_SIZE_KEY));
        } catch (IllegalArgumentException | NullPointerException ex) {
            logD(TAG, "Please check setting for banner size on your Mopub admin page.");
            bannerSize = BannerSize.BANNER;
        }

        this.customEventBannerListener = customEventBannerListener;

        bannerAdView = new BannerAdView(context);
        bannerAdView.setPlacementId(placementId);
        bannerAdView.setBannerSize(bannerSize);
        bannerAdView.setAdListener(adListener);

        bannerAdView.loadAd();
    }

    @Override
    protected void onInvalidate() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
    }

    private BannerAdListener adListener = new BannerAdListener() {
        @Override
        public void onAdLoaded() {
            if (customEventBannerListener != null) {
                customEventBannerListener.onBannerLoaded(bannerAdView);
            }
        }

        @Override
        public void onImpressed() {
            if (customEventBannerListener != null) {
                customEventBannerListener.onBannerImpression();
            }
        }

        @Override
        public void onClick() {
            if (customEventBannerListener != null) {
                customEventBannerListener.onBannerClicked();
            }
        }

        @Override
        public void onError(BuzzAdError buzzAdError) {
            if (customEventBannerListener != null) {
                switch (buzzAdError) {
                    case NOFILL:
                        customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
                        break;
                    case NETWORK_ERROR:
                        customEventBannerListener.onBannerFailed(MoPubErrorCode.NO_CONNECTION);
                        break;
                    case INTERNAL_ERROR:
                    default:
                        customEventBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
                        break;
                }
            }
        }
    };

    private static void logD(@NonNull final String tag, @NonNull final String message) {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, tag, message);
    }
}
