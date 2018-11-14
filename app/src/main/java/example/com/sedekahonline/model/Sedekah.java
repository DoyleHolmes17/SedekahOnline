package example.com.sedekahonline.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by DOCOTEL on 2/12/2018.
 */

public class Sedekah {
    int id;
    String nama;
    String noHp;
    String email;
    String alamat;
    byte[] gambar;
    String latitude;
    String longitude;

    public Sedekah(String nama, String noHp, String email, String alamat) {
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
    }

    public Sedekah(String nama, String noHp, String email, String alamat, byte[] gambar, String latitude, String longitude) {
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
        this.gambar = gambar;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Sedekah(String nama, String noHp, String email, String alamat, String latitude, String longitude) {
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Sedekah(int id, String nama, String noHp, String email, String alamat, byte[] gambar, String latitude, String longitude) {
        this.id = id;
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
        this.gambar = gambar;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static String getAddressFromLatLong(Double latitude, Double longitude, Context context) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.getMessage();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getGambar() {
        return gambar;
    }

    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
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
}
