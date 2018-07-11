package sample.buzznative.buzzvil.com.buzznativesample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buzzvil.buzzad.AdType;
import com.buzzvil.buzzad.BuzzAdError;
import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.AdListener;
import com.buzzvil.buzzad.nativead.NativeAd;
import com.buzzvil.buzzad.nativead.NativeAdView;

public class NativeAdActivity extends Activity {
    public static final String PLACEMENT_ID = "YOUR_APP_KEY";
    public static final String TAG = "NativeAdActivity";

    private NativeAdView nativeAdView;
    private Button btnLoadAd;
    private TextView tvAdResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView();

        bindViews();

        setClickListener();
    }

    void setContentView() {
        setContentView(R.layout.activity_fullscreen_style_native_ad);
    }

    void bindViews() {
        btnLoadAd = findViewById(R.id.btnLoadAd);
        tvAdResponse = findViewById(R.id.tvAdResponse);
        nativeAdView = findViewById(R.id.nativeAdView);
    }

    void setClickListener() {
        btnLoadAd.setOnClickListener(btnClickListener);
    }

    void requestAd() {
        NativeAd nativeAd = new NativeAd(this, PLACEMENT_ID);
        nativeAd.setAdListener(nativeAdListener);
        nativeAd.enableAdType(AdType.FULLSCREEN);
        nativeAd.loadAd();

        tvAdResponse.setText("Loading");
    }

    void setNativeAdView(Ad ad) {
        nativeAdView.setVisibility(View.VISIBLE);
        nativeAdView.setImageView(nativeAdView.findViewById(R.id.ivCoverImage));
        nativeAdView.setAd(ad);
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
            String message = "onAdLoaded - " + (ad != null ? "Success" : "Fail");
            if (ad != null) {
                // AdType.NATIVE or AdType.FULLSCREEN
                message += " " + ad.getAdType();
            }
            Log.e(TAG, "AdListener : " + message);
            tvAdResponse.setText(message);

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
