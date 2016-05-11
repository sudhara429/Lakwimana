package com.lakwimana.lakwimana;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Sudhara on 5/7/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
    String message=data.getString("message");


        sendNotification(message);
    }
    private void sendNotification(String message){
        Intent intent =new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestcode=0;//your request code here
        PendingIntent pendingIntent= PendingIntent.getActivity(this,requestcode,intent,PendingIntent.FLAG_ONE_SHOT);
        //setup nortification
        //sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //build nortification
        NotificationCompat.Builder noBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("My message abcd")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,noBuilder.build());//0 is Id of Notification

    }
}
