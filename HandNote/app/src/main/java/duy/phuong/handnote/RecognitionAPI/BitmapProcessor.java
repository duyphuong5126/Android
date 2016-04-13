package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.DTO.*;
import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.MyView.DrawingView.MyPath;

/**
 * Created by Phuong on 26/11/2015.
 */

/**
 * (Pre-)Processing bitmap and segmenting character areas. Features extraction after clustering
 */
public class BitmapProcessor {

    public interface RecognitionCallback {
        void onRecognizeSuccess(ArrayList<Character> listBitmaps);
    }

    public HashMap<String, Boolean> mMapFeatures;

    private ArrayList<Rect> mListRectangle;

    public BitmapProcessor() {
        mListRectangle = new ArrayList<>();
        mMapFeatures = new HashMap<>();
        resetMap();
    }

    public void resetMap() {
        mMapFeatures.put("A", false);
        mMapFeatures.put("B", false);
        mMapFeatures.put("C", false);
        mMapFeatures.put("D", false);
        mMapFeatures.put("E", false);
        mMapFeatures.put("F", false);
        mMapFeatures.put("G", false);
        mMapFeatures.put("H", false);
        mMapFeatures.put("I", false);
        mMapFeatures.put("J", false);
        mMapFeatures.put("K", false);
        mMapFeatures.put("L", false);
        mMapFeatures.put("M", false);
        mMapFeatures.put("N", false);
        mMapFeatures.put("O", false);
        mMapFeatures.put("P", false);
        mMapFeatures.put("Q", false);
        mMapFeatures.put("R", false);
        mMapFeatures.put("S", false);
        mMapFeatures.put("T", false);
        mMapFeatures.put("W", false);
        mMapFeatures.put("U", false);
        mMapFeatures.put("V", false);
        mMapFeatures.put("X", false);
        mMapFeatures.put("Y", false);
        mMapFeatures.put("Z", false);
        mMapFeatures.put("a", false);
        mMapFeatures.put("b", false);
        mMapFeatures.put("b1", false);
        mMapFeatures.put("d", false);
        mMapFeatures.put("e", false);
        mMapFeatures.put("f", false);
        mMapFeatures.put("g", false);
        mMapFeatures.put("h", false);
        mMapFeatures.put("i", false);
        mMapFeatures.put("j", false);
        mMapFeatures.put("k", false);
        mMapFeatures.put("k1", false);
        mMapFeatures.put("l", false);
        mMapFeatures.put("m", false);
        mMapFeatures.put("n", false);
        mMapFeatures.put("q", false);
        mMapFeatures.put("r", false);
        mMapFeatures.put("t", false);
        mMapFeatures.put("u", false);
        mMapFeatures.put("y", false);
        mMapFeatures.put("1", false);
        mMapFeatures.put("2", false);
        mMapFeatures.put("3", false);
        mMapFeatures.put("4", false);
        mMapFeatures.put("5", false);
        mMapFeatures.put("6", false);
        mMapFeatures.put("7", false);
        mMapFeatures.put("8", false);
        mMapFeatures.put("9", false);
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

    public static Bitmap cropBitmap(Bitmap src, int x, int y, int w, int h) {
        if (x >= src.getWidth() || x < 0 || y >= src.getHeight() || y < 0 || w <= 0 || h <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (i >= 0 && j >= 0) {
                    bitmap.setPixel(((i - x) >= 0) ? i - x : 0, ((j - y) >= 0) ? j - y : 0, src.getPixel(i, j));
                }
            }
        }
        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap src, float angle) {
        if (src != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return null;
    }

    public void onDetectCharacter(final Character character, RecognitionCallback callback) {
        this.mListRectangle.addAll(detectAreasOnBitmap(character.mBitmap, 0, 0));

        ArrayList<Character> detectedBitmaps = new ArrayList<>();
        for (Rect rect : mListRectangle) {
            Character character1 = new Character();
            character1.mBitmap = cropBitmap(character.mBitmap, rect.left, rect.top, rect.width(), rect.height());
            character1.mMyShape = character.mMyShape;
            character1.mParentHeight = character.mParentHeight;
            character1.mParentWidth = character.mParentWidth;
            character1.mRect = rect;
            detectedBitmaps.add(character1);
        }

        /*ArrayList<Character> resultBitmaps = new ArrayList<>();
        for (Character f : detectedBitmaps) {
            resultBitmaps.addAll(getSegments(f));
            highLight(f.mBitmap,0, f.mBitmap.getWidth() - 1, (int) (f.mBitmap.getHeight() * 0.8d), f.mBitmap.getHeight() - 1);
            resultBitmaps.add(f);
        }*/

        /*ArrayList<Character> resultBitmaps = new ArrayList<>();
        for (Character f : detectedBitmaps) {
            for (Bitmap bitmap : findContour(f.mBitmap)) {
                Character fContour = new Character();
                fContour.mBitmap = bitmap;
                resultBitmaps.add(fContour);
            }
        }*/
        callback.onRecognizeSuccess(/*resultBitmaps.isEmpty() ? detectedBitmaps : resultBitmaps*/detectedBitmaps);

        this.mListRectangle.clear();
    }

    public static Bitmap copyImage(Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++) {
                bitmap.setPixel(j, i, src.getPixel(j, i));
            }
        }
        return bitmap;
    }

    private ArrayList<Character> topDownSegmenting(Character character) {
        ArrayList<Character> list = new ArrayList<>();
        Bitmap src = copyImage(character.mBitmap);
        Point point = null;
        for (int row = 0; row < src.getHeight() && point == null; row++) {
            if (countRowSegments(row, src) == 2) {
                point = getInitialTopDownPoint(row, src);
            }
        }

        if (point != null) {
            boolean moved = false;
            boolean end = false;
            while (!end) {
                int col = point.x;
                int row = point.y;
                src.setPixel(point.x, point.y, Color.RED);

                if (row + 1 < src.getHeight()) {
                    if (src.getPixel(col, row + 1) == Color.WHITE) {
                        point = new Point(col, row + 1);
                        moved = true;
                    }
                }

                if (col + 1 < src.getWidth()) {
                    if (src.getPixel(col + 1, row) == Color.WHITE && !moved) {
                        point = new Point(col + 1, row);
                        moved = true;
                    }
                }

                if (col + 1 < src.getWidth() && row + 1 < src.getHeight() && !moved) {
                    if (src.getPixel(col + 1, row + 1) == Color.WHITE) {
                        point = new Point(col + 1, row + 1);
                        moved = true;
                    }
                }

                if (col - 1 >= 0 && row + 1 < src.getHeight()) {
                    if (src.getPixel(col - 1, row + 1) == Color.WHITE && !moved) {
                        point = new Point(col - 1, row + 1);
                        moved = true;
                    }
                }

                if (col - 1 >= 0) {
                    if (src.getPixel(col - 1, row) == Color.WHITE && !moved) {
                        point = new Point(col - 1, row);
                        moved = true;
                    }
                }

                if (moved) {
                    moved = false;
                } else {
                    end = true;
                }
            }
        }

        if (point != null) {
            if (point.x > 0 && point.y > 0) {
                Character f1 = new Character();
                f1.mBitmap = cropBitmap(character.mBitmap, 0, 0, point.x, character.mBitmap.getHeight());
                list.add(f1);
            }
            Character f2 = new Character();
            f2.mBitmap = cropBitmap(character.mBitmap, point.x + 1,
                    0, character.mBitmap.getWidth() - point.x - 1, character.mBitmap.getHeight());
            list.add(f2);
            Character f3 = new Character();
            f3.mBitmap = src;
            list.add(f3);
        }
        return list;
    }

    private ArrayList<Character> bottomUpSegmenting(Character character) {
        ArrayList<Character> list = new ArrayList<>();
        Bitmap original = character.mBitmap;
        Bitmap src = copyImage(character.mBitmap);
        Point point = null;
        for (int row = 0; row < src.getHeight(); row++) {
            if (countRowSegments(row, src) == 2) {
                if (getInitialTopDownPoint(row, src) != null) {
                    point = getInitialBottomUpPoint(row, src);
                }
            }
        }

        ArrayList<Point> points = new ArrayList<>();
        if (point != null) {
            boolean moved = false;
            boolean end = false;
            while (!end) {
                int col = point.x;
                int row = point.y;
                src.setPixel(point.x, point.y, Color.RED);
                points.add(point);

                if (row - 1 >= 0) {
                    if (src.getPixel(col, row - 1) == Color.WHITE) {
                        point = new Point(col, row - 1);
                        moved = true;
                    }
                }

                if (col + 1 < src.getWidth()) {
                    if (src.getPixel(col + 1, row) == Color.WHITE && !moved) {
                        point = new Point(col + 1, row);
                        moved = true;
                    }
                }

                if (col + 1 < src.getWidth() && row - 1 >= 0 && !moved) {
                    if (src.getPixel(col + 1, row - 1) == Color.WHITE) {
                        point = new Point(col + 1, row - 1);
                        moved = true;
                    }
                }

                if (col - 1 >= 0 && row - 1 >= 0) {
                    if (src.getPixel(col - 1, row - 1) == Color.WHITE && !moved) {
                        point = new Point(col - 1, row - 1);
                        moved = true;
                    }
                }

                if (col - 1 >= 0) {
                    if (src.getPixel(col - 1, row) == Color.WHITE && !moved) {
                        point = new Point(col - 1, row);
                        moved = true;
                    }
                }

                if (moved) {
                    moved = false;
                } else {
                    end = true;
                }
            }
        }

        Point bot = points.get(0);
        Point top = points.get(points.size() - 1);
        for (int i = bot.y + 1; i < src.getHeight(); i++) {
            points.add(new Point(bot.x, i));
        }
        for (int i = top.y - 1; i >= 0; i--) {
            points.add(new Point(bot.x, i));
        }

        if (point != null) {
            if (point.x > 0 && point.y > 0) {
                Character f1 = new Character();
                Bitmap bmp = /*cropBitmap(character.mBitmap, 0, 0, point.x, character.mBitmap.getHeight())*/
                        Bitmap.createBitmap(character.mBitmap.getWidth(), character.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                for (Point p : points) {
                    for (int i = 0; i <= p.x; i++) {
                        bmp.setPixel(i, p.y, original.getPixel(i, p.y));
                    }
                    for (int i = p.x + 1; i < original.getWidth(); i++) {
                        bmp.setPixel(i, p.y, Color.WHITE);
                    }
                }
                ArrayList<Rect> list1 = new ArrayList<>();
                list1.addAll(detectAreasOnBitmap(bmp, 0, 0));
                if (!list1.isEmpty()) {
                    for (Rect rect : list1) {
                        f1.mBitmap = cropBitmap(character.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                    }
                }
                list.add(f1);
            }
            Character f2 = new Character(); /*f2.mBitmap = cropBitmap(character.mBitmap, point.x + 1,
                    0, character.mBitmap.getWidth() - point.x - 1, character.mBitmap.getHeight());*/
            Bitmap bmp =
                    Bitmap.createBitmap(character.mBitmap.getWidth(), character.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            for (Point p : points) {
                for (int i = 0; i <= p.x; i++) {
                    bmp.setPixel(i, p.y, Color.WHITE);
                }
                for (int i = p.x + 1; i < original.getWidth(); i++) {
                    bmp.setPixel(i, p.y, original.getPixel(i, p.y));
                }
            }
            ArrayList<Rect> list1 = new ArrayList<>();
            list1.addAll(detectAreasOnBitmap(bmp, 0, 0));
            if (!list1.isEmpty()) {
                for (Rect rect : list1) {
                    f2.mBitmap = cropBitmap(character.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                }
            }
            list.add(f2);
            Character f3 = new Character();
            f3.mBitmap = src;
            list.add(f3);
        }
        return list;
    }

    private Point getInitialTopDownPoint(int row, Bitmap src) {
        boolean flag = false;
        for (int i = 0; i < src.getWidth(); i++) {
            if (src.getPixel(i, row) == Color.WHITE) {
                if (flag) {
                    return new Point(i, row);
                }
            } else {
                flag = true;
            }
        }
        return null;
    }

    private Point getInitialBottomUpPoint(int row, Bitmap src) {
        boolean flag = false;
        for (int i = src.getWidth() - 1; i >= 0; i--) {
            if (src.getPixel(i, row) == Color.WHITE) {
                if (flag) {
                    return new Point(i, row);
                }
            } else {
                flag = true;
            }
        }
        return null;
    }

    private void highLight(Bitmap src, int startX, int endX, int startY, int endY) {
        for (int i = startY; i <= endY; i++)
            for (int j = startX; j <= endX; j++) {
                if (src.getPixel(j, i) == Color.WHITE) {
                    src.setPixel(j, i, Color.RED);
                }
            }
    }

    private ArrayList<Character> getSegments(Character character) {
        ArrayList<Character> resultBitmaps = new ArrayList<>();
        Bitmap src = character.mBitmap;
        int height = src.getHeight();
        double threshold = height * 0.8d;
        ArrayList<Rect> listRect = new ArrayList<>();
        boolean flag = true;
        for (int i = 0; i < src.getWidth() - 1; i++) {
            if (getColumnHeightFromTop(src, i) < threshold) {
                int index = listRect.size() - 1;
                if (flag) {
                    listRect.add(new Rect(i, 0, i + 1, src.getHeight() - 1));
                    flag = false;
                } else {
                    listRect.get(index).right = listRect.get(index).right + 1;
                }
            } else {
                flag = true;
            }
        }

        for (int i = 0; i < listRect.size() - 1; i++) {
            if (listRect.get(i) != null) {
                boolean stop = false;
                for (int j = i + 1; j < listRect.size() && !stop; j++) {
                    if (listRect.get(j).left - listRect.get(i).right <= 0.05d * src.getWidth()) {
                        listRect.get(i).right = listRect.get(j).right;
                        listRect.set(j, null);
                    } else {
                        stop = true;
                    }
                }
            }
        }

        for (Rect rect : listRect) {
            if (rect != null) {
                Character fImage = new Character();
                fImage.mBitmap = cropBitmap(src, rect.left, rect.top, rect.width(), rect.height());
                resultBitmaps.add(fImage);
            }
        }
        return resultBitmaps;
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

    private int countBlack(Bitmap bitmap) {
        int count = 0;
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                count += (bitmap.getPixel(j, i) == Color.WHITE) ? 0 : 1;
            }
        return count;
    }

    private int getColumnHeightFromTop(Bitmap bitmap, int col) {
        for (int row = 0; row < bitmap.getHeight(); row++) {
            if (bitmap.getPixel(col, row) != Color.WHITE) {
                return row;
            }
        }
        return -1;
    }

    private int getColumnHeightFromBot(Bitmap bitmap, int col) {
        for (int row = bitmap.getHeight() - 1; row >= 0; row--) {
            if (bitmap.getPixel(col, row) != Color.WHITE) {
                return row;
            }
        }
        return -1;
    }

    private Point getFirstBlackPixel(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) != Color.WHITE) {
                    return new Point(j, i);
                }
            }
        return null;
    }

    private Point getFirstPixel(Bitmap bitmap, int color) {
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) == color) {
                    return new Point(j, i);
                }
            }
        return null;
    }

    private Point getLastBlackPixel(Bitmap bitmap) {
        for (int i = bitmap.getHeight() - 1; i >= 0; i--)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) != Color.WHITE) {
                    return new Point(j, i);
                }
            }
        return null;
    }

    private int countRowSegments(int y, Bitmap bitmap) {
        int count = 0;
        int dis = 0;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            if (bitmap.getPixel(i, y) != Color.WHITE) {
                dis++;
                if (i == bitmap.getWidth() - 1) {
                    count++;
                }
            } else {
                if (dis > 0) {
                    dis = 0;
                    count++;
                }
            }
        }
        return count;
    }

    private int countColumnSegments(int x, Bitmap bitmap) {
        int count = 0;
        int dis = 0;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (bitmap.getPixel(x, i) != Color.WHITE) {
                dis++;
                if (i == bitmap.getHeight() - 1) {
                    count++;
                }
            } else {
                if (dis > 0) {
                    dis = 0;
                    count++;
                }
            }
        }
        return count;
    }

    private int getBothSideStartRow(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getBothSideEndRow(Bitmap bitmap) {
        for (int i = bitmap.getHeight() - 1; i >= 0; i--) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getBothSideStartCol(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getBothSideEndCol(Bitmap bitmap) {
        for (int i = bitmap.getWidth() - 1; i >= 0; i--) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getHorizontalBlankCounts(int y, Bitmap bitmap) {
        int count = 0;
        int segments = countRowSegments(y, bitmap);
        if (segments == 2) {
            int pos = -1;
            for (int i = 0; i < bitmap.getWidth(); i++) {
                if (bitmap.getPixel(i, y) != Color.WHITE) {
                    if (count > 0) {
                        return count;
                    }
                    pos = i;
                } else {
                    if (pos >= 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int triangleFromBot(Character character) {
        Point lastBlack = getLastBlackPixel(character.mBitmap);
        if (lastBlack == null) {
            return -1;
        }

        int bothStart = getBothSideStartRow(character.mBitmap);

        if (bothStart >= 0) {
            int width = getHorizontalBlankCounts(bothStart, character.mBitmap);
            int decrease = 0, increase = 0;
            for (int i = bothStart; i <= lastBlack.y; i++) {
                int w = getHorizontalBlankCounts(i, character.mBitmap);

                if (w < width) {
                    decrease++;
                }
                if (w > width) {
                    increase++;
                }
                width = w;
            }
            if ((((double) decrease / (decrease + increase)) >= 0.6d) && width <= 0) {
                return 1;
            }
        }
        return 0;
    }

    private int triangleFromTop(Character character) {
        if (!verticalClosed(character.mBitmap)) {
            return -1;
        }
        Point firstBlack = getFirstBlackPixel(character.mBitmap);
        if (firstBlack == null) {
            return -1;
        }

        int bothEnd = getBothSideEndRow(character.mBitmap);

        if (bothEnd >= 0) {
            int width = getHorizontalBlankCounts(bothEnd, character.mBitmap);
            int decrease = 0, increase = 0;
            for (int i = bothEnd - 1; i >= firstBlack.y; i--) {
                int w = getHorizontalBlankCounts(i, character.mBitmap);

                if (w < width) {
                    decrease++;
                }
                if (w > width) {
                    increase++;
                }
                width = w;
            }

            Log.d("Decrease", "" + decrease);
            Log.d("Increase", "" + increase);
            if ((((double) decrease / (decrease + increase)) >= 0.6d) && width <= 0) {
                return 1;
            }
        }
        return 0;
    }

    private boolean verticalClosed(Bitmap bitmap) {
        int verticalOffset = bitmap.getHeight() / 3;
        Bitmap top = cropBitmap(bitmap, 0, 0, bitmap.getWidth(), verticalOffset);
        Bitmap bot = cropBitmap(bitmap, 0, 2 * verticalOffset, bitmap.getWidth(), bitmap.getHeight() / 3);
        boolean closedTop = detectAreasOnBitmap(top, 0, 0).size() == 1;
        boolean closedBot = detectAreasOnBitmap(bot, 0, 0).size() == 1;
        return closedTop || closedBot;
    }

    private int connectedComponentTop(Character character) {
        int fragmentHeight = character.mBitmap.getHeight() / 3;
        int startAbove = 0;
        Bitmap fragmentAbove = cropBitmap(character.mBitmap, 0, startAbove, character.mBitmap.getWidth(), fragmentHeight);
        if (fragmentAbove != null) {
            return detectAreasOnBitmap(fragmentAbove, 0, 0).size();
        }
        return 0;
    }

    private int connectedComponentBot(Character character) {
        int fragmentHeight = character.mBitmap.getHeight() / 3;
        int startBelow = character.mBitmap.getHeight() - fragmentHeight - 1;
        Bitmap fragmentBelow = cropBitmap(character.mBitmap, 0, startBelow, character.mBitmap.getWidth(), fragmentHeight);
        if (fragmentBelow != null) {
            return detectAreasOnBitmap(fragmentBelow, 0, 0).size();
        }
        return 0;
    }

    private Point getFirstBackFromBot(Bitmap src, int col) {
        for (int i = src.getHeight() - 1; i >= 0; i--) {
            if (src.getPixel(col, i) != Color.WHITE) {
                return new Point(col, i);
            }
        }
        return null;
    }

    private Point getFirstBackFromTop(Bitmap src, int col) {
        for (int i = 0; i < src.getHeight(); i++) {
            if (src.getPixel(col, i) != Color.WHITE) {
                return new Point(col, i);
            }
        }
        return null;
    }

    private boolean isSuitable(Point point, Bitmap src) {
        if (src.getPixel(point.x, point.y) == Color.WHITE) {
            int x = point.x, y = point.y;
            if (x - 1 >= 0) {
                if (src.getPixel(x - 1, y) != Color.WHITE && src.getPixel(x - 1, y) != Color.RED) {
                    return false;
                }
            }
            if (x + 1 < src.getWidth()) {
                if (src.getPixel(x + 1, y) != Color.WHITE && src.getPixel(x + 1, y) != Color.RED) {
                    return false;
                }
            }
            if (y + 1 < src.getHeight()) {
                if (src.getPixel(x, y + 1) != Color.WHITE && src.getPixel(x, y + 1) != Color.RED) {
                    return false;
                }
            }
            if (y - 1 >= 0) {
                if (src.getPixel(x, y - 1) != Color.WHITE && src.getPixel(x, y - 1) != Color.RED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private ArrayList<Bitmap> findContour(Bitmap bitmap) {
        ArrayList<Bitmap> list = new ArrayList<>();
        //list.add(bitmap);
        Bitmap src = Bitmap.createBitmap(bitmap.getWidth() + 2, bitmap.getHeight() + 2, Bitmap.Config.ARGB_8888);
        //Bitmap copy = Bitmap.createBitmap(bitmap.getWidth() + 2, bitmap.getHeight() + 2, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < src.getHeight(); i++)
            for (int j = 0; j < src.getWidth(); j++) {
                if (i == 0 || i == src.getHeight() - 1 || j == 0 || j == src.getWidth() - 1) {
                    src.setPixel(j, i, Color.WHITE);
                    //copy.setPixel(j, i, Color.WHITE);
                } else {
                    src.setPixel(j, i, bitmap.getPixel(j - 1, i - 1));
                    //copy.setPixel(j, i, bitmap.getPixel(j - 1, i - 1));
                }
            }

        for (int i = 0; i < src.getHeight(); i++)
            for (int j = 0; j < src.getWidth(); j++) {
                if (isSuitable(new Point(j, i), src)) {
                    src.setPixel(j, i, Color.RED);
                    //copy.setPixel(j, i, Color.RED);
                }
            }

        Point p = null;
        for (int i = 0; i < src.getHeight() && p == null; i++)
            for (int j = 0; j < src.getWidth() && p == null; j++) {
                if (src.getPixel(j, i) == Color.WHITE) {
                    p = new Point(j, i);
                }
            }

        ArrayList<MyPath> myPaths = new ArrayList<>();
        myPaths.add(new MyPath(new ArrayList<Point>()));
        int index = 0;
        while (p != null) {
            boolean end = false;
            boolean moved = false;
            while (!end) {
                int x = p.x;
                int y = p.y;
                src.setPixel(x, y, Color.RED);
                myPaths.get(index).getListPoint().add(p);

                if (x - 1 >= 0 && y + 1 < src.getHeight()) {
                    if (src.getPixel(x - 1, y + 1) == Color.WHITE) {
                        p = new Point(x - 1, y + 1);
                        moved = true;
                    }
                }

                if (x - 1 >= 0 && !moved) {
                    if (src.getPixel(x - 1, y) == Color.WHITE) {
                        p = new Point(x - 1, y);
                        moved = true;
                    }
                }

                if (x - 1 >= 0 && y - 1 >= 0 && !moved) {
                    if (src.getPixel(x - 1, y - 1) == Color.WHITE) {
                        p = new Point(x - 1, y - 1);
                        moved = true;
                    }
                }

                if (y + 1 < src.getHeight() && !moved) {
                    if (src.getPixel(x, y + 1) == Color.WHITE) {
                        p = new Point(x, y + 1);
                        moved = true;
                    }
                }

                if (y - 1 >= 0 && !moved) {
                    if (src.getPixel(x, y - 1) == Color.WHITE) {
                        p = new Point(x, y - 1);
                        moved = true;
                    }
                }

                if (x + 1 < src.getWidth() && y + 1 < src.getHeight() && !moved) {
                    if (src.getPixel(x + 1, y + 1) == Color.WHITE) {
                        p = new Point(x + 1, y + 1);
                        moved = true;
                    }
                }

                if (x + 1 < src.getWidth() && !moved) {
                    if (src.getPixel(x + 1, y) == Color.WHITE) {
                        p = new Point(x + 1, y);
                        moved = true;
                    }
                }

                if (x + 1 < src.getWidth() && y - 1 >= 0 && !moved) {
                    if (src.getPixel(x + 1, y - 1) == Color.WHITE) {
                        p = new Point(x + 1, y - 1);
                        moved = true;
                    }
                }

                if (moved) {
                    moved = false;
                } else {
                    end = true;
                }
            }
            p = null;
            for (int i = 0; i < src.getHeight() && p == null; i++)
                for (int j = 0; j < src.getWidth() && p == null; j++) {
                    if (src.getPixel(j, i) == Color.WHITE) {
                        p = new Point(j, i);
                    }
                }
            if (p != null) {
                myPaths.add(new MyPath(new ArrayList<Point>()));
                index++;
            }
        }

        //list.add(copy);
        ArrayList<MyPath> delete = new ArrayList<>();
        for (int i = 0; i < myPaths.size() - 1; i++) {
            if (myPaths.get(i).getListPoint().size() < 10) {
                delete.add(myPaths.get(i));
            }
            for (int j = i + 1; j < myPaths.size(); j++) {
                ArrayList<Point> list1 = myPaths.get(i).getListPoint(), list2 = myPaths.get(j).getListPoint();
                if (list1.size() > list2.size()) {
                    boolean intersect = false;
                    for (int k = 0; k < list2.size() && !intersect; k++) {
                        for (int l = 0; l < list1.size() && !intersect; l++) {
                            Point p1 = list2.get(k), p2 = list1.get(l);
                            if (Math.abs(p1.x - p2.x) <= 1 && Math.abs(p1.y - p2.y) <= 1) {
                                intersect = true;
                            }
                        }
                    }

                    if (intersect) {
                        list1.addAll(list2);
                        delete.add(myPaths.get(j));
                    }
                } else {
                    boolean intersect = false;
                    for (int k = 0; k < list1.size() && !intersect; k++) {
                        for (int l = 0; l < list2.size() && !intersect; l++) {
                            Point p1 = list1.get(k), p2 = list2.get(l);
                            if (Math.abs(p1.x - p2.x) <= 1 && Math.abs(p1.y - p2.y) <= 1) {
                                intersect = true;
                            }
                        }
                    }

                    if (intersect) {
                        list2.addAll(list1);
                        delete.add(myPaths.get(j));
                    }
                }
            }
        }

        for (MyPath myPath : delete) {
            myPaths.remove(myPath);
        }

        int count = 0;
        /*Bitmap b = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < b.getHeight(); i++)
            for (int j = 0; j < b.getWidth(); j++) {
                b.setPixel(j, i, Color.WHITE);
            }*/

        for (MyPath myPath : myPaths) {
            Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bmp.getHeight(); i++)
                for (int j = 0; j < bmp.getWidth(); j++) {
                    if (myPath.getListPoint().contains(new Point(j, i))) {
                        bmp.setPixel(j, i, Color.RED);
                        //b.setPixel(j, i, Color.RED);
                    } else {
                        bmp.setPixel(j, i, Color.WHITE);
                    }
                }

            list.add(bmp);
            if (!myPath.getListPoint().isEmpty()) {
                count++;
            }
        }
        //list.add(b);

        Log.d("Contour", "" + count);

        return list;
    }

    public Bundle featureExtraction(Character character, ArrayList<Label> list) {
        character.mListContours = findContour(character.mBitmap);
        character.mListRectContour = new ArrayList<>();
        ArrayList<Bitmap> contours = character.mListContours;
        if (contours.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(contours.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(contours.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    character.mListRectContour.add(rSmall);
                    character.mListRectContour.add(rBig);
                }
            }
        } else {
            if (contours.size() == 3) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(contours.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(contours.get(1), 0, 0);
                ArrayList<Rect> r3 = detectAreasOnBitmap(contours.get(2), 0, 0);
                if (r1 != null && r2 != null && r3 != null) {
                    if (r1.size() == 1 && r2.size() == 1 && r3.size() == 1) {
                        Rect rMin = null;
                        Rect r = null;
                        Rect rBig = null;
                        if (r1.get(0).width() > r2.get(0).width() && r2.get(0).width() > r3.get(0).width()) {
                            r = r2.get(0);
                            rMin = r3.get(0);
                            rBig = r1.get(0);
                        }
                        if (r1.get(0).width() > r3.get(0).width() && r3.get(0).width() > r2.get(0).width()) {
                            r = r3.get(0);
                            rMin = r2.get(0);
                            rBig = r1.get(0);
                        }
                        if (r2.get(0).width() > r1.get(0).width() && r1.get(0).width() > r3.get(0).width()) {
                            r = r1.get(0);
                            rMin = r3.get(0);
                            rBig = r2.get(0);
                        }
                        if (r2.get(0).width() > r3.get(0).width() && r3.get(0).width() > r1.get(0).width()) {
                            r = r3.get(0);
                            rMin = r1.get(0);
                            rBig = r2.get(0);
                        }
                        if (r3.get(0).width() > r1.get(0).width() && r1.get(0).width() > r2.get(0).width()) {
                            r = r1.get(0);
                            rMin = r2.get(0);
                            rBig = r3.get(0);
                        }
                        if (r3.get(0).width() > r2.get(0).width() && r2.get(0).width() > r1.get(0).width()) {
                            r = r2.get(0);
                            rMin = r1.get(0);
                            rBig = r3.get(0);
                        }

                        if (rMin != null && rBig != null && r != null) {
                            character.mListRectContour.add(rMin);
                            character.mListRectContour.add(r);
                            character.mListRectContour.add(rBig);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i).getLabel();
            Bundle bundle = new Bundle();
            bundle.putString("Char", string);
            switch (string) {
                case "A":
                    if (!mMapFeatures.get("A")) {
                        mMapFeatures.put("A", true);
                    }
                    if (isA(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;
                case "a":
                    if (!mMapFeatures.get("a")) {
                        mMapFeatures.put("a", true);
                    }
                    if (isa(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "B":
                    if (!mMapFeatures.get("B")) {
                        mMapFeatures.put("B", true);
                    }
                    if (isB(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "b":
                    if (!mMapFeatures.get("b")) {
                        mMapFeatures.put("b", true);
                    }
                    if (isb(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isb1(character)) {
                            return "b1";
                        }
                    }*/
                    break;

                case "b1":
                    if (!mMapFeatures.get("b1")) {
                        mMapFeatures.put("b1", true);
                    }
                    if (isb1(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isk1(character)) {
                            return "k1";
                        }
                    }*/
                    break;

                case "C":
                    if (!mMapFeatures.get("C")) {
                        mMapFeatures.put("C", true);
                    }
                    if (isC(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "D":
                    if (!mMapFeatures.get("D")) {
                        mMapFeatures.put("D", true);
                    }
                    if (isD(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "d":
                    if (!mMapFeatures.get("d")) {
                        mMapFeatures.put("d", true);
                    }
                    if (isd(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isA(character)) {
                            return "A";
                        }
                        if (isa(character)) {
                            return "a";
                        }
                        if (isU(character)) {
                            return "U";
                        }
                        if (isb1(character)) {
                            return "b1";
                        }
                    }*/
                    break;

                case "E":
                    if (!mMapFeatures.get("E")) {
                        mMapFeatures.put("E", true);
                    }
                    if (isE(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "e":
                    if (!mMapFeatures.get("e")) {
                        mMapFeatures.put("e", true);
                    }
                    if (ise(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isC(character)) {
                            return "C";
                        }
                    }*/
                    break;

                case "F":
                    if (!mMapFeatures.get("F")) {
                        mMapFeatures.put("F", true);
                    }
                    if (isF(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "f":
                    if (!mMapFeatures.get("f")) {
                        mMapFeatures.put("f", true);
                    }
                    if (isf(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isb1(character)) {
                            return "b1";
                        }
                    }*/
                    break;

                case "G":
                    if (!mMapFeatures.get("G")) {
                        mMapFeatures.put("G", true);
                    }
                    if (isG(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "g":
                    if (!mMapFeatures.get("g")) {
                        mMapFeatures.put("g", true);
                    }
                    if (isg(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "H":
                    if (!mMapFeatures.get("H")) {
                        mMapFeatures.put("H", true);
                    }
                    if (isH(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isA(character)) {
                            return "A";
                        }
                    }*/
                    break;

                case "h":
                    if (!mMapFeatures.get("h")) {
                        mMapFeatures.put("h", true);
                    }
                    if (ish(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isA(character)) {
                            return "A";
                        }
                    }*/
                    break;

                case "I":
                    if (!mMapFeatures.get("I")) {
                        mMapFeatures.put("I", true);
                    }
                    if (isI(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "i":
                    if (!mMapFeatures.get("i")) {
                        mMapFeatures.put("i", true);
                    }
                    if (isi(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "J":
                    if (!mMapFeatures.get("J")) {
                        mMapFeatures.put("J", true);
                    }
                    if (isJ(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "j":
                    if (!mMapFeatures.get("j")) {
                        mMapFeatures.put("j", true);
                    }
                    if (isj(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isU(character)) {
                            return "U";
                        }
                    }*/
                    break;

                case "K":
                    if (!mMapFeatures.get("K")) {
                        mMapFeatures.put("K", true);
                    }
                    if (isK(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isk(character)) {
                            return "k";
                        } else {
                            if (isk1(character)) {
                                return "k1";
                            }
                        }
                    }*/
                    break;

                case "k":
                    if (!mMapFeatures.get("k")) {
                        mMapFeatures.put("k", true);
                    }
                    if (isk(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "k1":
                    if (!mMapFeatures.get("k1")) {
                        mMapFeatures.put("k1", true);
                    }
                    if (isk1(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "L":
                    if (!mMapFeatures.get("L")) {
                        mMapFeatures.put("L", true);
                    }
                    if (isL(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "l":
                    if (!mMapFeatures.get("l")) {
                        mMapFeatures.put("l", true);
                    }
                    if (isl(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "M":
                    if (!mMapFeatures.get("M")) {
                        mMapFeatures.put("M", true);
                    }
                    if (isM(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "m":
                    if (!mMapFeatures.get("m")) {
                        mMapFeatures.put("m", true);
                    }
                    if (ism(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "N":
                    if (!mMapFeatures.get("N")) {
                        mMapFeatures.put("N", true);
                    }
                    if (isN(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "n":
                    if (!mMapFeatures.get("n")) {
                        mMapFeatures.put("n", true);
                    }
                    if (isn(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (ism(character)) {
                            return "m";
                        }
                    }*/
                    break;

                case "O":
                    if (!mMapFeatures.get("O")) {
                        mMapFeatures.put("O", true);
                    }
                    if (isO(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } else {
                        if (is0(character)) {
                            bundle.putString("Char", "0");
                            bundle.putBoolean("Result", true);
                            return bundle;
                        }
                        /*if (isC(character)) {
                            return "C";
                        }*/
                    }
                    break;

                case "P":
                    if (!mMapFeatures.get("P")) {
                        mMapFeatures.put("P", true);
                    }
                    if (isP(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isB(character)) {
                            return "B";
                        }
                    }*/
                    break;

                case "Q":
                    if (!mMapFeatures.get("Q")) {
                        mMapFeatures.put("Q", true);
                    }
                    if (isQ(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "q":
                    if (!mMapFeatures.get("q")) {
                        mMapFeatures.put("q", true);
                    }
                    if (isq(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "R":
                    if (!mMapFeatures.get("R")) {
                        mMapFeatures.put("R", true);
                    }
                    if (isR(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "r":
                    if (!mMapFeatures.get("r")) {
                        mMapFeatures.put("r", true);
                    }
                    if (isr(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "S":
                    if (!mMapFeatures.get("S")) {
                        mMapFeatures.put("S", true);
                    }
                    if (isS(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "U":
                    if (!mMapFeatures.get("U")) {
                        mMapFeatures.put("U", true);
                    }
                    if (isU(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "u":
                    if (!mMapFeatures.get("u")) {
                        mMapFeatures.put("u", true);
                    }
                    if (isu(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else if (isa(character)) {
                        return "a";
                    }*/
                    break;

                case "V":
                    if (!mMapFeatures.get("V")) {
                        mMapFeatures.put("V", true);
                    }
                    if (isV(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isH(character)) {
                            return "H";
                        }
                    }*/
                    break;

                case "T":
                    if (!mMapFeatures.get("T")) {
                        mMapFeatures.put("T", true);
                    }
                    if (isT(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "t":
                    if (!mMapFeatures.get("t")) {
                        mMapFeatures.put("t", true);
                    }
                    if (ist(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isZ(character)) {
                            return "Z";
                        }
                    }*/
                    break;

                case "W":
                    if (!mMapFeatures.get("W")) {
                        mMapFeatures.put("W", true);
                    }
                    if (isW(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "X":
                    if (!mMapFeatures.get("X")) {
                        mMapFeatures.put("X", true);
                    }
                    if (isX(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "Y":
                    if (!mMapFeatures.get("Y")) {
                        mMapFeatures.put("Y", true);
                    }
                    if (isY(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "y":
                    if (!mMapFeatures.get("y")) {
                        mMapFeatures.put("y", true);
                    }
                    if (isy(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isb1(character)) {
                            return "b1";
                        }
                    }*/
                    break;

                case "Z":
                    if (!mMapFeatures.get("Z")) {
                        mMapFeatures.put("Z", true);
                    }
                    if (isZ(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "1":
                    if (!mMapFeatures.get("1")) {
                        mMapFeatures.put("1", true);
                    }
                    if (is1(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isA(character)) {
                            return "A";
                        }
                    }*/
                    break;

                case "2":
                    if (!mMapFeatures.get("2")) {
                        mMapFeatures.put("2", true);
                    }
                    if (is2(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (isa(character)) {
                            return "a";
                        }
                        if (isd(character)) {
                            return "d";
                        }
                    }*/
                    break;

                case "3":
                    if (!mMapFeatures.get("3")) {
                        mMapFeatures.put("3", true);
                    }
                    if (is3(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "4":
                    if (!mMapFeatures.get("4")) {
                        mMapFeatures.put("4", true);
                    }
                    if (is4(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "5":
                    if (!mMapFeatures.get("5")) {
                        mMapFeatures.put("5", true);
                    }
                    if (is5(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "6":
                    if (!mMapFeatures.get("6")) {
                        mMapFeatures.put("6", true);
                    }
                    if (is6(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "7":
                    if (!mMapFeatures.get("7")) {
                        mMapFeatures.put("7", true);
                    }
                    if (is7(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                case "8":
                    if (!mMapFeatures.get("8")) {
                        mMapFeatures.put("8", true);
                    }
                    if (is8(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    } /*else {
                        if (is3(character)) {
                            return "3";
                        }
                    }*/
                    break;

                case "9":
                    if (!mMapFeatures.get("9")) {
                        mMapFeatures.put("9", true);
                    }
                    if (is9(character)) {
                        bundle.putBoolean("Result", true);
                        return bundle;
                    }
                    break;

                default:
                    break;
            }
        }
        String s = "";
        for (Label label : list) {
            s += label.getLabel();
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("Result", false);
        bundle.putString("Char", s);
        return bundle;
    }

    private boolean isA(Character character) {
        if (isW(character)) {
            return false;
        }
        ArrayList<Rect> listRect = character.mListRectContour;
        Bitmap src = character.mBitmap;
        if (listRect != null) {
            if (listRect.size() == 2 && connectedComponentTop(character) == 1) {
                Rect rSmall = listRect.get(0);
                boolean two = false;
                for (int i = src.getHeight() - 1; i >= rSmall.bottom && !two; i--) {
                    if (countRowSegments(i, src) == 2) {
                        two = true;
                    }
                }

                return two || connectedComponentBot(character) == 2;
            }
        }
        return false;
    }

    private boolean isa(Character character) {
        ArrayList<Rect> list = character.mListRectContour;
        Bitmap src = character.mBitmap;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);
            if (rSmall != null && rBig != null) {
                Bitmap right = cropBitmap(src, rSmall.right, 0, src.getWidth() - rSmall.right, src.getHeight());
                Character f = new Character();
                f.mBitmap = right;
                f.mListContours = findContour(right);
                boolean isi = isi(f), isL = isL(f);
                return isi || isL;
            }
        }
        return false;
    }

    private boolean isB(Character character) {
        Bitmap bitmap = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list.size() == 2) {
            int countThreeSegmentsCol = 0;
            int count = 0;
            boolean flag = false;
            for (int i = 0; i < bitmap.getHeight(); i++) {
                if (countRowSegments(i, bitmap) == 1) {
                    if (!flag) {
                        flag = true;
                        count++;
                    }
                } else {
                    flag = false;
                }
            }

            if (count != 2) {
                return false;
            }
            for (int i = 0; i < bitmap.getWidth(); i++) {
                if (countColumnSegments(i, bitmap) == 3) {
                    countThreeSegmentsCol++;
                }
            }
            return countThreeSegmentsCol > 0;
        } else {
            if (list.size() == 3) {
                int countFourSegmentsCol = 0;
                int count = 0;
                boolean flag = false;
                for (int i = 0; i < bitmap.getHeight(); i++) {
                    if (countRowSegments(i, bitmap) == 1) {
                        if (!flag) {
                            flag = true;
                            count++;
                        }
                    } else {
                        flag = false;
                    }
                }

                if (count == 2) {
                    for (int i = 0; i < bitmap.getWidth(); i++) {
                        if (countColumnSegments(i, bitmap) == 4) {
                            countFourSegmentsCol++;
                        }
                    }
                    return countFourSegmentsCol > 0;
                } else {
                    if (count == 3) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean isb(Character character) {
        /*ArrayList<Bitmap> contourImage = character.mListContours;
        if (contourImage.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(contourImage.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(contourImage.get(1), 0, 0);
            if (r1.size() == 1 && r2.size() == 1) {
                Rect area1 = r1.get(0), area2 = r2.get(0);
                Point p1 = getFirstPixel(contourImage.get(0), Color.RED);
                Point p2 = getFirstPixel(contourImage.get(1), Color.RED);
                if (p1 != null && p2 != null) {
                    if (p1.y > p2.y) {
                        return p2.x < contourImage.get(1).getWidth() / 2 && area1.bottom >= contourImage.get(0).getHeight() * 0.7d;
                    } else {
                        if (p2.y > p1.y) {
                            return p1.x < contourImage.get(0).getWidth() / 2 && area2.bottom >= contourImage.get(1).getHeight() * 0.7d;
                        }
                    }
                }
            }
        }*/
        Bitmap src = character.mBitmap;
        Bitmap rotated = rotateBitmap(src, 180);
        Character c = new Character();
        c.mBitmap = rotated;
        c.mListContours = findContour(rotated);
        c.mListRectContour = new ArrayList<>();
        ArrayList<Bitmap> contours = c.mListContours;
        if (contours.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(contours.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(contours.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    c.mListRectContour.add(rSmall);
                    c.mListRectContour.add(rBig);
                }
            }
        }
        return isq(c);
    }

    private boolean isb1(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                if (rect != null) {
                    if (rect.bottom <= src.getHeight() * 0.7d && rect.width() <= rect.height()) {
                        int offsetY = rect.bottom;
                        int offsetX = -1;
                        for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                            if (getColumnHeightFromTop(src, i) <= rect.bottom) {
                                offsetX = i;
                            }
                        }

                        if (offsetY >= 0 && offsetX >= 0) {
                            Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                            if (interested != null) {
                                int count = 0;
                                for (int i = interested.getHeight() - 1; i >= 0 && count < 5; i--) {
                                    if (countRowSegments(i, interested) == 2) {
                                        count++;
                                        offsetY = i;
                                    }
                                }

                                Bitmap interestedBot =
                                        cropBitmap(interested, 0, 0, interested.getWidth(), offsetY);
                                ArrayList<Rect> listRect = detectAreasOnBitmap(interestedBot, 0, 0);
                                if (listRect.size() == 2) {
                                    int firstTwo = -1;
                                    for (int i = 0; i < interested.getHeight() && firstTwo < 0; i++) {
                                        if (countRowSegments(i, interested) == 2) {
                                            firstTwo = i;
                                        }
                                    }

                                    if (firstTwo >= 0) {
                                        return firstTwo <= 0.5d * interested.getHeight();
                                    }
                                }

                            }
                        }
                    }
                }
            } else {
                if (list.size() == 3) {
                    Rect rSmall = list.get(0);
                    Rect rect = list.get(1);
                    if (rect != null && rSmall != null) {

                        if (rect.bottom <= src.getHeight() * 0.7d && rect.width() <= rect.height()) {
                            int offsetY = rect.bottom;
                            int offsetX = -1;
                            for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                                if (getColumnHeightFromTop(src, i) < rect.bottom) {
                                    offsetX = i;
                                }
                            }

                            if (offsetY >= 0) {
                                Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                                if (interested != null) {
                                    int count = 0;
                                    for (int i = interested.getHeight() - 1; i >= 0 && count < 5; i--) {
                                        if (countRowSegments(i, interested) == 2) {
                                            count++;
                                            offsetY = i;
                                        }
                                    }

                                    Bitmap interestedBot =
                                            cropBitmap(interested, 0, 0, interested.getWidth(), offsetY);
                                    ArrayList<Rect> listRect = detectAreasOnBitmap(interestedBot, 0, 0);
                                    if (listRect.size() == 2) {
                                        int big = (listRect.get(0).width() > listRect.get(1).width()) ?
                                                listRect.get(0).width() : listRect.get(1).width();
                                        int small = (listRect.get(0).width() <= listRect.get(1).width()) ?
                                                listRect.get(0).width() : listRect.get(1).width();

                                        return ((double) big) / small >= 1.3d && rSmall.left >= src.getWidth() * 0.3d;
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isC(Character character) {
        Bitmap src = character.mBitmap;
        int split = (int) (src.getWidth() * 0.2d);
        Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
        if (right != null) {
            ArrayList<Rect> listRect = detectAreasOnBitmap(right, 0, 0);
            for (int i = 0; i < right.getWidth(); i++) {
                if (countColumnSegments(i, right) == 3) {
                    return false;
                }
            }
            return listRect.size() == 2;
            /*if (listRect != null && listRect.size() == 2) {
                Rect rect1 = listRect.get(0);
                Bitmap right1 = cropBitmap(right, rect1.left, rect1.top, rect1.width(), rect1.height());
                int count1 = countBlack(right1);
                Rect rect2 = listRect.get(1);
                Bitmap right2 = cropBitmap(right, rect2.left, rect2.top, rect2.width(), rect2.height());
                int count2 = countBlack(right2);

                if (count1 > count2) {
                    return (double) count1 / count2 <= 1.5d && !triple;
                } else {
                    return (count1 == count2 || (double) count2 / count1 <= 1.5d) && !triple;
                }
            }*/
        }
        return false;
    }

    private boolean isD(Character character) {
        if (isB(character)) {
            return false;
        }
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);

            int countOne = 0, countTwo = 0, countThree = 0, firstOne = -1, lastOne = -1;
            boolean end = false;
            for (int i = 0; i < src.getWidth(); i++) {
                if (countColumnSegments(i, src) == 1) {
                    countOne++;
                }
                if (countColumnSegments(i, src) == 2) {
                    countTwo++;
                }
                if (countColumnSegments(i, src) == 3) {
                    countThree++;
                }
            }

            if (countOne + countTwo >= src.getWidth() * 0.95d && countThree == 0) {
                for (int i = 0; i < src.getHeight(); i++) {
                    if (countRowSegments(i, src) == 1) {
                        if (firstOne < 0) {
                            firstOne = i;
                        }
                        if (end) {
                            lastOne = i;
                        }
                    } else {
                        if (firstOne >= 0) {
                            end = true;
                        }
                    }
                }
                if (firstOne >= 0 && lastOne >= 0) {
                    return firstOne <= rSmall.top && lastOne >= rSmall.bottom;
                }
            }
        }

        return false;
    }

    private boolean isd(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rSmall = list.get(0);
                Rect rBig = list.get(1);

                if (Math.abs(rSmall.top - rBig.top) > Math.abs(rSmall.bottom - rBig.bottom)
                        && Math.abs(rSmall.right - rBig.right) > Math.abs(rSmall.left - rBig.left)) {
                    int offsetY = Math.abs(rSmall.top - rBig.top) / 2;
                    int offsetX = Math.abs(rSmall.left - rSmall.right) / 2;

                    Point point = new Point(offsetX, offsetY);
                    boolean moved = false, end = false;
                    while (!end) {
                        int x = point.x, y = point.y;
                        if (x + 1 < src.getWidth()) {
                            if (src.getPixel(x + 1, y) == Color.WHITE) {
                                point = new Point(x + 1, y);
                                moved = true;
                            }
                        }
                        if (x + 1 < src.getWidth() && y + 1 < src.getHeight() && !moved) {
                            if (src.getPixel(x + 1, y + 1) == Color.WHITE) {
                                point = new Point(x + 1, y + 1);
                                moved = true;
                            }
                        }
                        if (y + 1 < src.getHeight() && !moved) {
                            if (src.getPixel(x, y + 1) == Color.WHITE) {
                                point = new Point(x, y + 1);
                                moved = true;
                            }
                        }

                        if (moved) {
                            moved = false;
                        } else {
                            end = true;
                        }
                    }

                    Bitmap interested = cropBitmap(src, point.x, 0, src.getWidth() - point.x, src.getHeight());
                    if (interested != null) {
                        Character f = new Character();
                        f.mBitmap = interested;
                        f.mListContours = findContour(interested);
                        return isi(f) || isL(f);
                    }
                }
            }
        }
        return false;
    }

    private boolean isE(Character character) {
        if (character.mListContours.size() == 1) {
            int split = (int) (character.mBitmap.getWidth() * 0.3d);
            Bitmap src = character.mBitmap;
            Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
            ArrayList<Rect> rect = detectAreasOnBitmap(right, 0, 0);
            if (rect != null) {
                if (rect.size() == 3) {
                    boolean component1 = (rect.get(0).width() * 1.d) / src.getWidth() >= 0.4d;
                    boolean component2 = (rect.get(1).width() * 1.d) / src.getWidth() >= 0.4d;
                    boolean component3 = (rect.get(2).width() * 1.d) / src.getWidth() >= 0.4d;

                    return component1 && component2 && component3;
                }
            }
        }
        return false;
    }

    private boolean ise(Character character) {
        ArrayList<Rect> list = character.mListRectContour;
        Bitmap src = character.mBitmap;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);

            int offsetTop = Math.abs(rSmall.top - rBig.top);
            int offsetBot = Math.abs(rSmall.bottom - rBig.bottom);
            if (offsetBot >= offsetTop) {
                int countThree = 0;
                for (int i = 0; i < src.getWidth(); i++) {
                    if (countColumnSegments(i, src) == 3) {
                        countThree++;
                    }
                }
                return countThree > 0 && rSmall.width() >= rSmall.height();
            }
        }

        return false;
    }

    private boolean isF(Character character) {
        int mid = character.mBitmap.getWidth() / 2;
        Bitmap src = character.mBitmap;
        Bitmap right = cropBitmap(src, mid, 0, src.getWidth() - mid, src.getHeight());
        if (right != null) {
            ArrayList<Rect> list = detectAreasOnBitmap(right, 0, 0);
            if (list.size() == 2) {
                boolean closeToBot = false;
                for (Rect rect : list) {
                    if (rect.bottom >= src.getHeight() * 0.8d) {
                        closeToBot = true;
                    }
                }
                return !closeToBot;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isf(Character character) {
        Bitmap src = character.mBitmap;
        Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), (int) (src.getHeight() * 0.2d));
        if (detectAreasOnBitmap(top, 0, 0).size() >= 2) {
            return false;
        }
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                if (rect != null) {
                    boolean closeToBot = Math.abs(rect.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                    int count = 0;
                    int increase = 0;
                    boolean stop = false;
                    for (int i = 0; i < src.getHeight() && !stop; i++) {
                        int blank = getHorizontalBlankCounts(i, src);
                        if (blank > 0) {
                            if (blank >= count) {
                                increase++;
                                count = blank;
                            } else {
                                stop = true;
                            }
                        } else {
                            if (count > 0) {
                                stop = true;
                            }
                        }
                    }

                    return increase > 0 && closeToBot;
                }
            }
        }
        return false;
    }

    private boolean isG(Character character) {
        if (isS(character)) {
            return false;
        }
        if (character.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int split = (int) (src.getWidth() * 0.4d);
        Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
        if (right != null) {
            ArrayList<Rect> listRect = detectAreasOnBitmap(right, 0, 0);
            boolean triple = false;
            for (int i = 0; i < right.getWidth() && !triple; i++) {
                if (countColumnSegments(i, right) == 3) {
                    triple = true;
                }
            }
            if (listRect != null && listRect.size() == 2) {
                Rect rect1 = listRect.get(0);
                Bitmap right1 = cropBitmap(right, rect1.left, rect1.top, rect1.width(), rect1.height());
                int count1 = countBlack(right1);
                Rect rect2 = listRect.get(1);
                Bitmap right2 = cropBitmap(right, rect2.left, rect2.top, rect2.width(), rect2.height());
                int count2 = countBlack(right2);

                return (count1 >= 2 * count2 || count2 >= 2 * count1) && triple;
            }
        }
        return false;
    }

    private boolean isg(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 3) {
                Rect rMin = list.get(0), r = list.get(1);
                if (r != null && rMin != null) {
                    boolean closeToBot = Math.abs(rMin.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                    boolean tall = rMin.width() <= rMin.height();
                    boolean fat = r.width() >= r.height();
                    return closeToBot && tall && fat;
                }
            }
        }
        return false;
    }

    private boolean isH(Character character) {
        Bitmap src = character.mBitmap;
        Bitmap mid = cropBitmap(src, 0, (int) (src.getHeight() * 0.2d), src.getWidth(), (int) (src.getHeight() * 0.6d));
        if (detectAreasOnBitmap(mid, 0, 0).size() != 1) {
            return false;
        }
        if (triangleFromTop(character) != 1) {
            int fragmentHeight = src.getHeight() / 4;
            int startAbove = 0;
            int startBelow = src.getHeight() - fragmentHeight;
            Bitmap fragmentAbove = cropBitmap(src, 0, startAbove, src.getWidth(), fragmentHeight);
            Bitmap fragmentBelow = cropBitmap(src, 0, startBelow, src.getWidth(), fragmentHeight);

            ArrayList<Rect> listAbove = detectAreasOnBitmap(fragmentAbove, 0, 0);
            ArrayList<Rect> listBelow = detectAreasOnBitmap(fragmentBelow, 0, 0);

            if (listAbove.size() == 2 && listBelow.size() == 2) {
                boolean triangle = false;
                for (int i = 0; i < listAbove.size() && !triangle; i++) {
                    Rect rect = listAbove.get(i);
                    Character character1 = new Character();
                    character1.mBitmap = cropBitmap(src, rect.left, rect.top, rect.width(), rect.height());
                    if (triangleFromTop(character1) == 1 && triangleFromBot(character1) == 1) {
                        triangle = true;
                    }
                }
                if (triangle) {
                    return false;
                } else {
                    for (int i = 0; i < listBelow.size() && !triangle; i++) {
                        Rect rect = listBelow.get(i);
                        Character character1 = new Character();
                        character1.mBitmap = cropBitmap(src, rect.left, rect.top, rect.width(), rect.height());
                        if (triangleFromTop(character1) == 1 && triangleFromBot(character1) == 1) {
                            triangle = true;
                        }
                    }

                    return !triangle;
                }
            }

            return false;
        }
        return false;
    }

    private boolean ish(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                if (rect != null) {
                    if (rect.bottom <= src.getHeight() * 0.7d && rect.width() <= rect.height()) {
                        int offsetY = rect.bottom;
                        int offsetX = -1;
                        for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                            if (getColumnHeightFromTop(src, i) < rect.bottom) {
                                offsetX = i;
                            }
                        }

                        if (offsetY >= 0) {
                            Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                            if (interested != null) {
                                    /*int count = 0;
                                    for (int i = interested.getHeight() - 1; i >= 0 && count < 5; i--) {
                                        if (countRowSegments(i, interested) == 2) {
                                            count++;
                                            offsetY = i;
                                        }
                                    }

                                    Bitmap interestedBot =
                                            cropBitmap(interested, 0, offsetY, interested.getWidth(), interested.getHeight() - offsetY);
                                    if (interestedBot != null) {
                                        return detectAreasOnBitmap(interestedBot, 0, 0).size() == 2;
                                    }*/
                                offsetY = -1;
                                for (int i = 0; i < interested.getHeight() && offsetY < 0; i++) {
                                    if (countRowSegments(i, interested) == 2) {
                                        offsetY = i;
                                    }
                                }
                                Bitmap interestedBot =
                                        cropBitmap(interested, 0, offsetY, interested.getWidth(), interested.getHeight() - offsetY);
                                if (interestedBot != null) {
                                    Character c = new Character();
                                    c.mBitmap = interestedBot;
                                    c.mListContours = findContour(interestedBot);
                                    return isN(c) || isn(c);
                                }

                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isI(Character character) {
        Bitmap src = character.mBitmap;
        int countThree = 0;
        for (int i = 0; i < src.getWidth(); i++) {
            if (countColumnSegments(i, src) == 3) {
                countThree++;
            }
        }
        if (countThree > src.getWidth() * 0.3d) {
            return false;
        }
        Bitmap rotated = rotateBitmap(character.mBitmap, 90);
        Character f = new Character();
        f.mBitmap = rotated;
        return isH(f);
    }

    private boolean isi(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Bitmap> list = character.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int countOne = 0, countTwo = 0;
                int firstTwo = -1;
                for (int i = 0; i < src.getHeight(); i++) {
                    if (countRowSegments(i, src) == 2) {
                        countTwo++;
                        if (firstTwo < 0) {
                            firstTwo = i;
                        }
                    }
                    if (countRowSegments(i, src) == 1) {
                        countOne++;
                    }
                }
                return countOne >= 0.95d * src.getHeight() || firstTwo >= src.getHeight() * 0.5d;
            }
        }
        return false;
    }

    private boolean isJ(Character character) {
        if (character.mListContours.size() == 2) {
            return false;
        }
        Bitmap bitmap = character.mBitmap;
        int offsetY = (int) (bitmap.getHeight() * 0.2d);
        Bitmap upper = cropBitmap(bitmap, 0, 0, bitmap.getWidth(), offsetY);
        ArrayList<Rect> upperList = detectAreasOnBitmap(upper, 0, 0);
        if (upperList.size() == 1 && (double) upperList.get(0).width() / bitmap.getWidth() > 0.6d) {
            int offsetX = bitmap.getWidth() / 2;
            offsetY = (int) (bitmap.getHeight() * 0.6d);
            Bitmap underLeft = cropBitmap(bitmap, 0, offsetY, offsetX, bitmap.getHeight() - offsetY);
            Bitmap underRight = cropBitmap(bitmap, offsetX + 1, offsetY, bitmap.getWidth() - offsetX - 1, bitmap.getHeight() - offsetY);

            int countLeft = 0;
            for (int i = 0; i < underLeft.getHeight(); i++) {
                if (countRowSegments(i, underLeft) == 2) {
                    countLeft++;
                }
            }
            int countRight = 0;
            for (int i = 0; i < underRight.getHeight(); i++) {
                if (countRowSegments(i, underRight) == 2) {
                    countRight++;
                }
            }

            return countLeft > 0 && countRight == 0;
        }
        return false;
    }

    private boolean isj(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Bitmap> list = character.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int split = (int) (src.getWidth() * 0.5d);
                Bitmap right = cropBitmap(src, 0, 0, split, src.getHeight());
                ArrayList<Rect> rect = detectAreasOnBitmap(right, 0, 0);
                int count = 0;
                for (int i = 0; i < src.getHeight(); i++) {
                    if (countRowSegments(i, src) == 2) {
                        count++;
                    }
                }
                boolean halfUnder = rect.get(0).top > 0.5 * src.getHeight();
                boolean closeToBot = rect.get(0).bottom >= src.getHeight() * 0.85d;
                boolean segmentsTwo = count < src.getHeight() * 0.5d;
                if (rect.size() == 1) {
                    return (halfUnder && closeToBot && segmentsTwo);
                }
            }
        }
        return false;
    }

    private boolean isK(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Bitmap> list = character.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int startY = -1, endY = -1;
                for (int i = 0; i < src.getHeight() && startY < 0; i++) {
                    if (countRowSegments(i, src) == 2) {
                        startY = i;
                    }
                }
                for (int i = src.getHeight() - 1; i >= 0 && endY < 0; i--) {
                    if (countRowSegments(i, src) == 2) {
                        endY = i;
                    }
                }
                if (startY >= 0 && endY >= 0) {
                    int top = -1, bot = -1, left = -1, right = -1;
                    for (int i = startY; i <= endY && top < 0; i++) {
                        if (countRowSegments(i, src) == 1) {
                            top = i;
                        }
                    }
                    for (int i = endY; i >= startY && bot < 0; i--) {
                        if (countRowSegments(i, src) == 1) {
                            bot = i;
                        }
                    }

                    if (top >= 0 && bot >= 0 && left >= 0 && right >= 0) {
                        int row = (top + bot) / 2;
                        int col = (left + right) / 2;
                        Character Top = new Character();
                        Top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row);
                        Character Bot = new Character();
                        Bot.mBitmap = cropBitmap(src, 0, row, src.getWidth(), src.getHeight() - row);
                        return triangleFromBot(Top) == 1 && triangleFromTop(Bot) == 1;
                    }
                }
            }
        }
        return false;
    }

    private boolean isk(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Bitmap> list = character.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int x = -1;
                boolean one = false;
                for (int i = src.getWidth() - 1; i >= 0 && !one; i--) {
                    if (countColumnSegments(i, src) == 2) {
                        x = i;
                    } else {
                        if (x > 0) {
                            one = true;
                        }
                    }
                }

                int y = -1;
                if (x >= 0) {
                    for (int row = 0; row < src.getHeight() && y < 0; row++) {
                        if (src.getPixel(x, row) != Color.WHITE) {
                            y = row;
                        }
                    }

                    return (getHorizontalBlankCounts(y, src) < 2d * FingerDrawerView.CurrentPaintSize);
                }
                /*int startY = -1;
                for (int i = 0; i < src.getHeight() && startY < 0; i++) {
                    if (countRowSegments(i, src) == 2) {
                        startY = i;
                    }
                }

                if (startY >= 0) {
                    boolean begin = false;
                    int startX = -1;
                    for (int i = 0; i < src.getWidth() && startX < 0; i++) {
                        if (src.getPixel(i, startY) == Color.WHITE) {
                            if (begin) {
                                startX = i;
                            } else {
                                begin = true;
                            }
                        }
                    }

                    if (startX >= 0) {
                        Point pointTop = new Point();
                        boolean moved = false, end = false;
                        while (!end) {
                            int x = pointTop.x, y = pointTop.y;
                            if (x - 1 >= 0 && y + 1 < src.getHeight()) {
                                if (src.getPixel(x - 1, y + 1) == Color.WHITE) {
                                    pointTop = new Point(x - 1, y + 1);
                                    moved = true;
                                }
                            }
                            if (y + 1 < src.getHeight() && !moved) {
                                if (src.getPixel(x, y + 1) == Color.WHITE) {
                                    pointTop = new Point(x, y + 1);
                                    moved = true;
                                }
                            }
                            if (x - 1 >= 0 && !moved) {
                                if (src.getPixel(x - 1, y) == Color.WHITE) {
                                    pointTop = new Point(x - 1, y);
                                    moved = true;
                                }
                            }

                            if (moved) {
                                moved = false;
                            } else {
                                end = true;
                            }
                        }

                        int bot = -1;
                        for (int i = pointTop.y; i < src.getHeight() && bot < 0; i++) {
                            if (countRowSegments(i, src) == 2) {
                                bot = i;
                            }
                        }

                        if (bot >= 0) {
                            Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), pointTop.y + 2);
                            Character cTop = new Character(); cTop.mBitmap = top;
                            Bitmap bottom = cropBitmap(src, 0, bot - 2, src.getWidth(), src.getHeight() - bot + 2);
                            Character cBot = new Character(); cBot.mBitmap = bottom;
                            return isV(cTop) && isV_Inverse(cBot);
                        }
                    }
                }*/
            }
        }
        return false;
    }

    private boolean isk1(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() < 3) {
                return false;
            } else {
                if (list.size() == 3) {
                    Rect rBig = list.get(2);
                    Rect rTop = (Math.abs(rBig.top - list.get(0).top) < Math.abs(rBig.top - list.get(1).top) ? list.get(0) : list.get(1));
                    Rect rMid = (Math.abs(rBig.top - list.get(0).top) >= Math.abs(rBig.top - list.get(1).top) ? list.get(0) : list.get(1));
                    ;

                    if (rTop != null && rMid != null) {
                        int offsetY = rMid.bottom;
                        int offsetX = -1;
                        for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                            if (getColumnHeightFromTop(src, i) < rMid.bottom) {
                                offsetX = i;
                            }
                        }

                        if (offsetY >= 0) {
                            Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                            if (interested != null) {
                                        /*int two = 0;
                                        for (int i = 0; i < interested.getHeight(); i++) {
                                            if (countRowSegments(i, src) == 2) {
                                                two++;
                                            }
                                        }

                                        return two > 0;*/
                                Character f = new Character();
                                f.mBitmap = interested;
                                return isV_Inverse(f);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isL(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Bitmap> list = character.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int split = (int) (src.getWidth() * 0.7d);
                Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
                ArrayList<Rect> rect = detectAreasOnBitmap(right, 0, 0);
                int count = 0;
                for (int i = 0; i < src.getHeight(); i++) {
                    if (countRowSegments(i, src) == 2) {
                        count++;
                    }
                }
                boolean halfUnder = rect.get(0).top > 0.7 * src.getHeight();
                boolean closeToBot = rect.get(0).bottom >= src.getHeight() * 0.9d;
                boolean segmentsTwo = count <= src.getHeight() * 0.2d;
                if (rect.size() == 1) {
                    return (halfUnder && closeToBot && segmentsTwo);
                }
            }
        }
        return false;
    }

    private boolean isl(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                if (rect != null) {
                    if (rect.bottom <= src.getHeight() * 0.7d && rect.width() <= rect.height()) {
                        int offsetY = rect.bottom;
                        int offsetX = -1;
                        for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                            if (getColumnHeightFromTop(src, i) < rect.bottom) {
                                offsetX = i;
                            }
                        }

                        Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                        if (interested != null) {
                            int two = 0, one = 0;
                            for (int i = 0; i < interested.getHeight(); i++) {
                                if (countRowSegments(i, interested) == 2) {
                                    two++;
                                }
                                if (countRowSegments(i, interested) == 1) {
                                    one++;
                                }
                            }
                            return ((double) one / interested.getHeight()) >= 0.8d && ((double) two / interested.getHeight()) <= 0.3d;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isM(Character character) {
        Bitmap src = character.mBitmap;
        int left = (int) (src.getWidth() * 0.15d);
        Point blackLeft = getFirstBackFromBot(src, left);
        if (blackLeft != null) {
            boolean increase = false;
            for (int i = left + 1; i < src.getWidth() && !increase; i++) {
                Point p = getFirstBackFromBot(src, i);
                if (p != null) {
                    if (p.y <= blackLeft.y) {
                        blackLeft = new Point(p.x, p.y);
                    } else {
                        increase = true;
                    }
                }
            }
        }
        int right = (int) (src.getWidth() * 0.85d);
        Point blackRight = getFirstBackFromBot(src, right);
        if (blackRight != null) {
            boolean increase = false;
            for (int i = right - 1; i >= 0 && !increase; i--) {
                Point p = getFirstBackFromBot(src, i);
                if (p != null) {
                    if (p.y <= blackRight.y) {
                        blackRight = new Point(p.x, p.y);
                    } else {
                        increase = true;
                    }
                }
            }
        }

        if (blackLeft != null && blackRight != null) {
            if (Math.abs(blackLeft.x - blackRight.x) > 0 && src.getHeight() > 0) {
                Bitmap center = cropBitmap(src, blackLeft.x, 0, blackRight.x - blackLeft.x, src.getHeight());
                if (center != null) {
                    Character f = new Character();
                    f.mBitmap = center;
                    return isV(f);
                }
            }
        }
        return false;
    }

    private boolean ism(Character character) {
        if (character.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int right = (int) (src.getWidth() * 0.8d);
        int height = getColumnHeightFromBot(src, right);
        boolean decrease = false;
        for (int i = right - 1; i >= 0 && !decrease; i--) {
            int h = getColumnHeightFromTop(src, i);
            if (h >= height) {
                height = h;
                right = i;
            } else {
                decrease = true;
            }
        }
        Bitmap left = cropBitmap(src, 0, 0, right, src.getHeight());
        if (right - 5 >= 0) {
            Bitmap r = cropBitmap(src, right - 5, 0, src.getWidth() - right + 5, src.getHeight());
            if (left != null && r != null) {
                Character fLeft = new Character();
                fLeft.mBitmap = left;
                fLeft.mListContours = findContour(left);
                Character fRight = new Character();
                fRight.mBitmap = r;
                fRight.mListContours = findContour(r);
                if (isN(fLeft)) {
                    return true;
                }
                if (isN(fRight)) {
                    return true;
                }
                if (isn(fLeft)) {
                    return true;
                }
                if (isn(fRight)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isN(Character character) {
        Bitmap src = character.mBitmap;
        int mid = src.getWidth() / 2;
        int startTop = getColumnHeightFromTop(src, mid);
        int startBot = getColumnHeightFromBot(src, mid);

        Bitmap right = cropBitmap(src, mid, startTop, src.getWidth() - mid, src.getHeight() - startTop);
        Bitmap left = cropBitmap(src, 0, 0, mid, startBot);
        if (right != null && left != null) {
            Character fLeft = new Character();
            fLeft.mBitmap = left;
            Character fRight = new Character();
            fRight.mBitmap = right;
            return isV_Inverse(fLeft) && isV(fRight);
        }
        return false;
    }

    private boolean isn(Character character) {
        if (character.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int one = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                one++;
            }
        }
        int split = (int) (src.getHeight() * 0.7d);
        Bitmap bot = cropBitmap(src, 0, split, src.getWidth(), src.getHeight() - split);
        double ratio = ((double) one) / src.getHeight();
        int count = detectAreasOnBitmap(bot, 0, 0).size();
        return count == 2 && ratio <= 0.4d;
    }

    private boolean isO(Character character) {
        if (is0(character)) {
            return false;
        }
        ArrayList<Rect> list = character.mListRectContour;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);

            int offsetTop = Math.abs(rBig.top - rSmall.top);
            int offsetBot = Math.abs(rBig.bottom - rSmall.bottom);
            int offsetLeft = Math.abs(rBig.left - rSmall.left);
            int offsetRight = Math.abs(rBig.right - rSmall.right);
            boolean circleHorizontal = rBig.width() > rBig.height() * 0.85d;

            return (Math.abs(offsetBot - offsetTop) <= 3) && (Math.abs(offsetBot - offsetRight) <= 3) &&
                    (Math.abs(offsetBot - offsetLeft) <= 3) && circleHorizontal;
        }
        return false;
    }

    private boolean isP(Character character) {
        Bitmap bitmap = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rSmall = list.get(0);
                if (rSmall.bottom <= bitmap.getHeight() * 0.7d) {
                    int one = 0, two = 0;
                    int maxHorizontal = -1;
                    for (int i = (int) (rSmall.bottom + 2 * FingerDrawerView.CurrentPaintSize); i < bitmap.getHeight(); i++) {
                        if (countRowSegments(i, bitmap) == 1) {
                            one++;
                            int pos = -1;
                            for (int j = bitmap.getWidth() - 1; j >= 0 && pos < 0; j--) {
                                if (bitmap.getPixel(j, i) != Color.WHITE) {
                                    pos = j;
                                }
                            }

                            if (pos > maxHorizontal) {
                                maxHorizontal = pos;
                            }
                        }
                        if (countRowSegments(i, bitmap) == 2) {
                            two++;
                        }
                    }

                    return two < 0.1d * one && maxHorizontal >= 0 && maxHorizontal < bitmap.getWidth() * 0.5d;
                }
            }
        }
        /*int thinRows = 0;
        int fatRows = 0;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (getHorizontalBlankCounts(i, bitmap) > 0) {
                fatRows++;
            } else {
                if (getHorizontalBlankCounts(i, bitmap) == 0) {
                    thinRows++;
                }
            }
        }

        return ((double) fatRows / bitmap.getHeight() >= 0.2d) && ((double) fatRows / bitmap.getHeight() <= 0.8d)
                && ((double) thinRows / bitmap.getHeight() >= 0.2d);*/
        return false;
    }

    private boolean isQ(Character character) {
        Bitmap src = character.mBitmap;
        /*int start = -1;
        int end = -1;
        for (int i = src.getHeight() / 2; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 3) {
                if (start < 0) {
                    start = i;
                }
                end = i;
            }
        }

        return (start >= 0 && end > start) && (start >= src.getHeight() / 2);*/
        ArrayList<Rect> bitmaps = character.mListRectContour;
        if (bitmaps.size() == 2) {
            Rect rSmall = bitmaps.get(0);
            Rect rBig = bitmaps.get(1);
            if (rSmall != null && rBig != null) {
                Bitmap interested = cropBitmap(src, rSmall.left, rSmall.top, rSmall.width(), rSmall.height());
                if (interested != null) {
                    for (int i = interested.getHeight() - 1; i >= 0; i--) {
                        for (int j = interested.getWidth() - 1; j >= 0; j--) {
                            if (countRowSegments(i, interested) == 3 && countColumnSegments(j, interested) == 3) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isq(Character character) {
        Bitmap bitmap = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rSmall = list.get(0);

                if (rSmall.bottom <= bitmap.getHeight() * 0.7d) {
                    int one = 0, two = 0;
                    int maxHorizontal = -1;
                    for (int i = (int) (rSmall.bottom + 2 * FingerDrawerView.CurrentPaintSize); i < bitmap.getHeight(); i++) {
                        if (countRowSegments(i, bitmap) == 1) {
                            one++;
                            int pos = -1;
                            for (int j = bitmap.getWidth() - 1; j >= 0 && pos < 0; j--) {
                                if (bitmap.getPixel(j, i) != Color.WHITE) {
                                    pos = j;
                                }
                            }

                            if (pos > maxHorizontal) {
                                maxHorizontal = pos;
                            }
                        }
                        if (countRowSegments(i, bitmap) == 2) {
                            two++;
                        }
                    }

                    return two < 0.1d * one && maxHorizontal >= 0 && maxHorizontal > bitmap.getWidth() * 0.5d;
                }
            }
        }
        return false;
    }

    private boolean isR(Character character) {
        if (isB(character) || isS(character) || character.mListContours.size() != 2) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int mid = src.getWidth() / 2;
        Bitmap right = cropBitmap(src, mid, 0, src.getWidth() - mid, src.getHeight());
        if (right != null) {
            ArrayList<Rect> list = detectAreasOnBitmap(right, 0, 0);
            if (list.size() == 2) {
                boolean closeToBot = false;
                for (Rect rect : list) {
                    if (rect.bottom >= src.getHeight() * 0.8) {
                        closeToBot = true;
                    }
                }
                if (closeToBot) {
                    int bot = (int) (src.getHeight() * 0.8d);
                    Bitmap botBitmap = cropBitmap(src, 0, bot, src.getWidth(), src.getHeight() - bot);
                    return detectAreasOnBitmap(botBitmap, 0, 0).size() == 2;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isr(Character character) {
        Bitmap src = character.mBitmap;
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                Rect rBounder = list.get(1);
                boolean closeToTopLeft = Math.abs(rBounder.top - rect.top) == Math.abs(rBounder.left - rect.left);
                int split = (int) (src.getHeight() * 0.7d);
                Bitmap bitmap = cropBitmap(src, 0, split, src.getWidth(), src.getHeight() - split);
                return closeToTopLeft && detectAreasOnBitmap(bitmap, 0, 0).size() == 2;
            }
        }
        return false;
    }

    private boolean isS(Character character) {
        int mid = character.mBitmap.getWidth() / 2;
        Bitmap src = character.mBitmap;
        Bitmap left = cropBitmap(character.mBitmap, 0, 0, mid, src.getHeight());
        Bitmap right = cropBitmap(character.mBitmap, mid, 0, src.getWidth() - mid, src.getHeight());

        if (left != null && right != null) {
            ArrayList<Rect> leftList = detectAreasOnBitmap(left, 0, 0);
            ArrayList<Rect> rightList = detectAreasOnBitmap(right, 0, 0);

            if (leftList.size() == 2 && rightList.size() == 2) {
                int leftAboveArea = leftList.get(0).width() * leftList.get(0).height();
                int leftBelowArea = leftList.get(1).width() * leftList.get(1).height();
                int rightAboveArea = rightList.get(0).width() * rightList.get(0).height();
                int rightBelowArea = rightList.get(1).width() * rightList.get(1).height();

                return (leftAboveArea >= leftBelowArea) && (rightBelowArea > rightAboveArea);
            }
        }
        return false;
    }

    private boolean isT(Character character) {
        if (character.mListContours.size() >= 2 || isZ(character)) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int width = src.getWidth();
        int first = -1;
        int countFat = 0, countThin = 0;
        int countTwo = 0;
        boolean flag = false;
        int times = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                int count = 0;
                boolean stop = false;
                for (int j = 0; j < src.getWidth() && !stop; j++) {
                    if (src.getPixel(j, i) != Color.WHITE) {
                        count++;
                    } else {
                        if (count > 0) {
                            stop = true;
                        }
                    }
                }

                if (((double) count) / width >= 0.5d) {
                    countFat++;
                    if (first < 0) {
                        first = i;
                    }
                    if (!flag) {
                        times++;
                        flag = true;
                    }
                } else {
                    countThin++;
                    flag = false;
                }
            } else {
                countTwo++;
            }
        }

        if (times > 1) {
            return false;
        }

        if (first >= 0) {
            if (((double) countFat + countThin) / src.getHeight() >= 0.6d) {
                return ((double) first) / src.getHeight() <= 0.05d && countTwo <= 0.1d * src.getHeight();
            }
        }

        return false;
    }

    private boolean ist(Character character) {
        if (character.mListContours.size() >= 2 || isZ(character)) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int width = src.getWidth();
        int first = -1;
        int countFat = 0, countThin = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                int count = 0;
                boolean stop = false;
                for (int j = 0; j < src.getWidth() && !stop; j++) {
                    if (src.getPixel(j, i) != Color.WHITE) {
                        count++;
                    } else {
                        if (count > 0) {
                            stop = true;
                        }
                    }
                }

                if (((double) count) / width >= 0.5d) {
                    countFat++;
                    if (first < 0) {
                        first = i;
                    }
                } else {
                    countThin++;
                }
            }
        }

        if (first >= 0) {
            if (((double) countFat + countThin) / src.getHeight() >= 0.6d) {
                return ((double) first) / src.getHeight() >= 0.05d;
            }
        }

        return false;
    }

    private boolean isU(Character character) {
        int countThree = 0;
        for (int i = 0; i < character.mBitmap.getHeight(); i++) {
            if (countRowSegments(i, character.mBitmap) == 3) {
                countThree++;
            }
        }
        if (countThree == 0) {
            return (character.mListContours.size() == 1) && connectedComponentBot(character) == 1 &&
                    connectedComponentTop(character) == 2;
        }
        return false;
    }

    private boolean isu(Character character) {
        return (findContour(character.mBitmap).size() == 1);
    }

    private boolean isV_Inverse(Character character) {
        int connectedTop = connectedComponentTop(character);
        int connectedBot = connectedComponentBot(character);
        int triangleTop = triangleFromTop(character);
        return (connectedTop == 1 && connectedBot == 2 && triangleTop == 1);
    }

    private boolean isV(Character character) {
        int connectedTop = connectedComponentTop(character);
        int connectedBot = connectedComponentBot(character);
        int triangleBot = triangleFromBot(character);
        return (connectedTop == 2 && connectedBot == 1 && triangleBot == 1);
    }

    private boolean isW(Character character) {
        if (character.mListContours.size() >= 2) {
            return false;
        }

        Bitmap src = character.mBitmap;
        int left = (int) (src.getWidth() * 0.15d);
        Point blackLeft = getFirstBackFromTop(src, left);
        if (blackLeft != null) {
            boolean decrease = false;
            for (int i = left + 1; i < src.getWidth() && !decrease; i++) {
                Point p = getFirstBackFromTop(src, i);
                if (p != null) {
                    if (p.y >= blackLeft.y) {
                        blackLeft = new Point(p.x, p.y);
                    } else {
                        decrease = true;
                    }
                }
            }
        }
        int right = (int) (src.getWidth() * 0.85d);
        Point blackRight = getFirstBackFromTop(src, right);
        if (blackRight != null) {
            boolean decrease = false;
            for (int i = right - 1; i >= 0 && !decrease; i--) {
                Point p = getFirstBackFromBot(src, i);
                if (p != null) {
                    if (p.y >= blackRight.y) {
                        blackRight = new Point(p.x, p.y);
                    } else {
                        decrease = true;
                    }
                }
            }
        }

        if (blackLeft != null && blackRight != null) {
            Bitmap center = cropBitmap(src, blackLeft.x, 0, blackRight.x - blackLeft.x, src.getHeight());
            if (center != null) {
                Character f = new Character();
                f.mBitmap = center;
                return isV_Inverse(f);
            }
        }
        return false;
    }

    private boolean isX(Character character) {
        if (!isV(character) && !isW(character) && !isA(character) && !isY(character)) {
            Bitmap src = character.mBitmap;
            int startY = -1, endY = -1, startX = -1, endX = -1;
            for (int i = 0; i < src.getHeight() && startY < 0; i++) {
                if (countRowSegments(i, src) == 2) {
                    startY = i;
                }
            }
            for (int i = src.getHeight() - 1; i >= 0 && endY < 0; i--) {
                if (countRowSegments(i, src) == 2) {
                    endY = i;
                }
            }
            for (int i = 0; i < src.getWidth() && startX < 0; i++) {
                if (countColumnSegments(i, src) == 2) {
                    startX = i;
                }
            }
            for (int i = src.getWidth() - 1; i >= 0 && endX < 0; i--) {
                if (countColumnSegments(i, src) == 2) {
                    endX = i;
                }
            }
            if (startY >= 0 && endY >= 0 && startX >= 0 && endX >= 0) {
                int top = -1, bot = -1, left = -1, right = -1;
                for (int i = startY; i <= endY && top < 0; i++) {
                    if (countRowSegments(i, src) == 1) {
                        top = i;
                    }
                }
                for (int i = endY; i >= startY && bot < 0; i--) {
                    if (countRowSegments(i, src) == 1) {
                        bot = i;
                    }
                }
                for (int i = startX; i <= endX && left < 0; i++) {
                    if (countColumnSegments(i, src) == 1) {
                        left = i;
                    }
                }
                for (int i = endX; i >= startX && right < 0; i--) {
                    if (countColumnSegments(i, src) == 1) {
                        right = i;
                    }
                }

                if (top >= 0 && bot >= 0 && left >= 0 && right >= 0) {
                    int row = (top + bot) / 2;
                    int col = (left + right) / 2;
                    Character Top = new Character();
                    Top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row);
                    Character Bot = new Character();
                    Bot.mBitmap = cropBitmap(src, 0, row, src.getWidth(), src.getHeight() - row);
                    Character Left = new Character();
                    Bitmap bmp = cropBitmap(src, 0, 0, col, src.getHeight());
                    Left.mBitmap = rotateBitmap(bmp, 90);
                    Character Right = new Character();
                    bmp = cropBitmap(src, col, 0, src.getWidth() - col, src.getHeight());
                    Right.mBitmap = rotateBitmap(bmp, -90);
                    return triangleFromBot(Top) == 1 && triangleFromBot(Left) == 1 && triangleFromBot(Right) == 1 && triangleFromTop(Bot) == 1;
                }
            }
        }
        return false;
    }

    private boolean isY(Character character) {
        Bitmap src = character.mBitmap;
        int row = -1;

        for (int i = src.getHeight() - 1; i >= 0 && row < 0; i--) {
            if (countRowSegments(i, src) == 2) {
                row = i;
            }
        }

        if (row > 0) {
            if (row + 5 < src.getHeight()) {
                Character top = new Character();
                top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row + 5);
                return isV(top);
            }
        }
        return false;
    }

    private boolean isy(Character character) {
        Bitmap src = character.mBitmap;
        Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), (int) (src.getHeight() * 0.2d));
        if (detectAreasOnBitmap(top, 0, 0).size() < 2) {
            return false;
        }
        ArrayList<Rect> list = character.mListRectContour;
        if (list != null) {
            if (list.size() == 2) {
                Rect rect = list.get(0);
                if (rect != null) {
                    boolean closeToBot = Math.abs(rect.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                    int count = 0;
                    int increase = 0;
                    boolean stop = false;
                    for (int i = 0; i < src.getHeight() && !stop; i++) {
                        int blank = getHorizontalBlankCounts(i, src);
                        if (blank > 0) {
                            if (blank >= count) {
                                increase++;
                                count = blank;
                            } else {
                                stop = true;
                            }
                        } else {
                            if (count > 0) {
                                stop = true;
                            }
                        }
                    }

                    return increase > 0 && closeToBot;
                }
            }
        }
        return false;
    }

    private boolean isZ(Character character) {
        Bitmap src = rotateBitmap(character.mBitmap, 90);
        Character f = new Character();
        f.mBitmap = src;
        return isN(f);
    }

    private boolean is0(Character character) {
        ArrayList<Rect> list = character.mListRectContour;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);

            int offsetTop = Math.abs(rBig.top - rSmall.top);
            int offsetBot = Math.abs(rBig.bottom - rSmall.bottom);
            int offsetLeft = Math.abs(rBig.left - rSmall.left);
            int offsetRight = Math.abs(rBig.right - rSmall.right);
            Log.d("top", offsetTop + "");
            Log.d("bot", offsetBot + "");
            Log.d("left", offsetLeft + "");
            Log.d("right", offsetRight + "");
            boolean circleVertical = rBig.width() <= rBig.height() * 0.85d;

            return (Math.abs(offsetBot - offsetTop) <= 3) && (Math.abs(offsetBot - offsetRight) <= 3) &&
                    (Math.abs(offsetBot - offsetLeft) <= 3) && circleVertical;
        }
        return false;
    }

    private boolean is1(Character character) {
        Bitmap src = character.mBitmap;
        int pos = -1;
        boolean stop = false;
        for (int i = 0; i < src.getHeight() && !stop; i++) {
            if (countRowSegments(i, src) == 2) {
                pos = i;
            } else {

                if (pos >= 0) {
                    stop = true;
                }
            }
        }

        if (pos >= 0) {
            Bitmap interested = cropBitmap(src, 0, 0, src.getWidth(), pos);
            boolean isV_inverse = false;
            if (interested != null) {
                Character f = new Character();
                f.mBitmap = interested;
                isV_inverse = isV_Inverse(f);
            }
            pos = -1;
            for (int i = src.getHeight() - 1; i >= 0 && pos < 0; i--) {
                int count = 0;
                stop = false;
                if (countRowSegments(i, src) == 1) {
                    for (int j = 0; j < src.getWidth() && !stop; j++) {
                        if (src.getPixel(j, i) != Color.WHITE) {
                            count++;
                        } else {
                            if (count > 0) {
                                stop = true;
                            }
                        }
                    }
                }

                if (((double) count) / src.getWidth() >= 0.6d) {
                    pos = i;
                }
            }

            if (pos >= 0) {
                return ((double) pos) / src.getHeight() >= 0.9d && isV_inverse;
            }
        }
        return false;
    }

    private boolean is2(Character character) {
        ArrayList<Rect> list = character.mListRectContour;
        Bitmap src = character.mBitmap;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);

            int offsetTop = Math.abs(rSmall.top - rBig.top);
            int offsetBot = Math.abs(rSmall.bottom - rBig.bottom);
            int offsetLeft = Math.abs(rSmall.left - rBig.left);
            int offsetRight = Math.abs(rSmall.right - rBig.right);
            boolean closeToBotLeft = offsetTop > offsetBot && offsetLeft < offsetRight;
            int split = rSmall.right;
            Bitmap left = cropBitmap(src, 0, 0, split, src.getHeight());
            boolean segmentsTwo = detectAreasOnBitmap(left, 0, 0).size() == 2;

            int countThree = 0;
            if (left != null) {
                for (int i = 0; i < left.getWidth(); i++) {
                    if (countColumnSegments(i, left) == 3) {
                        countThree++;
                    }
                }
            }

            return closeToBotLeft && segmentsTwo && countThree >= src.getWidth() * 0.1d;
        } else {
            if (list.size() == 1) {
                boolean stop = false;
                Point point = null;
                for (int i = (int) (src.getHeight() * 0.9d); i >= 0 && !stop; i--) {
                    Point p = null;
                    for (int j = src.getWidth() - 1; j >= 0 && p == null; j--) {
                        if (src.getPixel(j, i) != Color.WHITE) {
                            p = new Point(j, i);
                        }
                    }
                    if (p != null) {
                        if (point == null) {
                            point = new Point(p.x, p.y);
                        } else {
                            if (point.x >= p.x) {
                                point = new Point(p.x, p.y);
                            } else {
                                stop = true;
                            }
                        }
                    }
                }

                if (point != null) {
                    Bitmap left = cropBitmap(src, 0, 0, point.x, point.y);
                    if (left != null) {
                        return detectAreasOnBitmap(left, 0, 0).size() == 2;
                    }
                }
            }
        }
        return false;
    }

    private boolean is3(Character character) {
        if (character.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int split = (int) (src.getWidth() * 0.6d);
        Bitmap left = cropBitmap(src, 0, 0, split, src.getHeight());
        ArrayList<Rect> rect = detectAreasOnBitmap(left, 0, 0);
        if (rect != null) {
            if (rect.size() == 2) {
                Rect top = (rect.get(0).top < rect.get(1).top) ? rect.get(0) : rect.get(1);
                Rect bot = (rect.get(0).top >= rect.get(1).top) ? rect.get(0) : rect.get(1);
                if (top.bottom < 0.5d * src.getHeight() && bot.top > 0.5d * src.getHeight()) {
                    int countThree = 0, countFour = 0;
                    for (int i = 0; i < src.getWidth(); i++) {
                        if (countColumnSegments(i, src) == 3) {
                            countThree++;
                        }
                        if (countColumnSegments(i, src) == 4) {
                            countFour++;
                        }
                    }

                    return countFour > 0 && countThree > 0;
                }
            } else {
                if (rect.size() == 3) {
                    Rect top = null;
                    Rect bot = null;
                    if (rect.get(0).top < rect.get(1).top && rect.get(1).top < rect.get(2).top) {
                        top = rect.get(0);
                        bot = rect.get(2);
                    }
                    if (rect.get(0).top < rect.get(2).top && rect.get(2).top < rect.get(1).top) {
                        top = rect.get(0);
                        bot = rect.get(1);
                    }
                    if (rect.get(1).top < rect.get(0).top && rect.get(0).top < rect.get(2).top) {
                        top = rect.get(1);
                        bot = rect.get(2);
                    }
                    if (rect.get(1).top < rect.get(2).top && rect.get(2).top < rect.get(0).top) {
                        top = rect.get(1);
                        bot = rect.get(0);
                    }
                    if (rect.get(2).top < rect.get(0).top && rect.get(0).top < rect.get(1).top) {
                        top = rect.get(2);
                        bot = rect.get(1);
                    }
                    if (rect.get(2).top < rect.get(1).top && rect.get(1).top < rect.get(0).top) {
                        top = rect.get(2);
                        bot = rect.get(0);
                    }
                    if (top != null && bot != null) {
                        if (top.bottom < 0.4d * src.getHeight() && bot.top > 0.6d * src.getHeight()) {
                            int countThree = 0, countFour = 0;
                            for (int i = 0; i < src.getWidth(); i++) {
                                if (countColumnSegments(i, src) == 3) {
                                    countThree++;
                                }
                                if (countColumnSegments(i, src) == 4) {
                                    countFour++;
                                }
                            }

                            return countFour > 0 && countThree > 0;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean is4(Character character) {
        if (character.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = character.mBitmap;
        Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), (int) (src.getHeight() * 0.3d));
        boolean openTop = false;
        if (top != null) {
            openTop = detectAreasOnBitmap(top, 0, 0).size() == 2;
        }
        Point point = new Point(0, src.getHeight() - 1);
        boolean moved = false, end = false;
        while (!end) {
            int x = point.x, y = point.y;
            if (x + 1 < src.getWidth() && y - 1 >= 0) {
                if (src.getPixel(x + 1, y - 1) == Color.WHITE) {
                    point = new Point(x + 1, y - 1);
                    moved = true;
                }
            }
            if (y - 1 >= 0 && !moved) {
                if (src.getPixel(x, y - 1) == Color.WHITE) {
                    point = new Point(x, y - 1);
                    moved = true;
                }
            }
            if (x + 1 >= 0 && !moved) {
                if (src.getPixel(x + 1, y) == Color.WHITE) {
                    point = new Point(x, y - 1);
                    moved = true;
                }
            }

            if (moved) {
                moved = false;
            } else {
                end = true;
            }
        }

        Bitmap interested = cropBitmap(src, point.x, 0, src.getWidth() - point.x, point.y);
        if (interested != null) {
            Character f = new Character();
            f.mBitmap = interested;
            f.mListContours = findContour(interested);
            return isL(f) && openTop;
        }
        return false;
    }

    private boolean is5(Character character) {
        if (character.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int countFatRow = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                int count = 0;
                for (int j = 0; j < src.getWidth(); j++) {
                    if (src.getPixel(j, i) != Color.WHITE) {
                        count++;
                    }
                }

                if (count >= 0.7d * src.getWidth()) {
                    countFatRow++;
                }
            }
        }
        int startRow = -1;
        int startCol = -1;
        int row = -1;
        for (int col = src.getWidth() - 1; col >= 0 && startCol < 0; col--) {
            row = getColumnHeightFromTop(src, col);
            if (row <= src.getHeight() * 0.3d) {
                startCol = col;
            }
        }

        if (row >= 0 && startCol >= 0) {
            for (int i = row + 1; i < src.getHeight() && startRow < 0; i++) {
                if (src.getPixel(startCol, i) == Color.WHITE) {
                    startRow = i;
                }
            }
        }

        if (startCol >= 0 && startRow >= 0) {

            Point point = new Point(startCol + 1, startRow);
            boolean moved = false, end = false;
            while (!end) {
                int x = point.x, y = point.y;
                if (x - 1 >= 0) {
                    if (src.getPixel(x - 1, y) == Color.WHITE) {
                        point = new Point(x - 1, y);
                        moved = true;
                    }
                }
                if (x - 1 >= 0 && y + 1 < src.getHeight() && !moved) {
                    if (src.getPixel(x - 1, y + 1) == Color.WHITE) {
                        point = new Point(x - 1, y + 1);
                        moved = true;
                    }
                }
                if (y + 1 < src.getHeight() && !moved) {
                    if (src.getPixel(x, y + 1) == Color.WHITE) {
                        point = new Point(x, y + 1);
                        moved = true;
                    }
                }

                if (moved) {
                    moved = false;
                } else {
                    end = true;
                }
            }

            Bitmap cropped = cropBitmap(src, 0, 0, src.getWidth(), point.y);
            if (cropped != null) {
                Bitmap interested = cropBitmap(cropped, 0, 0, cropped.getWidth(), (int) (cropped.getHeight() * 0.8d));
                if (interested != null) {
                    Bitmap rotated = rotateBitmap(interested, -90);
                    if (rotated != null) {
                        Character f = new Character();
                        f.mBitmap = rotated;
                        f.mListContours = findContour(rotated);
                        return isL(f) && countFatRow > 0;
                    }
                }
            }

        }
        return false;
    }

    private boolean is6(Character character) {
        ArrayList<Rect> list = character.mListRectContour;
        Bitmap src = character.mBitmap;
        if (list.size() == 2) {
            Rect rSmall = list.get(0);
            Rect rBig = list.get(1);

            int offsetTop = Math.abs(rSmall.top - rBig.top);
            int offsetBot = Math.abs(rSmall.bottom - rBig.bottom);
            if (offsetBot <= offsetTop && rSmall.width() >= src.getWidth() * 0.5d) {
                int countThree = 0;
                for (int i = 0; i < src.getWidth(); i++) {
                    if (countColumnSegments(i, src) == 3) {
                        countThree++;
                    }
                }
                if (countThree >= 0.5d * src.getWidth()) {
                    int split = src.getWidth() / 2;
                    Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
                    if (right != null) {
                        return detectAreasOnBitmap(right, 0, 0).size() == 2;
                    }
                }
            }
        }

        return false;
    }

    private boolean is7(Character character) {
        if (isZ(character)) {
            return false;
        }
        if (character.mListContours.size() >= 2 || isZ(character)) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int width = src.getWidth();
        int first = -1;
        int countFat = 0, countThin = 0;
        int countTwo = 0;
        boolean flag = false;
        int times = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                int count = 0;
                boolean stop = false;
                for (int j = 0; j < src.getWidth() && !stop; j++) {
                    if (src.getPixel(j, i) != Color.WHITE) {
                        count++;
                    } else {
                        if (count > 0) {
                            stop = true;
                        }
                    }
                }

                if (((double) count) / width >= 0.5d) {
                    countFat++;
                    if (first < 0) {
                        first = i;
                    }
                    if (!flag) {
                        times++;
                        flag = true;
                    }
                } else {
                    countThin++;
                    flag = false;
                }
            } else {
                countTwo++;
            }
        }

        if (times >= 2) {
            if (first >= 0) {
                if (((double) countFat + countThin) / src.getHeight() >= 0.7d) {
                    return ((double) first) / src.getHeight() <= 0.05d && countTwo <= 0.1d * src.getHeight();
                }
            }
        }

        return false;
    }

    private boolean is8(Character character) {
        if (character.mListRectContour.size() != 3) {
            return false;
        }
        Bitmap src = character.mBitmap;
        int count = 0;
        boolean flag = false;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                if (!flag) {
                    flag = true;
                    count++;
                }
            } else {
                flag = false;
            }
        }

        if (count != 3) {
            return false;
        }

        int countThree = 0, countFour = 0;
        for (int i = 0; i < src.getWidth(); i++) {
            int segments = countColumnSegments(i, src);
            if (segments == 3) {
                countThree++;
            }
            if (segments == 4) {
                countFour++;
            }
        }

        return countThree > 0 && countFour > 0 && (countFour + countThree) >= 0.5d * src.getWidth();
    }

    private boolean is9(Character character) {
        Bitmap rotated = rotateBitmap(character.mBitmap, 180);
        Character f = new Character();
        f.mBitmap = rotated;
        f.mListContours = findContour(rotated);
        f.mListRectContour = new ArrayList<>();
        ArrayList<Bitmap> contours = f.mListContours;
        if (contours.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(contours.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(contours.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    f.mListRectContour.add(rSmall);
                    f.mListRectContour.add(rBig);
                }
            }
        }
        return is6(f);
    }
}
