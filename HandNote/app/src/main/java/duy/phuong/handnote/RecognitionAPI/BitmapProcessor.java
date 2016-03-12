package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.MyPath;
import duy.phuong.handnote.MyView.DrawingView.MyShape;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 26/11/2015.
 */
public class BitmapProcessor {

    private ArrayList<Rect> mListRectangle;

    public BitmapProcessor() {
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

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private ArrayList<Rect> detectAreasOnBitmap(final Bitmap bitmap, int dx, int dy) {
        ArrayList<Rect> listDetectedArea = new ArrayList<>();
        HashMap<Integer, Integer> listColumns = new HashMap<>();
        int startCol = -1;
        int endCol = -1;

        //horizontal fragment
        for (int c = 0; c < bitmap.getWidth(); c++) {
            //find the start column
            if (checkNotEmptyColumn(c, bitmap)) {
                /*startCol = (c > 0) ? c - 1 : c;*/
                startCol = c;
            }

            boolean hasEndCol = false;
            if (startCol >= 0) {
                int c1 = startCol + 1;
                while (!hasEndCol) {
                    //find the end column
                    if (!checkNotEmptyColumn(c1, bitmap)) {
                        //endCol = (c1 >= bitmap.getWidth()) ? c1 : c1 + 1;
                        endCol = c1;
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
                    if (checkNotEmptyRow(r, startCol, endCol, bitmap)) {
                        /*startRow = (r > 0) ? r - 1 : r;*/
                        startRow = r;
                    }

                    boolean hasEndRow = false;
                    if (startRow >= 0) {
                        int r1 = startRow + 1;
                        while (!hasEndRow) {
                            //find the endRow
                            if (!checkNotEmptyRow(r1, startCol, endCol, bitmap)) {
                                /*endRow = (r1 >= bitmap.getHeight()) ? r1 : r1 + 1;*/
                                endRow = r1;
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

    private boolean checkNotEmptyColumn(int c, final Bitmap bitmap) {
        if (c >= bitmap.getWidth()) {
            return false;
        }
        for (int r = 0; r < bitmap.getHeight(); r++) {
            if (bitmap.getPixel(c, r) != Color.WHITE) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNotEmptyColumn(int c, int start, int end, final Bitmap bitmap) {
        if (c >= bitmap.getWidth()) {
            return false;
        }
        for (int i = start; i < end; i++) {
            if (bitmap.getPixel(c, i) != Color.WHITE) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNotEmptyRow(int r, int start, int end, final Bitmap bitmap) {
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

    public int checkVerticalEdges(Bitmap bitmap, int edgeWidth) {
        ArrayList<Rect> listRect = new ArrayList<>();
        Log.d("Width", "" + bitmap.getWidth());

        for (int startHorizontal = 0; startHorizontal < bitmap.getWidth(); startHorizontal++) {
            int endHorizontal = startHorizontal + edgeWidth - 1;
            if (endHorizontal >= bitmap.getWidth()) {
                return listRect.size();
            } else {
                int startVertical = checkStartVertical(bitmap, startHorizontal, endHorizontal);
                int endVertical = checkEndVertical(bitmap, startHorizontal, endHorizontal);

                if (startVertical < 0 || endVertical < 0) {
                    return listRect.size();
                } else {
                    int height = endVertical - startVertical + 1;
                    int verticalCount = 0;
                    if (height > 0 && (((double) height) / bitmap.getHeight()) > 0.5) {
                        for (int i = startHorizontal; i <= endHorizontal; i++) {
                            if ((((double) countVertical(bitmap, i, startVertical, endVertical)) / height) >= 0.5) {
                                verticalCount++;
                            }
                        }
                    }

                    if (verticalCount / edgeWidth > 0) {
                        Rect rect = new Rect(startHorizontal, startVertical, endHorizontal, endVertical);
                        boolean intersect = false;
                        for (Rect rect1 : listRect) {
                            if (rect.intersect(rect1)) {
                                intersect = true;
                            }
                        }
                        if (!intersect) {
                            listRect.add(rect);
                        }
                    }
                }
            }
        }

        ArrayList<Integer> temps = new ArrayList<>();

        for (int i = 0; i < listRect.size() - 1; i++)
            for (int j = i + 1; j < listRect.size(); j++) {
                if (listRect.get(i).intersect(listRect.get(j))) {
                    temps.add(j);
                }
            }

        for (int index : temps) {
            listRect.remove(index);
        }
        return listRect.size();
    }

    public int checkHorizontalEdges(Bitmap bitmap, int edgeHeight) {
        ArrayList<Rect> listRect = new ArrayList<>();

        for (int startVertical = 0; startVertical < bitmap.getHeight(); startVertical++) {
            int endVertical = startVertical + edgeHeight - 1;
            if (endVertical >= bitmap.getHeight()) {
                return listRect.size();
            } else {
                int startHorizontal = checkStartHorizontal(bitmap, startVertical, endVertical);
                int endHorizontal = checkEndHorizontal(bitmap, startVertical, endVertical);

                if (startHorizontal < 0 || endHorizontal < 0) {
                    return listRect.size();
                } else {
                    int width = endHorizontal - startHorizontal + 1;
                    int horizontalCount = 0;
                    if (width > 0 && (((double) width) / bitmap.getWidth()) > 0.5) {
                        for (int i = startVertical; i <= endVertical; i++) {
                            if ((((double) countHorizontal(bitmap, i, startHorizontal, endHorizontal)) / width) >= 0.5) {
                                horizontalCount++;
                            }
                        }
                    }

                    if (horizontalCount / edgeHeight > 0) {
                        Rect rect = new Rect(startVertical, startHorizontal, endVertical, endHorizontal);
                        boolean intersect = false;
                        for (Rect rect1 : listRect) {
                            if (rect.intersect(rect1)) {
                                intersect = true;
                            }
                        }
                        if (!intersect) {
                            listRect.add(rect);
                        }
                    }
                }
            }
        }

        ArrayList<Integer> temps = new ArrayList<>();

        for (int i = 0; i < listRect.size() - 1; i++)
            for (int j = i + 1; j < listRect.size(); j++) {
                if (listRect.get(i).intersect(listRect.get(j))) {
                    temps.add(j);
                }
            }

        for (int index : temps) {
            listRect.remove(index);
        }
        return listRect.size();
    }

    private int checkStartVertical(Bitmap bitmap, int startHorizontal, int endHorizontal) {
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (checkNotEmptyRow(i, startHorizontal, endHorizontal + 1, bitmap)) {
                return i;
            }
        }
        return -1;
    }

    private int checkStartHorizontal(Bitmap bitmap, int startVertical, int endVertical) {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            if (checkNotEmptyColumn(i, startVertical, endVertical + 1, bitmap)) {
                return i;
            }
        }
        return -1;
    }

    private int checkEndVertical(Bitmap bitmap, int startHorizontal, int endHorizontal) {
        int row = -1;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (checkNotEmptyRow(i, startHorizontal, endHorizontal + 1, bitmap)) {
                row = i;
            }
        }
        return row;
    }

    private int checkEndHorizontal(Bitmap bitmap, int startVertical, int endVertical) {
        int column = -1;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            if (checkNotEmptyColumn(i, startVertical, endVertical + 1, bitmap)) {
                column = i;
            }
        }
        return column;
    }

    private int countVertical(Bitmap bitmap, int column, int startVertical, int endVertical) {
        int count = 0;
        if (startVertical < 0 || endVertical < 0 || startVertical >= bitmap.getHeight() || endVertical >= bitmap.getHeight()) {
            return count;
        }
        for (int i = startVertical; i <= endVertical; i++) {
            if (bitmap.getPixel(column, i) != Color.WHITE) {
                count++;
            }
        }

        return count;
    }

    private int countHorizontal(Bitmap bitmap, int row, int startHorizontal, int endHorizontal) {
        int count = 0;
        if (startHorizontal < 0 || endHorizontal < 0 || startHorizontal >= bitmap.getWidth() || endHorizontal >= bitmap.getWidth()) {
            return count;
        }
        for (int i = startHorizontal; i <= endHorizontal; i++) {
            if (bitmap.getPixel(i, row) != Color.WHITE) {
                count++;
            }
        }

        return count;
    }

    public String featureExtraction(Bitmap bitmap, String[] list) {
        String result = "";
        for (String string : list) {
            switch (string) {
                case "A":
                    isA(bitmap);
                    break;

                default:
                    break;
            }
        }
        return result;
    }

    private boolean isA(Bitmap bitmap) {
        boolean result = false;
        ArrayList<MyPath> myPaths = getAreas(bitmap);
        Log.d("Ares", "" + myPaths.size());
        return result;
    }

    private ArrayList<MyPath> getAreas(Bitmap bitmap) {
        ArrayList<MyPath> myPaths = new ArrayList<>();
        myPaths.add(new MyPath(new ArrayList<Point>()));
        int index = 0;

        ArrayList<Point> points = new ArrayList<>();
        points.addAll(getListBlankPoint(bitmap));

        myPaths.get(index).getListPoint().add(points.remove(0));

        while (!points.isEmpty()) {
            ArrayList<Point> listPos = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                boolean flag = false;
                for (int j = 0; j < myPaths.get(index).getListPoint().size() && !flag; j++) {
                    if (isContiguous(myPaths.get(index).getListPoint().get(j), points.get(i))) {
                        listPos.add(points.get(i));
                        flag = true;
                    }
                }
            }

            if (listPos.size() > 0) {
                for (Point point: listPos) {
                    myPaths.get(index).getListPoint().add(point);
                    points.remove(point);
                }
            } else {
                myPaths.add(new MyPath(new ArrayList<Point>()));
                index++;
                if (!points.isEmpty()) {
                    myPaths.get(index).getListPoint().add(points.remove(0));
                }
            }
        }
        return myPaths;
    }

    private boolean isContiguous(Point point1, Point point2) {
        return ((Math.abs(point1.x - point2.x) <= 1) && (Math.abs(point1.y - point2.y) <= 1));
    }

    private ArrayList<Point> getListBlankPoint(Bitmap bitmap) {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) == Color.WHITE) {
                    points.add(new Point(j, i));
                }
            }

        return points;
    }

    private int countClosedAreas(byte[][] matrix) {
        int result = 0;
        byte color = 2;
        Point point = getFirstBlankPoint(matrix);
        while (point != null) {
            colorMatrix(color, point.x, point.y, matrix);
            color++;
            point = getFirstBlankPoint(matrix);
            result++;
        }
        return result;
    }

    private Point getFirstBlankPoint(byte[][] matrix) {
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    return new Point(j, i);
                }
            }

        return null;
    }

    private void colorMatrix(byte color, int x, int y, byte[][] matrix) {
        if (matrix[y][x] == 0) {
            matrix[y][x] = color;
            if (x - 1 >= 0 && y - 1 >= 0) {
                colorMatrix(color, x - 1, y - 1, matrix);
            }
            if (x - 1 >= 0 && y >= 0 && x - 1 < matrix[y].length) {
                colorMatrix(color, x - 1, y, matrix);
            }
            if (x - 1 >= 0 && y + 1 < matrix.length) {
                colorMatrix(color, x - 1, y + 1, matrix);
            }
            if (y - 1 >= 0) {
                colorMatrix(color, x, y - 1, matrix);
            }
            if (y + 1 < matrix.length) {
                colorMatrix(color, x, y + 1, matrix);
            }
            if (y - 1 >= 0 && x + 1 < matrix[y].length) {
                colorMatrix(color, x + 1, y - 1, matrix);
            }
            if (x + 1 < matrix[y].length) {
                colorMatrix(color, x + 1, y, matrix);
            }
            if (x + 1 < matrix[y].length && y + 1 < matrix.length) {
                colorMatrix(color, x + 1, y + 1, matrix);
            }
        }
    }

    private byte[][] bitmapToMatrix(Bitmap bitmap) {
        byte[][] matrix = new byte[bitmap.getHeight()][bitmap.getWidth()];
        for (int i = 0; i < bitmap.getHeight(); i++) {
            matrix[i] = new byte[bitmap.getWidth()];
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) != Color.WHITE) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        return matrix;
    }
}
