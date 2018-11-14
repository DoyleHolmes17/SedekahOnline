package example.com.sedekahonline.feature;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import example.com.sedekahonline.R;
import example.com.sedekahonline.helper.CameraHelper;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.DataUser;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private Button btnChangePass, btnUpdate, btnPict;
    private EditText etNama, etAlamat, etPhone;
    private ImageView ivImage;
    private ProgressDialog pd;
    private String id_user;
    private DataUser dataUser;
    private byte[] photo;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private String encoded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dataUser = getDataUser();
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnPict = (Button) findViewById(R.id.btnPict);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        etNama = (EditText) findViewById(R.id.etNama);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etPhone = (EditText) findViewById(R.id.etPhone);

        if (dataUser != null) {
            id_user = dataUser.getId() + "";
            getProfileData();
        }

        btnPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, NewPasswordActivity.class);
                intent.putExtra("email", dataUser.getEmail() + "");
                startActivity(intent);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etNama.getText().toString().isEmpty()) {
                    setError("Nama tidak boleh kosong");
                } else if (etAlamat.getText().toString().isEmpty()) {
                    setError("Alamat tidak boleh kosong");
                } else if (etPhone.getText().toString().isEmpty()) {
                    setError("No Hp tidak boleh kosong");
                } else {
                    if (photo != null) {
                        encoded = Base64.encodeToString(photo, Base64.DEFAULT);
                    } else {
                        encoded = "";
                    }
                    updateProfileData(encoded);
                }
            }
        });
    }

    public void updateProfileData(String encoded) {
        SOService mService = RetrofitClient.createRequest(this);
        pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);                    pd.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "updateuser");
        jsonObject.addProperty("user_id", dataUser.getId() + "");
        jsonObject.addProperty("nama", etNama.getText().toString());
        jsonObject.addProperty("alamat", etAlamat.getText().toString());
        jsonObject.addProperty("telepon", etPhone.getText().toString());
        jsonObject.addProperty("gambar", encoded);

        mService.loginUser(jsonObject).enqueue(new Callback<BaseResponseV2<DataUser>>() {
            @Override
            public void onResponse(Call<BaseResponseV2<DataUser>> call, Response<BaseResponseV2<DataUser>> response) {
                if (response.isSuccessful()) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
//                    Log.e("status", response.body().getStatus());
//                    Log.e("message", response.body().getMessage());
                    if (response.body().getStatus().equals("000")) {
                        dataUser = response.body().getData();

                        SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
                        Gson gson = new Gson();
                        String data = gson.toJson(dataUser);
                        editor.putString("dataUser", data);
                        editor.apply();
                        new DroidDialog.Builder(ProfileActivity.this)
                                .icon(R.drawable.ic_action_tick)
                                .title("")
                                .content("Update Profile Berhasil")
                                .cancelable(true, true)

                                .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                    @Override
                                    public void onNeutral(Dialog droidDialog) {
                                        droidDialog.dismiss();
                                    }
                                }).color(ContextCompat.getColor(ProfileActivity.this, R.color.light_green), ContextCompat.getColor(ProfileActivity.this, R.color.white),
                                ContextCompat.getColor(ProfileActivity.this, R.color.light_green))
                                .show();
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponseV2<DataUser>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                setError("Terjadi masalah koneksi");
//                Toast.makeText(ProfileActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                Log.e("error", t.getMessage() + "");
            }
        });
    }

    public void getProfileData() {
        SOService mService = RetrofitClient.createRequest(this);
        pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Tunggu Sebentar. . .");
        pd.setCanceledOnTouchOutside(false);                    pd.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "viewuser");
        jsonObject.addProperty("id_user", id_user);
        mService.loginUser(jsonObject).enqueue(new Callback<BaseResponseV2<DataUser>>() {
            @Override
            public void onResponse(Call<BaseResponseV2<DataUser>> call, Response<BaseResponseV2<DataUser>> response) {
                if (response.isSuccessful()) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
//                    Log.e("status", response.body().getStatus());
//                    Log.e("message", response.body().getMessage());
//                    Log.e("data", response.body().getData()+"");
                    if (response.body().getStatus().equals("000")) {
                        etNama.setText(response.body().getData().getNama());
                        etAlamat.setText(response.body().getData().getAlamat());
                        etPhone.setText(response.body().getData().getTelepon());
                        if (!response.body().getData().getGambar().equalsIgnoreCase("")) {
                            Picasso.with(ProfileActivity.this).load(response.body().getData().getGambar()).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivImage);
                        } else {
                            ivImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponseV2<DataUser>> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(ProfileActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                Log.e("errorr", t.getMessage() + "");
            }
        });
    }

    public DataUser getDataUser() {
        SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        return new Gson().fromJson(preferences.getString("dataUser", null), DataUser.class);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = CameraHelper.checkPermission(ProfileActivity.this);

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
        pd.setCanceledOnTouchOutside(false);                    pd.show();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

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

        ivImage.setImageBitmap(thumbnail);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 80, bos);
        photo = bos.toByteArray();

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
        bm.compress(Bitmap.CompressFormat.PNG, 0, bos);
        photo = bos.toByteArray();
        ivImage.setImageBitmap(bm);

    }

    public void setError(String message) {
        new DroidDialog.Builder(ProfileActivity.this)
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
