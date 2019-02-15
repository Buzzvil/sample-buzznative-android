package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Class for legacy name "BuzzNativeAdapter"
 */
public class BuzzNativeAdapter extends BaroAdapter {
    @Override
    protected void loadNativeAd(@NonNull final Context context,
                                @NonNull final CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        super.loadNativeAd(context, customEventNativeListener, localExtras, serverExtras);
    }
}
