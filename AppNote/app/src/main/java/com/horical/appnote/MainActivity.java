package com.horical.appnote;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.Background.MediaService;
import com.horical.appnote.BaseActivity.BaseActivity;
import com.horical.appnote.DTO.CalendarObject;
import com.horical.appnote.DTO.FileDTO.FileData;
import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;
import com.horical.appnote.Interfaces.EditNoteListener;
import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.DAO.NoteDataDAO;
import com.horical.appnote.LocalStorage.DAO.NoteReminderDAO;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Fragments.BaseFragment;
import com.horical.appnote.Fragments.CalendarFragment;
import com.horical.appnote.Fragments.ForgotAccountFragment;
import com.horical.appnote.Fragments.ListNotesFragment;
import com.horical.appnote.Fragments.LoginFragment;
import com.horical.appnote.Fragments.NewNoteFragment;
import com.horical.appnote.Fragments.SettingFragment;
import com.horical.appnote.Fragments.SignUpFragment;
import com.horical.appnote.Fragments.FilesManagerFragment;
import com.horical.appnote.Fragments.ViewAccountFragment;
import com.horical.appnote.Fragments.ViewNoteFragment;
import com.horical.appnote.Fragments.ViewReminderFragment;
import com.horical.appnote.Interfaces.MainInterface;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.MyView.Item.BaseItem;
import com.horical.appnote.MyView.Item.ItemNormal;
import com.horical.appnote.MyView.Item.ItemNormalWithNotification;
import com.horical.appnote.MyView.MyAdapter.SideMenuAdapter;
import com.horical.appnote.MyView.RoundImageView;
import com.horical.appnote.ServerStorage.Callback.DeleteFileCallback;
import com.horical.appnote.ServerStorage.Callback.DeleteNoteCallback;
import com.horical.appnote.ServerStorage.Callback.DownloadDataCallback;
import com.horical.appnote.ServerStorage.Callback.DownloadFileCallback;
import com.horical.appnote.ServerStorage.Callback.NoteCallback;
import com.horical.appnote.ServerStorage.Callback.UploadFileCallback;
import com.horical.appnote.ServerStorage.FileRoute;
import com.horical.appnote.ServerStorage.NoteRoute;
import com.horical.appnote.ServerStorage.Param.UploadFileParam;
import com.horical.appnote.ServerStorage.Param.UploadNoteParam;
import com.horical.appnote.ServerStorage.Response.FileResponse;
import com.horical.appnote.ServerStorage.Response.NoteResponse;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.MediaUtils;
import com.horical.appnote.Supports.SupportUtils;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity implements MainInterface, BaseActivity.ActivityAction,
        LoginFragment.LoginListener, SignUpFragment.SignUpListener, ImageButton.OnClickListener {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private DrawerLayout mMainLayout;
    private LinearLayout mActionBar;
    private LinearLayout mSideMenuArea;
    private TextView mTitleHeader, mInternetConnection;

    private RoundImageView mRoundImageView;

    private ArrayList<BaseItem> mSideMenuResource;
    private SideMenuAdapter mSideMenuAdapter;

    private String mFragmentName = "";

    private Bundle mBundle;

    private NoteDataDAO mNoteDataDAO;
    private NoteReminderDAO mNoteReminderDAO;

    private ArrayList<NoteData> mListNoteData;
    private ArrayList<NoteReminder> mListReminders;

    private ArrayList<FileData> mListImageFile;
    private ArrayList<FileData> mListVideoFile;
    private ArrayList<FileData> mListAudioFile;

    private ImageButton mButtonSearchNote, mButtonOptionMenu, mSyncButton;
    private AlertDialog mDialog;

    private Intent mReminderService;

    private TextView mTvAppTitle;
    private LinearLayout mProgressbarLayout;

    private int mNumberOfFile = 0;

    private int mCurrentUploadedFiles = 0;
    private int mCurrentSavedNote = 0;

    private ProgressBar mSyncProgress;
    private TextView mLogView;

    private boolean mInternetAvailability;

    private EditNoteListener mEditNoteListener;

    private boolean mListImagesIsReloading, mListVideosIsReloading, mListAudiosIsReloading;

    private enum SameFragmentProperty {
        SameCurrent, Exists, NotExists
    }

    public interface SyncDataCallback {
        void onSyncSuccess(String message);
        void onSyncFail(String message);
    }

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaUtils.setActivity(this);
        ApplicationSharedData.initResource(this);
        LanguageUtils.setContext(this);

        mNoteDataDAO = new NoteDataDAO(this);
        this.reloadListNote();

        mReminderService = new Intent(MainActivity.this, MediaService.class);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setNegativeButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        mDialog = mBuilder.create();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mListReminders.size(); i++) {
                    final NoteReminder noteReminder = mListReminders.get(i);
                    if (noteReminder.readyToRemind() == 0) {
                        final int pos = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.setTitle(noteReminder.getContent());
                                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        stopMedia();
                                        ViewReminderFragment fragment = (ViewReminderFragment) mFragmentManager.
                                                findFragmentByTag(BaseFragment.ViewReminderFragment);
                                        if (fragment != null) {
                                            fragment.refreshListReminder();
                                        }
                                        mListReminders.remove(pos);
                                    }
                                });
                                mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        Toast.makeText(MainActivity.this, "Later", Toast.LENGTH_SHORT).show();
                                        stopMedia();
                                    }
                                });
                                if (MainActivity.this.hasWindowFocus()) {
                                    mDialog.show();
                                }
                            }
                        });
                        startMedia(noteReminder.getVoice());
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        mBundle = new Bundle();

        //set up Main fragment
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (ApplicationSharedData.getUser().equals("")) {
            mFragmentName = BaseFragment.LoginFragment;
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setListener(this);
            mFragmentTransaction.add(R.id.fragmentsContainer, loginFragment, mFragmentName);
        } else {
            mFragmentName = BaseFragment.ListNotesFragment;
            ListNotesFragment listNotesFragment = new ListNotesFragment();
            mFragmentTransaction.add(R.id.fragmentsContainer, listNotesFragment, mFragmentName);
        }
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();

        //Get neccessary view
        mMainLayout = (DrawerLayout) findViewById(R.id.MainLayout);
        mActionBar = (LinearLayout) findViewById(R.id.ActionBar);
        mSideMenuArea = (LinearLayout) findViewById(R.id.areaSideMenu);
        ListView mSideMenu = (ListView) findViewById(R.id.sideMenu);
        mTitleHeader = (TextView) findViewById(R.id.TitleHeader);
        mButtonSearchNote = (ImageButton) findViewById(R.id.buttonSearch);
        mButtonSearchNote.setOnClickListener(this);
        mButtonOptionMenu = (ImageButton) findViewById(R.id.buttonOptionMenu);
        mButtonOptionMenu.setOnClickListener(this);
        mSyncButton = (ImageButton) findViewById(R.id.buttonSyncToServer);
        mSyncButton.setOnClickListener(this);
        mInternetConnection = (TextView) findViewById(R.id.tvInternet);
        mRoundImageView = (RoundImageView) findViewById(R.id.imageAvatar);
        mRoundImageView.setOnClickListener(this);
        mTvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        mSyncProgress = (ProgressBar) findViewById(R.id.SyncBar);
        mProgressbarLayout = (LinearLayout) findViewById(R.id.ProgressBarArea);
        mProgressbarLayout.setOnClickListener(this);
        mLogView = (TextView) findViewById(R.id.LogView);
        mLogView.setText(LanguageUtils.getSyncTitleString());
        TextView mMainProcess = (TextView) findViewById(R.id.tvMainProcess);
        mMainProcess.setText(LanguageUtils.getProcessingString());

        this.checkAvatar();

        //Set up resource for side menu
        ArrayList<String> menuStringResource = new ArrayList<>();
        Collections.addAll(menuStringResource, LanguageUtils.getSideMenu());
        menuStringResource.add("Last sync: " + (new Date()).toString());
        mSideMenuResource = new ArrayList<>();
        initSideMenu(menuStringResource);
        mSideMenuAdapter = new SideMenuAdapter(MainActivity.this, 0, mSideMenuResource);
        mSideMenu.setAdapter(mSideMenuAdapter);

        //set up for navigation button to control fragments
        findViewById(R.id.buttonNavigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListFragments.size() <= 0) mMainLayout.openDrawer(mSideMenuArea);
                else Back();
            }
        });

        //set up for menu to control fragments
        mSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = mSideMenuAdapter.getStringAt(i);
                if (title.equals(LanguageUtils.getNewNoteString())) {
                    NewFragment(BaseFragment.NewNoteFragment);
                }
                if (title.equals(LanguageUtils.getListNotesString())) {
                    NewFragment(BaseFragment.ListNotesFragment);
                }
                if (title.equals(LanguageUtils.getCalendarString())) {
                    NewFragment(BaseFragment.CalendarFragment);
                }
                if (title.equals(LanguageUtils.getFileManagerString())) {
                    NewFragment(BaseFragment.FileManagerFragment);
                }
                if (title.equals(LanguageUtils.getSettingString())) {
                    NewFragment(BaseFragment.SettingsFragment);
                }
                mMainLayout.closeDrawer(mSideMenuArea);
            }
        });

        mNoteReminderDAO = new NoteReminderDAO(this);
        this.reloadListReminder();

        this.reloadListFile(DataConstant.TYPE_IMAGE);
        this.reloadListFile(DataConstant.TYPE_VIDEOCLIP);
        this.reloadListFile(DataConstant.TYPE_VOICE);

        this.updateInternetStatus();

        this.updateUI();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setStatusBarColor(Color.parseColor("#075c52"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSearch:
                switch (mFragmentName) {
                    case BaseFragment.FileManagerFragment:
                        FilesManagerFragment listFileFragment = (FilesManagerFragment) mFragmentManager.findFragmentByTag(mFragmentName);
                        listFileFragment.toggleSearchBar();
                        break;

                    case BaseFragment.ListNotesFragment:
                        ListNotesFragment listNotesFragment = (ListNotesFragment) mFragmentManager.findFragmentByTag(mFragmentName);
                        listNotesFragment.toggleSearchBar();
                        break;
                    default:
                        break;
                }
                return;
            case R.id.ProgressBarArea:
                showLoadingEffect(false, "");
                return;
            case R.id.buttonSyncToServer:
                if (mInternetAvailability) {
                    this.syncData(new SyncDataCallback() {
                        @Override
                        public void onSyncSuccess(String message) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            showLoadingEffect(false, "");
                        }

                        @Override
                        public void onSyncFail(String message) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            showLoadingEffect(false, "");
                        }
                    }, true);
                } else {
                    Toast.makeText(MainActivity.this, "You are in offline!", Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.buttonOptionMenu:
                PopupMenu popupMenu = new PopupMenu(this, mButtonOptionMenu);
                popupMenu.getMenuInflater().inflate(LanguageUtils.getMainMenuRes(), popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.itemAboutUs:
                                break;
                            case R.id.itemHome:
                                NewFragment(BaseFragment.ListNotesFragment);
                                break;
                            case R.id.itemLogOut:
                                if (mInternetAvailability) {
                                    onLogout();
                                } else {
                                    Toast.makeText(MainActivity.this, LanguageUtils.getInternetOfflineString(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.itemViewAccount:
                                NewFragment(BaseFragment.ViewAccountFragment);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.stopMedia();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_en, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mListFragments.size() <= 0) {
            finish();
        } else {
            Back();
            if (mListFragments.size() <= 0) {
                Toast.makeText(this, LanguageUtils.getNotifyPressAgainString(), Toast.LENGTH_SHORT).show();
            }

            if (mEditNoteListener != null && ApplicationSharedData.isAutoSave()) {
                mEditNoteListener.saveNote(false);
            }
        }
    }

    /*implement BaseActivity.ActivityAction*/
    @Override
    public void NewFragment(String fragment_id) {
        this.showLoadingEffect(false, "");
        SameFragmentProperty mCheckSameStatus = checkSameFragment(fragment_id);
        if (mCheckSameStatus == SameFragmentProperty.SameCurrent) {
            Toast.makeText(getApplicationContext(), LanguageUtils.getNotifySameScreenString(), Toast.LENGTH_SHORT).show();
        } else {
            BaseFragment fragment;
            if (mFragmentManager.findFragmentByTag(mFragmentName) != null) {
                mListFragments.push(mFragmentName);
            } else {
                mListFragments.clear();
            }

            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentName = fragment_id;
            switch (fragment_id) {
                case BaseFragment.NewNoteFragment:
                    fragment = new NewNoteFragment();
                    mBundle = new Bundle();
                    mBundle.putBoolean("isCreate", true);
                    fragment.setArguments(mBundle);
                    this.updateSideMenuUI(LanguageUtils.getNewNoteString());
                    mEditNoteListener = (NewNoteFragment) fragment;
                    break;

                case BaseFragment.UpdateNoteFragment:
                    fragment = new NewNoteFragment();
                    mFragmentName = BaseFragment.NewNoteFragment;
                    mBundle.putBoolean("isCreate", false);
                    fragment.setArguments(mBundle);
                    this.updateSideMenuUI(LanguageUtils.getNewNoteString());
                    mEditNoteListener = (NewNoteFragment) fragment;
                    break;

                case BaseFragment.ListNotesFragment:
                    fragment = new ListNotesFragment();
                    mListFragments.clear();
                    this.updateSideMenuUI(LanguageUtils.getListNotesString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.ViewNoteFragment:
                    fragment = new ViewNoteFragment();
                    fragment.setArguments(mBundle);
                    this.updateSideMenuUI(LanguageUtils.getListNotesString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.CalendarFragment:
                    fragment = new CalendarFragment();
                    ((CalendarFragment) fragment).setCallback(this);
                    this.updateSideMenuUI(LanguageUtils.getCalendarString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.ViewReminderFragment:
                    fragment = new ViewReminderFragment();
                    fragment.setArguments(mBundle);
                    this.updateSideMenuUI(LanguageUtils.getCalendarString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.FileManagerFragment:
                    fragment = new FilesManagerFragment();
                    this.updateSideMenuUI(LanguageUtils.getFileManagerString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.SettingsFragment:
                    fragment = new SettingFragment();
                    this.updateSideMenuUI(LanguageUtils.getSettingString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.SignUpFragment:
                    fragment = new SignUpFragment();
                    ((SignUpFragment) fragment).setListener(this);
                    mEditNoteListener = null;
                    break;

                case BaseFragment.ForgotAccountFragment:
                    fragment = new ForgotAccountFragment();
                    mEditNoteListener = null;
                    break;

                case BaseFragment.ViewAccountFragment:
                    fragment = new ViewAccountFragment();
                    this.updateSideMenuUI(LanguageUtils.getSettingString());
                    mEditNoteListener = null;
                    break;

                case BaseFragment.LoginFragment:
                    fragment = new LoginFragment();
                    ((LoginFragment) fragment).setListener(this);
                    mEditNoteListener = null;
                    break;

                default:
                    mFragmentTransaction.commit();
                    return;
            }
            fragment.setMainListener(this);

            //if fragment is existed
            if (mCheckSameStatus == SameFragmentProperty.Exists && !fragment_id.equals(BaseFragment.ListNotesFragment)) {
                //get the old one
                getExistsFragment(mFragmentName);

                //begin update back stack
                Stack<String> stack = new Stack<>();
                String key;
                while (mListFragments.size() > 1) {
                    key = mListFragments.pop();
                    if (key.contains(mFragmentName)) break;
                    else stack.push(key);
                }

                while (!stack.isEmpty()) {
                    mListFragments.push(stack.pop());
                }
                //end update back stack

            } else {
                fragment.setFragmentName(mFragmentName);
                mFragmentTransaction.replace(R.id.fragmentsContainer, fragment, fragment.getFragmentName());
                mFragmentTransaction.addToBackStack(null);
            }
            if (mListFragments.size() >= 1 && !mFragmentName.contains(BaseFragment.ListNotesFragment)) {
                ((ImageButton) findViewById(R.id.buttonNavigation)).setImageResource(R.drawable.ic_arrow_back_white_24dp);
            } else {
                ((ImageButton) findViewById(R.id.buttonNavigation)).setImageResource(R.drawable.ic_menu_white_24dp);
            }
            mFragmentTransaction.commit();
        }

        this.updateUI();
    }

    @Override
    public void Back() {
        if (mListFragments.size() >= 1) {
            mFragmentName = mListFragments.pop();
            getExistsFragment(mFragmentName);
            mFragmentTransaction.commit();
        }
        if (mListFragments.size() == 0) {
            ((ImageButton) findViewById(R.id.buttonNavigation)).setImageResource(R.drawable.ic_menu_white_24dp);
        }
        switch (mFragmentName) {
            case BaseFragment.NewNoteFragment:
            case BaseFragment.UpdateNoteFragment:
                this.updateSideMenuUI(LanguageUtils.getNewNoteString());
                break;
            case BaseFragment.ListNotesFragment:
            case BaseFragment.ViewNoteFragment:
                this.updateSideMenuUI(LanguageUtils.getListNotesString());
                break;
            case BaseFragment.CalendarFragment:
            case BaseFragment.ViewReminderFragment:
                this.updateSideMenuUI(LanguageUtils.getCalendarString());
                break;
            case BaseFragment.FileManagerFragment:
                this.updateSideMenuUI(LanguageUtils.getFileManagerString());
                break;
            case BaseFragment.SettingsFragment:
                this.updateSideMenuUI(LanguageUtils.getSettingString());
                break;
        }
        this.updateUI();
    }

    /*MainActivity member method*/
    //set up data resource for side menu
    private void stopMedia() {
        if (MediaService.isRunning()) {
            stopService(mReminderService);
            MediaService.setRunning(false);
        }
    }

    private void startMedia(String audio) {
        if (!MediaService.isRunning()) {
            MediaService.setAudioName(audio);
            startService(mReminderService);
            MediaService.setRunning(true);
        }
    }

    private void getAllNotesFromServer() {
        //TODO get all notes from parse.com
        NoteRoute request = new NoteRoute();
        request.getAllNotes(new DownloadDataCallback<NoteData>() {
            @Override
            public void onDownloadSuccess(final ArrayList<NoteData> data) {
                if (data != null && !data.isEmpty()) {
                    for (NoteData noteData : data) {
                        for (NoteData note : mListNoteData) {
                            if (note.toString().equals(noteData.toString())) {
                                return;
                            }
                        }
                        NoteDataDAO dao = new NoteDataDAO(MainActivity.this);
                        dao.setNoteData(noteData);
                        dao.insertNote(new NewNoteFragment.CreateNoteCallback() {
                            @Override
                            public void onSuccess(String message) {
                                mCurrentSavedNote++;
                                if (mCurrentSavedNote == data.size()) {
                                    Toast.makeText(MainActivity.this, LanguageUtils.getNotifySyncSuccessString(), Toast.LENGTH_SHORT).show();
                                    reloadListNote();
                                    if (mFragmentName.equals(BaseFragment.ListNotesFragment)) {
                                        ListNotesFragment fragment = (ListNotesFragment) mFragmentManager.findFragmentByTag(mFragmentName);
                                        if (fragment != null) {
                                            fragment.reLoadListNote();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String message) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(MainActivity.this, LanguageUtils.getNotifyDownloadFailString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(NoteResponse response) {

            }
        });
        reloadListNote();
        NewFragment(BaseFragment.ListNotesFragment);
    }

    private void syncData(final SyncDataCallback callback, final boolean showMessage) {
        if (showMessage) {
            Toast.makeText(this, LanguageUtils.getNotifyLimitString(), Toast.LENGTH_LONG).show();
        }
        FileRoute fileRequest = new FileRoute();
        showLoadingEffect(true, LanguageUtils.getProcessingString());
        fileRequest.deleteAllFiles(new DeleteFileCallback() {
            @Override
            public void onSuccess(FileResponse response) {
                NoteRoute noteRequest = new NoteRoute();
                noteRequest.deleteAllNotes(new DeleteNoteCallback() {
                    @Override
                    public void onSuccess(NoteResponse response) {
                        uploadData(callback, showMessage);
                    }

                    @Override
                    public void onFail(ParseException e) {
                        if (showMessage) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        showLoadingEffect(false, "");
                    }
                });
            }

            @Override
            public void onFail(ParseException e) {
                if (showMessage) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                showLoadingEffect(false, "");
            }
        });
    }

    private void uploadData(final SyncDataCallback callback, final boolean showMessage) {

        final HashMap<String, String> listFiles = new HashMap<>();

        for (FileData data : mListImageFile) {
            listFiles.put(data.getFilePath(), DataConstant.TYPE_IMAGE);
        }
        for (FileData data : mListVideoFile) {
            listFiles.put(data.getFilePath(), DataConstant.TYPE_VIDEOCLIP);
        }
        for (FileData data : mListAudioFile) {
            listFiles.put(data.getFilePath(), DataConstant.TYPE_VOICE);
        }

        if (mListNoteData.isEmpty()) {
            callback.onSyncSuccess(LanguageUtils.getNotifyNothingUploadString());
            return;
        }

        FileRoute request = new FileRoute();
        final int all_files = ((mListImageFile != null) ? mListImageFile.size() : 0) +
                ((mListVideoFile != null) ? mListVideoFile.size() : 0) +
                ((mListAudioFile != null) ? mListAudioFile.size() : 0);
        if (all_files == 0) {
            NoteRoute note_request = new NoteRoute();
            for (NoteData noteData : mListNoteData) {
                if (noteData.getNoteData() != null && !noteData.getNoteData().isEmpty()) {
                    UploadNoteParam uploadNoteParam = new UploadNoteParam();
                    uploadNoteParam.userId = ApplicationSharedData.getUserID();
                    uploadNoteParam.id = String.valueOf(noteData.getNoteSummary().getID());
                    uploadNoteParam.content = noteData.createJSON();

                    note_request.uploadNote(uploadNoteParam, new NoteCallback() {
                        @Override
                        public void onSuccess(NoteResponse response) {
                            callback.onSyncSuccess(response.message);
                            showLoadingEffect(false, "");
                        }

                        @Override
                        public void onFail(ParseException e) {
                            callback.onSyncFail(e.getMessage());
                            showLoadingEffect(false, "");
                        }
                    });
                }
            }
        } else {
            for (Map.Entry<String, String> entry : listFiles.entrySet()) {
                UploadFileParam param = new UploadFileParam();
                param.type = entry.getValue();
                param.name = SupportUtils.getNameFromPath(entry.getKey());
                param.path = entry.getKey();
                request.uploadFile(param, new UploadFileCallback() {
                    @Override
                    public void onSuccess(FileResponse response) {
                        mCurrentUploadedFiles++;
                        if (mCurrentUploadedFiles == all_files) {
                            NoteRoute request = new NoteRoute();
                            for (NoteData noteData : mListNoteData) {
                                if (noteData.getNoteData() != null && !noteData.getNoteData().isEmpty()) {
                                    UploadNoteParam uploadNoteParam = new UploadNoteParam();
                                    uploadNoteParam.userId = ApplicationSharedData.getUserID();
                                    uploadNoteParam.id = String.valueOf(noteData.getNoteSummary().getID());
                                    uploadNoteParam.content = noteData.createJSON();

                                    request.uploadNote(uploadNoteParam, new NoteCallback() {
                                        @Override
                                        public void onSuccess(NoteResponse response) {
                                            callback.onSyncSuccess(response.message);
                                            showLoadingEffect(false, "");
                                        }

                                        @Override
                                        public void onFail(ParseException e) {
                                            callback.onSyncFail(e.getMessage());
                                            showLoadingEffect(false, "");
                                        }
                                    });
                                }
                            }

                            mCurrentUploadedFiles = 0;
                        }
                    }

                    @Override
                    public void onFail(ParseException e) {
                        if (showMessage) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        showLoadingEffect(false, "");
                    }
                });
            }
        }
    }

    private void deleteAllNoteInDatabase(final SyncDataCallback callback) {
        int real = 0, deleted = 0;
        NoteDataDAO noteDataDAO = new NoteDataDAO(MainActivity.this);
        for (NoteData noteData : mListNoteData) {
            if (noteData.getNoteData() != null && !noteData.getNoteData().isEmpty()) {
                real++;
                if (noteDataDAO.deleteNote(noteData.getNoteSummary().getID())) {
                    deleted++;
                }
            }
        }
        if (deleted == real) {
            callback.onSyncSuccess(LanguageUtils.getNotifyCompletedString());
        } else {
            callback.onSyncFail(LanguageUtils.getNotifyFailedString());
        }
    }

    private void initSideMenu(ArrayList<String> dataSource) {
        for (String data : dataSource) {
            if (data.equals(LanguageUtils.getNewNoteString()) ||
                    data.equals(LanguageUtils.getListNotesString()) ||
                    data.equals(LanguageUtils.getSettingString())) {

                ItemNormal itemNormal = new ItemNormal();
                itemNormal.setNameOfItem(data);
                itemNormal.setItemName(data);
                itemNormal.setFocused(false);
                if (data.equals(LanguageUtils.getNewNoteString())) {
                    itemNormal.setIllustrateImage(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_add_black_24dp));
                    itemNormal.setBitmapIllustrateImageFocus(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_add_green_24dp));
                } else {
                    if (data.equals(LanguageUtils.getListNotesString())) {
                        itemNormal.setFocused(true);
                        itemNormal.setIllustrateImage(
                                SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_list_black_24dp));
                        itemNormal.setBitmapIllustrateImageFocus(
                                SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_list_green_24dp));
                    } else {
                        itemNormal.setIllustrateImage(
                                SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_settings_black_24dp));
                        itemNormal.setBitmapIllustrateImageFocus(
                                SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_settings_green_24dp));
                    }
                }
                mSideMenuResource.add(itemNormal);
            }

            if (data.equals(LanguageUtils.getCalendarString()) || data.equals(LanguageUtils.getFileManagerString())) {
                ItemNormalWithNotification itemNotification = new ItemNormalWithNotification();
                itemNotification.setNameOfItem(data);
                itemNotification.setItemName(data);
                itemNotification.setFocused(false);
                if (data.equals(LanguageUtils.getCalendarString())) {
                    itemNotification.setIllustrateImage(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_event_note_black_24dp));
                    itemNotification.setBitmapIllustrateImageFocus(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_event_note_green_24dp));
                } else {
                    itemNotification.setIllustrateImage(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_folder_black_24dp));
                    itemNotification.setBitmapIllustrateImageFocus(
                            SupportUtils.getBitmapResource(MainActivity.this, R.drawable.ic_folder_green_24dp));
                }
                mSideMenuResource.add(itemNotification);
            }
        }
    }



    public void sortListFile(String type) {
        switch (type) {
            case DataConstant.TYPE_VOICE:
                for (int i = 0; i < mListAudioFile.size() - 1; i++)
                    for (int j = i + 1; j < mListAudioFile.size(); j++) {
                        if (mListAudioFile.get(i).compare(mListAudioFile.get(j)) == 1) {
                            Collections.swap(mListAudioFile, i, j);
                        }
                    }
                break;
            case DataConstant.TYPE_IMAGE:
                for (int i = 0; i < mListImageFile.size() - 1; i++)
                    for (int j = i + 1; j < mListImageFile.size(); j++) {
                        if (mListImageFile.get(i).compare(mListImageFile.get(j)) == 1) {
                            Collections.swap(mListImageFile, i, j);
                        }
                    }
                break;
            case DataConstant.TYPE_VIDEOCLIP:
                for (int i = 0; i < mListVideoFile.size() - 1; i++)
                    for (int j = i + 1; j < mListVideoFile.size(); j++) {
                        if (mListVideoFile.get(i).compare(mListVideoFile.get(j)) == 1) {
                            Collections.swap(mListVideoFile, i, j);
                        }
                    }
                break;
        }
    }

    //check if current fragment is the same with the on replacing fragment
    private SameFragmentProperty checkSameFragment(String fragment_id) {
        String searchObject;
        searchObject = fragment_id;
        if (mFragmentName.contains(searchObject)) return SameFragmentProperty.SameCurrent;
        for (String name : mListFragments) {
            if (name.contains(searchObject))
                return SameFragmentProperty.Exists;
        }
        return SameFragmentProperty.NotExists;
    }

    private void getExistsFragment(String name) {
        if (mFragmentManager.findFragmentByTag(name) != null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragmentsContainer, mFragmentManager.findFragmentByTag(name));
        }
    }

    private void updateSideMenuUI(String item_name) {
        for (BaseItem item : mSideMenuResource) {
            if (item.getNameOfItem().contains(item_name)) {
                item.setFocused(true);
            } else {
                item.setFocused(false);
            }
        }
        mSideMenuAdapter.notifyDataSetChanged();
    }

    private void checkAvatar() {
        String[] avatars = FileUtils.getListFile("Avatar");
        if (avatars != null && avatars.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatars[0]);
            if (bitmap != null) {
                mRoundImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void updateUI() {
        switch (mFragmentName) {
            case BaseFragment.CalendarFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getCalendarString());
                break;
            case BaseFragment.ViewAccountFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getAccountString());
                break;
            case BaseFragment.ForgotAccountFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mActionBar.setVisibility(View.GONE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getAccountString());
                break;
            case BaseFragment.FileManagerFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.VISIBLE);
                mTvAppTitle.setText(LanguageUtils.getFileString());
                break;
            case BaseFragment.ListNotesFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.VISIBLE);
                mTvAppTitle.setText(getResources().getString(R.string.app_name));
                break;
            case BaseFragment.LoginFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mActionBar.setVisibility(View.GONE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getAccountString());
                break;
            case BaseFragment.NewNoteFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.GONE);
                mButtonSearchNote.setVisibility(View.GONE);
                break;
            case BaseFragment.SettingsFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getSettingString());
                break;
            case BaseFragment.SignUpFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mActionBar.setVisibility(View.GONE);
                mButtonSearchNote.setVisibility(View.GONE);
                break;
            case BaseFragment.ViewNoteFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.GONE);
                mButtonSearchNote.setVisibility(View.GONE);
                return;
            case BaseFragment.ViewReminderFragment:
                mMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActionBar.setVisibility(View.VISIBLE);
                mButtonSearchNote.setVisibility(View.GONE);
                mTvAppTitle.setText(LanguageUtils.getCalendarString());
                break;
        }
        this.mTitleHeader.setText(ApplicationSharedData.getDisplayname());
    }

    /*CallbackInterface*/
    @Override
    public void ChangeFragment(String fragmentCaller, String fragmentDestination) {
        NewFragment(fragmentDestination);
    }

    @Override
    public void PassNoteSummary(NoteSummary noteSummary) {
        mBundle = new Bundle();
        mBundle.putSerializable("NoteSummary", noteSummary);
        NewFragment(BaseFragment.ViewNoteFragment);
    }

    @Override
    public void PassEditInfo(NoteData noteData) {
        mBundle = new Bundle();
        mBundle.putSerializable("NoteData", noteData);
        NewFragment(BaseFragment.UpdateNoteFragment);
    }

    @Override
    public void CalendarItemClicked(CalendarObject calendarObject) {
        mBundle = new Bundle();
        mBundle.putInt("day", calendarObject.getDay());
        mBundle.putInt("month", calendarObject.getMonth());
        mBundle.putInt("year", calendarObject.getYear());
        this.NewFragment(BaseFragment.ViewReminderFragment);
    }

    @Override
    public void PlayMedia(String path) {
        mBundle = new Bundle();
        mBundle.putString("MediaPath", path);
        Intent intentPlayMedia = new Intent(MainActivity.this, MediaPlayerActivity.class);
        intentPlayMedia.putExtra("MediaInfor", mBundle);
        startActivity(intentPlayMedia);
    }

    @Override
    public ArrayList<NoteData> getAllNotes() {
        if (mListNoteData == null || mListNoteData.isEmpty()) {
            this.reloadListNote();
        }
        return mListNoteData;
    }

    @Override
    public void showLoadingEffect(boolean flag, String message) {
        if (flag) {
            mSyncButton.setVisibility(View.GONE);
            mProgressbarLayout.setVisibility(View.VISIBLE);
            mSyncProgress.setVisibility(View.VISIBLE);
            mLogView.setText(message);
        } else {
            mSyncButton.setVisibility(View.VISIBLE);
            mProgressbarLayout.setVisibility(View.GONE);
            mSyncProgress.setVisibility(View.GONE);
            mLogView.setText(LanguageUtils.getSyncTitleString());
        }
    }

    @Override
    public void restartApplication() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public NoteData getRecentNote() {
        if (mListNoteData.isEmpty() || mListNoteData == null) {
            return null;
        }
        return mListNoteData.get(1);
    }

    @Override
    public void reloadListNote() {
        mListNoteData = new ArrayList<>();
        if (mNoteDataDAO != null) {
            ArrayList<NoteSummary> listNoteSummary = mNoteDataDAO.loadListNote();
            String mTimeStamp = null;
            if (mListNoteData == null) {
                mListNoteData = new ArrayList<>();
            } else {
                mListNoteData.clear();
            }

            for (int i = 0; i < listNoteSummary.size() - 1; i++)
                for (int j = i + 1; j < listNoteSummary.size(); j++) {
                    if (CalendarUtils.compareDate(listNoteSummary.get(i).getCreatedAt(), listNoteSummary.get(j).getCreatedAt()) < 0) {
                        Collections.swap(listNoteSummary, i, j);
                    }
                }

            for (NoteSummary noteSummary : listNoteSummary) {
                if (mTimeStamp == null ||
                        !mTimeStamp.equals(CalendarUtils.checkTimeStamp(CalendarUtils.getDateFromString(noteSummary.getCreatedAt())))) {
                    mListNoteData.add(new NoteData(noteSummary, null));
                    mTimeStamp = CalendarUtils.checkTimeStamp(CalendarUtils.getDateFromString(noteSummary.getCreatedAt()));
                }
                ArrayList<NoteDataLine> listNoteContent = mNoteDataDAO.loadNoteDetails(noteSummary.getID());
                mListNoteData.add(new NoteData(noteSummary, listNoteContent));
            }
        }
    }

    @Override
    public ArrayList<FileData> getAllFiles(String type) {
        switch (type) {
            case DataConstant.TYPE_IMAGE:
                if (mListImageFile == null) {
                    mListImageFile = new ArrayList<>();
                }
                return mListImageFile;
            case DataConstant.TYPE_VIDEOCLIP:
                if (mListVideoFile == null) {
                    mListVideoFile = new ArrayList<>();
                }
                return mListVideoFile;
            case DataConstant.TYPE_VOICE:
                if (mListAudioFile == null) {
                    mListAudioFile = new ArrayList<>();
                }
                return mListAudioFile;
        }
        return null;
    }

    @Override
    public void reloadListFile(final String type) {
        final String[] files = FileUtils.getListFile(type);
        if (mListImageFile == null) {
            mListImageFile = new ArrayList<>();
        } else {
            mListImageFile.clear();
        }
        if (mListVideoFile == null) {
            mListVideoFile = new ArrayList<>();
        } else {
            mListVideoFile.clear();
        }
        if (mListAudioFile == null) {
            mListAudioFile = new ArrayList<>();
        } else {
            mListAudioFile.clear();
        }

        mNumberOfFile = 0;

        switch (type) {
            case DataConstant.TYPE_IMAGE:
                mListImagesIsReloading = true;
                break;
            case DataConstant.TYPE_VIDEOCLIP:
                mListVideosIsReloading = true;
                break;
            case DataConstant.TYPE_VOICE:
                mListAudiosIsReloading = true;
                break;
        }

        if (files != null && files.length > 0) {
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    switch (type) {
                        case DataConstant.TYPE_IMAGE:
                            for (String file : files) {
                                mListImageFile.add(new FileData(SupportUtils.getNameFromPath(file),
                                        SupportUtils.checkFileSize(file), file, type));
                                mNumberOfFile++;
                            }
                            break;
                        case DataConstant.TYPE_VIDEOCLIP:
                            for (String file : files) {
                                mListVideoFile.add(new FileData(SupportUtils.getNameFromPath(file),
                                        SupportUtils.MilliSecToTime(SupportUtils.getDuration(Uri.parse(file), MainActivity.this)) + "",
                                        file, type));
                                mNumberOfFile++;
                            }
                            break;
                        case DataConstant.TYPE_VOICE:
                            for (String file : files) {
                                mListAudioFile.add(new FileData(SupportUtils.getNameFromPath(file),
                                        SupportUtils.MilliSecToTime(SupportUtils.getDuration(Uri.parse(file), MainActivity.this)) + "",
                                        file, type));
                                mNumberOfFile++;
                            }
                            break;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    for (BaseItem baseItem : mSideMenuResource) {
                        if (baseItem.getNameOfItem().contains(LanguageUtils.getFileManagerString())) {
                            ItemNormalWithNotification notification = (ItemNormalWithNotification) baseItem;
                            notification.setNotification(String.valueOf(mNumberOfFile));
                        }
                    }
                    mSideMenuAdapter.notifyDataSetChanged();

                    switch (type) {
                        case DataConstant.TYPE_IMAGE:
                            mListImagesIsReloading = false;
                            break;
                        case DataConstant.TYPE_VIDEOCLIP:
                            mListVideosIsReloading = false;
                            break;
                        case DataConstant.TYPE_VOICE:
                            mListAudiosIsReloading = false;
                            break;
                    }
                    sortListFile(type);
                }
            };
            asyncTask.execute();
        } else {
            for (BaseItem baseItem : mSideMenuResource) {
                if (baseItem.getNameOfItem().contains(LanguageUtils.getFileManagerString())) {
                    ItemNormalWithNotification notification = (ItemNormalWithNotification) baseItem;
                    notification.setNotification(String.valueOf(mNumberOfFile));
                }
            }
            mSideMenuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLogout() {
        syncData(new SyncDataCallback() {
            @Override
            public void onSyncSuccess(String message) {
                deleteAllNoteInDatabase(new SyncDataCallback() {
                    @Override
                    public void onSyncSuccess(String message) {
                        if (ApplicationSharedData.clearSession()) {
                            NewFragment(BaseFragment.LoginFragment);
                            mListFragments.clear();
                        }
                    }

                    @Override
                    public void onSyncFail(String message) {
                        if (ApplicationSharedData.clearSession()) {
                            NewFragment(BaseFragment.LoginFragment);
                            mListFragments.clear();
                        }
                    }
                });
            }

            @Override
            public void onSyncFail(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }, false);
    }

    @Override
    public boolean checkInternetAvailable() {
        return mInternetAvailability;
    }

    private void updateInternetStatus() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mInternetAvailability = checkInternetAvailibility();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInternetConnection.setText(
                                (mInternetAvailability)?LanguageUtils.getInternetOnlineString():LanguageUtils.getInternetOfflineString());
                        LoginFragment fragment = (LoginFragment) mFragmentManager.findFragmentByTag(BaseFragment.LoginFragment);
                        if (fragment != null) {
                            fragment.setInternetSignal(mInternetAvailability);
                        }
                    }
                });
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void reloadListReminder() {
        mListReminders = mNoteReminderDAO.getAllReminders();
        int mNumberOfReminders = 0;
        for (NoteReminder reminder : mListReminders) {
            if (CalendarUtils.checkToday(reminder.getTimeComplete())) {
                mNumberOfReminders++;
            }
        }
        if (mFragmentName.contains(BaseFragment.ViewReminderFragment)) {
            ViewReminderFragment fragment = (ViewReminderFragment) mFragmentManager.findFragmentByTag(mFragmentName);
            fragment.refreshListReminder();
        }
        for (BaseItem baseItem : mSideMenuResource) {
            if (baseItem.getNameOfItem().contains(LanguageUtils.getCalendarString())) {
                ItemNormalWithNotification notification = (ItemNormalWithNotification) baseItem;
                notification.setNotification(String.valueOf(mNumberOfReminders));
                mSideMenuAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean isReloadingFiles() {
        return mListImagesIsReloading && mListVideosIsReloading && mListAudiosIsReloading;
    }

    /*implement LoginFragment.LoginListner*/
    @Override
    public void onLoginSuccess(String username, String email, String id, String displayname, ParseFile avatar) {
        if (ApplicationSharedData.saveSession(username, email, id, displayname, "")) {
            Log.d("Log", "Session's stored successfully!");
        }

        if (avatar != null) {
            try {
                FileUtils.writeFile("Avatar", avatar.getData(), "UserAvatar");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.checkAvatar();
        FileRoute request = new FileRoute();
        request.getAllFileFromServer(new DownloadFileCallback() {
            @Override
            public void onSuccess(FileResponse response) {
                reloadListFile(DataConstant.TYPE_IMAGE);
                reloadListFile(DataConstant.TYPE_VIDEOCLIP);
                reloadListFile(DataConstant.TYPE_VOICE);
                Toast.makeText(MainActivity.this, response.message, Toast.LENGTH_SHORT).show();

                reloadListReminder();

                if (mNoteDataDAO.loadListNote().isEmpty()) {
                    getAllNotesFromServer();
                } else {
                    reloadListNote();
                    NewFragment(BaseFragment.ListNotesFragment);
                }
                updateUI();
            }

            @Override
            public void onFail(ParseException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        updateUI();
    }

    @Override
    public void onForgotPassword() {

    }

    @Override
    public void onSignUp() {
        this.NewFragment(BaseFragment.SignUpFragment);
    }

    /*implement SignUpFragment.SignUpListener*/
    @Override
    public void onSignUpSuccess(String username, String email, String id, String displayname, ParseFile avatar) {
        this.onLoginSuccess(username, email, id, displayname, avatar);
    }
}
