package leonproject.com.dailyselfie;

/**
 * Created by fudan on 3/22/15.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNM;
        mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyDailySelfieActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("It's time for another selfie!")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentIntent(contentIntent)
                .build();

        mNM.notify(1,notification);
    }
}

