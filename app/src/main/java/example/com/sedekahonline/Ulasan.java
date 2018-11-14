package example.com.sedekahonline;

/**
 * Created by DOCOTEL on 2/25/2018.
 */

public class Ulasan {
    String id;
    String nama;
    String gambar;
    String komentar;
    String tanggal;

    public Ulasan( String nama,String gambar, String komentar, String tanggal) {
        this.nama = nama;
        this.gambar = gambar;
        this.komentar = komentar;
        this.tanggal = tanggal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
