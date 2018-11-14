package example.com.sedekahonline.feature.schedule.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import example.com.sedekahonline.R;

/**
 * Created by DELL-PC on 6/13/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String get_your_key = intent.getExtras().getString("extra");
        String name = intent.getExtras().getString("name");
        Intent serviceIntent = new Intent(context, RingtonePlay.class);

        serviceIntent.putExtra("extra", get_your_key);
        serviceIntent.putExtra("name", name);
//        Log.e("Key is", get_your_key);

        context.startService(serviceIntent);



    }
}
