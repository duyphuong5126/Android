package duy.phuong.handnote;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.Label;
import duy.phuong.handnote.Fragment.BaseFragment;
import duy.phuong.handnote.Fragment.CreateNoteFragment;
import duy.phuong.handnote.Fragment.DrawingFragment;
import duy.phuong.handnote.Fragment.LearningFragment;
import duy.phuong.handnote.Fragment.MainFragment;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.MainListener;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.Input;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.SOM;
import duy.phuong.handnote.Support.LanguageUtils;
import duy.phuong.handnote.Support.SupportUtils;

public class MainActivity extends FragmentActivity implements MainListener, ImageButton.OnClickListener, BackPressListener {

    private FragmentManager mFragmentManager;
    private DrawerLayout mMainNavigator;
    private LinearLayout mSideMenu;
    private FrameLayout mLayoutBottomTabs;
    private ImageButton mButtonNavigator, mButtonCreate, mButtonBack;
    private LinearLayout mButtonTraining;
    private TextView mTvAppTitle;

    private BackPressListener mBackPressListener;
    private Stack<String> mStack;

    private SOM mGlobalSOM;
    private ArrayList<ClusterLabel> mGlobalMapNames;

    private String mCurrentUser = "Admin";
    private String mFragmentName = "";

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
        mTvAppTitle = (TextView) findViewById(R.id.tvAppTitle);

        mStack = new Stack<>();

        if ("Admin".equals(mCurrentUser)) {
            mButtonTraining.setVisibility(View.VISIBLE);
        }

        mFragmentManager = getSupportFragmentManager();

        initMapNames();
        initSOM();
        if (savedInstanceState != null) {
            mFragmentName = savedInstanceState.getString("Fragment");
            ArrayList<String> list = savedInstanceState.getStringArrayList("Stack");
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    mStack.push(list.get(i));
                }
            }
        }
        if (mFragmentName == null || mFragmentName.length() == 0) {
            mFragmentName = BaseFragment.MAIN_FRAGMENT;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkScreen();
        if (mFragmentManager.getBackStackEntryCount() <= 0) {
            this.showFragment(mFragmentName);
        }
    }

    private void initMapNames() {
        mGlobalMapNames = new ArrayList<>();
        try {
            String data = SupportUtils.getStringData("Trained", "MapNames.txt");
            if (data.length() == 0) {
                data = SupportUtils.getStringResource(this, R.raw.map_names_ver_1);
                SupportUtils.writeFile(data, "Trained", "MapNames.txt");
            }
            StringTokenizer tokenizer = new StringTokenizer(data, "\r\n");
            ArrayList<String> listTokens = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token != null) {
                    if (token.length() != 0) {
                        listTokens.add(token);
                    }
                }
            }

            if (!listTokens.isEmpty()) {
                for (int i = 0; i < listTokens.size(); i++) {
                    ClusterLabel clusterLabel = new ClusterLabel();
                    StringTokenizer tokenizer1 = new StringTokenizer(listTokens.get(i), ";");
                    ArrayList<String> strings = new ArrayList<>();
                    while (tokenizer1.hasMoreTokens()) {
                        String string = tokenizer1.nextToken();
                        if (!"".equals(string)) {
                            strings.add(string);
                        }
                    }

                    if (!strings.isEmpty()) {
                        for (String string : strings) {
                            String[] s = string.split(":");
                            if (s.length == 2) {
                                int count = Integer.valueOf(s[1]);
                                if (count > 0) {
                                    clusterLabel.addNewLabel(new Label(s[0], count));
                                }
                            }
                        }
                    }

                    mGlobalMapNames.add(clusterLabel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int sum = 0;
        for (ClusterLabel clusterLabel : mGlobalMapNames) {
            sum += clusterLabel.getTotal();
            String mapNames = "";
            for (Label label : clusterLabel.getListLabel()) {
                mapNames += label.getLabel() + ":" + clusterLabel.getLabelPercentage(label) + ";";
            }

            Log.d("List names", mapNames);
        }
        Log.d("Sum: ", "" + sum);
    }

    @Override
    public void initSOM() {
        try {
            String data = SupportUtils.getStringData("Trained", "SOM.txt");
            if (data.length() == 0) {
                data = SupportUtils.getStringResource(this, R.raw.som_ver_1);
                SupportUtils.writeFile(data, "Trained", "SOM.txt");
            }
            StringTokenizer tokenizer = new StringTokenizer(data, "|");
            ArrayList<String> listTokens = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token != null && !"".equals(token)) {
                    listTokens.add(token);
                }
            }

            if (!listTokens.isEmpty()) {
                double[][] weightMatrix = new double[SOM.NUMBERS_OF_CLUSTER][Input.VECTOR_DIMENSIONS];
                for (int i = 0; i < listTokens.size(); i++) {
                    String stringWeight = listTokens.get(i);
                    StringTokenizer stringTokenizer = new StringTokenizer(stringWeight, ";");
                    if (stringTokenizer.countTokens() == Input.VECTOR_DIMENSIONS) {
                        int index = 0;
                        while (stringTokenizer.hasMoreTokens()) {
                            weightMatrix[i][index] = Double.valueOf(stringTokenizer.nextToken());
                            index++;
                        }
                    }
                }

                mGlobalSOM = new SOM(weightMatrix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Fragment", mFragmentName);
        ArrayList<String> list = new ArrayList<>();
        while (!mStack.isEmpty()) {
            list.add(mStack.pop());
        }
        outState.putStringArrayList("Stack", list);
        super.onSaveInstanceState(outState);
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
                mBackPressListener = (CreateNoteFragment) baseFragment;
                break;

            default:
                break;
        }

        if (baseFragment != null) {
            baseFragment.setListener(this);
            mStack.push(name);
            addFragment(baseFragment, baseFragment.fragmentIdentify());
            mFragmentName = name;
        }

        checkScreen();
    }

    private void checkScreen() {
        String name = "";
        if (!mStack.isEmpty()) {
            name = mStack.peek();
        }
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

        mTvAppTitle.setText(LanguageUtils.getFragmentTitle(name, this));

        if (mStack.size() > 1) {
            mButtonBack.setVisibility(View.VISIBLE);
            mButtonNavigator.setVisibility(View.GONE);
        } else {
            mButtonBack.setVisibility(View.GONE);
            mButtonNavigator.setVisibility(View.VISIBLE);
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
    public SOM getGlobalSOM() {
        return mGlobalSOM;
    }

    @Override
    public ArrayList<ClusterLabel> getMapNames() {
        return mGlobalMapNames;
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
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.layout_prompt_1);
                dialog.setTitle("Enter password");
                final EditText editText = (EditText) dialog.findViewById(R.id.edtPrompt);
                Button button = (Button) dialog.findViewById(R.id.buttonConfirm);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = editText.getText().toString();
                        if ("5126".equals(password)) {
                            showFragment(BaseFragment.DRAWING_FRAGMENT);
                            mMainNavigator.closeDrawer(mSideMenu);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
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

    @Override
    protected void onDestroy() {
        mGlobalMapNames.clear();
        mGlobalMapNames = null;
        mGlobalSOM = null;
        onSaveInstanceState(new Bundle());
        super.onDestroy();
    }
}
