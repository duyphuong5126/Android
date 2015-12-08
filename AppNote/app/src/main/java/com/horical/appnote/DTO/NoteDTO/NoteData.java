package com.horical.appnote.DTO.NoteDTO;

import java.util.ArrayList;
import java.util.HashMap;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.DTO.BaseDTO;
import com.horical.appnote.Interfaces.NoteDataLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Phuong on 24/07/2015.
 */
public class NoteData extends BaseDTO {
    private NoteSummary mNoteSummary;
    private ArrayList<NoteDataLine> mNoteData;

    public NoteData() {
        this.mNoteSummary = new NoteSummary();
        this.mNoteData = new ArrayList<>();
    }

    public NoteData(NoteSummary noteSummary, ArrayList<NoteDataLine> noteData) {
        this.mNoteSummary = noteSummary;
        this.mNoteData = noteData;
    }

    public void clear() {
        mNoteData.clear();
        mNoteSummary = null;
    }

    public NoteSummary getNoteSummary() {
        return mNoteSummary;
    }

    public void setNoteSummary(NoteSummary noteSummary) {
        this.mNoteSummary = noteSummary;
    }

    public ArrayList<NoteDataLine> getNoteData() {
        return mNoteData;
    }

    @Override
    public String toString() {
        String content = "";
        content += mNoteSummary.getTitle() + "\n" + mNoteSummary.getCreatedAt() + "\n" + mNoteSummary.getModifiedAt() + "\n";
        if (mNoteData != null) {
            for (NoteDataLine noteDataLine : mNoteData) {
                switch (noteDataLine.typeIdentify()) {
                    case DataConstant.TYPE_TEXT:
                        content += ((NoteText) noteDataLine).getContent() + "\n";
                        break;
                    case DataConstant.TYPE_VIDEOCLIP:
                        content += ((NoteVideoClip) noteDataLine).getFileName() + "\n";
                        break;
                    case DataConstant.TYPE_VOICE:
                        content += ((NoteVoice) noteDataLine).getFileName() + "\n";
                        break;
                    default:
                        break;
                }
            }
        }
        return content;
    }

    public void setNoteData(ArrayList<NoteDataLine> noteData) {
        this.mNoteData = noteData;
    }

    @Override
    public int checkTypeDTO() {
        return BaseDTO.NOTE_OBJECT;
    }

    @Override
    public NoteData parse(JSONObject jsonObject) {
        NoteData noteData = new NoteData();
        ArrayList<NoteDataLine> noteDataLines = new ArrayList<NoteDataLine>();
        NoteSummary noteSummary = new NoteSummary();
        noteSummary.setUploaded(true);
        JSONArray jsonArray;
        try {
            if (jsonObject.has("title")) {
                noteSummary.setTitle(jsonObject.getString("title"));
            }
            if (jsonObject.has("dateCreated")) {
                noteSummary.setCreatedAt(jsonObject.getString("dateCreated"));
            }
            if (jsonObject.has("dateModified")) {
                noteSummary.setCreatedAt(jsonObject.getString("dateModified"));
            }
            if (jsonObject.has("content")) {
                jsonArray = new JSONArray(jsonObject.getString("content"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("content")) {
                        switch (object.getString("type")) {
                            case DataConstant.TYPE_TEXT:
                                NoteText noteText = new NoteText();
                                noteText.setType(object.getString("type"));
                                noteText.setContent(object.getString("content"));
                                noteDataLines.add(noteText);
                                break;
                            case DataConstant.TYPE_IMAGE:
                                NoteImage noteImage = new NoteImage();
                                noteImage.setFilePath(object.getString("content"));
                                noteDataLines.add(noteImage);
                                break;
                            case DataConstant.TYPE_VIDEOCLIP:
                                NoteVideoClip noteVideoClip = new NoteVideoClip(object.getString("content"));
                                noteDataLines.add(noteVideoClip);
                                break;
                            case DataConstant.TYPE_VOICE:
                                NoteVoice noteVoice = new NoteVoice(object.getString("content"));
                                noteDataLines.add(noteVoice);
                                break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        noteData.setNoteSummary(noteSummary);
        noteData.setNoteData(noteDataLines);
        return noteData;
    }

    @Override
    public String createJSON() {
        JSONObject jsonNote = new JSONObject();
        try {
            jsonNote.put("title", mNoteSummary.getTitle());
            jsonNote.put("dateCreated", mNoteSummary.getCreatedAt());
            jsonNote.put("dateModified", mNoteSummary.getModifiedAt());
            JSONArray jsonNoteLines = new JSONArray();
            for (NoteDataLine noteDataLine : mNoteData) {
                switch (noteDataLine.typeIdentify()) {
                    case DataConstant.TYPE_TEXT:
                        JSONObject jsonNoteText = new JSONObject();
                        NoteText noteText = (NoteText) noteDataLine;
                        jsonNoteText.put("type", noteText.typeIdentify());
                        jsonNoteText.put("content", noteText.getContent());
                        jsonNoteLines.put(jsonNoteText);
                        break;
                    case DataConstant.TYPE_IMAGE:
                        JSONObject jsonNoteImage = new JSONObject();
                        NoteImage noteImage = (NoteImage) noteDataLine;
                        jsonNoteImage.put("type", noteImage.typeIdentify());
                        jsonNoteImage.put("content", noteImage.getFilePath());
                        jsonNoteLines.put(jsonNoteImage);
                        break;
                    case DataConstant.TYPE_VOICE:
                        JSONObject jsonNoteVoice = new JSONObject();
                        NoteVoice noteVoice = (NoteVoice) noteDataLine;
                        jsonNoteVoice.put("type", noteVoice.typeIdentify());
                        jsonNoteVoice.put("content", noteVoice.getFilePath());
                        jsonNoteLines.put(jsonNoteVoice);
                        break;
                    case DataConstant.TYPE_VIDEOCLIP:
                        JSONObject jsonNoteVideo = new JSONObject();
                        NoteVideoClip noteVideoClip = (NoteVideoClip) noteDataLine;
                        jsonNoteVideo.put("type", noteVideoClip.typeIdentify());
                        jsonNoteVideo.put("content", noteVideoClip.getFilePath());
                        jsonNoteLines.put(jsonNoteVideo);
                        break;
                    default:
                        break;
                }
            }
            jsonNote.put("content", jsonNoteLines);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonNote.toString();
    }
}
