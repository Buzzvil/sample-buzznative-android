package com.buzzvil.baro.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.interstitialad.InterstitialAd;
import com.buzzvil.baro.interstitialad.InterstitialAdListener;

public class InterstitialAdActivity extends AppCompatActivity {

    private Button btnLoadAd;
    private Button btnShowAd;
    private TextView tvAdResponse;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        interstitialAd = new InterstitialAd(InterstitialAdActivity.this);
        interstitialAd.setPlacementId(BuildConfig.PLACEMENT_ID);
        interstitialAd.setInterstitialAdListener(interstitialAdListener);

        btnLoadAd = findViewById(R.id.btnLoadAd);
        btnLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interstitialAd.load();
            }
        });

        btnShowAd = findViewById(R.id.btnShowAd);
        btnShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });

        tvAdResponse = findViewById(R.id.tvAdResponse);
    }

    private InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
        @Override
        public void onAdLoaded() {
            tvAdResponse.setText("onAdLoaded");
        }

        @Override
        public void onAdDismissed() {
            tvAdResponse.setText("onAdDismissed");
            interstitialAd.destroy();
        }

        @Override
        public void onAdImpressed() {
            tvAdResponse.setText("onAdImpressed");
        }

        @Override
        public void onAdClicked() {
            tvAdResponse.setText("onAdClicked");
        }

        @Override
        public void onAdLeftApplication() {
            tvAdResponse.setText("onAdLeftApplication");
        }

        @Override
        public void onError(BuzzAdError buzzAdError) {
            tvAdResponse.setText("onAdError");
        }
    };
}
