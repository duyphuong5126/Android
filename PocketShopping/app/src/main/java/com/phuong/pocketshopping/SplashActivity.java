package com.phuong.pocketshopping;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.phuong.pocketshopping.MyView.CircularProgressBar;

import java.util.Random;

public class SplashActivity extends Activity {
    private AsyncTask<Void, Integer, Void> mLoadDataTask;
    private boolean isLoadDataCompleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window windows = getWindow();
            windows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            windows.setStatusBarColor(Color.parseColor("#0090ED"));
        }
        isLoadDataCompleted = false;
        final CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgress);
        circularProgressBar.setProgressColor(Color.parseColor("#58B269"));
        circularProgressBar.setProgress(0);
        final Random random = new Random();
        mLoadDataTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (!isLoadDataCompleted)
                {
                    int time = random.nextInt(450) + 50;
                    long current = System.currentTimeMillis();
                    while (System.currentTimeMillis() - current < time);
                    publishProgress(1);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                int current = circularProgressBar.getProgress();
                if (current < 100) {
                    if (values != null) {
                        if (values.length > 0) {
                            circularProgressBar.setProgress(current + values[0]);
                        }
                    }
                } else {
                    isLoadDataCompleted = true;
                }
            }
        };
        mLoadDataTask.execute();
    }
}
