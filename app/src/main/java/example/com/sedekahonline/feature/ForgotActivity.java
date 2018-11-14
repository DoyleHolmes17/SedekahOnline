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

public class ForgotActivity extends AppCompatActivity {
    EditText etEmail;
    String email;
    EditText etCapthcha;
    Button btSend;
    CaptchaImageView imageCaptcha;
    ImageView regenCaptcha;
    Context context = ForgotActivity.this;
    SOService mService;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass);
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
        etEmail = (EditText) findViewById(R.id.etEmail);
        etCapthcha = (EditText) findViewById(R.id.etCapthcha);
        regenCaptcha = (ImageView) findViewById(R.id.regenCaptcha);
        btSend = (Button) findViewById(R.id.btSend);
        imageCaptcha = (CaptchaImageView) findViewById(R.id.imageCaptcha);
        imageCaptcha.setCaptchaType(CaptchaImageView.CaptchaGenerator.BOTH);
        imageCaptcha.setCaptchaLength(6);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                if (email.isEmpty()) {
                    setError("Email kosong");
//                    Toast.makeText(context, "Email kosong", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
//            } else if (!isEmailValid(email)) {
//                Toast.makeText(context, "Masukkan email yang benar", Toast.LENGTH_SHORT).show();
//                edEmail.requestFocus();
                } else if (!etCapthcha.getText().toString().equals(imageCaptcha.getCaptchaCode())) {
                    setError("Captcha salah");
//                    Toast.makeText(context, "Captcha salah", Toast.LENGTH_SHORT).show();
                    imageCaptcha.regenerate();
                    etCapthcha.requestFocus();
                    etCapthcha.setText("");
                } else {
                    mService = RetrofitClient.createRequest(context);
                    pd = new ProgressDialog(context);
                    pd.setMessage("Tunggu Sebentar. . .");
                    pd.setCanceledOnTouchOutside(false);                    pd.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "resetuserpassword");
                    jsonObject.addProperty("email", email);

                    mService.callService(jsonObject).enqueue(new Callback<BaseResponseV2>() {
                        @Override
                        public void onResponse(Call<BaseResponseV2> call, Response<BaseResponseV2> response) {
                            if (response.isSuccessful()) {
                                if (pd.isShowing()) {
                                    pd.dismiss();
                                }
                                new DroidDialog.Builder(ForgotActivity.this)
                                        .icon(R.drawable.ic_action_tick)
                                        .title("")
                                        .content("Check email untuk password baru")
                                        .cancelable(true, true)

                                        .neutralButton("OK", new DroidDialog.onNeutralListener() {
                                            @Override
                                            public void onNeutral(Dialog droidDialog) {
                                                Intent intent = new Intent(context, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).color(ContextCompat.getColor(ForgotActivity.this, R.color.light_green), ContextCompat.getColor(ForgotActivity.this, R.color.white),
                                        ContextCompat.getColor(ForgotActivity.this, R.color.light_green))
                                        .show();

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
                            Log.e("errorr", t.getMessage() + "");
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
        new DroidDialog.Builder(ForgotActivity.this)
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
