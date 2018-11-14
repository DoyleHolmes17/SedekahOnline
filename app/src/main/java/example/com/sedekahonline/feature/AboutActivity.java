package example.com.sedekahonline.feature;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import example.com.sedekahonline.R;

public class AboutActivity extends AppCompatActivity {
    Toolbar mToolBar;
    TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        tvAbout = findViewById(R.id.tvAbout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Tentang Aplikasi");

        tvAbout.setText("Assalamu'alaikum wr. wb.\n" +
                "Aplikasi ini dibuat untuk memudahkan masyarakat luas dalam mencari lokasi sedekah, didalam aplikasi ini juga terdapat menu lain yang juga bermanfaat diantarantanya adalah : jadwal shalat, arah kiblat, dan Al-Qur'an. Pengembang menyadari aplikasi ini masih banyak terdapat kekurangan maka dari itu kritik dan saran dari pengguna sangat diharapkan oleh pengembang.\n\n Untuk kritik dan saran silahkan hubungi (082237221118/WA, email:ilham.eva@gmail.com/ilham.apriyanto@pegadaian.co.id).\n" +
                "\nTerima Kasih.\n" +
                "Wassalamu'alaikum wr. wb.");
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
