package example.com.sedekahonline.model;

public class JadwalShalatSwitch {

    private boolean imsya;
    private boolean fajr;
    private boolean dhuhr;
    private boolean asr;
    private boolean maghrib;
    private boolean isha;

    public JadwalShalatSwitch() {
    }

    public JadwalShalatSwitch(boolean imsya, boolean fajr, boolean dhuhr, boolean asr, boolean maghrib, boolean isha) {
        this.imsya = imsya;
        this.fajr = fajr;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.isha = isha;
    }

    public boolean isImsya() {

        return imsya;
    }

    public void setImsya(boolean imsya) {
        this.imsya = imsya;
    }

    public boolean isFajr() {
        return fajr;
    }

    public void setFajr(boolean fajr) {
        this.fajr = fajr;
    }

    public boolean isDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(boolean dhuhr) {
        this.dhuhr = dhuhr;
    }

    public boolean isAsr() {
        return asr;
    }

    public void setAsr(boolean asr) {
        this.asr = asr;
    }

    public boolean isMaghrib() {
        return maghrib;
    }

    public void setMaghrib(boolean maghrib) {
        this.maghrib = maghrib;
    }

    public boolean isIsha() {
        return isha;
    }

    public void setIsha(boolean isha) {
        this.isha = isha;
    }
}
