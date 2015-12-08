package com.horical.appnote.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.MainActivity;
import com.horical.appnote.R;
import com.horical.appnote.ServerStorage.Callback.LoginCallback;
import com.horical.appnote.ServerStorage.Param.LoginParam;
import com.horical.appnote.ServerStorage.Response.LoginResponse;
import com.horical.appnote.ServerStorage.UserRoute;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.parse.ParseException;
import com.parse.ParseFile;

/**
 * Created by Phuong on 03/09/2015.
 */
public class LoginFragment extends BaseFragment implements Button.OnClickListener{
    private EditText mEdtUsername, mEdtPassword;
    private Button mBtnLogin, mBtnSignUp, mBtnForgot;
    private TextView mTvInternetSignal, mTvOr;
    private ImageView mImgInternetSignal;
    private LinearLayout mLoginProcessLayout;


    public interface LoginListener{
        void onLoginSuccess(String username, String email, String id, String displayname, ParseFile avatar);
        void onForgotPassword();
        void onSignUp();
    }

    private LoginListener mListener;

    public void setListener(LoginListener listener) {
        this.mListener = listener;
    }

    public LoginFragment(){
        this.mLayout_xml_id = R.layout.fragment_login;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvOr = (TextView) mFragmentView.findViewById(R.id.tvDivider);
        mTvOr.setText(LanguageUtils.getOrString());

        mEdtUsername = (EditText) this.mFragmentView.findViewById(R.id.edtUsername);
        mEdtUsername.setHint(LanguageUtils.getUsernameOrEmailString());

        mEdtPassword = (EditText) this.mFragmentView.findViewById(R.id.edtPassword);
        mEdtPassword.setHint(LanguageUtils.getPasswordString());

        mBtnLogin = (Button) this.mFragmentView.findViewById(R.id.buttonLogin);
        mBtnLogin.setOnClickListener(this);
        mBtnLogin.setText(LanguageUtils.getLoginString().toUpperCase());

        mBtnSignUp = (Button) this.mFragmentView.findViewById(R.id.buttonSignup);
        mBtnSignUp.setOnClickListener(this);
        mBtnSignUp.setText(LanguageUtils.getSignUpString());

        mBtnForgot = (Button) this.mFragmentView.findViewById(R.id.buttonForgetPassword);
        mBtnForgot.setOnClickListener(this);
        mBtnForgot.setText(LanguageUtils.getForgotPasswordString());

        mTvInternetSignal = (TextView) mFragmentView.findViewById(R.id.tvInternetSignal);
        mImgInternetSignal = (ImageView) mFragmentView.findViewById(R.id.imgInternetSignal);
        mLoginProcessLayout = (LinearLayout) mFragmentView.findViewById(R.id.layoutLoginProcess);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void doLogin(final String username, String password) {
        mLoginProcessLayout.setVisibility(View.VISIBLE);
        UserRoute request = new UserRoute();
        LoginParam param = new LoginParam();
        param.username = username;
        param.password = password;
        request.doLogin(param, new LoginCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                Toast.makeText(mActivity, response.message, Toast.LENGTH_SHORT).show();
                if (response.success) {
                    mListener.onLoginSuccess(response.username, response.email, response.userId, response.displayname, response.avatar);
                }
                mLoginProcessLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFail(ParseException e) {

            }
        });
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                if (mMainInterface.checkInternetAvailable()) {
                    doLogin(mEdtUsername.getText().toString().trim(), mEdtPassword.getText().toString().trim());
                } else {
                    Toast.makeText(mActivity, LanguageUtils.getInternetOfflineString(), Toast.LENGTH_SHORT);
                }
                return;
            case R.id.buttonForgetPassword:
                this.mListener.onForgotPassword();
                return;
            case R.id.buttonSignup:
                this.mListener.onSignUp();
                return;
            default:
                break;
        }
    }

    public void setInternetSignal(boolean isInternetConnected) {
        mTvInternetSignal.setText((isInternetConnected)?LanguageUtils.getInternetOnlineString() : LanguageUtils.getInternetOfflineString());
        mImgInternetSignal.setImageResource((isInternetConnected)?
                R.drawable.ic_signal_wifi_4_bar_white_24dp:R.drawable.ic_signal_wifi_off_white_24dp);
    }

    @Override
    public void onStop() {
        super.onStop();
        mLoginProcessLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLoginProcessLayout.setVisibility(View.GONE);
    }
}
