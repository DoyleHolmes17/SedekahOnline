package example.com.sedekahonline.feature;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.alquran.activity.AlquranActivity;
import example.com.sedekahonline.feature.compass.activity.CompassActivity;
import example.com.sedekahonline.feature.map.MapsActivity;
import example.com.sedekahonline.feature.schedule.activity.ScheduleActivity;
import example.com.sedekahonline.model.DataUser;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private LinearLayout llProfile, llKiblat, llJadwal, llAlquran, llSedekah, llGift, llAbout;
    private Intent intent;
    private ImageView ivImage;
    private String from;
    private Boolean asGuest;
    private Toolbar mToolBar;
    private TextView tvNama, tvEmail, tvNoHp, tvAddress;
    private SharedPreferences preferences;
    private DataUser dataUser;
    private CardView cvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Spot Sedekah");
        Gson gson = new Gson();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        dataUser = gson.fromJson(preferences.getString("dataUser", null), DataUser.class);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));
        }

        intent = getIntent();
        asGuest = preferences.getBoolean("asGuest", false);

        Log.e("asGuest", asGuest.toString());
        cvProfile = (CardView) findViewById(R.id.cvProfile);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        llProfile = (LinearLayout) findViewById(R.id.llProfile);
        llJadwal = (LinearLayout) findViewById(R.id.llJadwal);
        llKiblat = (LinearLayout) findViewById(R.id.llKiblat);
        llAlquran = (LinearLayout) findViewById(R.id.llAlquran);
        llSedekah = (LinearLayout) findViewById(R.id.llSedekah);
        llGift = (LinearLayout) findViewById(R.id.llGift);
        llAbout = (LinearLayout) findViewById(R.id.llAbout);

        tvNama = (TextView) findViewById(R.id.tvNama);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvNoHp = (TextView) findViewById(R.id.tvNoHp);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        llProfile.setVisibility(View.GONE);
        cvProfile.setVisibility(View.GONE);
        if (!asGuest && dataUser != null) {
//            Log.e("dataUser",dataUser.getNama());
            cvProfile.setVisibility(View.VISIBLE);
            llProfile.setVisibility(View.VISIBLE);

            tvNama.setText(dataUser.getNama());
            tvEmail.setText(dataUser.getEmail());
            tvNoHp.setText(dataUser.getTelepon());
            tvAddress.setText(dataUser.getAlamat());
            if (!dataUser.getGambar().equalsIgnoreCase("")) {
                Picasso.with(MainActivity.this).load(dataUser.getGambar()).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivImage);
            } else {
                ivImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
            }
        }

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        llJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    startActivity(intent);
                }
            }
        });

        llKiblat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                    startActivity(intent);
                }
            }
        });
        llAlquran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    Intent intent = new Intent(MainActivity.this, AlquranActivity.class);
                    startActivity(intent);
                }
            }
        });
        llSedekah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (asGuest) {
//                    setError("Harus masuk untuk bisa mengakses menu ini");
//          } else {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
//                }
            }
        });
        llGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                setError("Under Construction");
            }
        });
        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setError("Under Construction");
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (asGuest) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
                finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (asGuest) {
            menu = null;
        } else {
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {

            case R.id.menuLogout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("dataUser", null);
                editor.apply();
                Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent3);
                finish();
                break;
        }
        return true;
    }

    public void setError(String message) {
        new DroidDialog.Builder(MainActivity.this)
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
}
