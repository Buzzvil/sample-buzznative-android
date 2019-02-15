package sample.buzznative.buzzvil.com.buzznativesample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.nativead.Ad;
import com.buzzvil.baro.nativead.AdListener;
import com.buzzvil.baro.nativead.AssetBinder;
import com.buzzvil.baro.nativead.NativeAd;
import com.buzzvil.baro.nativead.NativeAdView;

import static sample.buzznative.buzzvil.com.buzznativesample.NativeAdActivity.AdStyle.FEED;


public class NativeAdActivity extends Activity {
    public static final String TAG = "NativeAdActivity";

    public enum AdStyle {
        FEED, BANNER
    }

    private NativeAd nativeAd = null;

    private NativeAdView nativeAdView;
    private Button btnLoadAd;
    private TextView tvAdResponse;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AdStyle adStyle = (AdStyle) getIntent().getSerializableExtra("AdStyle");

        setContentView(adStyle);

        final AssetBinder assetBinder = getAssetBinder(adStyle);
        nativeAd = new NativeAd.Builder(this, BuildConfig.PLACEMENT_ID, nativeAdListener)
                .setAssetBinder(assetBinder)
                .build();

        setupViews(assetBinder);
    }

    void setContentView(final AdStyle adStyle) {
        switch (adStyle) {
            case FEED:
                setContentView(R.layout.activity_feed_style_native_ad);
                break;
            case BANNER:
                setContentView(R.layout.activity_banner_style_native_ad);
                break;
        }
    }

    AssetBinder getAssetBinder(final AdStyle adStyle) {
        final AssetBinder.Builder builder = new AssetBinder.Builder()
                .setTitleId(R.id.tvTitle)
                .setIconImageId(R.id.ivIcon)
                .setCallToActionId(R.id.btnCTA)
                .setSponsoredId(R.id.tvSponsored);

        if (adStyle == FEED) {
            builder.setCoverMediaId(R.id.viewCoverMedia)
                    .setDescriptionId(R.id.tvDescription);
        }
        return builder.build();
    }

    void setupViews(final AssetBinder assetBinder) {
        btnLoadAd = findViewById(R.id.btnLoadAd);
        tvAdResponse = findViewById(R.id.tvAdResponse);
        nativeAdView = findViewById(R.id.nativeAdView);

        btnLoadAd.setOnClickListener(btnClickListener);
        nativeAdView.setAssets(assetBinder);
    }

    void requestAd() {
        nativeAd.loadAd();

        tvAdResponse.setText("Loading");
    }

    void setNativeAdView(@NonNull final Ad ad) {
        nativeAdView.setVisibility(View.VISIBLE);
        final Ad oldAd = nativeAdView.unregisterAd();
        if (oldAd != null) {
            oldAd.destroy();
        }
        nativeAdView.registerAd(ad);

        if (TextUtils.isEmpty(ad.getCallToAction())) {
            nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.GONE);
        } else {
            ((Button) nativeAdView.findViewById(R.id.btnCTA)).setText(ad.getCallToAction());
            nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnLoadAd:
                    requestAd();
                    break;
            }
        }
    };

    AdListener nativeAdListener = new AdListener() {
        @Override
        public void onError(BuzzAdError error) {
            Log.e(TAG, "AdListener : onError - " + error);
            tvAdResponse.setText("AdListener : onError - " + error);
            nativeAdView.setVisibility(View.GONE);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Log.e(TAG, "AdListener : onAdLoaded - " + (ad != null ? "Success" : "Fail"));
            tvAdResponse.setText("onAdLoaded - " + (ad != null ? "Success" : "Fail"));

            if (ad == null) {
                nativeAdView.setVisibility(View.GONE);
            } else {
                nativeAdView.setVisibility(View.VISIBLE);
                setNativeAdView(ad);
            }
        }

        @Override
        public void onImpressed(Ad ad) {
            Log.d(TAG, "AdListener : onImpressed - " + ad);
        }

        @Override
        public void onClick(Ad ad) {
            Log.d(TAG, "AdListener : onClicked - " + ad);
        }
    };

}
