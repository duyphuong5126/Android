package com.horical.appnote.MyView.NoteDataView;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Phuong on 05/09/2015.
 */
public class FormattedText {

    public static final byte NORMAL = 1;
    public static final byte BOLD = 2;
    public static final byte ITALIC = 3;
    public static final byte UNDERLINE = 4;
    public static final byte BOLD_ITALIC = 5;
    public static final byte BOLD_UNDERLINE = 6;
    public static final byte ITALIC_UNDERLINE = 7;
    public static final byte BOLD_ITALIC_UNDERLINE = 8;
    private ArrayList<Byte> mFormats;
    private static byte mCurrentFormat = NORMAL;
    private EditText mEditText;

    public FormattedText(EditText editText) {
        mFormats = new ArrayList<>();
        mEditText = editText;
    }

    public FormattedText(EditText editText, int number) {
        mFormats = new ArrayList<>();
        mEditText = editText;
        for (int i = 0; i < number; i++) {
            mFormats.add(NORMAL);
        }
    }

    public FormattedText(EditText editText, String format) {
        mFormats = new ArrayList<>();
        mEditText = editText;
        for (int i = 0; i < format.length(); i++) {
            mFormats.add(Byte.valueOf(String.valueOf(format.charAt(i))));
        }
    }

    public int getFormatLength() {
        return mFormats.size();
    }

    public String formatToString() {
        String result = "";
        for (int format : mFormats) {
            result += String.valueOf(format);
        }
        return result;
    }

    public boolean matchEditText(int id) {
        return mEditText.getId() == id;
    }

    public ArrayList<Byte> getFormats() {
        return mFormats;
    }

    public void addFormatItem(int position) {
        if (position >= 0) {
            mFormats.add(position, mCurrentFormat);
        }
    }

    public static String changeCurrentFormat(byte format) {
        switch (mCurrentFormat) {
            case NORMAL:
                mCurrentFormat = format;
                break;
            case BOLD:
                if (format == BOLD) {
                    mCurrentFormat = NORMAL;
                } else {
                    mCurrentFormat += format;
                }
                break;
            case ITALIC:
                if (format == ITALIC) {
                    mCurrentFormat = NORMAL;
                } else {
                    mCurrentFormat += format;
                }
                break;
            case UNDERLINE:
                if (format == UNDERLINE) {
                    mCurrentFormat = NORMAL;
                } else {
                    mCurrentFormat += format;
                }
                break;
            case BOLD_ITALIC:
                if (format == BOLD || format == ITALIC) {
                    mCurrentFormat -= format;
                } else {
                    mCurrentFormat = BOLD_ITALIC_UNDERLINE;
                }
                break;
            case BOLD_UNDERLINE:
                if (format == BOLD || format == UNDERLINE) {
                    mCurrentFormat -= format;
                } else {
                    mCurrentFormat = BOLD_ITALIC_UNDERLINE;
                }
                break;
            case ITALIC_UNDERLINE:
                if (format == UNDERLINE || format == ITALIC) {
                    mCurrentFormat -= format;
                } else {
                    mCurrentFormat = BOLD_ITALIC_UNDERLINE;
                }
                break;
            case BOLD_ITALIC_UNDERLINE:
                if (format == BOLD) {
                    mCurrentFormat = ITALIC_UNDERLINE;
                } else {
                    if (format == ITALIC) {
                        mCurrentFormat = BOLD_UNDERLINE;
                    } else {
                        mCurrentFormat = BOLD_ITALIC;
                    }
                }
                break;
            default:
                return "";
        }

        return getCurrentMode();
    }

    private static String getCurrentMode(){
        switch (mCurrentFormat) {
            case FormattedText.NORMAL:
                return "normal";
            case FormattedText.BOLD:
                return "bold";
            case FormattedText.ITALIC:
                return "italic";
            case FormattedText.UNDERLINE:
                return "underline";
            case FormattedText.BOLD_ITALIC:
                return "bold_italic";
            case FormattedText.BOLD_UNDERLINE:
                return "bold_underline";
            case FormattedText.ITALIC_UNDERLINE:
                return "italic_underline";
            case FormattedText.BOLD_ITALIC_UNDERLINE:
                return "bold_italic_underline";
            default:
                return "";
        }
    }

    public void addNewBold(int position) {
        if (position < mFormats.size()) {
            mFormats.add(position, FormattedText.BOLD);
        }
    }

    public void addNewItalic(int position) {
        if (position < mFormats.size()) {
            mFormats.add(position, FormattedText.ITALIC);
        }
    }

    public void addNewUnderline(int position) {
        if (position < mFormats.size()) {
            mFormats.add(position, FormattedText.UNDERLINE);
        }
    }

    public void addNewNormal(int position) {
        if (position < mFormats.size()) {
            mFormats.add(position, FormattedText.NORMAL);
        }
    }

    public void removeCharacter(int position) {
        if (position < mFormats.size() && position >= 0) {
            mFormats.remove(position);
        }
    }

    public Editable getFormattedText() {
        Editable editable = mEditText.getText();
        Log.d("getFormattedText", "in");
        for (int i = 0; i < mFormats.size(); i++) {
            Log.d("Pos", "" + i + " type: " + mFormats.get(i));
            Log.d("Text", editable.toString());
            switch (mFormats.get(i)) {
                case BOLD:
                    editable.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, 0);
                    break;
                case ITALIC:
                    editable.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, 0);
                    break;
                case UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    break;
                case BOLD_ITALIC:
                    editable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, i + 1, 0);
                    break;
                case BOLD_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, 0);
                    break;
                case ITALIC_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, 0);
                    break;
                case BOLD_ITALIC_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, i + 1, 0);
                    break;
                default:
                    editable.setSpan(new StyleSpan(Typeface.NORMAL), i, i + 1, 0);
            }
        }
        return editable;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public static String formatString(String content, String format) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
            jsonObject.put("format", format);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getContentFromFormatedJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.has("content")?jsonObject.getString("content"):"";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormatFromFormatedJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.has("format")?jsonObject.getString("format"):"";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Editable getFormattedText(String content, String format) {
        Editable editable = Editable.Factory.getInstance().newEditable(content);
        for (int i = 0; i < format.length(); i++) {
            switch (Byte.valueOf(String.valueOf(format.charAt(i)))) {
                case BOLD:
                    editable.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case ITALIC:
                    editable.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case BOLD_ITALIC:
                    editable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case BOLD_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case ITALIC_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case BOLD_ITALIC_UNDERLINE:
                    editable.setSpan(new UnderlineSpan(), i, i + 1, 0);
                    editable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
            }
        }
        return editable;
    }
}
