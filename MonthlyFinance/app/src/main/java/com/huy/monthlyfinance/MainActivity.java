package com.huy.monthlyfinance;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huy.monthlyfinance.Fragments.BaseFragment;
import com.huy.monthlyfinance.Fragments.BudgetFragment;
import com.huy.monthlyfinance.Fragments.ExpenseManagerFragment;
import com.huy.monthlyfinance.Fragments.OverViewFragment;
import com.huy.monthlyfinance.Fragments.RecommendationFragment;
import com.huy.monthlyfinance.Listener.MainListener;
import com.huy.monthlyfinance.Listener.NavigationListener;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.SideMenuItem;
import com.huy.monthlyfinance.MyView.RoundImageView;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

public class MainActivity extends Activity implements View.OnClickListener, MainListener, NavigationListener {
    private DrawerLayout mDrawer;
    private LinearLayout mLayoutSideMenu;
    private TreeMap<String, BaseFragment> mFragments;
    private FragmentManager mManager;
    private ArrayList<SideMenuItem> mMenuItems;
    private BasicAdapter<SideMenuItem> mSideMenuAdapter;
    private FrameLayout mLayoutTopSideMenu;
    private RoundImageView mImageAvatar;
    private LinearLayout mLayoutProgress;

    private static final int SELECT_IMAGE = 1;
    private Uri mCapturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();
        TextView textUser = (TextView) findViewById(R.id.textUser);
        textUser.setText(PreferencesUtils.getString(PreferencesUtils.CURRENT_EMAIL, resources.getString(R.string.welcome)));

        mDrawer = (DrawerLayout) findViewById(R.id.mainLayout);
        mLayoutSideMenu = (LinearLayout) findViewById(R.id.layoutSideMenu);
        mLayoutTopSideMenu = (FrameLayout) findViewById(R.id.layoutTopSideMenu);
        mImageAvatar = (RoundImageView) findViewById(R.id.imageAvatar);

        mLayoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);

        MainApplication mainApplication = MainApplication.getInstance();
        mFragments = new TreeMap<>();
        OverViewFragment overViewFragment = new OverViewFragment();
        overViewFragment.setListener(this);
        this.addFragment(overViewFragment);
        mainApplication.registerDataListener(overViewFragment);
        ExpenseManagerFragment expenseManagerFragment = new ExpenseManagerFragment();
        expenseManagerFragment.setListener(this);
        expenseManagerFragment.setNavListener(this);
        this.addFragment(expenseManagerFragment);
        mainApplication.registerDataListener(expenseManagerFragment);
        BudgetFragment budgetFragment = new BudgetFragment();
        budgetFragment.setListener(this);
        budgetFragment.setNavListener(this);
        this.addFragment(budgetFragment);
        mainApplication.registerDataListener(budgetFragment);
        RecommendationFragment recommendationFragment = new RecommendationFragment();
        recommendationFragment.setListener(this);
        recommendationFragment.setNavListener(this);
        this.addFragment(recommendationFragment);
        mainApplication.registerDataListener(recommendationFragment);

        ListView mSideMenu = (ListView) findViewById(R.id.sideMenu);
        mMenuItems = new ArrayList<>();
        mMenuItems.add(new SideMenuItem(R.mipmap.ic_cash_24dp, getString(R.string.budget), ""));
        mMenuItems.add(new SideMenuItem(R.mipmap.ic_expense_24dp, getString(R.string.expenses), ""));
        mMenuItems.add(new SideMenuItem(R.mipmap.ic_report_24dp, getString(R.string.recommendation), ""));
        mMenuItems.add(new SideMenuItem(R.mipmap.ic_chart_24dp, getString(R.string.statistic), ""));
        mSideMenuAdapter = new BasicAdapter<>(mMenuItems, R.layout.item_side_menu, getLayoutInflater());
        mSideMenu.setAdapter(mSideMenuAdapter);
        mSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int pos = 0; pos < mMenuItems.size(); pos++) {
                    mMenuItems.get(pos).setFocused(pos == i);
                }
                mSideMenuAdapter.notifyDataSetChanged();
                if (mMenuItems.get(i).getTextName().equals(getString(R.string.expenses))) {
                    showFragment(ExpenseManagerFragment.class, null);
                }
                if (mMenuItems.get(i).getTextName().equals(getString(R.string.budget))) {
                    showFragment(BudgetFragment.class, null);
                }
                if (mMenuItems.get(i).getTextName().equals(getString(R.string.recommendation))) {
                    final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    @SuppressLint("InflateParams")
                    View view1 = getLayoutInflater().inflate(R.layout.layout_supp_conf, null);
                    dialog.setContentView(view1);
                    view1.findViewById(R.id.cont_and_enter).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isOpenSupportForm", true);
                            showFragment(RecommendationFragment.class, bundle);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                mDrawer.closeDrawer(mLayoutSideMenu);
            }
        });

        findViewById(R.id.buttonEditAvatar).setOnClickListener(this);

        String path = PreferencesUtils.getString(PreferencesUtils.AVATAR, "");
        if (!path.isEmpty()) {
            Bitmap mAvatar;
            mAvatar = BitmapFactory.decodeFile(path);
            if (mAvatar != null) {
                mImageAvatar.setImageBitmap(mAvatar);
            }
        }

        mManager = getFragmentManager();
        mManager.beginTransaction().add(R.id.layoutFragmentsContainer, overViewFragment, overViewFragment.getClass().getName()).commit();
        BaseFragment.setCurrent(overViewFragment.getClass().getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    boolean isFromCamera;
                    if (data == null) {
                        isFromCamera = true;
                    } else {
                        if (data.getAction() == null) {
                            isFromCamera = true;
                        } else {
                            isFromCamera = data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    String path;
                    if (isFromCamera) {
                        path = SupportUtils.getPath(mCapturedImage, MainActivity.this);
                    } else {
                        path = SupportUtils.getPath(data.getData(), MainActivity.this);
                    }
                    if (path != null) {
                        Bitmap mAvatar;
                        mAvatar = BitmapFactory.decodeFile(path);
                        if (mAvatar != null) {
                            PreferencesUtils.setValue(PreferencesUtils.AVATAR, path);
                            mImageAvatar.setImageBitmap(mAvatar);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void addFragment(BaseFragment fragment) {
        if (mFragments != null) {
            mFragments.put(fragment.getClass().getName(), fragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEditAvatar:
                intentLoadImage();
                break;
            default:
                break;
        }
    }

    @Override
    public void toggleSideMenu(boolean isOpen) {
        if (isOpen) {
            mDrawer.openDrawer(mLayoutSideMenu, true);
        } else {
            mDrawer.closeDrawer(mLayoutSideMenu, true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBarColor(int color) {
        if (SupportUtils.checkLollipopOrAbove()) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void showFragment(Class c, Bundle data) {
        BaseFragment fragment = mFragments.get(c.getName());
        if (!c.getName().equals(BaseFragment.getCurrent())) {
            fragment.setArguments(data);
        }
        mManager.beginTransaction().replace(R.id.layoutFragmentsContainer, fragment, fragment.getClass().getName()).commit();
        BaseFragment.setCurrent(c.getName());
    }

    @Override
    public void changeSideMenuColor(int color) {
        mLayoutTopSideMenu.setBackgroundColor(color);
        mImageAvatar.setColor(color);
    }

    @Override
    public void toggleProgress(boolean isShow) {
        if (mLayoutProgress != null) {
            mLayoutProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public ArrayList<Object> getData(Class c) {
        return null;
    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void navBack() {
        for (int pos = 0; pos < mMenuItems.size(); pos++) {
            mMenuItems.get(pos).setFocused(false);
        }
        mSideMenuAdapter.notifyDataSetChanged();
        OverViewFragment overViewFragment = (OverViewFragment) mFragments.get(OverViewFragment.class.getName());
        mManager.beginTransaction().replace(R.id.layoutFragmentsContainer, overViewFragment, overViewFragment.getClass().getName()).commit();
        BaseFragment.setCurrent(OverViewFragment.class.getName());
    }

    @Override
    public void onBackPressed() {
        if (BaseFragment.getCurrent() != null) {
            if (BaseFragment.getCurrent().equals(OverViewFragment.class.getName())) {
                super.onBackPressed();
            } else {
                navBack();
            }
        }
    }

    private void intentLoadImage() {
        Intent intentPick = new Intent();
        intentPick.setType("image/*");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Captured" + File.separator);
        if (!root.exists()) {
            root.mkdirs();
        } else {
            String name = "Captured_" + System.nanoTime();
            File dir = new File(root, name);
            mCapturedImage = Uri.fromFile(dir);

            Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImage);

            Intent intentChooser = Intent.createChooser(intentPick, "Select a source");
            intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentTakePhoto});
            startActivityForResult(intentChooser, SELECT_IMAGE);
        }
    }
}
