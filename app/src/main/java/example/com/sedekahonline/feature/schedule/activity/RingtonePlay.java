package example.com.sedekahonline.feature.schedule.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import example.com.sedekahonline.R;


public class RingtonePlay extends Service {

    MediaPlayer media_song;
    boolean isRunning;
    int startId;
    String alarmSound;
    IBinder mBinder = new LocalBinder();
    private SharedPreferences prefs;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LocalService", "Received start id " + startId + ": " + intent);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String state = intent.getExtras().getString("extra");
        String name = intent.getExtras().getString("name");

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;

            case "alarm off":
                startId = 0;
                break;

            default:
                startId = 0;
                break;
        }

        // music is not playing and alarm is on play music
        if (!this.isRunning && startId == 1) {
            int music = 0;
            alarmSound = prefs.getString("alarmSound", "Adzan");
            if (alarmSound.equals("Adzan")) {
                if (name.equalsIgnoreCase("fajr")) {
                    music = R.raw.adzan_shubuh;
                } else {
                    music = R.raw.adzan;
                }
            }else {
                music= R.raw.beduk;
            }

            media_song = MediaPlayer.create(this, music);
            if (!name.equalsIgnoreCase("imsya") || !alarmSound.equals("Tanpa Suara")) {
                media_song.start();
            }
            media_song.setLooping(false);

            this.isRunning = true;
            this.startId = 0;

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent intent_mainActivity = new Intent(this.getApplicationContext(), ScheduleActivity.class);
            intent_mainActivity.putExtra("stop", "stop");
            intent_mainActivity.putExtra("name", name + "");
            intent_mainActivity.setAction("stop-"+name);
            intent_mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent_MainActivity = PendingIntent.getActivity(this, 0, intent_mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Waktunya salat, Adzan sudah berkumandang")
                    .setContentText("Tekan untuk menghentikan adzan")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent_MainActivity)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            notificationManager.notify(0, notification);

        }
        // music is playing and alarm off is pressed turn off the music
        else if (this.isRunning && startId == 0) {
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        }

        // these are just to bug-proof the app
        // music is not playing and alarm is off do nothing
        else if (!this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;
        }

        // music is playing and alarm is on do nothing
        else if (this.isRunning && startId == 1) {
            this.isRunning = true;
            this.startId = 1;
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RingtonePlay getServerInstance() {
            return RingtonePlay.this;
        }
    }
}
