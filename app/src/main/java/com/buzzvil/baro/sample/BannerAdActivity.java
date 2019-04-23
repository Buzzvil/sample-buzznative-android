package com.buzzvil.baro.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.bannerad.BannerAdListener;
import com.buzzvil.baro.bannerad.BannerAdView;

public class BannerAdActivity extends AppCompatActivity {

    private Button btnLoadAd;
    private TextView tvAdResponse;

    private BannerAdView bannerAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_ad);

        tvAdResponse = findViewById(R.id.tvAdResponse);

        btnLoadAd = findViewById(R.id.btnLoadAd);
        btnLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd();
            }
        });

        bannerAdView = findViewById(R.id.bannerAdView);
        bannerAdView.setAdListener(bannerAdListener);
    }

    private void loadAd() {
        bannerAdView.loadAd();
    }

    private final BannerAdListener bannerAdListener = new BannerAdListener() {
        @Override
        public void onAdLoaded() {
            tvAdResponse.setText("onAdLoaded");
            bannerAdView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onImpressed() {
            tvAdResponse.setText("onImpressed");
        }

        @Override
        public void onClick() {
            tvAdResponse.setText("onCliecked");
        }

        @Override
        public void onError(BuzzAdError buzzAdError) {
            tvAdResponse.setText(buzzAdError.toString());
            bannerAdView.setVisibility(View.GONE);
        }
    };
}
