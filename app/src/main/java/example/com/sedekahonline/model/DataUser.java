package example.com.sedekahonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataUser implements Parcelable {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("telepon")
    @Expose
    private String telepon;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("gambar")
    @Expose
    private String gambar;
    @SerializedName("reset")
    @Expose
    private Boolean reset;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.email);
        dest.writeString(this.alamat);
        dest.writeString(this.telepon);
        dest.writeString(this.status);
        dest.writeString(this.gambar);
        dest.writeByte((byte) (reset ? 1 : 0));
    }

    protected DataUser(Parcel in) {
        this.id = in.readInt();
        this.nama = in.readString();
        this.email = in.readString();
        this.alamat = in.readString();
        this.telepon = in.readString();
        this.status = in.readString();
        this.gambar = in.readString();
        this.reset = in.readByte() != 0;
    }

    public static final Creator<DataUser> CREATOR = new Creator<DataUser>() {
        @Override
        public DataUser createFromParcel(Parcel source) {
            return new DataUser(source);
        }

        @Override
        public DataUser[] newArray(int size) {
            return new DataUser[size];
        }
    };
}