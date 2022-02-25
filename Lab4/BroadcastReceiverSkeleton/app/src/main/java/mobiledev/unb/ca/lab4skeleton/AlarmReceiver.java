package mobiledev.unb.ca.lab4skeleton;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = "LAB4-AlarmReceiver: ";
    private static final String CHANNEL_ID = "AlarmNoti";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive");

        Intent openMain = new Intent(context,MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(context,0,openMain,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Take a picture!")
                .setContentText("It is time to take a picture, tap to open")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingintent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
        Log.i(TAG,"Sent Notification");

    }

    
}
