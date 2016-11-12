package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class User extends BaseDTO{
    private String mUserName;
    private String mPassword;
    private String mEmail;

    public User() {
        super();
    }

    public User(String UserName, String Password, String Email) {
        super();
        this.mUserName = UserName;
        this.mPassword = Password;
        this.mEmail = Email;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
