package com.horical.appnote.ServerStorage;

import com.horical.appnote.ServerStorage.Callback.ChangePasswordCallback;
import com.horical.appnote.ServerStorage.Callback.LoginCallback;
import com.horical.appnote.ServerStorage.Callback.SignUpCallback;
import com.horical.appnote.ServerStorage.Entities.UserObject;
import com.horical.appnote.ServerStorage.Param.ChangePasswordParam;
import com.horical.appnote.ServerStorage.Param.LoginParam;
import com.horical.appnote.ServerStorage.Param.SignUpParam;
import com.horical.appnote.ServerStorage.Response.ChangePasswordResponse;
import com.horical.appnote.ServerStorage.Response.LoginResponse;
import com.horical.appnote.ServerStorage.Response.SignUpResponse;
import com.horical.appnote.Supports.LanguageUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Phuong on 06/11/2015.
 */
public class UserRoute extends BaseRoute<UserObject> {

    public UserRoute() {
        super(UserObject.class);
    }

    public void doLogin(final LoginParam param, final LoginCallback callback) {
        this.getAllDataFromServer(new GetDataCallback<UserObject>() {
            @Override
            public void onReceiveData(ArrayList<UserObject> data) {
                if (data != null) {
                    for (UserObject userObject : data) {
                        if (param.password.equals(userObject.getPassword()) &&
                                (param.username.equals(userObject.getUsername()) || param.username.equals(userObject.getEmail()))) {
                            LoginResponse response = new LoginResponse();
                            response.message = LanguageUtils.getLoginSuccessString();
                            response.success = true;
                            response.username = userObject.getUsername();
                            response.displayname = userObject.getDisplayname();
                            response.email = userObject.getEmail();
                            response.userId = userObject.getUserId();
                            if (userObject.getAvatar() != null) {
                                response.avatar = userObject.getAvatar();
                            }
                            callback.onSuccess(response);
                            return;
                        }
                    }
                }
                LoginResponse response = new LoginResponse();
                response.message = LanguageUtils.getWrongAccountString();
                response.success = false;
                callback.onSuccess(response);
            }

            @Override
            public void onError(ParseException e) {
                callback.onFail(e);
            }
        });
    }

    public void doSignUp(final SignUpParam param, final SignUpCallback callback) {
        this.getAllDataFromServer(new GetDataCallback<UserObject>() {
            @Override
            public void onReceiveData(ArrayList<UserObject> data) {
                if (data != null) {
                    for (UserObject userObject : data) {
                        if (param.username.equals(userObject.getUsername()) || param.email.equals(userObject.getEmail())) {
                            SignUpResponse response = new SignUpResponse();
                            response.message = LanguageUtils.getAccountExistedString();
                            response.success = false;
                            callback.onSuccess(response);
                            return;
                        }
                    }

                    final UserObject userObject = new UserObject();
                    userObject.setUsername(param.username);
                    userObject.setEmail(param.email);
                    userObject.setPassword(param.password);
                    userObject.setDisplayname(param.displayname);
                    if (param.avatar != null) {
                        userObject.setAvatar(param.avatar);
                    }
                    final String userId = "User" + System.nanoTime();
                    userObject.setUserID(userId);
                    userObject.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            SignUpResponse response = new SignUpResponse();
                            response.message = LanguageUtils.getSignUpSuccessString();
                            response.success = true;
                            response.displayname = param.displayname;
                            response.userId = userId;
                            response.email = param.email;
                            response.username = param.username;
                            callback.onSuccess(response);
                        }
                    });
                }
            }

            @Override
            public void onError(ParseException e) {
                callback.onFail(e);
            }
        });
    }

    public void doChangePassword(final ChangePasswordParam param, final ChangePasswordCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(UserObject.USER_ID, param.userId);
        conditions.put(UserObject.PASSWORD, param.oldPassword);
        this.getAllDataFromServer(new GetDataCallback<UserObject>() {
            @Override
            public void onReceiveData(ArrayList<UserObject> data) {
                if (data == null || data.isEmpty()) {
                    ChangePasswordResponse response = new ChangePasswordResponse();
                    response.success = false;
                    response.message = LanguageUtils.getWrongCurrentPasswordString();
                    callback.onSuccess(response);
                } else {
                    UserObject userObject = data.get(0);
                    userObject.put(UserObject.PASSWORD, param.newPassword);
                    userObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ChangePasswordResponse response = new ChangePasswordResponse();
                                response.success = true;
                                response.message = LanguageUtils.getPasswordChangeString();
                                callback.onSuccess(response);
                            } else {
                                callback.onFail(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(ParseException e) {

            }
        }, conditions);
    }
}
