package sample.buzznative.buzzvil.com.buzznativesample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnFeedStyle;
    private Button btnBannerStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFeedStyle = (Button) findViewById(R.id.btnFeedStyle);
        btnBannerStyle = (Button) findViewById(R.id.btnBannerStyle);

        btnFeedStyle.setOnClickListener(btnClickListener);
        btnBannerStyle.setOnClickListener(btnClickListener);
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFeedStyle:
                    startActivity(new Intent(MainActivity.this, NativeAdActivity.class).putExtra("type", NativeAdActivity.FEED_STYLE));
                    break;

                case R.id.btnBannerStyle:
                    startActivity(new Intent(MainActivity.this, NativeAdActivity.class).putExtra("type", NativeAdActivity.BANNER_STYLE));
                    break;
            }
        }
    };

}
