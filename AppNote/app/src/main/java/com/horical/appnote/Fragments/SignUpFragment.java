package com.horical.appnote.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.MyView.RoundImageView;
import com.horical.appnote.R;
import com.horical.appnote.ServerStorage.Callback.SignUpCallback;
import com.horical.appnote.ServerStorage.Param.SignUpParam;
import com.horical.appnote.ServerStorage.Response.SignUpResponse;
import com.horical.appnote.ServerStorage.UserRoute;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.SupportUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

/**
 * Created by Phuong on 03/09/2015.
 */
public class SignUpFragment extends BaseFragment implements Button.OnClickListener {
    private EditText mEdtUsername, mEdtEmail, mEdtDisplayName, mEdtPassword;
    private RoundImageView mAvatarView;

    private String mAvatarPath = "";
    private Bitmap mAvatar = null;
    private static final int LOAD_IMAGE = 1;

    public SignUpFragment(){
        this.mLayout_xml_id = R.layout.fragment_signup;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSignup:
                if (!mMainInterface.checkInternetAvailable()) {
                    Toast.makeText(mActivity, LanguageUtils.getInternetOfflineString(), Toast.LENGTH_SHORT).show();
                }
                final SignUpParam param = new SignUpParam();
                param.username = mEdtUsername.getText().toString();
                param.email = mEdtEmail.getText().toString();
                param.displayname = mEdtDisplayName.getText().toString();
                param.password = mEdtPassword.getText().toString();
                if (!param.email.equals("") && !param.username.equals("") && !param.password.equals("") ) {
                    //TODO sign up on parse.com database
                    final UserRoute request = new UserRoute();
                    if (mAvatar != null && !mAvatarPath.equals("")) {
                        FileUtils.writeFile("Avatar", mAvatarPath, "UserAvatar");
                        final ParseFile parseFile = new ParseFile(SupportUtils.getNameFromPath(mAvatarPath),
                                FileUtils.readFileAsBytes(mAvatarPath, DataConstant.TYPE_IMAGE));
                        parseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                param.avatar = parseFile;
                                request.doSignUp(param, new SignUpCallback() {
                                    @Override
                                    public void onSuccess(SignUpResponse response) {
                                        Toast.makeText(mActivity, response.message, Toast.LENGTH_SHORT).show();
                                        if (response.success) {
                                            mListener.onSignUpSuccess(
                                                    response.username, response.email, response.userId, response.displayname, parseFile);
                                        }
                                    }

                                    @Override
                                    public void onFail(ParseException e) {

                                    }
                                });
                            }
                        });
                    } else {
                        request.doSignUp(param, new SignUpCallback() {
                            @Override
                            public void onSuccess(SignUpResponse response) {
                                Toast.makeText(mActivity, response.message, Toast.LENGTH_SHORT).show();
                                if (response.success) {
                                    mListener.onSignUpSuccess(
                                            response.username, response.email, response.userId, response.displayname, null);
                                }
                            }

                            @Override
                            public void onFail(ParseException e) {

                            }
                        });
                    }

                } else {
                    Toast.makeText(mActivity, LanguageUtils.getMissFieldsString(), Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.imageAvatar:
                loadImageAvatar();
        }
    }

    private void loadImageAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, LanguageUtils.getSelectFileString()), LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case LOAD_IMAGE:
                    Uri uri = data.getData();
                    mAvatarPath = (FileUtils.checkFileIsFromContent(uri.getPath()) ? FileUtils.getPath(uri, mActivity) : uri.getPath());
                    mAvatar = BitmapFactory.decodeFile(mAvatarPath);
                    mAvatarView.setImageBitmap(mAvatar);
                    break;
            }
        }
    }

    public interface SignUpListener{
        void onSignUpSuccess(String username, String email, String id, String displayname, ParseFile avatar);
    }

    private SignUpListener mListener;

    public void setListener(SignUpListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView mTvSignUpTitle = (TextView) mFragmentView.findViewById(R.id.tvSignUpTitle);
        mTvSignUpTitle.setText(LanguageUtils.getSignUpString());

        mEdtUsername = (EditText) mFragmentView.findViewById(R.id.edtUsername);
        mEdtUsername.setHint(LanguageUtils.getUserNameString());

        mEdtDisplayName = (EditText) mFragmentView.findViewById(R.id.edtDisplayName);
        mEdtDisplayName.setHint(LanguageUtils.getDisplayNameString());

        mEdtEmail = (EditText) mFragmentView.findViewById(R.id.edtEmail);

        mEdtPassword = (EditText) mFragmentView.findViewById(R.id.edtPassword);
        mEdtPassword.setHint(LanguageUtils.getPasswordString());

        Button mBtnSignUp = (Button) mFragmentView.findViewById(R.id.buttonSignup);
        mBtnSignUp.setText(LanguageUtils.getSignUpString());
        mBtnSignUp.setOnClickListener(this);

        mAvatarView = (RoundImageView) mFragmentView.findViewById(R.id.imageAvatar);
        mAvatarView.setOnClickListener(this);
    }

    @Override
    public void updateUI() {

    }
}
