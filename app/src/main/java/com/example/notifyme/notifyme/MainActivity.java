package com.example.notifyme.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String PRIMARY_NOTIFICATION_CHANNEL = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;
    public static final String ACTION_UPDATE_NOTIFICATION = "com.example.notifyme.ACTION_UPDATE_NOTIFICATION";

    private Button button_notify;
    private Button button_cancel;
    private Button button_update;
    private NotificationManager nm;
    private NotificationReceiver receiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        button_update = findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotifications();
            }
        });

        button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        createNotificationChannel();
        setNotificationButtonState(true, false, false);

        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    public void cancelNotification() {
        nm.cancel(NOTIFICATION_ID);

        setNotificationButtonState(true, false, false);
    }

    public void updateNotifications(){
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));

        nm.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, false, true);
    }

    private void sendNotification(){
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePI = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                updateIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update", updatePI);

        nm.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, true, true);
    }

    private NotificationCompat.Builder getNotificationBuilder(){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notifyPI = PendingIntent.getActivity(this, NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(this, PRIMARY_NOTIFICATION_CHANNEL)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text")
                .setSmallIcon(R.drawable.ic_notify)
                .setContentIntent(notifyPI)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    public void createNotificationChannel(){
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // create a notification channel
            NotificationChannel channel = new NotificationChannel(PRIMARY_NOTIFICATION_CHANNEL,
                    "Mascot Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setDescription("Notification from the mascot!");
            nm.createNotificationChannel(channel);
        }
    }

    public void setNotificationButtonState(boolean isNotifyEn, boolean isUpdateEn, boolean isCancelEn){
        button_notify.setEnabled(isNotifyEn);
        button_update.setEnabled(isUpdateEn);
        button_cancel.setEnabled(isCancelEn);
    }

    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
