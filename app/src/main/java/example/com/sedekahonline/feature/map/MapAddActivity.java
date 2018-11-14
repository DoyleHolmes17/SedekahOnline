package example.com.sedekahonline.feature.map;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.AddSedekahActivity;
import example.com.sedekahonline.helper.AndroidPermissions;
import example.com.sedekahonline.model.Sedekah;

/**
 * Created by DOCOTEL on 1/26/2018.
 */

public class MapAddActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, NewGPSTracker.UpdateLocationListener {
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private TextView tvLocation;
    private Button btnAdd;
    private String locationLatLng;
    private String location;
    private Toolbar mToolBar;
    private ProgressDialog pd;
    private ImageView ivMarker;
    private GoogleMap googleMap;
    private LatLng latLng;
    private int interval = 1000;
    private boolean firstTime = true;
    private Context mContext;
    private String TAG = "MapAddActivity";
    private Bundle mBundle;
    private NewGPSTracker gpsTracker;
    private LatLng addressLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));
        }


        pd = new ProgressDialog(MapAddActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        mBundle = savedInstanceState;
        // check if GPS enabled
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        locationLatLng = getIntent().getStringExtra("locationLatLng");
        btnAdd = (Button) findViewById(R.id.btnAdd);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Pilih Lokasi");
        gpsTracker = new NewGPSTracker(getApplicationContext());
        gpsTracker.setLocationListener(this);
        init();

    }

    private void init() {
        location = getIntent().getStringExtra("location");
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (!AndroidPermissions.getInstance().checkLocationPermission(MapAddActivity.this)) {
                AndroidPermissions.getInstance().displayLocationPermissionAlert(MapAddActivity.this);
            } else {
                setUpMap();
            }
        } else {
            setUpMap();
        }
        mContext = getApplicationContext();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = tvLocation.getText().toString();

                Intent intent = new Intent(MapAddActivity.this, AddSedekahActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("locationLatLng", locationLatLng);
                startActivity(intent);
                finish();

            }
        });
    }

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            ActivityCompat.requestPermissions(_a, PERMISSIONS_LOCATION, PERMISSION_REQUEST_CODE_LOCATION);
            Toast.makeText(MapAddActivity.this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;

        }
    }

    private void setUpMap() {

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ivMarker = (ImageView) findViewById(R.id.map_custom_marker);

        supportMapFragment.getMapAsync(this);


    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null && firstTime) {
            if (location.getLatitude() != 17.3700) {
                firstTime = false;
                googleMap.clear();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                addressLatLng = latLng;

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                if (locationLatLng != null) {
                    latLng = new LatLng(Double.parseDouble(locationLatLng.split(",")[0]), Double.parseDouble(locationLatLng.split(",")[1]));
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(),
                    "This device is not supported Google Play Service", Toast.LENGTH_LONG)
                    .show();
            finish();
            return false;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AndroidPermissions.REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                    updateLocation();
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), MapAddActivity.this);
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        gpsTracker.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsTracker.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    private void updateLocation() {
        if (gpsTracker != null) {
            gpsTracker.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (!AndroidPermissions.getInstance().checkLocationPermission(MapAddActivity.this)) {
                    AndroidPermissions.getInstance().displayLocationPermissionAlert(MapAddActivity.this);
                }
            }
            gpsTracker.startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getApplicationContext(), this)) {
            googleMap.setMyLocationEnabled(true);
        } else {
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), MapAddActivity.this);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                if()
            }
        });
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (addressLatLng != null) {
                    try {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        tvLocation.setText(Sedekah.getAddressFromLatLong(addressLatLng.latitude, addressLatLng.longitude, MapAddActivity.this));
                    } catch (IndexOutOfBoundsException e) {
                        tvLocation.setText("Tunggu Sebentar. . .");
                    }
                }
            }
        });
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                tvLocation.setText("Tunggu Sebentar. . .");
                if (cameraPosition != null) {
                    googleMap.clear();
                    if (cameraPosition.target.latitude != 0 && cameraPosition.target.longitude != 0) {
                        locationLatLng = cameraPosition.target.latitude + "," + cameraPosition.target.longitude;
                        addressLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (location != null) {
            Intent intent = new Intent(MapAddActivity.this, AddSedekahActivity.class);
            intent.putExtra("location", location);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (location != null) {
                    Intent intent = new Intent(MapAddActivity.this, AddSedekahActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}