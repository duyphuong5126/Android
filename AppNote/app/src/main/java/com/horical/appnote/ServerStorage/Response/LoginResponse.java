package com.horical.appnote.ServerStorage.Response;

import com.parse.ParseFile;

/**
 * Created by Phuong on 06/11/2015.
 */
public class LoginResponse extends BaseResponse {
    public String username;
    public String displayname;
    public String email;
    public String userId;
    public ParseFile avatar;
}
