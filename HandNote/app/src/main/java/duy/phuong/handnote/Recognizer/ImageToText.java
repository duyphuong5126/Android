package duy.phuong.handnote.Recognizer;

import android.graphics.Point;
import android.graphics.Rect;
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
    public interface ConvertingCompleteCallback {
        void convertingComplete(String result, HashMap<Input, Point> map);
    }

    private Recognizer mRecognizer;

    public ImageToText(SOM som, ArrayList<ClusterLabel> mapNames) {
        mRecognizer = new Recognizer(som, mapNames);
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

    public void imageToText(ArrayList<Line> currentLines, ArrayList<Character> listCharacters, ConvertingCompleteCallback callback) {
        HashMap<Input, Point> map = new HashMap<>();
        for (Character character : listCharacters) {
            character.isSorted = false;
        }

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
            String p = "";
            for (Line line : lines) {
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
                    String text = "";
                    int h = Math.abs(line.mMaxBottom - line.mMinTop);
                    for (int i = 0; i < line.mCharacters.size(); i++) {
                        Character c = line.mCharacters.get(i);
                        if (c.mAlphabet == null) {
                            Bundle bundle = mRecognizer.recognize(c);
                            int x = bundle.getInt("cordX");
                            int y = bundle.getInt("cordY");
                            Input input = (Input) bundle.getSerializable("input");
                            String result = bundle.getString("result");
                            map.put(input, new Point(x, y));
                            Log.d("Result", "bitmap " + i + " :" + result);
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
                        }
                        text += c.mAlphabet;
                    }
                    p += text + " ";
                }
            }
            callback.convertingComplete(p, map);
        }
    }
}
