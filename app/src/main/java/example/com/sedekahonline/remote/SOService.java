package example.com.sedekahonline.remote;

import com.google.gson.JsonObject;

import example.com.sedekahonline.model.BaseResponseV1;
import example.com.sedekahonline.model.BaseResponseV2;
import example.com.sedekahonline.model.DataSedekah;
import example.com.sedekahonline.model.DataUser;
import example.com.sedekahonline.model.JadwalShalat;
import example.com.sedekahonline.model.Ulasan;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SOService {

//    @GET("{lokasi}/dayli.json")
//    Call<Items> getJadwalSholat(@Path("lokasi") String periode);


    @POST(".")
    Call<BaseResponseV2<DataUser>> loginUser(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV1<DataSedekah>> daftarSedekah(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV2<DataSedekah>> dataSedekah(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV1<Ulasan>> dataUlasan(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV2<JadwalShalat>> dataJadwal(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV2<Ulasan>> dataUlasanSingle(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV2> simpleResponse(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV2> callService(@Body JsonObject jsonObject);

    @POST(".")
    Call<BaseResponseV1> callServiceArray(@Body JsonObject jsonObject);
}