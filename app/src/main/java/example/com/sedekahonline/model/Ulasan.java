package example.com.sedekahonline.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DOCOTEL on 2/25/2018.
 */

public class Ulasan {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nama_user")
    @Expose
    private String nama_user;
    @SerializedName("id_sedekah")
    @Expose
    private String idSedekah;
    @SerializedName("tanggal_ulasan")
    @Expose
    private String tanggalUlasan;
    @SerializedName("ulasan")
    @Expose
    private String ulasan;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getIdSedekah() {
        return idSedekah;
    }

    public void setIdSedekah(String idSedekah) {
        this.idSedekah = idSedekah;
    }

    public String getTanggalUlasan() {
        return tanggalUlasan;
    }

    public void setTanggalUlasan(String tanggalUlasan) {
        this.tanggalUlasan = tanggalUlasan;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void setUlasan(String ulasan) {
        this.ulasan = ulasan;
    }

    public String getCreatedAt() {
        return createdAt;
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
}
