package duy.phuong.handnote.Recognizer;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;
import duy.phuong.handnote.Recognizer.MachineLearning.SOM;

/**
 * Created by Phuong on 25/05/2016.
 */
public class ImageToText {
    private int mCountCharacters;
    private int mLineCount;
    private SOM mSOM;
    private ArrayList<ClusterLabel> mLabels;
    public interface ConvertingCompleteCallback {
        void convertingComplete(String result, HashMap<Input, Point> map);
    }

    public ImageToText(SOM som, ArrayList<ClusterLabel> mapNames) {
        mSOM = som; mLabels = mapNames;
    }

    private boolean isSameLine(Character c1, Character c2) {
        Rect r1 = c1.mRect, r2 = c2.mRect;
        int mid1 = r1.height() / 2 + r1.top, mid2 = r2.height() / 2 + r2.top;
        return (r1.top <= mid2 && r1.bottom >= mid2) || (r2.top <= mid1 && r2.bottom >= mid1);
    }

    private void makeLine(Character c, ArrayList<Character> line, ArrayList<Character> characters) {
        line.add(c);
        c.isSorted = true;
        for (Character character : characters) {
            if (!character.isSorted) {
                if (isSameLine(c, character)) {
                    makeLine(character, line, characters);
                }
            }
        }
    }

    public void imageToText(ArrayList<Line> currentLines, ArrayList<Character> listCharacters, final ConvertingCompleteCallback callback) {
        final HashMap<Input, Point> map = new HashMap<>();
        for (Character character : listCharacters) {
            character.isSorted = false;
        }
        mCountCharacters = mLineCount = 0;

        final ArrayList<Line> lines = new ArrayList<>();
        for (Character c : listCharacters) {
            if (!c.isSorted) {
                Line line = null;
                for (int i = 0; i < currentLines.size() && line == null; i++) {
                    Line currentLine = currentLines.get(i);
                    if ((currentLine.mTop <= c.mRect.top && currentLine.mBottom >= c.mRect.top) ||
                            (currentLine.mTop <= c.mRect.bottom && currentLine.mBottom >= c.mRect.bottom)) {
                        line = currentLine;
                    }
                }

                if (line != null) {
                    line.mCharacters = new ArrayList<>();
                    makeLine(c, line.mCharacters, listCharacters);
                    lines.add(line);
                }
            }
        }

        if (!lines.isEmpty()) {
            final String[] lists = new String[lines.size()];
            mCountCharacters = listCharacters.size();
            for (Line line : lines) {
                final int indexLine = lines.indexOf(line);
                if (!line.mCharacters.isEmpty()) {
                    boolean end = false;
                    while (!end) {
                        boolean swapped = false;
                        for (int i = 1; i < line.mCharacters.size(); i++) {
                            Character c1 = line.mCharacters.get(i), cp = line.mCharacters.get(i - 1);
                            if (c1.mRect.left < cp.mRect.left) {
                                Collections.swap(line.mCharacters, i, i - 1);
                                swapped = true;
                            }
                        }

                        if (!swapped) {
                            end = true;
                        }
                    }
                    final String[] text = new String[line.mCharacters.size()];
                    final int h = Math.abs(line.mMaxBottom - line.mMinTop);
                    mLineCount = line.mCharacters.size();
                    for (int i = 0; i < line.mCharacters.size(); i++) {
                        final Character c = line.mCharacters.get(i);
                        final int index = i;
                        if (c.mAlphabet == null) {
                            final AsyncTask<Void, Void, Bundle> asyncTask = new AsyncTask<Void, Void, Bundle>() {
                                @Override
                                protected Bundle doInBackground(Void... params) {
                                    Recognizer recognizer = new Recognizer(mSOM, mLabels);
                                    return recognizer.recognize(c);
                                }

                                @Override
                                protected void onPostExecute(Bundle bundle) {
                                    super.onPostExecute(bundle);
                                    int x = bundle.getInt("cordX");
                                    int y = bundle.getInt("cordY");
                                    Input input = (Input) bundle.getSerializable("input");
                                    String result = bundle.getString("result");
                                    map.put(input, new Point(x, y));
                                    Log.d("Result", "bitmap " + index + " :" + result);
                                    switch (result) {
                                        case "C":case "O":case "P":case "S":case "V":case "W":case "X":case "Z":
                                            if (c.mRect.height() <= 0.7d * h) {
                                                c.mAlphabet = result.toLowerCase();
                                            } else {
                                                c.mAlphabet = result;
                                            }
                                            break;

                                        case "b1":
                                        case "k1":
                                            c.mAlphabet = result.substring(0, 1);
                                            break;

                                        default:
                                            c.mAlphabet = result;
                                            break;
                                    }
                                    text[index] = c.mAlphabet;
                                    if (mLineCount <= 1) {
                                        String t = "";
                                        for (String s : text) {
                                            t += s;
                                        }
                                        lists[indexLine] = t;
                                    } else {
                                        mLineCount--;
                                    }
                                    if (mCountCharacters <= 1) {
                                        String p = "";
                                        for (String s : lists) {
                                            p += s + " ";
                                        }
                                        callback.convertingComplete(p, map);
                                    } else {
                                        mCountCharacters--;
                                    }
                                }
                            };
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                asyncTask.execute();
                            }
                        }
                    }
                }
            }
        }
    }
}
