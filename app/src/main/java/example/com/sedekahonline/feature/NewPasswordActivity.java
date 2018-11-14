package example.com.sedekahonline.feature;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;

import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.JsonObject;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.remote.RetrofitClient;
import example.com.sedekahonline.remote.SOService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.jinesh.captchaimageviewlib.CaptchaImageView;

/**
 * Created by DOCOTEL on 12/4/2017.
 */

public class NewPasswordActivity extends AppCompatActivity {
    EditText etOldPass, etNewPass, etNewPassConf;
    String email, oldPass, newPass, newPassConf, fromProfile;
    EditText etCapthcha;
    Button btSend;
    CaptchaImageView imageCaptcha;
    ImageView regenCaptcha;
    Context context = NewPasswordActivity.this;
    SOService mService;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pass);
//        edEmail.setText("rizki@docotel.com");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_green));

        }
        email = getIntent().getStringExtra("email");
        oldPass = getIntent().getStringExtra("pass");
        fromProfile = getIntent().getStringExtra("fromProfile");
        etCapthcha = (EditText) findViewById(R.id.etCapthcha);
        etOldPass = (EditText) findViewById(R.id.etOldPass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etNewPassConf = (EditText) findViewById(R.id.etNewPassConf);
        regenCaptcha = (ImageView) findViewById(R.id.regenCaptcha);
        btSend = (Button) findViewById(R.id.btSend);

        imageCaptcha = (CaptchaImageView) findViewById(R.id.imageCaptcha);
        imageCaptcha.setCaptchaType(CaptchaImageView.CaptchaGenerator.BOTH);
        imageCaptcha.setCaptchaLength(6);
        if (oldPass != null) {
            etOldPass.setText(oldPass + "");
        }
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = etOldPass.getText().toString();
                newPass = etNewPass.getText().toString();
                newPassConf = etNewPassConf.getText().toString();
                if (oldPass.isEmpty()) {
                    setError("Password lama kosong");
//                    Toast.makeText(context, "Password lama kosong", Toast.LENGTH_SHORT).show();
                    etOldPass.requestFocus();
                } else if (newPass.isEmpty()) {
                    setError("Password baru kosong");
//                    Toast.makeText(context, "Password baru kosong", Toast.LENGTH_SHORT).show();
                    etNewPass.requestFocus();
                } else if (newPassConf.isEmpty()) {
                    setError("Password konfirmasi kosong");
//                    Toast.makeText(context, "Password konfirmasi kosong", Toast.LENGTH_SHORT).show();
                    etNewPassConf.requestFocus();
                } else if (!newPassConf.equals(newPass)) {
                    setError("Password baru dan password konfirmasi tidak sesuai");
//                    Toast.makeText(context, "Password baru dan password konfirmasi tidak sesuai", Toast.LENGTH_LONG).show();
                    etNewPass.requestFocus();
                } else if (!etCapthcha.getText().toString().equals(imageCaptcha.getCaptchaCode())) {
                    setError("Captcha salah");
//                    Toast.makeText(context, "Captcha salah", Toast.LENGTH_SHORT).show();
                    imageCaptcha.regenerate();
                    etCapthcha.requestFocus();
                    etCapthcha.setText("");
                } else {
                    mService = RetrofitClient.createRequest(NewPasswordActivity.this);
                    pd = new ProgressDialog(context);
                    pd.setMessage("Tunggu Sebentar. . .");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "changeuserpassword");
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("old_password", oldPass);
                    jsonObject.addProperty("new_password", newPass);

                    mService.simpleResponse(jsonObject).enqueue(new Callback<BaseResponseV2>() {
                        @Override
                        public void onResponse(Call<BaseResponseV2> call, Response<BaseResponseV2> response) {
                            if (response.isSuccessful()) {
                                if (pd.isShowing()) {
                                    pd.dismiss();
                                }
                                if (response.body().getStatus().equals("000")) {
                                    new DroidDialog.Builder(NewPasswordActivity.this)
                                            .icon(R.drawable.ic_action_tick)
                                            .title("")
                                            .content("Password baru telah disimpan")
                                            .cancelable(true, true)

                                            .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                                @Override
                                                public void onNeutral(Dialog droidDialog) {
                                                    Intent intent;
                                                    if (fromProfile != null) {
                                                        intent = new Intent(context, ProfileActivity.class);
                                                    } else {
                                                        intent = new Intent(context, MainActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).color(ContextCompat.getColor(NewPasswordActivity.this, R.color.light_green), ContextCompat.getColor(NewPasswordActivity.this, R.color.white),
                                            ContextCompat.getColor(NewPasswordActivity.this, R.color.light_green))
                                            .show();

                                } else {
                                    setError("Data email atau password salah");
//                                    Toast.makeText(context, "Data email atau password salah", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                setError("Response Fail");
//                                Toast.makeText(context, "Response Fail", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<BaseResponseV2> call, Throwable t) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            setError(t.getMessage() + "");
//                            Toast.makeText(context, "tunggu sebentar. . .", Toast.LENGTH_SHORT).show();
                            Log.e("error", t.getMessage() + "");
                        }
                    });

                }
            }
        });

        regenCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCaptcha.regenerate();
            }
        });

    }

    public void setError(String message) {
        new DroidDialog.Builder(NewPasswordActivity.this)
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
