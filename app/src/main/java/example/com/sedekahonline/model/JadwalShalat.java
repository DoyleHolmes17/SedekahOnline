package example.com.sedekahonline.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DOCOTEL on 10/5/2017.
 */

public class JadwalShalat {


    private String imsya;
    @SerializedName("Fajr")
    @Expose
    private String fajr;
    @SerializedName("Dhuhr")
    @Expose
    private String dhuhr;
    @SerializedName("Asr")
    @Expose
    private String asr;
    @SerializedName("Maghrib")
    @Expose
    private String maghrib;
    @SerializedName("Isha")
    @Expose
    private String isha;

    public JadwalShalat(String imsya, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        this.imsya = imsya;
        this.fajr = fajr;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.isha = isha;
    }

    public String getImsya() {
        return imsya;
    }

    public void setImsya(String imsya) {
        this.imsya = imsya;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(String dhuhr) {
        this.dhuhr = dhuhr;
    }

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsha() {
        return isha;
    }

    public void setIsha(String isha) {
        this.isha = isha;
    }

}
