package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.NativeAdView;
import com.mopub.common.logging.MoPubLog;

import java.util.WeakHashMap;

public class BuzzNativeRenderer implements MoPubAdRenderer<BuzzNativeAdapter.BuzzNativeForMoPub> {
    private static final String TAG = "BuzzNativeRenderer";

    /**
     * ID for the frame layout that wraps the Google ad view.
     */
    @IdRes
    private static final int ID_WRAPPING_FRAME = 2001;

    /**
     * ID for the Google native ad view.
     */
    @IdRes
    private static final int ID_BUZZ_NATIVE_VIEW = 2002;

    /**
     * A view binder containing the layout resource and views to be rendered by the renderer.
     */
    private final ViewBinder mViewBinder;

    /**
     * A weak hash map used to keep track of view holder so that the views can be properly recycled.
     */
    private final WeakHashMap<View, BuzzNativeForMopubViewHolder> mViewHolderMap;

    public BuzzNativeRenderer(ViewBinder viewBinder) {
        this.mViewBinder = viewBinder;
        this.mViewHolderMap = new WeakHashMap<>();
    }

    @NonNull
    @Override
    public View createAdView(@NonNull Context context, @Nullable ViewGroup parent) {
        logD(TAG, "createAdView()");
        View view = LayoutInflater.from(context).inflate(mViewBinder.layoutId, parent, false);
        // Create a frame layout and add the inflated view as a child. This will allow us to add
        // the Google native ad view into the view hierarchy at render time.
        FrameLayout wrappingView = new FrameLayout(context);
        wrappingView.setId(ID_WRAPPING_FRAME);
        wrappingView.addView(view);
        logD(TAG, "Ad view created.");
        return wrappingView;
    }

    @Override
    public void renderAdView(@NonNull View view,
                             @NonNull BuzzNativeAdapter.BuzzNativeForMoPub buzzNativeAd) {
        logD(TAG, "renderAdView()");
        BuzzNativeForMopubViewHolder viewHolder = mViewHolderMap.get(view);
        if (viewHolder == null) {
            viewHolder = BuzzNativeForMopubViewHolder.fromViewBinder(view, mViewBinder);
            mViewHolderMap.put(view, viewHolder);
        }

        removeBuzzNativeAdView(view);

        NativeAdView nativeAdView = new NativeAdView(view.getContext());
        updateBuzzNativeAdView(buzzNativeAd, viewHolder, nativeAdView);
        insertBuzzNativeAdView(nativeAdView, view);
    }

    /**
     * This method will add the given BuzzNative ad view into the view hierarchy of the given
     * MoPub native ad view.
     *
     * @param buzzNativeAdView  Native ad view to be added as a parent to the MoPub's
     *                          view.
     * @param moPubNativeAdView MoPub's native ad view created by this renderer.
     */
    private static void insertBuzzNativeAdView(NativeAdView buzzNativeAdView,
                                               View moPubNativeAdView) {
        if (moPubNativeAdView instanceof FrameLayout
                && moPubNativeAdView.getId() == ID_WRAPPING_FRAME) {
            buzzNativeAdView.setId(ID_BUZZ_NATIVE_VIEW);
            FrameLayout outerFrame = (FrameLayout) moPubNativeAdView;
            View actualView = outerFrame.getChildAt(0);

            buzzNativeAdView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            outerFrame.removeView(actualView);
            buzzNativeAdView.addView(actualView, 0);
            outerFrame.addView(buzzNativeAdView);
        } else {
            Log.w(TAG, "Couldn't add BuzzNative ad view. Wrapping view not found.");
        }
    }

    /**
     * This method will remove the BuzzNative ad view from the view hierarchy if one is present.
     *
     * @param view the view from which to remove the BuzzNative ad view.
     */
    protected static void removeBuzzNativeAdView(@NonNull View view) {
        if (view instanceof FrameLayout && view.getId() == ID_WRAPPING_FRAME) {
            View adView = view.findViewById(ID_BUZZ_NATIVE_VIEW);
            if (adView != null) {
                ViewGroup outerView = (ViewGroup) view;
                int index = outerView.indexOfChild(adView);
                outerView.removeView(adView);
                View actualNativeView = ((ViewGroup) adView).getChildAt(0);
                if (actualNativeView != null) {
                    ((ViewGroup) adView).removeView(actualNativeView);
                    outerView.addView(actualNativeView, index);
                }

                if (adView instanceof NativeAdView) {
                    ((NativeAdView) adView).destroy();
                }
            }
        }
    }

    /**
     * This method will render the given native ad view using the native ad and set the views to
     * BuzzNative ad view.
     *
     * @param buzzNativeAd         a static native ad object containing the required assets to
     *                             set to the native ad view.
     * @param buzzNativeViewHolder a static native view holder object containing the mapped
     *                             views from the view binder.
     * @param nativeAdView         the BuzzNative ad view in the view hierarchy.
     */
    private void updateBuzzNativeAdView(BuzzNativeAdapter.BuzzNativeForMoPub buzzNativeAd,
                                        BuzzNativeForMopubViewHolder buzzNativeViewHolder,
                                        NativeAdView nativeAdView) {
        nativeAdView.setTitleView(buzzNativeViewHolder.titleView);
        nativeAdView.setDescriptionView(buzzNativeViewHolder.descriptionView);
        nativeAdView.setImageView(buzzNativeViewHolder.imageView);
        nativeAdView.setIconView(buzzNativeViewHolder.iconView);
        nativeAdView.setCallToActionView(buzzNativeViewHolder.callToActionView);
        nativeAdView.setSponsoredView(buzzNativeViewHolder.sponsoredView);

        final Ad oldAd = nativeAdView.setAd(buzzNativeAd.getAd());
        if (oldAd != null) {
            oldAd.destroy();
        }
    }

    @Override
    public boolean supports(@NonNull BaseNativeAd nativeAd) {
        return nativeAd instanceof BuzzNativeAdapter.BuzzNativeForMoPub;
    }

    private static void logD(final String tag, final String message) {
        MoPubLog.d(tag + ": " + message);
    }

    private static void logD(final String tag, final String message, final Throwable throwable) {
        MoPubLog.d(tag + ": " + message, throwable);
    }

    private static class BuzzNativeForMopubViewHolder {
        private static final String TAG = "BuzzNativeForMopubViewHolder";

        @Nullable
        View mainView;
        @Nullable
        View titleView;
        @Nullable
        View sponsoredView;
        @Nullable
        View privacyImageView;
        @Nullable
        View imageView;
        @Nullable
        View iconView;
        @Nullable
        View descriptionView;
        @Nullable
        View callToActionView;

        private static final BuzzNativeForMopubViewHolder EMPTY_VIEW_HOLDER =
                new BuzzNativeForMopubViewHolder();

        @NonNull
        public static BuzzNativeRenderer.BuzzNativeForMopubViewHolder fromViewBinder(@NonNull View view,
                                                                                     @NonNull ViewBinder viewBinder) {
            logD(TAG, "fromViewBinder()");
            final BuzzNativeForMopubViewHolder viewHolder = new BuzzNativeForMopubViewHolder();
            viewHolder.mainView = view;
            try {
                viewHolder.titleView = view.findViewById(viewBinder.titleId);
                final Integer sponsoredId = viewBinder.extras.get("sponsored");
                if (sponsoredId != null) {
                    viewHolder.sponsoredView = view.findViewById(sponsoredId);
                }
                viewHolder.privacyImageView = view.findViewById(viewBinder.privacyInformationIconImageId);
                viewHolder.imageView = view.findViewById(viewBinder.mainImageId);
                viewHolder.iconView = view.findViewById(viewBinder.iconImageId);
                viewHolder.descriptionView = view.findViewById(viewBinder.textId);
                viewHolder.callToActionView = view.findViewById(viewBinder.callToActionId);
                return viewHolder;
            } catch (ClassCastException exception) {
                logD(TAG, "Could not cast from id in ViewBinder to expected View type", exception);
                return EMPTY_VIEW_HOLDER;
            }
        }
    }
}
