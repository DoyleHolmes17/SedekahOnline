package example.com.sedekahonline.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DOCOTEL on 10/5/2017.
 */

public class Location {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nama_kota")
    @Expose
    private String nama_kota;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_kota() {
        return nama_kota;
    }

    public void setNama_kota(String nama_kota) {
        this.nama_kota = nama_kota;
    }
}
