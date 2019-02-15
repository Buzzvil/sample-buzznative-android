package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.buzzvil.baro.nativead.Ad;
import com.buzzvil.baro.nativead.AssetBinder;
import com.buzzvil.baro.nativead.NativeAdView;
import com.mopub.common.logging.MoPubLog;

import java.util.WeakHashMap;

import static com.mopub.common.logging.MoPubLog.AdLogEvent.CUSTOM;

public class BaroRenderer implements MoPubAdRenderer<BaroAdapter.BaroForMopub> {
    private static final String TAG = "BaroRenderer";

    /**
     * ID for the frame layout that wraps the NativeAdView of BARO SDK.
     */
    // @IdRes
    private static final int ID_WRAPPING_FRAME = 200001;

    /**
     * ID for the NativeAdView of BARO SDK.
     */
    // @IdRes
    private static final int ID_BARO_AD_VIEW = 200002;

    /**
     * A view binder containing the layout resource and views to be rendered by the renderer.
     */
    private final ViewBinder viewBinder;
    private final AssetBinder assetBinder;

    /**
     * A weak hash map used to keep track of view holder so that the views can be properly recycled.
     */
    private final WeakHashMap<View, NativeAdView> baroNativeAdViewHolderMap;

    public BaroRenderer(final ViewBinder viewBinder) {
        this.viewBinder = viewBinder;
        this.assetBinder = convertToAssetBinder(viewBinder);
        this.baroNativeAdViewHolderMap = new WeakHashMap<>();
    }

    @NonNull
    @Override
    public View createAdView(@NonNull final Context context, @Nullable final ViewGroup parent) {
        logD(TAG, "createAdView()");
        final View view = LayoutInflater.from(context).inflate(viewBinder.layoutId, parent, false);
        // Create a frame layout and add the inflated view as a child. This will allow us to add
        // the Google native ad view into the view hierarchy at render time.
        final FrameLayout wrappingView = new FrameLayout(context);
        wrappingView.setId(ID_WRAPPING_FRAME);
        wrappingView.addView(view);
        logD(TAG, "Ad view created.");
        return wrappingView;
    }

    @Override
    public void renderAdView(@NonNull final View mopubNativeAdView,
                             @NonNull final BaroAdapter.BaroForMopub baroNativeAd) {
        logD(TAG, "renderAdView()");
        NativeAdView baroNativeAdView = getBaroNativeAdView(mopubNativeAdView);
        if (baroNativeAdView == null) {
            baroNativeAdView = getViewHolder(mopubNativeAdView);
            insertBaroNativeAdView(baroNativeAdView, mopubNativeAdView);
        }
        updateBaroNativeAdView(baroNativeAd, baroNativeAdView);
    }

    @Override
    public boolean supports(@NonNull final BaseNativeAd nativeAd) {
        return nativeAd instanceof BaroAdapter.BaroForMopub;
    }

    public static AssetBinder convertToAssetBinder(final ViewBinder mopubViewBinder) {
        final AssetBinder.Builder assetBinderBuilder = new AssetBinder.Builder();
        final Integer sponsoredId = mopubViewBinder.extras.get("sponsored");
        if (sponsoredId != null) {
            assetBinderBuilder.setSponsoredId(sponsoredId);
        }
        return assetBinderBuilder.setTitleId(mopubViewBinder.titleId)
                .setCoverImageId(mopubViewBinder.mainImageId)
                .setIconImageId(mopubViewBinder.iconImageId)
                .setDescriptionId(mopubViewBinder.textId)
                .setCallToActionId(mopubViewBinder.callToActionId)
                .build();
    }

    /**
     * This method will add the given NativeAdView of BARO SDK into the given MoPub's NativeAdView.
     *
     * @param baroNativeAdView  NativeAdView of BARO SDK
     * @param mopubNativeAdView MoPub's native ad view created by this renderer.
     */
    private static void insertBaroNativeAdView(final NativeAdView baroNativeAdView,
                                               final View mopubNativeAdView) {
        if (mopubNativeAdView instanceof FrameLayout && mopubNativeAdView.getId() == ID_WRAPPING_FRAME) {
            baroNativeAdView.setId(ID_BARO_AD_VIEW);
            FrameLayout outerFrame = (FrameLayout) mopubNativeAdView;
            baroNativeAdView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            outerFrame.addView(baroNativeAdView);
            baroNativeAdView.setAssetParent(outerFrame);
        } else {
            Log.w(TAG, "Couldn't add BuzzNative ad view. Wrapping view not found.");
        }
    }

    @Nullable
    private static NativeAdView getBaroNativeAdView(@NonNull final View mopubNativeAdView) {
        if (mopubNativeAdView instanceof FrameLayout && mopubNativeAdView.getId() == ID_WRAPPING_FRAME) {
            return mopubNativeAdView.findViewById(ID_BARO_AD_VIEW);
        }
        return null;
    }

    /**
     * This method will render the given native ad view using the native ad and set the views to
     * BuzzNative ad view.
     *
     * @param baroNativeAd     a static native ad object containing the required assets to
     *                         set to the native ad view.
     * @param baroNativeAdView the BuzzNative ad view in the view hierarchy.
     */
    private static void updateBaroNativeAdView(final BaroAdapter.BaroForMopub baroNativeAd,
                                               final NativeAdView baroNativeAdView) {
        final Ad oldAd = baroNativeAdView.unregisterAd();
        if (oldAd != null) {
            oldAd.destroy();
        }
        baroNativeAdView.registerAd(baroNativeAd.getAd());
    }

    private static void logD(final String tag, final String message) {
        MoPubLog.log(CUSTOM, tag + ": " + message);
    }

    private static void logD(final String tag, final String message, final Throwable throwable) {
        MoPubLog.log(CUSTOM, tag + ": " + message, throwable);
    }

    private NativeAdView getViewHolder(@NonNull final View mopubNativeAdView) {
        final NativeAdView viewHolder;
        if (baroNativeAdViewHolderMap.containsKey(mopubNativeAdView)) {
            viewHolder = baroNativeAdViewHolderMap.get(mopubNativeAdView);
        } else {
            viewHolder = new NativeAdView(mopubNativeAdView.getContext());
            viewHolder.setAssetParent(mopubNativeAdView instanceof ViewGroup ? (ViewGroup) mopubNativeAdView : null);
            viewHolder.setAssets(assetBinder);
            baroNativeAdViewHolderMap.put(mopubNativeAdView, viewHolder);
        }
        return viewHolder;
    }
}
