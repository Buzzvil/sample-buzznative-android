package sample.buzznative.buzzvil.com.buzznativesample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buzzvil.buzzad.BuzzAdError;
import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.AdListener;
import com.buzzvil.buzzad.nativead.NativeAd;
import com.buzzvil.buzzad.nativead.NativeAdView;

public class MainActivity extends AppCompatActivity {
    public static final String PLACEMENT_ID = "474364197940281";
    public static final String TAG = "MainActivity";
    private static final boolean IS_WITH_XML = true;
    private NativeAdView nativeAdView;
    private Button btnLoadAd;
    private TextView tvAdResponse;
    private LinearLayout llAdHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        btnLoadAd.setOnClickListener(btnClickListener);
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

    void bindViews() {
        tvAdResponse = (TextView) findViewById(R.id.tvAdResponse);
        btnLoadAd = (Button) findViewById(R.id.btnLoadAd);
        nativeAdView = (NativeAdView) findViewById(R.id.nativeAdView);
        llAdHolder = (LinearLayout) findViewById(R.id.llAdHolder);
    }

    void requestAd() {
        NativeAd nativeAd = new NativeAd(this, PLACEMENT_ID);
        nativeAd.setAdListener(nativeAdListener); //adListener 선언은 Step5 참조
        nativeAd.loadAd();

        tvAdResponse.setText("Loading");
    }

    void setNativeAdViewWithoutXml(Ad ad) {
        NativeBannerView view = new NativeBannerView(MainActivity.this);
        view.setAd(ad);
        llAdHolder.addView(view);
    }

    void setNativeAdViewWithXml(Ad ad) {
        nativeAdView.setVisibility(View.VISIBLE);
        nativeAdView.setTitleView(nativeAdView.findViewById(R.id.tvTitle));
        nativeAdView.setDescriptionView(nativeAdView.findViewById(R.id.tvDescription));
        nativeAdView.setImageView(nativeAdView.findViewById(R.id.ivCoverImage));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ivIcon));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.btnCTA));
        nativeAdView.setSponsoredView(nativeAdView.findViewById(R.id.tvSponsored));
        nativeAdView.setAd(ad);

        if (TextUtils.isEmpty(ad.getCallToAction()) == false) {
            ((Button)nativeAdView.findViewById(R.id.btnCTA)).setText(ad.getCallToAction());
            nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.VISIBLE);
        }
        else {
            nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.GONE);
        }
    }

    AdListener nativeAdListener = new AdListener() {
        @Override
        public void onError(BuzzAdError error) {
            Log.e(TAG, "AdListener : onError - " + error);
            tvAdResponse.setText("AdListener : onError - " + error);
            nativeAdView.setVisibility(View.GONE);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Log.e(TAG, "AdListener : onAdLoaded - " + (ad != null? "Success" : "Fail"));
            tvAdResponse.setText("onAdLoaded - " + (ad != null? "Success" : "Fail"));

            if(ad == null) {
                nativeAdView.setVisibility(View.GONE);
            } else {
                if(IS_WITH_XML) {
                    nativeAdView.setVisibility(View.VISIBLE);
                    setNativeAdViewWithXml(ad);
                } else {
                    setNativeAdViewWithoutXml(ad);
                }
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
