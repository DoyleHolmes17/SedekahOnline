package example.com.sedekahonline.feature.alquran.activity;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.alquran.database.datasource.SurahDataSource;
import example.com.sedekahonline.feature.alquran.fragment.AyahWordFragment;
import example.com.sedekahonline.feature.alquran.settings.Config;

public class AyahWordActivity extends AppCompatActivity {

    final private static int SCREEN_TIMEOUT = 600;
    static public String surahName;
    //to keep screen on stuff
    static boolean keepScreenOn;
    final private Handler mHandler = new Handler();
    //to keep screen on stuff
    private Runnable clearScreenOn = new Runnable() {
        @Override
        public void run() {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayah);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = this.getIntent().getExtras();
        surahName = bundle.getString(SurahDataSource.SURAH_NAME_TRANSLATE);
        getSupportActionBar().setTitle(surahName);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, AyahWordFragment.newInstance(bundle))
                    .commit();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        keepScreenOn = sharedPreferences.getBoolean(Config.KEEP_SCREEN_ON, Config.defaultKeepScreenOn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //to keep screen on stuff
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //to keep screen on stuff
        if (keepScreenOn) {
            mHandler.removeCallbacks(clearScreenOn);
            mHandler.postDelayed(clearScreenOn, SCREEN_TIMEOUT * 1000);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
}
