package example.com.sedekahonline.feature;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.map.MapsActivity;
import example.com.sedekahonline.model.ImageData;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class DetailSedekahActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Button btnUlasan, btnMap;
    private String gambar;
    private String nama, nohp, email, alamat, location, id_sedekah;
    private TextView tvName, tvEmail, tvPhone, tvAddress;
    private ImageView ivPhoto;
    private RecyclerView rvPhoto;
    private ImageListAdapter imageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_detail_sedekah);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        btnUlasan = (Button) findViewById(R.id.btnUlasan);
        btnMap = (Button) findViewById(R.id.btnMap);

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        rvPhoto = findViewById(R.id.rvPhoto);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAddress = (TextView) findViewById(R.id.tvAddress);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Detail Data");

        id_sedekah = getIntent().getStringExtra("id_sedekah");
        nama = getIntent().getStringExtra("nama");
        gambar = getIntent().getStringExtra("gambar");
        nohp = getIntent().getStringExtra("nohp");
        email = getIntent().getStringExtra("email");
        alamat = getIntent().getStringExtra("alamat");
        location = getIntent().getStringExtra("location");

        if (nama != null) {
            tvName.setText(nama);
        }
        if (nohp != null) {
            tvPhone.setText(nohp);
        }
        if (email != null) {
            tvEmail.setText(email);
        }
        if (alamat != null) {
            tvAddress.setText(alamat);
        }
        if (gambar != null) {

            ivPhoto.setVisibility(View.GONE);
            rvPhoto.setVisibility(View.VISIBLE);

            List<ImageData> imageDataList = new ArrayList<>();
            Type token = new TypeToken<List<ImageData>>() {
            }.getType();
            imageDataList = new Gson().fromJson(gambar, token);
            if (imageDataList.size() > 0) {
                Log.e("data", "here");

                LinearLayoutManager layoutManager = new LinearLayoutManager(DetailSedekahActivity.this, LinearLayoutManager.HORIZONTAL, false);

                imageListAdapter = new ImageListAdapter(DetailSedekahActivity.this, imageDataList);
                rvPhoto.setLayoutManager(layoutManager);
                rvPhoto.setHasFixedSize(true);
                rvPhoto.setAdapter(imageListAdapter);

//            Bitmap bitmap = BitmapFactory.decodeByteArray(gambar, 0, gambar.length);
//            ivPhoto.setImageBitmap(bitmap);
//            Picasso.with(this).load(gambar).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivPhoto);
            } else {
                Log.e("data", "here");
                ivPhoto.setVisibility(View.VISIBLE);
                rvPhoto.setVisibility(View.GONE);
                ivPhoto.setImageResource(R.drawable.default_profile);
            }
        }

        btnUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(DetailSedekahActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DetailSedekahActivity.this, UlasanActivity.class);
                intent.putExtra("id_sedekah", id_sedekah);
                startActivity(intent);
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailSedekahActivity.this, MapsActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("nama", nama);
                intent.putExtra("nohp", nohp);
                startActivity(intent);
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
}
