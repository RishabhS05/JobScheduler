package com.example.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

public class JobExecuter extends AsyncTask<Void,Integer,String> {
     int counter =0;
     private JobParameters params;
    private WeakReference<JobService> jobServiceReference;
   // JobService jobService;

    public JobExecuter(JobParameters params, JobService jobService) {
        this.params = params;
       jobServiceReference = new WeakReference<>(jobService);
    }

    public static final String TAG = JobExecuter.class.getSimpleName();
    /**
 * An asynchronous task is defined by a computation that runs
 * on a background thread and whose result is published on the UI thread.
 * An asynchronous task is defined by 3 generic types, called
 * Params, Progress and Result
 * */
    @Override
    protected String doInBackground(Void... voids) {

//        Log.d(TAG, "doInBackground: "+ "task"+ counter +" is finshied");
        for (int i=0 ;i<10;i++)
            Log.d(TAG, "loop "+ i);
        return "Background running task  is finished" ;
    }

        @Override
        protected void onPostExecute(String s) {
        counter++ ;
        JobService jobService = jobServiceReference.get();
        jobService.jobFinished(params,true);
        Log.d(TAG, "onPostExecute: " +s);
        Intent intent = new Intent("JOB_FINISHED");
        intent.putExtra("result", s);
        Toast.makeText(jobService.getApplicationContext(), "started job " + counter
                , Toast.LENGTH_SHORT).show();
        LocalBroadcastManager.getInstance(jobService.getApplicationContext()).sendBroadcast(intent);

    }

}
