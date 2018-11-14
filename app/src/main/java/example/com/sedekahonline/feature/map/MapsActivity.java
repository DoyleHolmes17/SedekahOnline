package example.com.sedekahonline.feature.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.sedekah.SedekahActivity;
import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.DataSedekah;
import example.com.sedekahonline.model.Sedekah;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ArrayList<LatLng> MarkerPoints;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private LinearLayout llAdd, llSearch, llRange;
    private Boolean isUp;
    private View cvMenu2, cvMenu;
    private Toolbar mToolBar;
    private LatLng latLng;
    private Polyline polylineFinal;
    private List<DataSedekah> sedekahList = new ArrayList<>();
    private GoogleMap mMap;
    //    private DatabaseHandler db;
    private String locationData, nama, nohp;
    private ProgressDialog pd;
    private SeekBar seekBar;
    private SharedPreferences preferences;
    private Boolean asGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        isUp = false;

        pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        asGuest = preferences.getBoolean("asGuest", false);
        llAdd = (LinearLayout) findViewById(R.id.llAdd);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        llRange = (LinearLayout) findViewById(R.id.llRange);
        cvMenu = findViewById(R.id.cvMenu);
        cvMenu2 = findViewById(R.id.cvMenu2);
        locationData = getIntent().getStringExtra("location");
        nama = getIntent().getStringExtra("nama");
        nohp = getIntent().getStringExtra("nohp");
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Spot Sedekah");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }

        cvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSlideViewButtonClick();
            }
        });
        cvMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSlideViewButtonClick();
            }
        });
        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    Intent intent = new Intent(MapsActivity.this, MapAddActivity.class);
                    startActivity(intent);
                }
            }
        });
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SedekahActivity.class);
                startActivity(intent);
            }
        });

        llRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                dialogBuilder.setCancelable(false);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_range, null);
                dialogBuilder.setView(dialogView);

                seekBar = (SeekBar) dialogView.findViewById(R.id.seekBar);
                TextView mToolBar = (TextView) dialogView.findViewById(R.id.toolbar);
                mToolBar.setText("Setting jarak pencarian");
                final TextView tvValue = (TextView) dialogView.findViewById(R.id.tvValue);
                seekBar.setProgress(Integer.parseInt(preferences.getString("rangeSedekah", "5")));
                seekBar.getThumb().setColorFilter(getResources().getColor(R.color.light_green), PorterDuff.Mode.MULTIPLY);
                tvValue.setText(seekBar.getProgress() + " km");
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (progress < 5) {
                            seekBar.setProgress(5);
                        }
                        tvValue.setText(seekBar.getProgress() + " km");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("rangeSedekah", tvValue.getText().toString().split(" ")[0]);
                        editor.apply();
                        getData(tvValue.getText().toString().split(" ")[0]);
                        alertDialog.dismiss();

                    }
                });
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (locationData != null) {
            cvMenu.setVisibility(View.GONE);
            cvMenu2.setVisibility(View.GONE);
            double targetLat = Double.parseDouble(locationData.split(",")[0]);
            double targetLng = Double.parseDouble(locationData.split(",")[1]);
            double lat = 0;
            double lng = 0;
            if (latLng != null) {
                lat = latLng.latitude;
                lng = latLng.longitude;
            }
            Log.e("lat", targetLat + "");
            Log.e("lng", targetLng + "");
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(targetLat, targetLng)).title(nama).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            LatLng currentLatLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title("Current Position");
            markerOptions.icon(getMarkerIcon("#00B764"));

            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera


            if (polylineFinal != null) {
                polylineFinal.remove();
            }

            LatLng positionTarget = new LatLng(targetLat, targetLng);
            LatLng position = null;
            if (mCurrLocationMarker != null) {
                position = mCurrLocationMarker.getPosition();
            }
            String url = getUrl(position, marker.getPosition());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 9));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionTarget, 15));
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
//        db = new DatabaseHandler(this);
//        sedekahList = (ArrayList<Sedekah>) db.daftarSedekah();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (latLng != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(getMarkerIcon("#00B764"));

                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                }
                //move map camera

                marker.showInfoWindow();
                if (polylineFinal != null) {
                    polylineFinal.remove();
                }
                LatLng position = null;
                if (mCurrLocationMarker != null) {
                    position = mCurrLocationMarker.getPosition();
                    Log.e("mCurrLocationMarker", position + " ");
                }
                String url = getUrl(position, marker.getPosition());
                Log.d("onMapClick", url.toString());
                FetchUrl FetchUrl = new FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                return true;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = null;
                try {

                    // Getting view from the layout file info_window_layout
                    v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                    // Getting reference to the TextView to set latitude
                    TextView nameTxt = (TextView) v.findViewById(R.id.nameTxt);
                    TextView mobileTxt = (TextView) v.findViewById(R.id.mobileTxt);
                    TextView alamatTxt = (TextView) v.findViewById(R.id.alamatTxt);
                    if (nama == null) {
                        for (int i = 0; sedekahList.size() > i; i++) {
                            if (sedekahList.get(i).getNama_lengkap().equals(arg0.getTitle())) {
                                nameTxt.setText(sedekahList.get(i).getNama_lengkap());
                                mobileTxt.setText(sedekahList.get(i).getNoHp());
                                alamatTxt.setText(Sedekah.getAddressFromLatLong(Double.parseDouble(sedekahList.get(i).getLatitude().split(",")[0]), Double.parseDouble(sedekahList.get(i).getLongitude().split(",")[1]), MapsActivity.this));
                            }
                        }
                    } else {
                        nameTxt.setText(nama);
                        mobileTxt.setText(nohp);
                        alamatTxt.setText(Sedekah.getAddressFromLatLong(Double.parseDouble(locationData.split(",")[0]), Double.parseDouble(locationData.split(",")[1]), MapsActivity.this));

                    }

                } catch (Exception ev) {
                    System.out.print(ev.getMessage());
                }

                return v;
            }
        });

    }

    private String getUrl(LatLng origin, LatLng dest) {
        if (origin != null && dest != null) {
            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
            return url;
        }
        return null;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (locationData == null) {
                getData(preferences.getString("rangeSedekah", "5"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void checkLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                            if (mMap != null) {
                                mMap.setMyLocationEnabled(true);
                            }
                        }
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    // method definition
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(250);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);

    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(250);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public void onSlideViewButtonClick() {
        Log.e("isUp", isUp + "");
        slideMenu();

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

    public void slideMenu() {
        if (isUp) {

            slideDown(cvMenu2);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cvMenu.setVisibility(View.GONE);
                    cvMenu2.setVisibility(View.GONE);
                    slideUp(cvMenu);
                }
            }, 250);

            cvMenu.clearAnimation();
            cvMenu2.clearAnimation();
        } else if (!isUp) {

            slideDown(cvMenu);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cvMenu.setVisibility(View.GONE);
                    cvMenu2.setVisibility(View.GONE);
                    slideUp(cvMenu2);
                }
            }, 250);

            cvMenu.clearAnimation();
            cvMenu2.clearAnimation();

        }
        isUp = !isUp;
    }

    public void getData(final String jarak) {
        SOService mService = RetrofitClient.createRequest(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "listnearestalms");
        jsonObject.addProperty("latitude", latLng.latitude + "");
        jsonObject.addProperty("longitude", latLng.longitude + "");
        jsonObject.addProperty("distance", jarak + "");

//            jsonObject.addProperty("page_size", "5");

        mService.daftarSedekah(jsonObject).enqueue(new Callback<BaseResponseV1<DataSedekah>>() {
            @Override
            public void onResponse(Call<BaseResponseV1<DataSedekah>> call, Response<BaseResponseV1<DataSedekah>> response) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (response.isSuccessful()) {

                    Log.e("status", response.body().getStatus());
                    Log.e("message", response.body().getMessage() + " ");
                    mMap.clear();
                    sedekahList.clear();
                    if (response.body().getStatus().equalsIgnoreCase("000")) {

                        sedekahList.addAll(response.body().getData());

                        for (int i = 0; sedekahList.size() > i; i++) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(sedekahList.get(i).getLatitude()), Double.parseDouble(sedekahList.get(i).getLongitude()))).title(sedekahList.get(i).getNama_lengkap()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                    }
                    int zoom = 0;
                    if (Integer.parseInt(jarak) < 11) {
                        zoom = 12;
                    } else {
                        zoom = 11;
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
                    slideMenu();
                } else {
                    Toast.makeText(MapsActivity.this, "Response Fail", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<BaseResponseV1<DataSedekah>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(MapsActivity.this, "Response Fail", Toast.LENGTH_SHORT).show();
                Log.e("errorr", t.getMessage() + "");
            }
        });
    }

    public void setError(String message) {
        new DroidDialog.Builder(MapsActivity.this)
                .icon(R.drawable.ic_action_close)
                .title("")
                .content(message)
                .cancelable(true, true)
                .neutralButton("OK", new DroidDialog.onNeutralListener() {
                    @Override
                    public void onNeutral(Dialog droidDialog) {
                        droidDialog.dismiss();
                    }
                }).color(ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.red))
                .show();
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route

            if (lineOptions != null) {
                polylineFinal = mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}