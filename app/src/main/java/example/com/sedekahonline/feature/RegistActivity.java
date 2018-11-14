package example.com.sedekahonline.feature;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.JsonObject;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.sedekahonline.helper.ValidateHelper.isEmailValid;
import static example.com.sedekahonline.helper.ValidateHelper.isValidName;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class RegistActivity extends AppCompatActivity {
    Button btnReg;
    EditText etEmail, etName, etPass, etPassConf, etPhone, etAddress;
    ProgressDialog pd;
    SOService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_regist);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPassConf = (EditText) findViewById(R.id.etPasswordKonfirm);
        btnReg = (Button) findViewById(R.id.btnRegist);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String passKonf = etPassConf.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String name = etName.getText().toString();
                if (name.isEmpty()) {
                    setError("Nama tidak Boleh kosong");
                    etName.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Nama tidak Boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    setError("Email tidak Boleh kosong");
                    etEmail.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Email tidak Boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (!isValidName(name)) {
                    etName.requestFocus();
                    setError("Nama hanya berupa abjad");
                } else if (!isEmailValid(email)) {
                    etEmail.requestFocus();
                    setError("Format email salah");
                } else if (pass.isEmpty()) {
                    setError("Password tidak Boleh kosong");
                    etPass.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Password tidak Boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (passKonf.isEmpty()) {
                    setError("Password Konfirmasi tidak Boleh kosong");
                    etPassConf.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Password Konfirmasi tidak Boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (!passKonf.equals(pass)) {
                    setError("Password Konfirmasi dan password tidak cocok");
                    etPassConf.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Password Konfirmasi dan password tidak cocok", Toast.LENGTH_SHORT).show();
                } else if (phone.isEmpty()) {
                    setError("Nomor hp tidak boleh kosong");
                    etPhone.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Nomor hp tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    setError("Alamat tidak boleh kosong");
                    etAddress.requestFocus();
//                    Toast.makeText(RegistActivity.this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    mService = RetrofitClient.createRequest(RegistActivity.this);
                    pd = new ProgressDialog(RegistActivity.this);
                    pd.setMessage("Tunggu Sebentar. . .");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "registeruser");
                    jsonObject.addProperty("nama", name);
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("password", pass);
                    jsonObject.addProperty("alamat", address);
                    jsonObject.addProperty("telepon", phone);

                    mService.callService(jsonObject).enqueue(new Callback<BaseResponseV2>() {
                        @Override
                        public void onResponse(Call<BaseResponseV2> call, Response<BaseResponseV2> response) {
                            if (response.isSuccessful()) {
                                if (pd.isShowing()) {
                                    pd.dismiss();
                                }

                                new DroidDialog.Builder(RegistActivity.this)
                                        .icon(R.drawable.ic_action_tick)
                                        .title("")
                                        .content("Registrasi berhasil")
                                        .cancelable(true, true)

                                        .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                            @Override
                                            public void onNeutral(Dialog droidDialog) {
                                                Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).color(ContextCompat.getColor(RegistActivity.this, R.color.light_green), ContextCompat.getColor(RegistActivity.this, R.color.white),
                                        ContextCompat.getColor(RegistActivity.this, R.color.light_green))
                                        .show();


                            } else {
                                setError("Response Fail");
//                                Toast.makeText(RegistActivity.this, "Response Fail", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<BaseResponseV2> call, Throwable t) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            setError("Terjadi masalah koneksi");
//                            Toast.makeText(RegistActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
                            Log.e("errorr", t.getMessage() + "");
                        }
                    });

                }
            }
        });

    }

    public void setError(String message) {
        new DroidDialog.Builder(RegistActivity.this)
                .icon(R.drawable.ic_action_close)
                .title("")
                .content(message)
                .cancelable(true, true)

                .neutralButton("OK", new DroidDialog.onNeutralListener() {
                    @Override
                    public void onNeutral(Dialog droidDialog) {
                        Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).color(ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.red))
                .show();
    }

}
