package duy.phuong.handnote;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.Label;
import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.DTO.SideMenuItem;
import duy.phuong.handnote.Fragment.BaseFragment;
import duy.phuong.handnote.Fragment.CreateNoteFragment;
import duy.phuong.handnote.Fragment.DrawingFragment;
import duy.phuong.handnote.Fragment.LearningFragment;
import duy.phuong.handnote.Fragment.MainFragment;
import duy.phuong.handnote.Fragment.TemplatesFragment;
import duy.phuong.handnote.Fragment.ViewNoteFragment;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.MainListener;
import duy.phuong.handnote.MyView.RoundImageView;
import duy.phuong.handnote.MyView.SideMenuAdapter;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;
import duy.phuong.handnote.Recognizer.MachineLearning.SOM;
import duy.phuong.handnote.Support.LanguageUtils;
import duy.phuong.handnote.Support.SharedPreferenceUtils;
import duy.phuong.handnote.Support.SupportUtils;

public class MainActivity extends FragmentActivity implements MainListener, ImageButton.OnClickListener, BackPressListener, MainFragment.ShowNoteListener {

    private FragmentManager mFragmentManager;
    private DrawerLayout mMainNavigator;
    private LinearLayout mSideMenu;
    private FrameLayout mLayoutBottomTabs;
    private ImageButton mButtonNavigator, mButtonCreate, mButtonBack;
    private TextView mTvAppTitle, mTvUsername;
    private RoundImageView mAvatar;

    private BackPressListener mBackPressListener;
    private Stack<String> mStack;

    private SOM mGlobalSOM;
    private ArrayList<ClusterLabel> mGlobalMapNames;

    private String mFragmentName = "";

    private Note mCurrentNote;
    private SideMenuAdapter mSideMenuAdapter;
    private ListView mListSideMenu;
    private ArrayList<SideMenuItem> mItems;

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
        mButtonBack = (ImageButton) findViewById(R.id.buttonBack);
        mButtonBack.setOnClickListener(this);
        mTvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        mTvUsername = (TextView) findViewById(R.id.tvUsername);
        mAvatar = (RoundImageView) findViewById(R.id.imageAvatar);
        mListSideMenu = (ListView) findViewById(R.id.listSideMenu);

        mStack = new Stack<>();

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
            mCurrentNote = (Note) savedInstanceState.getSerializable("Note");
        }
        if (mFragmentName == null || mFragmentName.length() == 0) {
            mFragmentName = BaseFragment.MAIN_FRAGMENT;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mItems = new ArrayList<>();
        mItems.add(new SideMenuItem(R.mipmap.ic_android_black_24dp, R.mipmap.ic_android_red_24dp, getString(R.string.training_en), false));
        mItems.add(new SideMenuItem(R.mipmap.ic_format_list_bulleted_black_24dp, R.mipmap.ic_list_red_24dp, getString(R.string.templates_fragment_en), false));
        mSideMenuAdapter = new SideMenuAdapter(mItems, MainActivity.this, R.layout.layout_side_menu);
        mListSideMenu.setAdapter(mSideMenuAdapter);
        mSideMenuAdapter.notifyDataSetChanged();
        mListSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                SideMenuItem item = mItems.get(position);
                switch (item.mTitle) {
                    case "Training":
                        final Dialog dialog = new Dialog(MainActivity.this);
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
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.show();
                        break;
                    case "Templates":
                        showFragment(BaseFragment.TEMPLATES_FRAGMENT);
                        break;
                    default:
                        for (int i = 0; i < mItems.size(); i++) {
                            mItems.get(i).mFocused = i == position;
                        }
                        mSideMenuAdapter.notifyDataSetChanged();
                        break;
                }
                toggleMainNavigator(false);
            }
        });
        Bitmap bitmap = SupportUtils.getAvatar();
        if (bitmap != null) {
            mAvatar.setImageBitmap(bitmap);
        }
        mTvUsername.setText(SharedPreferenceUtils.getCurrentName());
        if (mFragmentManager.getBackStackEntryCount() <= 0) {
            this.showFragment(mFragmentName);
        } else {
            List<Fragment> list = mFragmentManager.getFragments();
            for (Fragment fragment : list) {
                BaseFragment baseFragment = (BaseFragment) fragment;
                if (baseFragment != null) {
                    baseFragment.setListener(this);
                    if (baseFragment.fragmentIdentify().equals(BaseFragment.MAIN_FRAGMENT)) {
                        MainFragment mainFragment = (MainFragment) baseFragment;
                        mainFragment.setShowNoteListener(this);
                    }

                    if (baseFragment.fragmentIdentify().equals(BaseFragment.VIEW_NOTE_FRAGMENT)) {
                        ViewNoteFragment viewNoteFragment = (ViewNoteFragment) baseFragment;
                        viewNoteFragment.setNote(mCurrentNote);
                    }
                }
            }

            if (mStack.isEmpty()) {
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        BaseFragment baseFragment = (BaseFragment) list.get(i);
                        if (baseFragment != null) {
                            mStack.push(baseFragment.fragmentIdentify());
                        }
                    }
                }
            }
        }
        checkScreen();
    }

    private void initMapNames() {
        mGlobalMapNames = new ArrayList<>();
        try {
            String data = SupportUtils.getStringData("Trained", "MapNames.txt");
            if (data.length() == 0) {
                data = SupportUtils.getStringResource(this, R.raw.map_names);
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
                mapNames += label.getLabel() + ":" + clusterLabel.getLabelPercentage(label) + "-" + label.getCount() + ";";
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
                data = SupportUtils.getStringResource(this, R.raw.som);
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
        List<Fragment> listFragments = mFragmentManager.getFragments();
        for (Fragment fragment : listFragments) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            if (baseFragment != null) {
                if (baseFragment.fragmentIdentify().equals(BaseFragment.VIEW_NOTE_FRAGMENT)) {
                    ViewNoteFragment viewNoteFragment = (ViewNoteFragment) baseFragment;
                    Note note = viewNoteFragment.getNote();
                    if (note != null) {
                        outState.putSerializable("Note", note);
                    }
                }
            }
        }
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
                MainFragment mainFragment = new MainFragment();
                mainFragment.setShowNoteListener(this);
                baseFragment = mainFragment;
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

            case BaseFragment.VIEW_NOTE_FRAGMENT:
                ViewNoteFragment viewNoteFragment = new ViewNoteFragment();
                mBackPressListener = viewNoteFragment;
                baseFragment = viewNoteFragment;
                break;

            case BaseFragment.TEMPLATES_FRAGMENT:
                baseFragment = new TemplatesFragment();
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
        } else {
            if (mFragmentName != null) {
                name = mFragmentName;
            }
        }
        switch (name) {
            case BaseFragment.DRAWING_FRAGMENT:
                for (SideMenuItem item : mItems) {
                    item.mFocused = item.mTitle.equals(getString(R.string.training_en));
                }
                this.toggleMainBottomTabs(false);
                break;
            case BaseFragment.LEARNING_FRAGMENT:
                for (SideMenuItem item : mItems) {
                    item.mFocused = item.mTitle.equals(getString(R.string.training_en));
                }
                this.toggleMainBottomTabs(false);
                break;
            case BaseFragment.CREATE_NOTE_FRAGMENT:
            case BaseFragment.VIEW_NOTE_FRAGMENT:
                this.toggleMainBottomTabs(false);
                break;
            case BaseFragment.TEMPLATES_FRAGMENT:
                for (SideMenuItem item : mItems) {
                    item.mFocused = item.mTitle.equals(getString(R.string.templates_fragment_en));
                }
                this.toggleMainBottomTabs(true);
                break;
            default:
                for (SideMenuItem item : mItems) {
                    item.mFocused = false;
                }
                this.toggleMainBottomTabs(true);
                break;
        }

        mSideMenuAdapter.notifyDataSetChanged();

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
    public Note getCurrentNote() {
        return mCurrentNote;
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
    protected void onStop() {
        onSaveInstanceState(new Bundle());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showNote(Note note) {
        mCurrentNote = note;
        showFragment(BaseFragment.VIEW_NOTE_FRAGMENT);
    }
}
