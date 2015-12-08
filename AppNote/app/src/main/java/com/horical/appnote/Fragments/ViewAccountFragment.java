package com.horical.appnote.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.MyView.RoundImageView;
import com.horical.appnote.R;
import com.horical.appnote.ServerStorage.Callback.ChangePasswordCallback;
import com.horical.appnote.ServerStorage.Param.ChangePasswordParam;
import com.horical.appnote.ServerStorage.Response.ChangePasswordResponse;
import com.horical.appnote.ServerStorage.UserRoute;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.parse.ParseException;

/**
 * Created by phuong on 09/10/2015.
 */
public class ViewAccountFragment extends BaseFragment implements View.OnClickListener {
    private String mUserName, mEmail, mAvatar, mUserId;

    private TextView mTvUsername, mTvEmail;
    private EditText mEdtOldPassword, mEdtNewPasword;
    private Button mBtnChangePassword, mBtnSaveChangePassword;

    private RoundImageView mAvatarView;

    private LinearLayout mLayoutChangePassword;
    public ViewAccountFragment() {
        this.mLayout_xml_id = R.layout.fragment_view_account;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserName = ApplicationSharedData.getUser();
        mEmail = ApplicationSharedData.getEmail();
        mAvatar = ApplicationSharedData.getAVATAR();
        mUserId = ApplicationSharedData.getUserID();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvUsername = (TextView) mFragmentView.findViewById(R.id.tvUserName);
        mTvEmail = (TextView) mFragmentView.findViewById(R.id.tvEmail);

        mEdtOldPassword = (EditText) mFragmentView.findViewById(R.id.edtOldPassword);
        mEdtOldPassword.setHint(LanguageUtils.getOldPasswordString());

        mEdtNewPasword = (EditText) mFragmentView.findViewById(R.id.edtNewPassword);
        mEdtNewPasword.setHint(LanguageUtils.getNewPasswordString());

        mBtnChangePassword = (Button) mFragmentView.findViewById(R.id.buttonChangePassword);
        mBtnChangePassword.setOnClickListener(this);
        mBtnChangePassword.setText(LanguageUtils.getChangePasswordString());

        mBtnSaveChangePassword = (Button) mFragmentView.findViewById(R.id.buttonSaveChangePassword);
        mBtnSaveChangePassword.setOnClickListener(this);
        mBtnSaveChangePassword.setText(LanguageUtils.getSaveChangesString().toUpperCase());

        mAvatarView = (RoundImageView) mFragmentView.findViewById(R.id.imageAvatar);
        mLayoutChangePassword = (LinearLayout) mFragmentView.findViewById(R.id.layoutEditPassword);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvUsername.setText(mUserName);
        if (mEmail == null || mEmail.equals("")) {
            mTvEmail.setVisibility(View.GONE);
        } else {
            mTvEmail.setText(mEmail);
        }
        this.checkAvatar();
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonChangePassword:
                if (mLayoutChangePassword.getVisibility() == View.GONE) {
                    mLayoutChangePassword.setVisibility(View.VISIBLE);
                    ((Button) view).setText(LanguageUtils.getCancelString().toUpperCase());
                } else {
                    mLayoutChangePassword.setVisibility(View.GONE);
                    ((Button) view).setText(LanguageUtils.getChangePasswordString().toUpperCase());
                }
                break;
            case R.id.buttonSaveChangePassword:
                if (mMainInterface.checkInternetAvailable()) {
                    String oldPassword = mEdtOldPassword.getText().toString();
                    String newPassword = mEdtNewPasword.getText().toString();
                    doChangePassword(oldPassword, newPassword);
                } else {
                    Toast.makeText(mActivity, LanguageUtils.getInternetOfflineString(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        this.hideSoftKeyboard();
    }

    public void doChangePassword(String oldPassword, String newPassword) {
        //TODO change password on parse.com
        ChangePasswordParam param = new ChangePasswordParam();
        param.userId = ApplicationSharedData.getUserID();
        param.oldPassword = oldPassword;
        param.newPassword = newPassword;

        UserRoute request = new UserRoute();
        request.doChangePassword(param, new ChangePasswordCallback() {
            @Override
            public void onSuccess(ChangePasswordResponse response) {
                Toast.makeText(mActivity, response.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(ParseException e) {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAvatar() {
        String[] avatars = FileUtils.getListFile("Avatar");
        if (avatars != null && avatars.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatars[0]);
            if (bitmap != null) {
                mAvatarView.setImageBitmap(bitmap);
            }
        }
    }

    private void hideSoftKeyboard() {
        View view = mActivity.getCurrentFocus();
        InputMethodManager input = (InputMethodManager) this.mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
