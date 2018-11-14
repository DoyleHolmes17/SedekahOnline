package example.com.sedekahonline.feature.schedule.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import example.com.sedekahonline.R;
import example.com.sedekahonline.helper.GpsHelper;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.JadwalShalat;
import example.com.sedekahonline.model.JadwalShalatSwitch;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {
    Gson gson = new Gson();
    String stop, name;
    private Date d;
    private int id_alarm = 0;
    private PendingIntent pendingIntent;
    private long startDate;
    private TextView tvLokasi, tvAlarmSound;
    private String city, alarmSound;
    private Intent myIntent;
    private AlarmManager am;
    private ProgressDialog pd;
    private LocationManager locationManager;
    private GpsHelper gps;
    private JadwalShalat jadwalSalat;
    private JadwalShalatSwitch jadwalSalatSwitch;
    private TextView tvImsya, tvSubuh, tvDzuhur, tvAshar, tvMaghrib, tvIsya;
    private RecyclerView rvKota;
    private SOService mService;
    private Toolbar mToolBar;
    private LinearLayout llLokasi, llAlarm;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String tanggal, bulan, tahun, jadwalImsya, jadwalSubuh, jadwalDhuhur, jadwalAshr, jadwalMaghrib, jadwalIsya;
    private SwitchCompat switchImsya, switchSubuh, switchDzuhur, switchAshar, switchMaghrib, switchIsya;
    private Location mLastLocation;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Jadwal Salat");
        pd = new ProgressDialog(ScheduleActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        editor = prefs.edit();

        getLocation();

        llAlarm = (LinearLayout) findViewById(R.id.llAlarm);
        llLokasi = (LinearLayout) findViewById(R.id.llLokasi);

        tvLokasi = (TextView) findViewById(R.id.tvLokasi);
        tvAlarmSound = (TextView) findViewById(R.id.tvAlarmSound);

        myIntent = new Intent(ScheduleActivity.this, AlarmReceiver.class);
//        Intent serviceIntent = new Intent(ScheduleActivity.this, RingtonePlay.class);
//        this.stopService(serviceIntent);

        switchImsya = (SwitchCompat) findViewById(R.id.switchImsya);
        switchSubuh = (SwitchCompat) findViewById(R.id.switchSubuh);
        switchDzuhur = (SwitchCompat) findViewById(R.id.switchDzuhur);
        switchAshar = (SwitchCompat) findViewById(R.id.switchAshar);
        switchMaghrib = (SwitchCompat) findViewById(R.id.switchMaghrib);
        switchIsya = (SwitchCompat) findViewById(R.id.switchIsya);

        tvImsya = (TextView) findViewById(R.id.tvImsya);
        tvSubuh = (TextView) findViewById(R.id.tvSubuh);
        tvDzuhur = (TextView) findViewById(R.id.tvDzuhur);
        tvAshar = (TextView) findViewById(R.id.tvAshar);
        tvMaghrib = (TextView) findViewById(R.id.tvMaghrib);
        tvIsya = (TextView) findViewById(R.id.tvIsya);


        llLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ScheduleActivity.this, KotaActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        llAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ScheduleActivity.this);
//                Toast.makeText(ScheduleActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String[] tanggalan = date.split("-");

        tanggal = tanggalan[0];
        bulan = tanggalan[1];
        tahun = tanggalan[2];


//        Toast.makeText(this, "Alarm belum bisa bekerja", Toast.LENGTH_SHORT).show();
    }

    public void getData(String city) {
//        Log.e("city", city + "");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "getprayerschedule");
        jsonObject.addProperty("city_name", city + "");
        mService = RetrofitClient.createRequest(this);
        mService.dataJadwal(jsonObject).enqueue(new Callback<BaseResponseV2<JadwalShalat>>() {
            @Override
            public void onResponse(Call<BaseResponseV2<JadwalShalat>> call, Response<BaseResponseV2<JadwalShalat>> response) {
                if (response.isSuccessful()) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
//                    Log.e("status", response.body().getStatus());
//                    Log.e("message", response.body().getMessage() + "");
                    if (response.body().getStatus().equalsIgnoreCase("000")) {
                        jadwalSalat = response.body().getData();

                        Gson gson = new Gson();
                        String data = gson.toJson(jadwalSalat);
                        editor.putString("jadwalSalat", data + "");
                        editor.apply();
                        setTime();
                    } else {
                        new DroidDialog.Builder(ScheduleActivity.this)
                                .icon(R.drawable.ic_action_close)
                                .title("")
                                .content("Kami tidak bisa mengatur jadwal salat berdasar lokasi Anda! Kami akan setting pada kota Jogja")
                                .cancelable(true, true)
                                .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                    @Override
                                    public void onNeutral(Dialog droidDialog) {
                                        droidDialog.dismiss();
                                        getData("jogja");
                                        editor.putString("city", "jogja");
                                        editor.apply();
                                        tvLokasi.setText("jogja");
                                    }
                                }).color(ContextCompat.getColor(ScheduleActivity.this, R.color.red), ContextCompat.getColor(ScheduleActivity.this, R.color.white),
                                ContextCompat.getColor(ScheduleActivity.this, R.color.red))
                                .show();
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponseV2<JadwalShalat>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                setError("Terjadi masalah koneksi");
//                Toast.makeText(ScheduleActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                Log.e("error", t.getMessage() + "");
            }
        });
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

    public void setAlarmTime(String time, int id_alarm, String name) {
        long timeInMs = 0;
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        String myTime = String.valueOf(hour) + ":" + String.valueOf(minute);

        Date date = null;
        Date customDate;
        // today at your defined time Calendar
        Calendar customCalendar = new GregorianCalendar();
        // set hours and minutes
        customCalendar.set(Calendar.HOUR_OF_DAY, hour);
        customCalendar.set(Calendar.MINUTE, minute);
        customCalendar.set(Calendar.SECOND, 0);
        customCalendar.set(Calendar.MILLISECOND, 0);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {

            date = sdf.parse(myTime);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        customDate = customCalendar.getTime();
        if (customDate != null) {
            timeInMs = customDate.getTime();
        }
        if (System.currentTimeMillis() > timeInMs) {
            customCalendar.add(Calendar.DATE, 1);
        }
        customDate = customCalendar.getTime();
        timeInMs = customDate.getTime();

        if (stop != null) {
            customDate = customCalendar.getTime();
        }

//        Log.e("customDate",customDate+"");;
        Date c = Calendar.getInstance().getTime();
        myIntent.putExtra("extra", "alarm on");
        myIntent.putExtra("name", name);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                timeInMs, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(ScheduleActivity.this, id_alarm, myIntent, pendingIntent.FLAG_UPDATE_CURRENT));
        //        sendBroadcast(myIntent);
    }

    public void setTime() {

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        jadwalSalat = new Gson().fromJson(prefs.getString("jadwalSalat", null), JadwalShalat.class);
        if (jadwalSalat != null && jadwalSalat.getFajr() != null) {

            jadwalImsya = jadwalSalat.getFajr();
            jadwalSubuh = jadwalSalat.getFajr();
            jadwalDhuhur = jadwalSalat.getDhuhr();
            jadwalAshr = jadwalSalat.getAsr();
            jadwalMaghrib = jadwalSalat.getMaghrib();
            jadwalIsya = jadwalSalat.getIsha();

            try {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                Date d = df.parse(jadwalImsya);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, -10);
                jadwalImsya = df.format(cal.getTime());
            } catch (ParseException e) {
                e.getMessage();
            }

            tvImsya.setText(jadwalImsya);
            tvSubuh.setText(jadwalSubuh);
            tvDzuhur.setText(jadwalDhuhur);
            tvAshar.setText(jadwalAshr);
            tvMaghrib.setText(jadwalMaghrib);
            tvIsya.setText(jadwalIsya);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jadwalSalatSwitch = gson.fromJson(prefs.getString("jadwalSalatSwitch", null), JadwalShalatSwitch.class);
        city = prefs.getString("city", null);
        alarmSound = prefs.getString("alarmSound", "adzan");
        if (city != null) {
            tvLokasi.setText(city);
        }
        if (alarmSound != null) {
            tvAlarmSound.setText(alarmSound);
        }
        setTime();

        if (jadwalSalatSwitch != null) {
            switchImsya.setChecked(jadwalSalatSwitch.isImsya());
            switchSubuh.setChecked(jadwalSalatSwitch.isFajr());
            switchDzuhur.setChecked(jadwalSalatSwitch.isDhuhr());
            switchAshar.setChecked(jadwalSalatSwitch.isAsr());
            switchMaghrib.setChecked(jadwalSalatSwitch.isMaghrib());
            switchIsya.setChecked(jadwalSalatSwitch.isIsha());
        }
        onCheckSwitchCompact(switchImsya, tvImsya.getText().toString(), "imsya", 0);
        onCheckSwitchCompact(switchSubuh, tvSubuh.getText().toString(), "fajr", 1);
        onCheckSwitchCompact(switchDzuhur, tvDzuhur.getText().toString(), "dhuhr", 2);
        onCheckSwitchCompact(switchAshar, tvAshar.getText().toString(), "asr", 3);
        onCheckSwitchCompact(switchMaghrib, tvMaghrib.getText().toString(), "maghrib", 4);
        onCheckSwitchCompact(switchIsya, tvIsya.getText().toString(), "isha", 5);

        stop = getIntent().getStringExtra("stop");
        name = getIntent().getStringExtra("name");
        if (stop != null) {
            setTimeAgain(stop + "-" + name);
        }
    }

    public void setTimeAgain(String message) {
        setTime();
        String stop = message.split("-")[0];
        String name = message.split("-")[1];

        if (stop != null) {
            int req = 0;
            String time;
            myIntent.putExtra("extra", "alarm off");
            myIntent.putExtra("name", name);
            if (name.equalsIgnoreCase("imsya")) {
                req = 0;
                time = jadwalImsya;
            } else if (name.equalsIgnoreCase("fajr")) {
                req = 1;
                time = jadwalSubuh;
            } else if (name.equalsIgnoreCase("dhuhr")) {
                req = 2;
                time = jadwalDhuhur;
            } else if (name.equalsIgnoreCase("asr")) {
                req = 3;
                time = jadwalAshr;
            } else if (name.equalsIgnoreCase("maghrib")) {
                req = 4;
                time = jadwalMaghrib;
            } else if (name.equalsIgnoreCase("isha")) {
                req = 5;
                time = jadwalIsya;
            } else {
                req = 6;
                time = "00:00";
            }
            Log.e("message", message + " 123");
            Log.e("name", name + " 123");
            Log.e("req", req + " 123");
            Log.e("time", time + " 123");
            am.cancel(PendingIntent.getBroadcast(ScheduleActivity.this, req, myIntent, pendingIntent.FLAG_UPDATE_CURRENT));
            sendBroadcast(myIntent);
            setAlarmTime(time, req, name);
        }
    }

    public void getLocation() {
        gps = new GpsHelper(ScheduleActivity.this, ScheduleActivity.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String location = "";
            String city = "";
            Geocoder geocoder = new Geocoder(ScheduleActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                city = addresses.get(0).getLocality();
                String subAdminArea = addresses.get(0).getSubAdminArea();
                String adminArea = addresses.get(0).getAdminArea();
                location = city + ", " + subAdminArea + ", " + adminArea;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            if (this.city != city) {
                getData(city);
                editor.putString("city", city + "");
                editor.apply();
            }
        } else {
            gps.showSettingsAlert();
        }
    }

    public void onCheckSwitchCompact(SwitchCompat switchCompat, final String time, final String text, final int idAlarm) {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (jadwalSalatSwitch == null) {
                    jadwalSalatSwitch = new JadwalShalatSwitch();
                }
                if (isChecked) {

                    if (text.equalsIgnoreCase("imsya")) {
                        setAlarmTime(time, idAlarm, "imsya");
                    } else if (text.equalsIgnoreCase("fajr")) {
                        setAlarmTime(time, idAlarm, "fajr");
                    } else if (text.equalsIgnoreCase("dhuhr")) {
                        setAlarmTime(time, idAlarm, "dhuhr");
                    } else if (text.equalsIgnoreCase("asr")) {
                        setAlarmTime(time, idAlarm, "asr");
                    } else if (text.equalsIgnoreCase("maghrib")) {
                        setAlarmTime(time, idAlarm, "maghrib");
                    } else if (text.equalsIgnoreCase("isha")) {
                        setAlarmTime(time, idAlarm, "isha");
                    } else {
                        setAlarmTime(time, idAlarm, "other");
                    }

//                    Toast.makeText(ScheduleActivity.this, "alarm on", Toast.LENGTH_SHORT).show();

                    if (text.equalsIgnoreCase("imsya")) {
                        jadwalSalatSwitch.setImsya(true);
                    } else if (text.equalsIgnoreCase("fajr")) {
                        jadwalSalatSwitch.setFajr(true);
                    } else if (text.equalsIgnoreCase("dhuhr")) {
                        jadwalSalatSwitch.setDhuhr(true);
                    } else if (text.equalsIgnoreCase("asr")) {
                        jadwalSalatSwitch.setAsr(true);
                    } else if (text.equalsIgnoreCase("maghrib")) {
                        jadwalSalatSwitch.setMaghrib(true);
                    } else if (text.equalsIgnoreCase("isha")) {
                        jadwalSalatSwitch.setIsha(true);
                    }


                } else {
                    myIntent.putExtra("extra", "alarm off");
                    am.cancel(PendingIntent.getBroadcast(ScheduleActivity.this, idAlarm, myIntent, pendingIntent.FLAG_UPDATE_CURRENT));

                    // stopping the ringtone
                    sendBroadcast(myIntent);
                    if (text.equalsIgnoreCase("imsya")) {
                        jadwalSalatSwitch.setImsya(false);
                    } else if (text.equalsIgnoreCase("fajr")) {
                        jadwalSalatSwitch.setFajr(false);
                    } else if (text.equalsIgnoreCase("dhuhr")) {
                        jadwalSalatSwitch.setDhuhr(false);
                    } else if (text.equalsIgnoreCase("asr")) {
                        jadwalSalatSwitch.setAsr(false);
                    } else if (text.equalsIgnoreCase("maghrib")) {
                        jadwalSalatSwitch.setMaghrib(false);
                    } else if (text.equalsIgnoreCase("isha")) {
                        jadwalSalatSwitch.setIsha(false);
                    }
                }

                String data = new Gson().toJson(jadwalSalatSwitch);
                editor.putString("jadwalSalatSwitch", data + "");
                editor.apply();
            }

        });
    }

    public void setError(String message) {
        new DroidDialog.Builder(ScheduleActivity.this)
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

    public void showDialog(final Activity activity) {
        final RadioGroup radioGroup;

        TextView tvSave;

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ScheduleActivity.this);
        final View mView = layoutInflaterAndroid.inflate(R.layout.alarm_setting, null);
        if (alarmSound != null) {
            RadioButton radioButton;
            int i;
            if (alarmSound.equalsIgnoreCase("Adzan")) {
                i = R.id.rbAdzan;
            } else if (alarmSound.equalsIgnoreCase("Bedug")) {
                i = R.id.rbBedug;
            } else {
                i = R.id.rbNone;
            }
            radioButton = (RadioButton) mView.findViewById(i);
            radioButton.setChecked(true);
        }

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ScheduleActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
        radioGroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        tvSave = (TextView) mView.findViewById(R.id.tvSave);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) mView.findViewById(selectedId);
//                Toast.makeText(activity, radioButton.getText() + "", Toast.LENGTH_SHORT).show();
                editor.putString("alarmSound", radioButton.getText() + "");
                editor.apply();
                tvAlarmSound.setText(radioButton.getText());
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String data = intent.getAction();
        setTimeAgain(data);
    }
}
