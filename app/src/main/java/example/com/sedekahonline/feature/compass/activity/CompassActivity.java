package example.com.sedekahonline.feature.compass.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.compass.widget.CompassView;
import example.com.sedekahonline.feature.compass.widget.CompassView2;
import example.com.sedekahonline.feature.schedule.activity.KotaActivity;
import example.com.sedekahonline.helper.GpsHelper;
import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.JadwalShalat;
import example.com.sedekahonline.remote.ApiUtils;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompassActivity extends AppCompatActivity {

    private static final String TAG = "CompassActivity";
    private CompassView2 compass;
    Location location;
    private Toolbar mToolBar;
    private GpsHelper gps;
    double latitude=0,longitude=0;
    private boolean isGPSEnabled;
    private LocationManager locationManager;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_green));

        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Arah kiblat");
        compass = new CompassView2(this);
        compass.arrowView = (ImageView) findViewById(R.id.main_image_hands);
        compass.context = this;
        compass.activity = this;
        compass.textView = (TextView) findViewById(R.id.tvMessage);
//        getLocation();

//        location = new Location("current");
//        mKaabaLocation = new Location("kaaba");
//        mKaabaLocation.setLatitude(21.422487);
//        mKaabaLocation.setLongitude(39.826206);
//        mCompassView = (CompassView) findViewById(R.id.compass);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////        Log.e("latitude", location.getLatitude()+" 12");
////        Log.e("longitude", location.getLongitude()+" 12");
//        Toast.makeText(this, latitude+","+longitude, Toast.LENGTH_SHORT).show();
//        location.setLatitude(latitude);
//        location.setLongitude(longitude);
//
//        float azimuth = location.bearingTo(mKaabaLocation);
//        azimuth = azimuth < 0 ? azimuth + 360 : azimuth;
//        mCompassView.setAzimuth(azimuth);

    }
    public void getLocation() {
        gps = new GpsHelper(this, this);

        if (gps.canGetLocation()) {
            Log.e("can", "can");
             latitude = gps.getLatitude();
             longitude = gps.getLongitude();
             compass.location = new Location("currentLocation");
            compass.location.setLatitude(latitude);
            compass.location.setLatitude(longitude);

        } else {
            gps.showSettingsAlert();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }
}
