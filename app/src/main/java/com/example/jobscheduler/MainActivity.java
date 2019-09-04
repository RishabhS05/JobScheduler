package com.example.jobscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    public static final int JOB_ID = 1;
    public static final String CHANNEL= "channel_1";
    public static final long INTERVAL= 1L;
    public static final String TAG =MainActivity.class.getSimpleName();
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    Button button ,start ;
     int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         button = findViewById(R.id.hit);
         start = findViewById(R.id.start);
        Log.d(TAG, "onCreate: Job started " );
        jobBuilderDetails();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            jobScheduler.schedule(jobInfo);
//                Toast.makeText(getApplicationContext(),
//                        "started service ", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).
                        registerReceiver(broadcastReceiver,
                                new IntentFilter("JOB_FINISHED"));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobScheduler.cancel(JOB_ID);
                Toast.makeText(getApplicationContext(),
                        "Background Service has been stopped ",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void jobBuilderDetails() {
        ComponentName componentName = new ComponentName(this,
               MyJobSchedular.class);
        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID,componentName);
        if  (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)jobBuilder.setPeriodic(INTERVAL);
        else jobBuilder.setMinimumLatency(INTERVAL);
        jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobBuilder.setPersisted(true);
        jobInfo = jobBuilder.build();
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
    }

    private void sendNotification() {
        Intent intent =new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(
                getApplicationContext(),
                1, intent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            makeNotificationChannel(pendingIntent);
        }

         else if  (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
         NotificationCompat.Builder notification = new NotificationCompat.Builder(this,CHANNEL)
                 .setSmallIcon(R.drawable.service_icon)
                 .setContentTitle("service is running......")
                 .setContentText("sab cahal raha hai " + count++)
                 .setContentIntent(pendingIntent)
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT);
         NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
         notificationManager.notify(1,notification.build());
         }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendNotification();
            Toast.makeText(context, "Job has been finished ", Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(PendingIntent pendingIntent)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, "channel_1");
        builder.setContentTitle("Service is running......");
        builder.setContentText("sab chal raha hai ");
        builder.setSmallIcon(R.drawable.service_icon);
        builder.setTicker("Service is running.. :)");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        NotificationChannel channel = new NotificationChannel("channel_1", "channel_1", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("this is a default channel");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        manager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
