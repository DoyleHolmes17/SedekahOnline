package example.com.sedekahonline.feature.schedule.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.schedule.adapter.KotaAdapter;
import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.Location;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DOCOTEL on 1/16/2018.
 */

public class KotaActivity extends AppCompatActivity {
    ProgressDialog pd;
    RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvKota;
    private SOService mService;
    private EditText etKota;
    private KotaAdapter adapter;
    private List<Location> listData, listDataShown;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(KotaActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);                    pd.show();
        setContentView(R.layout.activity_kota);
        rvKota = (RecyclerView) findViewById(R.id.rvKota);
        etKota = (EditText) findViewById(R.id.etKota);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Pilih Kota");
        getData();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));
        }

        etKota.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text;
                int size = etKota.length();
                if (size == 0) {
                    adapter = new KotaAdapter(KotaActivity.this, listData);

                } else if (size > 0) {
                    text = etKota.getText().toString();
                    listDataShown = new ArrayList<>();
                    for (int i = 0; listData.size() > i; i++) {
                        if (listData.get(i).getNama_kota().substring(0, size).equalsIgnoreCase(text)) {
                            listDataShown.add(listData.get(i));
                        }
                    }
                    adapter = new KotaAdapter(KotaActivity.this, listDataShown);
                }
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvKota.setLayoutManager(mLayoutManager);
                rvKota.setItemAnimator(new DefaultItemAnimator());
                rvKota.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void getData() {
//        mService = RetrofitClient.getScheduleService();
//        mService.getKota().enqueue(new Callback<BaseResponseV1<Location>>() {
//            @Override
//            public void onResponse(Call<BaseResponseV1<Location>> call, Response<BaseResponseV1<Location>> response) {
//                if (response.isSuccessful()) {
//                    String status = response.body().getStatus();
//                    if (pd.isShowing()) {
//                        pd.dismiss();
//                    }
//                    if (status.equals("sukses")) {
//                        listData = response.body().getData();
////                        Log.e("showResponse", response.body().toString());
//                        adapter = new KotaAdapter(KotaActivity.this, listData);
//
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                        rvKota.setLayoutManager(mLayoutManager);
//                        rvKota.setItemAnimator(new DefaultItemAnimator());
//                        rvKota.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Response Fail", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponseV1<Location>> call, Throwable t) {
////                Toast.makeText(getApplicationContext(), "tunggu sebentar. . .", Toast.LENGTH_SHORT).show();
//                Log.e("errorr", t.getMessage() + "");
//                getData();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(KotaActivity.this, ScheduleActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(KotaActivity.this, ScheduleActivity.class);
        startActivity(intent);
        finish();
    }
}
