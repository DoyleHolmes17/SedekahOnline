package example.com.sedekahonline.feature.compass.widget;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import example.com.sedekahonline.helper.GpsHelper;

public class CompassView2 implements SensorEventListener {
    private static final String TAG = "Compass";
    // compass arrow to rotate
    public ImageView arrowView = null;
    public TextView textView = null;
    public Location location = null;
    private Location mKaabaLocation;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    public Context context=null;
    public Activity activity=null;
    private float currentAzimuth = 0;
    private float kaabahAzimuth = 0;

    private GpsHelper gps;
    double latitude=0,longitude=0;

    public CompassView2(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



    }

    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        getLocation();
        mKaabaLocation = new Location("Mecca");
        mKaabaLocation.setLatitude(21.422487);
        mKaabaLocation.setLongitude(39.826206);
        float azimuth = location.bearingTo(mKaabaLocation);
        kaabahAzimuth = azimuth < 0 ? azimuth + 360 : azimuth;
        Log.e(TAG, "location.getLatitude(): " + location.getLatitude());
        Log.e(TAG, "location.getLongitude(): " + location.getLongitude());
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

//        Log.i(TAG, "will set rotation from " + currentAzimuth + " to "
//                + azimuth);
//        Log.e("azimuth",currentAzimuth+"");
        currentAzimuth= currentAzimuth-kaabahAzimuth;
//        Log.e("currentAzimuth",currentAzimuth+"");
//        Log.e("azimuth",azimuth+"");
//        Log.e(TAG, "kaabahAzimuth: " + kaabahAzimuth);
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        if (Math.round(currentAzimuth + 10) >= Math.round(kaabahAzimuth) && Math.round(currentAzimuth - 10) <= Math.round(kaabahAzimuth)) {
            textView.setText("Device mengarah ke kiblat");
        } else if (Math.round(currentAzimuth + 11) > Math.round(kaabahAzimuth) || Math.round(currentAzimuth) <= Math.round(kaabahAzimuth)/2){
            textView.setText("Geser device ke kiri, agar mengarah ke kiblat");
        } else if (Math.round(currentAzimuth - 11) < Math.round(kaabahAzimuth) || Math.round(currentAzimuth) >= Math.round(kaabahAzimuth)/2){
            textView.setText("Geser device ke kanan, agar mengarah ke kiblat");
        }
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                // Log.d(TAG, "azimuth (deg): " + azimuth);
                adjustArrow();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void getLocation() {
        gps = new GpsHelper(context, activity);

        if (gps.canGetLocation()) {
//            Log.e("can", "can");
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
           location = new Location("currentLocation");
            location.setLatitude(latitude);
           location.setLongitude(longitude);

        } else {
            gps.showSettingsAlert();
        }

    }
}
