package example.com.sedekahonline.feature.alquran.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Locale;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.alquran.database.DatabaseHelper;
import example.com.sedekahonline.feature.alquran.database.datasource.SurahDataSource;
import example.com.sedekahonline.feature.alquran.fragment.SurahFragment;
import example.com.sedekahonline.feature.alquran.model.Surah;
import example.com.sedekahonline.feature.alquran.settings.Config;

public class AlquranActivity extends AppCompatActivity {

    static String lang;
    SharedPreferences FirstRunPrefs = null;
    SharedPreferences dbVersionPrefs = null;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alquran);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        FirstRunPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        dbVersionPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if ((dbVersionPrefs.getInt(Config.DATABASE_VERSION, 0) == DatabaseHelper.DATABASE_VERSION)) {

            lang = sharedPreferences.getString(Config.LANG, Config.defaultLang);

            setLocaleIndo();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, SurahFragment.newInstance())
                    .commit();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DatabaseHelper.DATABASE_VERSION > dbVersionPrefs.getInt(Config.DATABASE_VERSION, 0)) {
            Log.d("MyActivity onResume()", "First Run or dbUpgrade");
            {

                new AsyncInsertData().execute();

            }
        }//checking sharedPrefs finished

    }


    public void setLocaleIndo() {
        Locale locale = new Locale(Config.LANG_INDO);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getApplicationContext().getResources().getDisplayMetrics());

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

    private class AsyncInsertData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(AlquranActivity.this);
            progressDialog.setMessage("Tunggu Sebentar. . .");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("onInBackground()", "Data Inserting ");
            SurahDataSource surahDataSource = new SurahDataSource(AlquranActivity.this);
            ArrayList<Surah> surahArrayList = surahDataSource.getEnglishSurahArrayList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("AlquranActivity", "Data Inserted ");
            //then set 'firstrun' as false, using the following line to edit/commit prefs and set dbversion

            dbVersionPrefs.edit().putInt(Config.DATABASE_VERSION, DatabaseHelper.DATABASE_VERSION).apply();
            progressDialog.dismiss();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, SurahFragment.newInstance())
                    .commit();

        }

    }
}
