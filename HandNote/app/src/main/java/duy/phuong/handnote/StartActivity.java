package duy.phuong.handnote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import duy.phuong.handnote.MyView.IntroductionPager;
import duy.phuong.handnote.MyView.RoundImageView;
import duy.phuong.handnote.Support.SharedPreferenceUtils;
import duy.phuong.handnote.Support.SupportUtils;

public class StartActivity extends Activity implements View.OnClickListener{
    private ViewPager mIntroPager;
    private IntroductionPager mPagerAdapter;
    private ScheduledExecutorService mService;
    private int mPosition;
    private HashMap<Integer, View> mListLayout;
    private LinearLayout mLayoutLoading, mLayoutIntro, mLayoutLogin;
    private LinearLayout mButtonStartApp;
    private LinearLayout mButtonGetStarted;
    private ImageButton mButtonVerify, mButtonCancel, mButtonLoadAvatar;
    private EditText mEdtName;

    private RoundImageView mSelectedImage;
    private Bitmap mAvatar;

    public static final int SELECT_IMAGE = 1;

    private long mCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mButtonStartApp = (LinearLayout) findViewById(R.id.buttonStartApp);
        mButtonStartApp.setOnClickListener(this);
        mLayoutLoading = (LinearLayout) findViewById(R.id.loadScreen);
        mLayoutIntro = (LinearLayout) findViewById(R.id.layoutIntro);
        mLayoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLayoutLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() - time < 5000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
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
        asyncTask.execute();

    }

    private void showLoginScreen() {
        mLayoutIntro.setVisibility(View.GONE);
        mLayoutLogin.setVisibility(View.VISIBLE);
        mButtonGetStarted = (LinearLayout) findViewById(R.id.buttonGetStarted);
        mButtonGetStarted.setOnClickListener(this);
        mButtonCancel = (ImageButton) findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(this);
        mButtonVerify = (ImageButton) findViewById(R.id.buttonVerify);
        mButtonVerify.setOnClickListener(this);
        mButtonLoadAvatar = (ImageButton) findViewById(R.id.buttonSelectAvatar);
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
                "Let write all your letters by your fingers or your pen (if you have one) \non your device screen surface\n and we'll recognize it for you."
        ));
        contents.add(new SpannableString(
                "It can be smarter and smarter by recognizing your text.\n Here we give you some features that allow you to know generally how this app work."
        ));
        contents.add(new SpannableString(
                "Our app have no third-party engines, so it's still quite limited. \n If you're interested in developing this kind of app, please help us to improve it or tell us you problems by contacting us."
        ));
        String contact = "Social: Facebook. \nMail: G-mail";
        SpannableString contactSpan = new SpannableString(contact);
        ClickableSpan clickFacebook = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(StartActivity.this, "Facebook: Duy Phuong", Toast.LENGTH_LONG).show();
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

        mPagerAdapter = new IntroductionPager(this, titles, contents, R.layout.layout_slide);
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
        mService = Executors.newSingleThreadScheduledExecutor();
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
                SharedPreferenceUtils.viewedIntro();
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

        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intentChooser = Intent.createChooser(intentPick, "Select a source");
        intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentTakePhoto});
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
                    String path = SupportUtils.getPath(data.getData(), StartActivity.this);
                    mAvatar = BitmapFactory.decodeFile(path);
                    if (mAvatar != null) {
                        mSelectedImage.setImageBitmap(mAvatar);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
