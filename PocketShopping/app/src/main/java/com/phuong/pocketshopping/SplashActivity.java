package com.phuong.pocketshopping;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.phuong.pocketshopping.MyView.CircularProgressBar;

import java.util.Random;

public class SplashActivity extends BaseActivity {
    private AsyncTask<Void, Integer, Void> mLoadDataTask;
    private boolean isLoadDataCompleted;
    private enum MODE {
        SPLASHING,
        LOADING
    }
    private MODE mMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMode = MODE.SPLASHING;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isLoadDataCompleted = false;
        final CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgress);
        circularProgressBar.setProgressColor(Color.parseColor("#58B269"));
        circularProgressBar.setProgress(0);
        final Random random = new Random();

        final ImageView imageSplash = (ImageView) findViewById(R.id.imageSplash);
        final LinearLayout layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
        mLoadDataTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (!isLoadDataCompleted)
                {
                    int time = random.nextInt(250) + 50;
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                StartActivity.start(SplashActivity.this);
                finish();
            }
        };
        AsyncTask<Void, Void, Void> prepareTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                circularProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() - time < 3000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                imageSplash.setVisibility(View.GONE);
                layoutLoading.setVisibility(View.VISIBLE);
                circularProgressBar.setVisibility(View.VISIBLE);
                mMode = MODE.LOADING;
                setStatusBarColor();
                mLoadDataTask.execute();
            }
        };
        prepareTask.execute();
    }

    @Override
    protected int getStatusBarColor() {
        return Color.parseColor(mMode == MODE.SPLASHING? "#5BBFD7" : "#0090ED");
    }
}
