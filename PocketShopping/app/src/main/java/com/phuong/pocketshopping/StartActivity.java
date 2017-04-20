package com.phuong.pocketshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#0090ED");
    }

    public static void start(Context source) {
        Intent intent = new Intent(source, StartActivity.class);
        source.startActivity(intent);
    }
}
