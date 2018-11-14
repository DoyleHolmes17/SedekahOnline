package example.com.sedekahonline.feature.sedekah;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.DaftarSedekahAdapter;
import example.com.sedekahonline.feature.map.MapAddActivity;
import example.com.sedekahonline.helper.DatabaseHandler;
import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.DataSedekah;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SedekahActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView rvSedekah;
    private DaftarSedekahAdapter adapter;
    private List<DataSedekah> sedekahList = new ArrayList<>();
    private ImageView ivAdd;
    private DatabaseHandler db;
    private SOService mService;
    private ProgressDialog pd;
    private SharedPreferences preferences;
    private Boolean asGuest;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sedekah);
        pd = new ProgressDialog(SedekahActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        asGuest = preferences.getBoolean("asGuest", false);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        db = new DatabaseHandler(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar Sedekah");
        rvSedekah = (RecyclerView) findViewById(R.id.rvSedekah);
        ivAdd = (ImageView) findViewById(R.id.ivAdd);

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    Intent intent = new Intent(SedekahActivity.this, MapAddActivity.class);
                    startActivity(intent);
                }
            }
        });

        getDataSedekah();
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

    public void setError(String message) {
        try {

            new DroidDialog.Builder(SedekahActivity.this)
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
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public void getDataSedekah() {
        mService = RetrofitClient.createRequest(this);
        pd.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "listverifiedalms");
        jsonObject.addProperty("page", "1");
        jsonObject.addProperty("page_size", "5");

        mService.daftarSedekah(jsonObject).enqueue(new Callback<BaseResponseV1<DataSedekah>>() {
            @Override
            public void onResponse(Call<BaseResponseV1<DataSedekah>> call, Response<BaseResponseV1<DataSedekah>> response) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (response.isSuccessful()) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SedekahActivity.this, LinearLayoutManager.VERTICAL, false);
                    try {
                        sedekahList.addAll(response.body().getData());
                        adapter = new DaftarSedekahAdapter(SedekahActivity.this, sedekahList);
                        rvSedekah.setLayoutManager(layoutManager);
                        rvSedekah.setHasFixedSize(true);
                        rvSedekah.setAdapter(adapter);
                    } catch (NullPointerException e) {
                        Log.e("errorMessage", e.getMessage() + "");
                    }
                } else {
                    setError("Response Fail");
//                    Toast.makeText(SedekahActivity.this, "Response Fail", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<BaseResponseV1<DataSedekah>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                setError("Terjadi masalah koneksi");
//                Toast.makeText(SedekahActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                Log.e("errorr", t.getMessage() + "");
            }
        });
    }
}
