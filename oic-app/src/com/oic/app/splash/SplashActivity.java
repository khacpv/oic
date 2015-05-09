package com.oic.app.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.oic.app.home.HomeActivity;

public class SplashActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startActivity(new Intent(this, HomeActivity.class));
        this.finish();
    }
}
