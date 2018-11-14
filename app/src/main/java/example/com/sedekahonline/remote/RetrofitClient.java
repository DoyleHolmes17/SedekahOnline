package example.com.sedekahonline.remote;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import example.com.sedekahonline.BuildConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static example.com.sedekahonline.remote.ApiUtils.BASE_URL;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SOService createRequest(Context context) {
        return RetrofitClient.getClient(context).create(SOService.class);
    }

    public static Retrofit getClient(Context context) {
        return retrofit(okhttpBuilderWithHeader(context), BASE_URL);
    }

    public static Retrofit getClient(Context context, String url) {
        return retrofit(okhttpBuilderWithHeader(context), url);
    }

    public static SOService createRequest(Context context, String url) {
        return RetrofitClient.getClient(context, url).create(SOService.class);
    }

    public static OkHttpClient.Builder okhttpBuilderWithHeader(Context context) {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder();
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        okhttpBuilder.cache(cache);

        try {
            okhttpBuilder.sslSocketFactory(new TLSSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(interceptor);
        }

        return okhttpBuilder;
    }

    public static Retrofit retrofit(OkHttpClient.Builder okhttpBuilder, String baseUrl) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
