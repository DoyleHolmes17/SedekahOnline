package example.com.sedekahonline.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataSedekah {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nama_lengkap")
    @Expose
    private String nama_lengkap;
    @SerializedName("foto")
    @Expose
    private List<ImageData> gambar;
    @SerializedName("alamat_lengkap")
    @Expose
    private String alamat;
    @SerializedName("no_hp")
    @Expose
    private String noHp;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("status_sedekah")
    @Expose
    private String statusSedekah;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("keterangan")
    @Expose
    private String keterangan;
    @SerializedName("ulasan")
    @Expose
    private List<Ulasan> ulasan;

    public static String getAddressFromLatLong(Double latitude, Double longitude, Context context) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.getMessage();
        } catch (IndexOutOfBoundsException e) {
            Log.e("IndexOutOfBoundsEx", e.getMessage() + "");
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public List<ImageData> getGambar() {
        return gambar;
    }

    public void setGambar(List<ImageData> gambar) {
        this.gambar = gambar;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatusSedekah() {
        return statusSedekah;
    }

    public void setStatusSedekah(String statusSedekah) {
        this.statusSedekah = statusSedekah;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Ulasan> getUlasan() {
        return ulasan;
    }

    public void setUlasan(List<Ulasan> ulasan) {
        this.ulasan = ulasan;
    }
}
