package com.example.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;


public class MyJobSchedular extends JobService {
    public static final String TAG = MyJobSchedular.class.getSimpleName();
    private JobExecuter jobExecuter;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        jobExecuter= new JobExecuter(jobParameters,this);
        jobExecuter.execute();
        return true ;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
jobExecuter.cancel(true);
        return false;
    }
}
