package sample.buzznative.buzzvil.com.buzznativesample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.buzzvil.baro.BuzzAdError;
import com.buzzvil.baro.nativead.Ad;
import com.buzzvil.baro.nativead.AdListener;
import com.buzzvil.baro.nativead.AssetBinder;
import com.buzzvil.baro.nativead.NativeAd;
import com.buzzvil.baro.nativead.NativeAdView;

import java.util.LinkedList;
import java.util.Queue;

public class NativeAdRecyclerViewActivity extends Activity {
    public static final String TAG = "NARecyclerViewActivity";

    private RecyclerView recyclerView;
    private Button btnLoadAd;
    private TextView tvAdResponse;

    private AssetBinder assetBinder;
    private NativeAd nativeAd;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView();

        assetBinder = getAssetBinder();
        nativeAd = new NativeAd.Builder(NativeAdRecyclerViewActivity.this, BuildConfig.PLACEMENT_ID, nativeAdListener)
                .setAssetBinder(assetBinder)
                .enableParallelRequest()
                .build();
        setupViews();
    }

    void setContentView() {
        setContentView(R.layout.activity_recyclerview_style_native_ad);
    }

    AssetBinder getAssetBinder() {
        return new AssetBinder.Builder()
                .setTitleId(R.id.tvTitle)
                .setIconImageId(R.id.ivIcon)
                .setCallToActionId(R.id.btnCTA)
                .setSponsoredId(R.id.tvSponsored)
                .setCoverMediaId(R.id.viewCoverMedia)
                .setDescriptionId(R.id.tvDescription)
                .build();
    }

    void setupViews() {
        btnLoadAd = findViewById(R.id.btnLoadAd);
        tvAdResponse = findViewById(R.id.tvAdResponse);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerAdapter = new RecyclerAdapter();

        btnLoadAd.setOnClickListener(btnClickListener);
        recyclerView.setAdapter(recyclerAdapter);
    }

    void requestAd() {
        nativeAd.loadAd();

        tvAdResponse.setText("Loading");
    }

    void setNativeAdView(Ad ad) {
        recyclerView.setVisibility(View.VISIBLE);
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
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Log.e(TAG, "AdListener : onAdLoaded - " + (ad != null ? "Success" : "Fail"));
            tvAdResponse.setText("onAdLoaded - " + (ad != null ? "Success" : "Fail"));

            if (ad == null) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerAdapter.addAdvertisement(ad);
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

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_TEXT = 0;
        private final int VIEW_TYPE_NATIVE_AD = 1;

        private final Queue<Ad> ads = new LinkedList<>();

        RecyclerAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_TEXT:
                    View textView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_text, viewGroup, false);
                    return new TextViewHolder(textView);
                case VIEW_TYPE_NATIVE_AD:
                    View nativeAdView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_native_ad, viewGroup, false);
                    return new NativeAdViewHolder(nativeAdView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            switch (viewHolder.getItemViewType()) {
                case VIEW_TYPE_TEXT:
                    TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
                    textViewHolder.setText("Item: " + position);
                    break;
                case VIEW_TYPE_NATIVE_AD:
                    NativeAdViewHolder nativeAdViewHolder = (NativeAdViewHolder) viewHolder;
                    nativeAdViewHolder.setAd(ads.poll());
                    requestAd();
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return ((position % 5) == 4) ? VIEW_TYPE_NATIVE_AD : VIEW_TYPE_TEXT;
        }

        @Override
        public int getItemCount() {
            return 100000;
        }

        public void addAdvertisement(Ad ad) {
            this.ads.add(ad);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.text);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }

    class NativeAdViewHolder extends RecyclerView.ViewHolder {
        NativeAdView nativeAdView;

        NativeAdViewHolder(View view) {
            super(view);
            nativeAdView = view.findViewById(R.id.nativeAdView);
            nativeAdView.setAssets(assetBinder);
        }

        public void setAd(Ad ad) {
            if (ad == null) {
                return;
            }
            final Ad oldAd = nativeAdView.unregisterAd();
            if (oldAd != null) {
                oldAd.destroy();
            }
            nativeAdView.registerAd(ad);
            if (!TextUtils.isEmpty(ad.getCallToAction())) {
                ((Button) nativeAdView.findViewById(R.id.btnCTA)).setText(ad.getCallToAction());
                nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.VISIBLE);
            } else {
                nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.GONE);
            }
        }
    }
}
