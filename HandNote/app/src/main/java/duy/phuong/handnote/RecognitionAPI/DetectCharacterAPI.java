package duy.phuong.handnote.RecognitionAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.MyPoint;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 26/11/2015.
 */
public class DetectCharacterAPI {

    private ArrayList<Rect> mListRectangle;

    public DetectCharacterAPI() {
        mListRectangle = new ArrayList<>();
    }

    public void onDetectCharacter(final Bitmap src, RecognitionCallback callback) {
        this.mListRectangle.addAll(detectAreasOnBitmap(src, 0, 0));

        ArrayList<Bitmap> resultBitmaps = new ArrayList<>();
        for (Rect rect : mListRectangle) {
            resultBitmaps.add(SupportUtils.cropBitmap(src, rect.left, rect.top, rect.width(), rect.height()));
        }

        callback.onRecognizeSuccess(resultBitmaps);

        this.mListRectangle.clear();
    }


    private ArrayList<MyPoint> getNeighborsPoint(MyPoint point, Bitmap src) {
        ArrayList<MyPoint> list = new ArrayList<>();
        int left = point.x - 1, right = point.x + 1, above = point.y - 1, under = point.y + 1;
        if (left >= 0) {
            if (src.getPixel(left, point.y) == Color.BLACK) {
                list.add(new MyPoint(left, point.y));
            }
            if (above >= 0 && src.getPixel(left, above) == Color.BLACK) {
                list.add(new MyPoint(left, above));
            }

            if (under < src.getHeight() && src.getPixel(left, under) == Color.BLACK) {
                list.add(new MyPoint(left, under));
            }
        }

        if (above >= 0 && src.getPixel(point.x, above) == Color.BLACK) {
            list.add(new MyPoint(point.x, above));
        }

        if (under < src.getHeight() && src.getPixel(point.x, under) == Color.BLACK) {
            list.add(new MyPoint(point.x, under));
        }

        if (right < src.getWidth()) {
            if (src.getPixel(right, point.y) == Color.BLACK) {
                list.add(new MyPoint(right, point.y));
            }
            if (above >= 0 && src.getPixel(right, above) == Color.BLACK) {
                list.add(new MyPoint(right, above));
            }

            if (under < src.getHeight() && src.getPixel(right, under) == Color.BLACK) {
                list.add(new MyPoint(right, under));
            }
        }
        Log.d("Count", "" + list.size());
        return list;
    }

    private ArrayList<Rect> detectAreasOnBitmap(final Bitmap bitmap, int dx, int dy) {
        ArrayList<Rect> listDetectedArea = new ArrayList<>();
        HashMap<Integer, Integer> listColumns = new HashMap<>();
        int startCol = -1;
        int endCol = -1;

        //horizontal fragment
        for (int c = 0; c < bitmap.getWidth(); c++) {
            //find the start column
            if (checkEmptyColumn(c, bitmap)) {
                startCol = (c > 0) ? c - 1 : c;
            }

            boolean hasEndCol = false;
            if (startCol >= 0) {
                int c1 = startCol + 1;
                while (!hasEndCol) {
                    //find the end column
                    if (!checkEmptyColumn(c1, bitmap)) {
                        endCol = (c1 >= bitmap.getWidth()) ? c1 : c1 + 1;
                    }

                    if (endCol > startCol) {
                        //save startCol (key) and endCol (value)
                        listColumns.put(startCol, endCol);
                        //save current anchor
                        c = endCol;

                        startCol = endCol = -1;
                        hasEndCol = true;
                    }
                    c1++;
                }
            }
        }

        //vertical fragment
        for (Map.Entry<Integer, Integer> entry : listColumns.entrySet()) {
            startCol = endCol = -1;
            if (entry.getKey() >= 0 && entry.getValue() >= 0) {
                startCol = entry.getKey();
                endCol = entry.getValue();
                int startRow = -1;
                int endRow = -1;
                for (int r = 0; r < bitmap.getHeight(); r++) {
                    //find the start row
                    if (checkEmptyRow(r, startCol, endCol, bitmap)) {
                        startRow = (r > 0) ? r - 1 : r;
                    }

                    boolean hasEndRow = false;
                    if (startRow >= 0) {
                        int r1 = startRow + 1;
                        while (!hasEndRow) {
                            //find the endRow
                            if (!checkEmptyRow(r1, startCol, endCol, bitmap)) {
                                endRow = (r1 >= bitmap.getHeight()) ? r1 : r1 + 1;
                            }

                            if (endRow > startRow) {
                                //save rectangle
                                listDetectedArea.add(new Rect(startCol + dx, startRow + dy, endCol + dx, endRow + dy));

                                r = endRow;
                                startRow = endRow = -1;
                                hasEndRow = true;
                            }
                            r1++;
                        }
                    }
                }
            }
        }

        return listDetectedArea;
    }

    private boolean checkEmptyColumn(int c, final Bitmap bitmap) {
        if (c >= bitmap.getWidth()) {
            return false;
        }
        for (int r = 0; r < bitmap.getHeight(); r++) {
            if (bitmap.getPixel(c, r) != Color.WHITE) {
                return true;
            }
        }
        int m = 1, n = 2;
        return false;
    }

    private boolean checkEmptyRow(int r, int start, int end, final Bitmap bitmap) {
        if (r >= bitmap.getHeight()) {
            return false;
        }
        for (int i = start; i < end; i++) {
            if (bitmap.getPixel(i, r) != Color.WHITE) {
                return true;
            }
        }
        return false;
    }
}
