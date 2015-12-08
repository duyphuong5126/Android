package com.horical.appnote.ServerStorage;

import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.DAO.NoteDataDAO;
import com.horical.appnote.ServerStorage.Callback.DeleteNoteCallback;
import com.horical.appnote.ServerStorage.Callback.DownloadDataCallback;
import com.horical.appnote.ServerStorage.Callback.NoteCallback;
import com.horical.appnote.ServerStorage.Entities.FileObject;
import com.horical.appnote.ServerStorage.Entities.NoteObject;
import com.horical.appnote.ServerStorage.Param.UploadNoteParam;
import com.horical.appnote.ServerStorage.Response.NoteResponse;
import com.horical.appnote.Supports.LanguageUtils;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Phuong on 12/11/2015.
 */
public class NoteRoute extends BaseRoute<NoteObject> {
    private int mCurrentDeletedNotes = 0;
    public NoteRoute() {
        super(NoteObject.class);
    }

    public void uploadNote(final UploadNoteParam param, final NoteCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(NoteObject.USER_ID, ApplicationSharedData.getUserID());
        this.getAllDataFromServer(new GetDataCallback<NoteObject>() {
            @Override
            public void onReceiveData(ArrayList<NoteObject> data) {
                NoteResponse response = new NoteResponse();
                for (NoteObject noteObject : data) {
                    if (param.userId.equals(noteObject.getUserId())) {
                        response.success = true;
                        response.message = LanguageUtils.getNotifyNoteExistedString();
                        callback.onSuccess(response);
                        return;
                    }
                }

                NoteObject noteObject = new NoteObject();
                noteObject.setUserId(param.userId);
                noteObject.setId(param.id);
                noteObject.setContent(param.content);
                noteObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            callback.onFail(e);
                        } else {
                            NoteResponse response = new NoteResponse();
                            response.success = true;
                            response.message = LanguageUtils.getNotifyUploadSuccessString();
                            callback.onSuccess(response);
                        }
                    }
                });
            }

            @Override
            public void onError(ParseException e) {

            }
        }, conditions);
    }

    public void deleteAllNotes(final DeleteNoteCallback callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(FileObject.USER_ID, ApplicationSharedData.getUserID());

        this.getAllDataFromServer(new GetDataCallback<NoteObject>() {
            @Override
            public void onReceiveData(final ArrayList<NoteObject> data) {
                if (data.isEmpty()) {
                    NoteResponse response = new NoteResponse();
                    response.success = true;
                    response.message = LanguageUtils.getNotifyNothingDownloadString();
                    callback.onSuccess(response);
                } else {
                    for (NoteObject noteObject : data) {
                        noteObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    NoteResponse response = new NoteResponse();
                                    mCurrentDeletedNotes++;
                                    if (mCurrentDeletedNotes == data.size()) {
                                        response.success = true;
                                        response.message =
                                                LanguageUtils.getDeleteString() + " " + LanguageUtils.getNotifyCompletedString().toLowerCase();
                                        callback.onSuccess(response);
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

            }
        }, conditions);
    }

    public void getAllNotes(final DownloadDataCallback<NoteData> callback) {
        HashMap<String, String> conditions = new HashMap<>();
        conditions.put(FileObject.USER_ID, ApplicationSharedData.getUserID());

        this.getAllDataFromServer(new GetDataCallback<NoteObject>() {
            @Override
            public void onReceiveData(ArrayList<NoteObject> data) {
                if (data != null && !data.isEmpty()) {
                    ArrayList<NoteData> list = new ArrayList<>();
                    NoteData noteData = new NoteData();
                    for (NoteObject noteObject : data) {
                        NoteData item = null;
                        try {
                            item = noteData.parse(new JSONObject(noteObject.getContent()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list.add(item);
                    }
                    if (list.isEmpty()) {
                        NoteResponse response = new NoteResponse();
                        response.success = false;
                        response.message = LanguageUtils.getNotifyDownloadFailString();
                        callback.onFail(response);
                    } else {
                        callback.onDownloadSuccess(list);
                    }
                } else {
                    NoteResponse response = new NoteResponse();
                    response.success = true;
                    response.message = LanguageUtils.getNotifyNothingDownloadString();
                    callback.onFail(response);
                }
            }

            @Override
            public void onError(ParseException e) {

            }
        }, conditions);
    }
}
