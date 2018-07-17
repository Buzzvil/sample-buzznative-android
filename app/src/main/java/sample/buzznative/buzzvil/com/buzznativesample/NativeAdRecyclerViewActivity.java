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

import com.buzzvil.buzzad.BuzzAdError;
import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.AdListener;
import com.buzzvil.buzzad.nativead.NativeAd;
import com.buzzvil.buzzad.nativead.NativeAdView;

public class NativeAdRecyclerViewActivity extends Activity {
    public static final String TAG = "NARecyclerViewActivity";

    private RecyclerView recyclerView;
    private Button btnLoadAd;
    private TextView tvAdResponse;

    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView();

        bindViews();

        setClickListener();
    }

    void setContentView() {
        setContentView(R.layout.activity_recyclerview_style_native_ad);
    }

    void bindViews() {
        btnLoadAd = findViewById(R.id.btnLoadAd);
        tvAdResponse = findViewById(R.id.tvAdResponse);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    void setClickListener() {
        btnLoadAd.setOnClickListener(btnClickListener);
    }

    void requestAd() {
        NativeAd nativeAd = new NativeAd(this, BuildConfig.PLACEMENT_ID);
        nativeAd.setAdListener(nativeAdListener);
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
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Log.e(TAG, "AdListener : onAdLoaded - " + (ad != null ? "Success" : "Fail"));
            tvAdResponse.setText("onAdLoaded - " + (ad != null ? "Success" : "Fail"));

            if (ad == null) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerAdapter.setAdvertisement(ad);
                recyclerAdapter.notifyDataSetChanged();
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

        private Ad ad = null;

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
                    nativeAdViewHolder.setAd(ad);
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

        public void setAdvertisement(Ad ad) {
            this.ad = ad;
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
            nativeAdView.setTitleView(nativeAdView.findViewById(R.id.tvTitle));
            nativeAdView.setDescriptionView(nativeAdView.findViewById(R.id.tvDescription));
            nativeAdView.setImageView(nativeAdView.findViewById(R.id.ivCoverImage));
            nativeAdView.setIconView(nativeAdView.findViewById(R.id.ivIcon));
            nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.btnCTA));
            nativeAdView.setSponsoredView(nativeAdView.findViewById(R.id.tvSponsored));
        }

        public void setAd(Ad ad) {
            if (ad == null) {
                return;
            }
            nativeAdView.setAd(ad);
            if (!TextUtils.isEmpty(ad.getCallToAction())) {
                ((Button) nativeAdView.findViewById(R.id.btnCTA)).setText(ad.getCallToAction());
                nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.VISIBLE);
            } else {
                nativeAdView.findViewById(R.id.btnCTA).setVisibility(View.GONE);
            }
        }
    }
}
