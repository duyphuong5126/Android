package duy.phuong.handnote.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Recognizer.BitmapProcessor;
import duy.phuong.handnote.Recognizer.ImageToText;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;

/**
 * Created by Phuong on 24/05/2016.
 */
public class WebFragment extends BaseFragment implements BitmapProcessor.DetectCharactersCallback, BackPressListener {
    private WebView mWebView;
    private FingerDrawerView mDrawer;
    private LinearLayout mLayoutProcessing;
    private String mUrl = "";
    private static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private Pattern mPattern;
    public WebFragment() {
        mLayoutRes = R.layout.fragment_web;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(this);
        mDrawer.setDisplayListener(this);
        mDrawer.setDefault();
        mPattern = Pattern.compile(URL_REGEX);
        mWebView = (WebView) mFragmentView.findViewById(R.id.webSearch);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mLayoutProcessing.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLayoutProcessing.setVisibility(View.GONE);
            }
        });
        mLayoutProcessing = (LinearLayout) mFragmentView.findViewById(R.id.layoutProcessing);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.WEB_FRAGMENT;
    }

    @Override
    public void onBeginDetect(Bundle bundle) {
        mLayoutProcessing.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetectSuccess(final ArrayList<Character> listCharacters) {
        if (listCharacters.size() > 0) {
            for (Character character : listCharacters) {
                character.isSorted = false;
            }

            final ArrayList<Line> currentLines = mDrawer.getLines();

            ImageToText imageToText = new ImageToText(mListener.getGlobalSOM(), mListener.getMapNames());
            imageToText.imageToText(currentLines, listCharacters, new ImageToText.ConvertingCompleteCallback() {
                @Override
                public void convertingComplete(String result, HashMap<Input, Point> map) {
                    Matcher m = mPattern.matcher(result);
                    if(m.find()) {
                        mUrl = result;
                    } else {
                        mUrl = "https://www.google.com.vn/search?q=" + result;
                    }
                    mWebView.loadUrl(mUrl);
                }
            });
        } else {
            mLayoutProcessing.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean doBack() {
        if (!mDrawer.isEmpty()) {
            mDrawer.emptyDrawer();
            mUrl = "";
            return true;
        }
        return false;
    }
}
