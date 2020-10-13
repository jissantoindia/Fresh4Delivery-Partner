package com.seclob.fbpartner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.seclob.fbpartner.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Notification.DEFAULT_ALL;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "SECLOB";
    public String action="";
    PendingIntent pendingIntent;
    Uri defaultSoundUri;
    String type,datam;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        try {
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            Log.e(TAG, "Json Exception: " + remoteMessage.getData().toString());
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            showNotification(title,message);


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
            showNotification("Error!",e.getMessage());
        }




    }


    private void showNotification(String title, String message) {

        Log.d("SECLOB",title);

        Intent intent = new Intent(this, SplashActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        NotificationCompat.Builder builder =  new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.bit)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setWhen(System.currentTimeMillis())
                .setDefaults(DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "SECLOB";
            // The user-visible name of the channel.
            CharSequence name = "SECLOB";
            // The user-visible description of the channel.
            String description = "Not";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            builder.setChannelId("SECLOB");
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(0, builder.build());
        }
        else {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, builder.build());

        }



    }

}