package example.com.sedekahonline.feature;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.map.MapAddActivity;
import example.com.sedekahonline.helper.CameraHelper;
import example.com.sedekahonline.helper.DatabaseHandler;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.ImageData;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static example.com.sedekahonline.helper.ValidateHelper.isValidName;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class AddSedekahActivity extends AppCompatActivity {
    private List<String> tipeMustahiq = new ArrayList<>();
    private Button btnAdd, btnChange;
    private Toolbar mToolBar;
    private ImageView ivImage1, ivImage2, ivImage3, ivImage4, ivImage5;
    private TextView tvTambahGambar1, tvTambahGambar2, tvTambahGambar3, tvTambahGambar4, tvTambahGambar5;
    private String location, locationLatLng;
    private EditText etAddress, etName, etAlias, etPhone, etDescription;
    private DatabaseHandler db;
    private ProgressDialog pd;
    private String encoded1, encoded2, encoded3, encoded4, encoded5;
    private String name, phone, alias, address, description, tipe = "";
    private JsonObject jsonObject, gson;
    private SOService mService;
    private Spinner spinnerTipe;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private byte[] photo1, photo2, photo3, photo4, photo5;
    private String image = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_sedekah);
        pd = new ProgressDialog(AddSedekahActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);
        db = new DatabaseHandler(this);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        tipeMustahiq = Arrays.asList(getResources().getStringArray(R.array.tipe_mustahiq));
        spinnerTipe = (Spinner) findViewById(R.id.spinnerTipe);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tipeMustahiq);
        spinnerTipe.setAdapter(adapter);
        spinnerTipe.setSelection(0);

        spinnerTipe.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i != 0) {
                            tipe = adapterView.getItemAtPosition(i).toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
        etName = (EditText) findViewById(R.id.etName);

        tvTambahGambar1 = (TextView) findViewById(R.id.tvTambahGambar1);
        tvTambahGambar2 = (TextView) findViewById(R.id.tvTambahGambar2);
        tvTambahGambar3 = (TextView) findViewById(R.id.tvTambahGambar3);
        tvTambahGambar4 = (TextView) findViewById(R.id.tvTambahGambar4);
        tvTambahGambar5 = (TextView) findViewById(R.id.tvTambahGambar5);

        ivImage1 = (ImageView) findViewById(R.id.ivImage1);
        ivImage2 = (ImageView) findViewById(R.id.ivImage2);
        ivImage3 = (ImageView) findViewById(R.id.ivImage3);
        ivImage4 = (ImageView) findViewById(R.id.ivImage4);
        ivImage5 = (ImageView) findViewById(R.id.ivImage5);

        etAlias = (EditText) findViewById(R.id.etAlias);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        mService = RetrofitClient.createRequest(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));
        }
        requestPermissions();
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Tambah Data");

        location = getIntent().getStringExtra("location");
        locationLatLng = getIntent().getStringExtra("locationLatLng");
//        Log.e("locationLatLng", locationLatLng + "");
        try {
            etAddress.setText(location + "");
        } catch (NullPointerException e) {
            etAddress.setText("");
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the windowm
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnChange = (Button) findViewById(R.id.btnChange);
        ivImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                image = "ivImage1";
            }
        });
        ivImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                image = "ivImage2";
            }
        });
        ivImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                image = "ivImage3";
            }
        });
        ivImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                image = "ivImage4";
            }
        });
        ivImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                image = "ivImage5";
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSedekahActivity.this, MapAddActivity.class);
                intent.putExtra("locationLatLng", locationLatLng);
                startActivity(intent);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                phone = etPhone.getText().toString();
                alias = etAlias.getText().toString();
                address = etAddress.getText().toString();
                description = etDescription.getText().toString();

                if (name.equals("")) {
                    etName.requestFocus();
                    setError("Nama tidak boleh kosong");
//                    Toast.makeText(AddSedekahActivity.this, "nama belum diisi", Toast.LENGTH_SHORT).show();
                } else if (alias.equals("")) {
                    etAlias.requestFocus();
                    setError("Alias tidak boleh kosong");
//                    Toast.makeText(AddSedekahActivity.this, "alias belum diisi", Toast.LENGTH_SHORT).show();
                } else if (!isValidName(name)) {
                    etName.requestFocus();
                    setError("Nama hanya berupa abjad");
                } else if (address.equals("")) {
                    etAddress.requestFocus();
                    setError("Alamat tidak boleh kosong");
                } else if (tipe.equals("")) {
                    spinnerTipe.requestFocus();
                    setError("pilih tipe mustahiq");
                } else if (address.equals("")) {
                    etDescription.requestFocus();
                    setError("Keterangan tidak boleh kosong");

//                    Toast.makeText(AddSedekahActivity.this, "alamat belum diisi", Toast.LENGTH_SHORT).show();
//                } else if (photo==null) {
//                    Toast.makeText(AddSedekahActivity.this, "foto belum terpilih", Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncTaskRunner().execute("");
                }
            }
        });

    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CameraHelper.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddSedekahActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = CameraHelper.checkPermission(AddSedekahActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 70, bos);

        if (image.equals("ivImage1")) {
            photo1 = bos.toByteArray();
            ivImage1.setImageBitmap(thumbnail);
            tvTambahGambar1.setVisibility(View.GONE);
        } else if (image.equals("ivImage2")) {
            photo2 = bos.toByteArray();
            ivImage2.setImageBitmap(thumbnail);
            tvTambahGambar2.setVisibility(View.GONE);
        } else if (image.equals("ivImage3")) {
            photo3 = bos.toByteArray();
            ivImage3.setImageBitmap(thumbnail);
            tvTambahGambar3.setVisibility(View.GONE);
        } else if (image.equals("ivImage4")) {
            photo4 = bos.toByteArray();
            ivImage4.setImageBitmap(thumbnail);
            tvTambahGambar4.setVisibility(View.GONE);
        } else if (image.equals("ivImage5")) {
            photo5 = bos.toByteArray();
            ivImage5.setImageBitmap(thumbnail);
            tvTambahGambar5.setVisibility(View.GONE);
        }


        if (pd.isShowing()) {
            pd.dismiss();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, bos);

        if (image.equals("ivImage1")) {
            photo1 = bos.toByteArray();
            ivImage1.setImageBitmap(bm);
            tvTambahGambar1.setVisibility(View.GONE);
        } else if (image.equals("ivImage2")) {
            photo2 = bos.toByteArray();
            ivImage2.setImageBitmap(bm);
            tvTambahGambar2.setVisibility(View.GONE);
        } else if (image.equals("ivImage3")) {
            photo3 = bos.toByteArray();
            ivImage3.setImageBitmap(bm);
            tvTambahGambar3.setVisibility(View.GONE);
        } else if (image.equals("ivImage4")) {
            photo4 = bos.toByteArray();
            ivImage4.setImageBitmap(bm);
            tvTambahGambar4.setVisibility(View.GONE);
        } else if (image.equals("ivImage5")) {
            photo5 = bos.toByteArray();
            ivImage5.setImageBitmap(bm);
            tvTambahGambar5.setVisibility(View.GONE);
        }


    }

    public void setError(String message) {
        new DroidDialog.Builder(AddSedekahActivity.this)
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            List<ImageData> imageDataList = new ArrayList<>();
            if (photo1 != null) {
                encoded1 = Base64.encodeToString(photo1, Base64.DEFAULT).replaceAll("\\n","");
            } else {
                encoded1 = "";
            }
            imageDataList.add(new ImageData(encoded1));
            if (photo2 != null) {
                encoded2 = Base64.encodeToString(photo2, Base64.DEFAULT).replaceAll("\\n","");
            } else {
                encoded2 = "";
            }
            imageDataList.add(new ImageData(encoded2));

            if (photo3 != null) {
                encoded3 = Base64.encodeToString(photo3, Base64.DEFAULT).replaceAll("\\n","");
            } else {
                encoded3 = "";
            }
            imageDataList.add(new ImageData(encoded3));

            if (photo4 != null) {
                encoded4 = Base64.encodeToString(photo4, Base64.DEFAULT).replaceAll("\\n","");
            } else {
                encoded4 = "";
            }
            imageDataList.add(new ImageData(encoded4));

            if (photo5 != null) {
                encoded5 = Base64.encodeToString(photo5, Base64.DEFAULT).replaceAll("\\n","");
            } else {
                encoded5 = "";
            }
            imageDataList.add(new ImageData(encoded5));


            JSONObject jsonItem1 = new JSONObject();
            JSONObject jsonItem2 = new JSONObject();
            JSONObject jsonItem3 = new JSONObject();
            JSONObject jsonItem4 = new JSONObject();
            JSONObject jsonItem5 = new JSONObject();
            JSONArray list = new JSONArray();
            try {
                for (int i = 0; imageDataList.size() > i; i++) {
                    if (i == 0) {
                        jsonItem1.put("image", imageDataList.get(i).getImage());
                        list.put(jsonItem1);
                    } else if (i == 1) {
                        if (!imageDataList.get(i).getImage().equals("")) {
                            jsonItem2.put("image", imageDataList.get(i).getImage());
                            list.put(jsonItem2);
                        }
                    } else if (i == 2) {
                        if (!imageDataList.get(i).getImage().equals("")) {
                            jsonItem3.put("image", imageDataList.get(i).getImage());
                            list.put(jsonItem3);
                        }
                    } else if (i == 3) {
                        if (!imageDataList.get(i).getImage().equals("")) {
                            jsonItem4.put("image", imageDataList.get(i).getImage());
                            list.put(jsonItem4);
                        }
                    } else if (i == 4) {
                        if (!imageDataList.get(i).getImage().equals("")) {
                            jsonItem5.put("image", imageDataList.get(i).getImage());
                            list.put(jsonItem5);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject jsonObject1 = new JSONObject();
//            jsonObject = new JsonObject();
//            jsonObject.addProperty("type", "createalms");
//            jsonObject.addProperty("nama_lengkap", name);
//            jsonObject.addProperty("nama_alias", alias);
//            jsonObject.addProperty("tipe", tipe);
//            jsonObject.addProperty("alamat_lengkap", address);
//            jsonObject.addProperty("keterangan", description);
//            jsonObject.addProperty("no_hp", phone);
//            jsonObject.addProperty("latitude", locationLatLng.split(",")[0]);
//            jsonObject.addProperty("longitude", locationLatLng.split(",")[1]);
//            jsonObject.addProperty("foto", new Gson().toJson(imageDataList));
            try {
                jsonObject1.put("type", "createalms");
                jsonObject1.put("nama_lengkap", name);
                jsonObject1.put("nama_alias", alias);
                jsonObject1.put("tipe", tipe);
                jsonObject1.put("alamat_lengkap", address);
                jsonObject1.put("keterangan", description);
                jsonObject1.put("no_hp", phone);
                jsonObject1.put("latitude", locationLatLng.split(",")[0]);
                jsonObject1.put("longitude", locationLatLng.split(",")[1]);
                jsonObject1.put("foto", list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gson = new JsonParser().parse(jsonObject1.toString()).getAsJsonObject();
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "jsonData.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(new Gson().toJson(gson) + "");
                writer.flush();
                writer.close();
                Log.e("sukses", " sukses");
            } catch (IOException e) {
                Log.e("errorMessage", e.getMessage() + " 1");
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Log.e("jsonObject", new Gson().toJson(gson) + " 1");
            mService.simpleResponse(gson).enqueue(new Callback<BaseResponseV2>() {
                @Override
                public void onResponse(Call<BaseResponseV2> call, Response<BaseResponseV2> response) {

                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    Log.e("status", response.body().getStatus() + "");
                    Log.e("message", response.body().getMessage() + "");
                    if (response.isSuccessful()) {

                        if (response.body().getStatus().equalsIgnoreCase("000")) {
                            new DroidDialog.Builder(AddSedekahActivity.this)
                                    .icon(R.drawable.ic_action_tick)
                                    .title("")
                                    .content("Sukses menambah data")
                                    .cancelable(true, true)

                                    .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                        @Override
                                        public void onNeutral(Dialog droidDialog) {
                                            Intent intent = new Intent(AddSedekahActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).color(ContextCompat.getColor(AddSedekahActivity.this, R.color.light_green), ContextCompat.getColor(AddSedekahActivity.this, R.color.white),
                                    ContextCompat.getColor(AddSedekahActivity.this, R.color.light_green))
                                    .show();

                        } else {
                            setError(response.body().getMessage() + "");
//                            Toast.makeText(AddSedekahActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setError("Response Fail");
//                        Toast.makeText(AddSedekahActivity.this, "Response Fail", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onFailure(Call<BaseResponseV2> call, Throwable t) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    setError("Terjadi masalah koneksi");
//                    Toast.makeText(AddSedekahActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                    Log.e("errorr", t.getMessage() + "");
                }
            });
        }


        @Override
        protected void onPreExecute() {
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}

