package com.oic.app.home;

import android.app.Activity;
import android.os.Bundle;
import com.oic.map.view.OicMapView;

/**
 * Created by khacpham on 5/9/15.
 */
public class HomeActivity extends Activity {

    OicMapView oicMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oicMap = new OicMapView(this, null);

    }
}
