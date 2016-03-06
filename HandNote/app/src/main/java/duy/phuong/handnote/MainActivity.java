package duy.phuong.handnote;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Stack;

import duy.phuong.handnote.Fragment.BaseFragment;
import duy.phuong.handnote.Fragment.CreateNoteFragment;
import duy.phuong.handnote.Fragment.DrawingFragment;
import duy.phuong.handnote.Fragment.LearningFragment;
import duy.phuong.handnote.Fragment.MainFragment;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.MainListener;

public class MainActivity extends FragmentActivity implements MainListener, ImageButton.OnClickListener, BackPressListener {

    private FragmentManager mFragmentManager;
    private DrawerLayout mMainNavigator;
    private LinearLayout mSideMenu;
    private FrameLayout mLayoutBottomTabs;
    private ImageButton mButtonNavigator, mButtonCreate, mButtonBack;
    private LinearLayout mButtonTraining;

    private BackPressListener mBackPressListener;
    private Stack<String> mStack;

    private boolean mCanExit;

    private String mCurrentUser = "Admin";

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
        mButtonTraining = (LinearLayout) findViewById(R.id.buttonTraining);
        mButtonTraining.setOnClickListener(this);
        mButtonBack = (ImageButton) findViewById(R.id.buttonBack);
        mButtonBack.setOnClickListener(this);

        mCanExit = false;

        mStack = new Stack<>();

        if ("Admin".equals(mCurrentUser)) {
            mButtonTraining.setVisibility(View.VISIBLE);
        }

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
        BaseFragment baseFragment = null;
        switch (name) {
            case BaseFragment.MAIN_FRAGMENT:
                baseFragment = new MainFragment();
                break;

            case BaseFragment.DRAWING_FRAGMENT:
                baseFragment = new DrawingFragment();
                mBackPressListener = (DrawingFragment) baseFragment;
                break;

            case BaseFragment.LEARNING_FRAGMENT:
                baseFragment = new LearningFragment();
                break;

            case BaseFragment.CREATE_NOTE_FRAGMENT:
                baseFragment = new CreateNoteFragment();
                break;

            default:
                break;
        }

        if (baseFragment != null) {
            baseFragment.setListener(this);
            mStack.push(name);
            addFragment(baseFragment, baseFragment.fragmentIdentify());
            mCanExit = false;
        }

        checkScreen();
    }

    private void checkScreen() {
        if (!mStack.isEmpty()) {
            String name = mStack.peek();
            switch (name) {
                case BaseFragment.DRAWING_FRAGMENT:
                case BaseFragment.LEARNING_FRAGMENT:
                case BaseFragment.CREATE_NOTE_FRAGMENT:
                    this.toggleMainBottomTabs(false);
                    break;
                default:
                    this.toggleMainBottomTabs(true);
                    break;
            }

            if (mStack.size() > 1) {
                mButtonBack.setVisibility(View.VISIBLE);
                mButtonNavigator.setVisibility(View.GONE);
            } else {
                mButtonBack.setVisibility(View.GONE);
                mButtonNavigator.setVisibility(View.VISIBLE);
            }
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
        mLayoutBottomTabs.setVisibility((show) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMainNavigator:
                this.toggleMainNavigator(true);
                break;
            case R.id.buttonCreate:
                showFragment(BaseFragment.CREATE_NOTE_FRAGMENT);
                break;
            case R.id.buttonTraining:
                showFragment(BaseFragment.DRAWING_FRAGMENT);
                mMainNavigator.closeDrawer(mSideMenu);
                break;
            case R.id.buttonBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressListener == null || !mBackPressListener.doBack()) {
            if (!doBack()) {
                super.onBackPressed();
            } else {
                finish();
            }
            if (!mStack.isEmpty()) {
                mStack.pop();
            }
            checkScreen();
        }
    }

    @Override
    public boolean doBack() {
        return (mFragmentManager.getBackStackEntryCount() == 1);
    }

    private void addFragment(Fragment fragment, String name) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.layoutFragmentContainer, fragment, name);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clearBackStack() {
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
            mFragmentManager.popBackStack();
        }
    }
}
