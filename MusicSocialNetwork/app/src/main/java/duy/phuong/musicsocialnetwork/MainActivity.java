package duy.phuong.musicsocialnetwork;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import duy.phuong.musicsocialnetwork.Listener.SwipeListener;
import duy.phuong.musicsocialnetwork.View.FragmentAdapter;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout mLayoutIntro = (LinearLayout) findViewById(R.id.layoutIntro);
        final ViewPager mFragmentPager = (ViewPager) findViewById(R.id.fragmentPager);
        mFragmentPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        mFragmentPager.setOnTouchListener(new SwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
            }
        });
        (findViewById(R.id.buttonViewedIntro)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutIntro.setVisibility(View.GONE);
            }
        });
    }
}
