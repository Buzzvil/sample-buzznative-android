package sample.buzznative.buzzvil.com.buzznativesample;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.buzzvil.buzzad.nativead.Ad;
import com.buzzvil.buzzad.nativead.NativeAdView;

/**
 * Created by jim on 2018. 5. 2..
 */

public class NativeBannerView extends FrameLayout {
    NativeAdView nativeAdView;

    ViewGroup vGroupNativeAdFrame;
    ImageView ivIcon;
    TextView tvTitle;
    TextView tvContents;
    TextView tvAction;
    TextView tvSponsored;

    public NativeBannerView(Context context) {
        super(context);
        inflate(getContext(), R.layout.view_native_ad_banner, this);

        vGroupNativeAdFrame = (ViewGroup) findViewById(R.id.vGroupNativeAdFrame);
        ivIcon = (ImageView) findViewById(R.id.ivIcon);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvContents = (TextView) findViewById(R.id.tvContents);
        tvAction = (TextView) findViewById(R.id.tvAction);
        tvSponsored = (TextView) findViewById(R.id.tvSponsored);
    }

    public void setAd(Ad ad) {
        nativeAdView = new NativeAdView(getContext());
        nativeAdView.setTitleView(tvTitle);
        nativeAdView.setDescriptionView(tvContents);
        nativeAdView.setIconView(ivIcon);
        nativeAdView.setCallToActionView(tvAction);
        nativeAdView.setSponsoredView(tvSponsored);
        if (TextUtils.isEmpty(ad.getCallToAction()) == false) {
            tvAction.setVisibility(View.VISIBLE);
            tvAction.setText(ad.getCallToAction());
        }
        else {
            tvAction.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(ad.getIconUrl()) == false) {
            Glide.with(getContext()).load(ad.getIconUrl()).into(ivIcon);
        }
        else {
            ivIcon.setImageDrawable(ad.getIconDrawable());
        }

        nativeAdView.setAd(ad);
        vGroupNativeAdFrame.addView(nativeAdView, -1);
    }
}
