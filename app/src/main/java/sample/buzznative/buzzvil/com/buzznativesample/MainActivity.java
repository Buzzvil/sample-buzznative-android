package sample.buzznative.buzzvil.com.buzznativesample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickButton(View view) {
        NativeAdActivity.AdStyle adStyle = null;
        switch (view.getId()) {
            case R.id.btnFeedStyle:
                adStyle = NativeAdActivity.AdStyle.FEED;
                break;
            case R.id.btnBannerStyle:
                adStyle = NativeAdActivity.AdStyle.BANNER;
                break;
        }
        if (adStyle != null) {
            startActivity(new Intent(MainActivity.this, NativeAdActivity.class)
                    .putExtra("AdStyle", adStyle));
        }
    }
}
