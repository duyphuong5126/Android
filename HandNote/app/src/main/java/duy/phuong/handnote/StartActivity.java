package duy.phuong.handnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.MyView.IntroductionPager;
import duy.phuong.handnote.MyView.RoundImageView;
import duy.phuong.handnote.Support.SharedPreferenceUtils;
import duy.phuong.handnote.Support.SupportUtils;

public class StartActivity extends Activity implements View.OnClickListener {
    private ViewPager mIntroPager;
    private int mPosition;
    private HashMap<Integer, View> mListLayout;
    private LinearLayout mLayoutLoading, mLayoutIntro, mLayoutLogin, mLayoutInstalling;
    private ImageButton mButtonVerify;
    private ImageButton mButtonCancel;
    private EditText mEdtName;

    private RoundImageView mSelectedImage;
    private Bitmap mAvatar;

    public static final int SELECT_IMAGE = 1;

    private long mCurrentTime;

    private Uri mCapturedImage;

    private AsyncTask<Void, Integer, Void> mEVTask;
    private TextView mTextProgress;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private ProgressBar mProgress;
    private int mProgressNumber, mMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        LinearLayout mButtonStartApp = (LinearLayout) findViewById(R.id.buttonStartApp);
        mButtonStartApp.setOnClickListener(this);
        mLayoutLoading = (LinearLayout) findViewById(R.id.loadScreen);
        mLayoutIntro = (LinearLayout) findViewById(R.id.layoutIntro);
        mLayoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        mLayoutLoading.setVisibility(View.VISIBLE);
        mProgress = (ProgressBar) findViewById(R.id.Progress);
        mLayoutInstalling = (LinearLayout) findViewById(R.id.layoutInstalling);
        mTextProgress = (TextView) findViewById(R.id.textProgress);
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("You haven't installed dictionary yet. Install it now?").setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(StartActivity.this, "You can't use translate feature before installed dictionary", Toast.LENGTH_LONG).show();
                if (!SharedPreferenceUtils.isViewIntro()) {
                    mLayoutLoading.setVisibility(View.GONE);
                    showIntroScreen();
                } else {
                    if (SharedPreferenceUtils.getCurrentName().isEmpty()) {
                        showLoginScreen();
                    } else {
                        intentMain();
                    }
                }
            }
        }).setPositiveButton("Install", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initDict();
            }
        });
        mDialog = mBuilder.create();

        if (!SharedPreferenceUtils.isLoadedDict()) {
            if (mDialog != null) {
                mDialog.show();
            }
        } else {
            if (!SharedPreferenceUtils.isViewIntro()) {
                mLayoutLoading.setVisibility(View.GONE);
                showIntroScreen();
            } else {
                if (SharedPreferenceUtils.getCurrentName().isEmpty()) {
                    showLoginScreen();
                } else {
                    intentMain();
                }
            }
        }
    }

    private void initDict() {
        final LocalStorage localStorage = new LocalStorage(getApplicationContext());
        final SQLiteDatabase db = localStorage.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        Resources resources = getResources();
        final InputStream inputStream = resources.openRawResource(R.raw.eng_vi);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        localStorage.deleteAllDict(db);
        mEVTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLayoutLoading.setVisibility(View.VISIBLE);
                if (mDialog != null) {
                    if (mDialog.isShowing()) {
                        mDialog.cancel();
                    }
                }
                try {
                    mMax = inputStream.available();
                    mProgress.setMax(mMax);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mLayoutInstalling.setVisibility(View.VISIBLE);
                mProgressNumber = 0;
                Toast.makeText(StartActivity.this, "Installation begin...", Toast.LENGTH_LONG).show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        Log.d("line", line);
                        StringTokenizer tokenizer = new StringTokenizer(line, "#");
                        if (tokenizer.countTokens() == 2) {
                            String word = tokenizer.nextToken();
                            String definition = tokenizer.nextToken();
                            Log.d("Infor", "w: " + word + ", def: " + definition);
                            Log.d("Insert result", String.valueOf(localStorage.inertEV_DictLine(word, null, definition, db, contentValues)));
                        } else {
                            if (tokenizer.countTokens() == 3) {
                                String word = tokenizer.nextToken();
                                String pronunciation = tokenizer.nextToken();
                                String definition = tokenizer.nextToken();
                                Log.d("Infor", "w: " + word + ", pro: " + pronunciation + ", def: " + definition);
                                Log.d("Insert result", String.valueOf(localStorage.inertEV_DictLine(word, pronunciation, definition, db, contentValues)));
                            }
                        }
                        publishProgress(line.getBytes().length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mProgressNumber += values[0];
                mTextProgress.setText("Installing " + Math.round((((double) mProgressNumber)/mMax) * 100) + "%, please wait...");
                mProgress.setProgress(mProgressNumber);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                SharedPreferenceUtils.loadedDict(true);
                if (!SharedPreferenceUtils.isViewIntro()) {
                    showIntroScreen();
                    mLayoutLoading.setVisibility(View.GONE);
                } else {
                    if (SharedPreferenceUtils.getCurrentName().isEmpty()) {
                        showLoginScreen();
                        mLayoutLoading.setVisibility(View.GONE);
                    } else {
                        intentMain();
                    }
                }
            }
        };
        if (mEVTask != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mEVTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mEVTask.execute();
            }
        }
    }

    private void showLoginScreen() {
        mLayoutIntro.setVisibility(View.GONE);
        mLayoutLogin.setVisibility(View.VISIBLE);
        LinearLayout mButtonGetStarted = (LinearLayout) findViewById(R.id.buttonGetStarted);
        mButtonGetStarted.setOnClickListener(this);
        mButtonCancel = (ImageButton) findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(this);
        mButtonVerify = (ImageButton) findViewById(R.id.buttonVerify);
        mButtonVerify.setOnClickListener(this);
        ImageButton mButtonLoadAvatar = (ImageButton) findViewById(R.id.buttonSelectAvatar);
        mButtonLoadAvatar.setOnClickListener(this);
        mSelectedImage = (RoundImageView) findViewById(R.id.selectedAvatar);
        mEdtName = (EditText) findViewById(R.id.edtCurrentName);
        mEdtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    mButtonCancel.setVisibility(View.VISIBLE);
                    mButtonVerify.setVisibility(View.GONE);
                } else {
                    mButtonCancel.setVisibility(View.GONE);
                    mButtonVerify.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showIntroScreen() {
        mLayoutIntro.setVisibility(View.VISIBLE);
        mLayoutLogin.setVisibility(View.GONE);
        mListLayout = new HashMap<>();
        mListLayout.put(0, findViewById(R.id.itemOuter1));
        mListLayout.put(1, findViewById(R.id.itemOuter2));
        mListLayout.put(2, findViewById(R.id.itemOuter3));
        mListLayout.put(3, findViewById(R.id.itemOuter4));
        mListLayout.put(4, findViewById(R.id.itemOuter5));

        FrameLayout frame1 = (FrameLayout) findViewById(R.id.intro1);
        frame1.setOnClickListener(this);
        FrameLayout frame2 = (FrameLayout) findViewById(R.id.intro2);
        frame2.setOnClickListener(this);
        FrameLayout frame3 = (FrameLayout) findViewById(R.id.intro3);
        frame3.setOnClickListener(this);
        FrameLayout frame4 = (FrameLayout) findViewById(R.id.intro4);
        frame4.setOnClickListener(this);
        FrameLayout frame5 = (FrameLayout) findViewById(R.id.intro5);
        frame5.setOnClickListener(this);

        final ArrayList<SpannableString> titles = new ArrayList<>();
        titles.add(new SpannableString("Welcome, Writer"));
        titles.add(new SpannableString("How To Use"));
        titles.add(new SpannableString("About Our AI"));
        titles.add(new SpannableString("About This App"));
        titles.add(new SpannableString("Contact Us"));
        ArrayList<SpannableString> contents = new ArrayList<>();
        contents.add(new SpannableString(
                "If you are looking for an application that can understand your handwriting text, \n here we bring you some solutions for this."
        ));
        contents.add(new SpannableString(
                "Let write all your letters by your fingers or pen \non your device screen surface\n and we'll recognize it for you."
        ));
        contents.add(new SpannableString(
                "It can be smarter and smarter by recognizing your text.\n Here we give you some features that allow you to know generally how this app work."
        ));
        contents.add(new SpannableString(
                "If you're interested in developing this kind of app, please help us to improve it by contacting."
        ));
        String contact = "Social: Facebook. \nMail: G-mail";
        SpannableString contactSpan = new SpannableString(contact);
        ClickableSpan clickFacebook = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(StartActivity.this, "Facebook: Duy Phuong", Toast.LENGTH_LONG).show();
                String url = "https://www.facebook.com/duyphuong.nguyenhoang.7";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#FF051770"));
            }
        };
        ClickableSpan clickMail = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(StartActivity.this, "Mail: duyphuong5126@gmail.com", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#FF051770"));
            }
        };
        contactSpan.setSpan(clickFacebook, contact.indexOf("Facebook"), contact.indexOf("Facebook") + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contactSpan.setSpan(clickMail, contact.indexOf("G-mail"), contact.indexOf("G-mail") + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contents.add(contactSpan);

        IntroductionPager mPagerAdapter = new IntroductionPager(this, titles, contents, R.layout.layout_slide);
        mIntroPager = (ViewPager) findViewById(R.id.layoutSlide);
        mIntroPager.setAdapter(mPagerAdapter);
        mCurrentTime = System.currentTimeMillis();
        mPosition = 0;
        mIntroPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentTime = System.currentTimeMillis();
                mPosition = position;
                for (Map.Entry<Integer, View> entry : mListLayout.entrySet()) {
                    if (entry.getKey() == mPosition) {
                        entry.getValue().setVisibility(View.VISIBLE);
                    } else {
                        entry.getValue().setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ScheduledExecutorService mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - mCurrentTime) / 1000 >= 10) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPosition++;
                            if (mPosition >= titles.size()) {
                                mPosition = 0;
                            }
                            mIntroPager.setCurrentItem(mPosition, true);
                        }
                    });
                }
            }
        }, 6, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intro1:
                mPosition = 0;
                break;
            case R.id.intro2:
                mPosition = 1;
                break;
            case R.id.intro3:
                mPosition = 2;
                break;
            case R.id.intro4:
                mPosition = 3;
                break;
            case R.id.intro5:
                mPosition = 4;
                break;
            case R.id.buttonGetStarted:
                String name = mEdtName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(StartActivity.this, "Your name is empty", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferenceUtils.setCurrentName(name);
                    if (mAvatar != null) {
                        SupportUtils.saveImage(mAvatar, "Avatar", "avatar", ".png");
                    }
                    intentMain();
                }
                break;
            case R.id.buttonStartApp:
                SharedPreferenceUtils.viewedIntro(true);
                if (SharedPreferenceUtils.getCurrentName().isEmpty()) {
                    showLoginScreen();
                }
                break;
            case R.id.buttonSelectAvatar:
                intentLoadImage();
                break;
            default:
                break;
        }
        if (mIntroPager != null) {
            mIntroPager.setCurrentItem(mPosition, true);
        }
    }

    private void intentLoadImage() {
        Intent intentPick = new Intent();
        intentPick.setType("image/*");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Captured" + File.separator);
        if (!root.exists()) {
            root.mkdirs();
        }
        String name = "Captured_" + System.nanoTime();
        File dir = new File(root, name);
        mCapturedImage = Uri.fromFile(dir);

        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImage);

        Intent intentChooser = Intent.createChooser(intentPick, "Select a source");
        intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentTakePhoto});
        startActivityForResult(intentChooser, SELECT_IMAGE);

    }

    private void intentMain() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    boolean isFromCamera = false;
                    if (data == null) {
                        isFromCamera = true;
                    } else {
                        if (data.getAction() == null) {
                            isFromCamera = true;
                        } else {
                            isFromCamera = data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    String path = null;
                    if (isFromCamera) {
                        path = SupportUtils.getPath(mCapturedImage, StartActivity.this);
                    } else {
                        path = SupportUtils.getPath(data.getData(), StartActivity.this);
                    }
                    if (path != null) {
                        mAvatar = BitmapFactory.decodeFile(path);
                        if (mAvatar != null) {
                            mSelectedImage.setImageBitmap(mAvatar);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
