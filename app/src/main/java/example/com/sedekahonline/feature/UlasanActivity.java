package example.com.sedekahonline.feature;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.DataUser;
import example.com.sedekahonline.model.Ulasan;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UlasanActivity extends AppCompatActivity {

    private UlasanAdapter adapter;
    private SOService mService;
    private String id_sedekah;
    private Toolbar mToolBar;
    private RecyclerView rvUlasan;
    private List<Ulasan> ulasanList = new ArrayList<>();
    private ProgressDialog pd;
    private Button btSend;
    private EditText etUlasan;
    private SharedPreferences preferences;
    private DataUser dataUser;
    private Boolean asGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ulasan);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ulasan");
        Gson gson = new Gson();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        asGuest = preferences.getBoolean("asGuest", false);
        dataUser = gson.fromJson(preferences.getString("dataUser", null), DataUser.class);
        rvUlasan = (RecyclerView) findViewById(R.id.rvUlasan);
        btSend = (Button) findViewById(R.id.btSend);
        etUlasan = (EditText) findViewById(R.id.etUlasan);
        id_sedekah = getIntent().getStringExtra("id_sedekah");
        Log.e("id_sedekah", id_sedekah + "");
        mService = RetrofitClient.createRequest(this);
        pd = new ProgressDialog(UlasanActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        dataUlasan();
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asGuest) {
                    setError("Harus masuk untuk bisa mengakses menu ini");
                } else {
                    if (etUlasan.getText().toString().equals("")) {
                        setError("Komentar tidak boleh kosong");
//                    Toast.makeText(UlasanActivity.this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "addreview");
                    jsonObject.addProperty("id_sedekah", id_sedekah);
                    jsonObject.addProperty("id_user", dataUser.getId());
                    jsonObject.addProperty("ulasan", etUlasan.getText().toString());

                    mService.simpleResponse(jsonObject).enqueue(new Callback<BaseResponseV2>() {
                        @Override
                        public void onResponse(Call<BaseResponseV2> call, Response<BaseResponseV2> response) {
                            if (response.isSuccessful()) {
                                if (pd.isShowing()) {
                                    pd.dismiss();
                                }
                                Log.e("status", response.body().getStatus());
                                Log.e("message", response.body().getMessage());
                                if (response.body().getStatus().equals("000")) {
                                    new DroidDialog.Builder(UlasanActivity.this)
                                            .icon(R.drawable.ic_action_tick)
                                            .title("")
                                            .content("Berhasil tambah data")
                                            .cancelable(true, true)

                                            .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                                @Override
                                                public void onNeutral(Dialog droidDialog) {
                                                    etUlasan.setText("");
                                                    dataUlasan();
                                                    droidDialog.dismiss();
                                                }
                                            }).color(ContextCompat.getColor(UlasanActivity.this, R.color.light_green), ContextCompat.getColor(UlasanActivity.this, R.color.white),
                                            ContextCompat.getColor(UlasanActivity.this, R.color.light_green))
                                            .show();

                                } else {
//                                Toast.makeText(UlasanActivity.this, "Terjadi masalah pada server", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<BaseResponseV2> call, Throwable t) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            setError("Terjadi masalah koneksi");
//                        Toast.makeText(UlasanActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                            Log.e("error", t.getMessage() + "");
                        }
                    });
                }
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

    public void dataUlasan() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "listreview");
        jsonObject.addProperty("page", "1");
        jsonObject.addProperty("page_size", "10");
        jsonObject.addProperty("id_sedekah", id_sedekah);

        mService.dataUlasan(jsonObject).enqueue(new Callback<BaseResponseV1<Ulasan>>() {
            @Override
            public void onResponse(Call<BaseResponseV1<Ulasan>> call, Response<BaseResponseV1<Ulasan>> response) {
                if (response.isSuccessful()) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    Log.e("status", response.body().getStatus());
                    Log.e("message", response.body().getMessage() + "");
                    if (response.body().getStatus().equals("000")) {
                        ulasanList = response.body().getData();

                        LinearLayoutManager layoutManager = new LinearLayoutManager(UlasanActivity.this, LinearLayoutManager.VERTICAL, false);

                        adapter = new UlasanAdapter(UlasanActivity.this, ulasanList);
                        rvUlasan.setLayoutManager(layoutManager);
                        rvUlasan.setHasFixedSize(true);
                        rvUlasan.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        setError("Tidak Ada Data.");
//                        Toast.makeText(UlasanActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponseV1<Ulasan>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                setError("Terjadi masalah koneksi");
//                Toast.makeText(UlasanActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                Log.e("errorr", t.getMessage() + "");
            }
        });
    }

    public void setError(String message) {
        new DroidDialog.Builder(UlasanActivity.this)
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
