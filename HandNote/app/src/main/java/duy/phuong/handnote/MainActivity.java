package duy.phuong.handnote;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import duy.phuong.handnote.Fragment.BaseFragment;
import duy.phuong.handnote.Fragment.TrainingFragment;
import duy.phuong.handnote.Fragment.MainFragment;
import duy.phuong.handnote.Listener.MainListener;

public class MainActivity extends FragmentActivity implements MainListener, ImageButton.OnClickListener {

    private FragmentManager mFragmentManager;
    private DrawerLayout mMainNavigator;
    private LinearLayout mSideMenu;
    private FrameLayout mLayoutBottomTabs;
    private ImageButton mButtonNavigator, mButtonCreate;
    private Button mButtonTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutBottomTabs = (FrameLayout) findViewById(R.id.layoutTabsBottom);
        mSideMenu = (LinearLayout) findViewById(R.id.layoutSideMenu);
        mMainNavigator = (DrawerLayout) findViewById(R.id.layoutMainNavigator);
        mButtonNavigator = (ImageButton) findViewById(R.id.buttonMainNavigator);
        mButtonNavigator.setOnClickListener(this);
        mButtonCreate = (ImageButton) findViewById(R.id.buttonCreate);
        mButtonCreate.setOnClickListener(this);
        mButtonTraining = (Button) findViewById(R.id.buttonTraining);
        mButtonTraining.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();

        this.showFragment(BaseFragment.MAIN_FRAGMENT);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFragment(String name) {
        this.clearBackStack();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (name) {
            case BaseFragment.TRAINING_FRAGMENT:
                TrainingFragment trainingFragment = new TrainingFragment();
                trainingFragment.setListener(this);
                transaction.replace(R.id.layoutFragmentContainer, trainingFragment, trainingFragment.fragmentIdentify());
                transaction.commit();
                break;

            case BaseFragment.MAIN_FRAGMENT:
                MainFragment mainFragment = new MainFragment();
                mainFragment.setListener(this);
                transaction.replace(R.id.layoutFragmentContainer, mainFragment, mainFragment.fragmentIdentify());
                transaction.commit();
                break;
            default:
                break;
        }
        switch (name) {
            case BaseFragment.TRAINING_FRAGMENT:
                this.toggleMainBottomTabs(false);
                break;
            default:
                this.toggleMainBottomTabs(true);
                break;
        }
    }

    @Override
    public void toggleMainNavigator(boolean show) {
        if (show) {
            mMainNavigator.openDrawer(mSideMenu);
            return;
        }
        mMainNavigator.closeDrawer(mSideMenu);
    }

    @Override
    public void toggleMainBottomTabs(boolean show) {
        mLayoutBottomTabs.setVisibility((show)?View.VISIBLE:View.GONE);
    }

    private void clearBackStack() {
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMainNavigator:
                this.toggleMainNavigator(true);
                break;
            case R.id.buttonCreate:
                break;
            case R.id.buttonTraining:
                showFragment(BaseFragment.TRAINING_FRAGMENT);
                break;
        }
    }
}
