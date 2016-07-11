package alibaba.httpdns_android_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NormalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        getSupportActionBar().setTitle(R.string.normal_scene);

        // 在Native场景下使用HTTPDNS
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkRequestUsingHttpDNS.main(getApplicationContext());
            }
        }).start();
    }
}
