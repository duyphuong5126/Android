package com.horical.appnote.ServerStorage;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.ServerStorage.Callback.DeleteFileCallback;
import com.horical.appnote.ServerStorage.Callback.DownloadFileCallback;
import com.horical.appnote.ServerStorage.Callback.UploadFileCallback;
import com.horical.appnote.ServerStorage.Entities.FileObject;
import com.horical.appnote.ServerStorage.Param.UploadFileParam;
import com.horical.appnote.ServerStorage.Response.FileResponse;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Phuong on 08/11/2015.
 */
public class FileRoute extends BaseRoute<FileObject> {
    private int mCurrentDeletedFiles = 0;
    public FileRoute() {
        super(FileObject.class);
    }

    public void uploadFile(final UploadFileParam param, final UploadFileCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(FileObject.USER_ID, ApplicationSharedData.getUserID());

        this.getAllDataFromServer(new GetDataCallback<FileObject>() {
            @Override
            public void onReceiveData(ArrayList<FileObject> data) {
                FileResponse response = new FileResponse();
                for (FileObject object : data) {
                    if (param.name.equals(object.getName())) {
                        response.message = LanguageUtils.getNotifyFileExistedString();
                        response.success = false;
                        callback.onSuccess(response);
                        return;
                    }
                }
                final ParseFile file = new ParseFile(param.name, FileUtils.readFileAsBytes(param.path, param.type));
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        FileObject fileObject = new FileObject();
                        fileObject.setName(param.name);
                        fileObject.setType(param.type);
                        fileObject.setUserId(ApplicationSharedData.getUserID());
                        fileObject.setContent(file);

                        fileObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                FileResponse response = new FileResponse();
                                if (e == null) {
                                    response.message = LanguageUtils.getNotifyUploadSuccessString();
                                    response.success = true;
                                    callback.onSuccess(response);
                                } else {
                                    callback.onFail(e);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(ParseException e) {

            }
        }, conditions);
    }

    public void getAllFileFromServer(final DownloadFileCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(FileObject.USER_ID, ApplicationSharedData.getUserID());

        this.getAllDataFromServer(new GetDataCallback<FileObject>() {
            @Override
            public void onReceiveData(ArrayList<FileObject> data) {
                FileResponse response = new FileResponse();
                if (!data.isEmpty()) {
                    boolean final_result = true;
                    for (FileObject fileObject : data) {
                        try {
                            boolean result = FileUtils.writeFile(fileObject.getType(), fileObject.getContent().getData(), fileObject.getName());
                            if (result == false) {
                                final_result = result;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    response.success = final_result;
                    if (final_result) {
                        response.message = LanguageUtils.getNotifyDownloadSuccessString();
                    } else {
                        response.message = LanguageUtils.getNotifyDownloadFailString();
                    }
                } else {
                    response.success = true;
                    response.message = LanguageUtils.getNotifyNothingDownloadString();
                }

                callback.onSuccess(response);
            }

            @Override
            public void onError(ParseException e) {
                callback.onFail(e);
            }
        }, conditions);
    }

    public void deleteAllFiles(final DeleteFileCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(FileObject.USER_ID, ApplicationSharedData.getUserID());

        this.getAllDataFromServer(new GetDataCallback<FileObject>() {
            @Override
            public void onReceiveData(final ArrayList<FileObject> data) {
                if (data.isEmpty()) {
                    FileResponse response = new FileResponse();
                    response.success = true;
                    response.message = LanguageUtils.getNotifyNothingDeleteString();
                    callback.onSuccess(response);
                } else {
                    for (FileObject fileObject : data) {
                        fileObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mCurrentDeletedFiles++;
                                    FileResponse response = new FileResponse();
                                    if (mCurrentDeletedFiles == data.size()) {
                                        response.success = true;
                                        response.message = LanguageUtils.getDeleteFileSuccessString();
                                        callback.onSuccess(response);
                                        mCurrentDeletedFiles = 0;
                                    }
                                } else {
                                    callback.onFail(e);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(ParseException e) {
                callback.onFail(e);
            }
        }, conditions);
    }
}
