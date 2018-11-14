package example.com.sedekahonline.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;

import example.com.sedekahonline.R;
import example.com.sedekahonline.helper.DatabaseHandler;
import example.com.sedekahonline.model.DataUser;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Boolean sudahInput = prefs.getBoolean("SudahInputData", false);
        if (!sudahInput) {
            new DatabaseHandler(this).tambahData();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("SudahInputData", true);
            editor.apply();
        }


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
               DataUser userData = new Gson().fromJson(preferences.getString("dataUser", null), DataUser.class);
                if (userData != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);


    }
}
