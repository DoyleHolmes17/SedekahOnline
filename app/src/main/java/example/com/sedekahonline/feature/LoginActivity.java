package example.com.sedekahonline.feature;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.DataUser;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.sedekahonline.helper.ValidateHelper.isEmailValid;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class
LoginActivity extends AppCompatActivity {
    Button btnLogin, btnLoginGuest;
    EditText etEmail, etPass;
    TextView tvForgot, tvRegist;
    DataUser userData;
    ProgressDialog pd;
    DataUser dataUser = null;
    SharedPreferences preferences;
    private SOService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        preferences.edit().putBoolean("asGuest",false).apply();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLoginGuest = (Button) findViewById(R.id.btnLoginGuest);
        tvRegist = (TextView) findViewById(R.id.tvRegist);
        tvForgot = (TextView) findViewById(R.id.tvForgot);

//        etEmail.setText("rizki.samudra12@gmail.com");
//        etPass.setText("123123");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                final String pass = etPass.getText().toString();
                if (email.isEmpty()) {
                    etEmail.requestFocus();
                    setError("Email tidak Boleh kosong");
                } else if (!isEmailValid(email)) {
                    etEmail.requestFocus();
                    setError("Format email salah");
                } else if (pass.isEmpty()) {
                    etPass.requestFocus();
                    setError("Password tidak Boleh kosong");
                } else {
                    mService = RetrofitClient.createRequest(LoginActivity.this);
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Tunggu Sebentar. . .");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "loginuser");
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("password", pass);

                    mService.loginUser(jsonObject).enqueue(new Callback<BaseResponseV2<DataUser>>() {
                        @Override
                        public void onResponse(Call<BaseResponseV2<DataUser>> call, Response<BaseResponseV2<DataUser>> response) {
                            if (response.isSuccessful()) {
                                if (pd.isShowing()) {
                                    pd.dismiss();
                                }
                                Log.e("status", response.body().getStatus());
                                Log.e("message", response.body().getMessage());
                                if (response.body().getStatus().equals("000")) {
                                    dataUser = response.body().getData();

                                    SharedPreferences.Editor editor = preferences.edit();
                                    Gson gson = new Gson();
                                    String data = gson.toJson(dataUser);
                                    editor.putString("dataUser", data);
                                    editor.apply();
                                    Intent intent;
                                    if (dataUser.getReset()) {
                                        intent = new Intent(LoginActivity.this, NewPasswordActivity.class);
                                        intent.putExtra("email", dataUser.getEmail() + "");
                                        intent.putExtra("pass", pass + "");
                                    } else {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        finish();
                                    }
                                    startActivity(intent);
                                } else {
                                    setError(response.body().getMessage() + "");
//                                    Toast.makeText(LoginActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();

                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<BaseResponseV2<DataUser>> call, Throwable t) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            setError("Terjadi masalah koneksi");
//                            Toast.makeText(LoginActivity.this, "Terjadi masalah koneksi", Toast.LENGTH_SHORT).show();
//                            Log.e("errorr", t.getMessage() + "");
                        }
                    });
                }
            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });
        tvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
            }
        });
        btnLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putBoolean("asGuest",true).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("asGuest", true);
                startActivity(intent);
            }
        });
    }

    public void setError(String message) {
        new DroidDialog.Builder(LoginActivity.this)
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
