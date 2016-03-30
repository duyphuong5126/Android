package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.DTO.FloatingImage;

/**
 * Created by Phuong on 26/11/2015.
 */

/**
 * (Pre-)Processing bitmap and segmenting character areas. Features extraction after clustering
 */
public class BitmapProcessor {

    public interface RecognitionCallback {
        void onRecognizeSuccess(ArrayList<FloatingImage> listBitmaps);
    }

    private ArrayList<Rect> mListRectangle;

    public BitmapProcessor() {
        mListRectangle = new ArrayList<>();
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
        if (x >= src.getWidth() || x < 0 || y >= src.getHeight() || y < 0) {
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

    public void onDetectCharacter(final FloatingImage floatingImage, RecognitionCallback callback) {
        this.mListRectangle.addAll(detectAreasOnBitmap(floatingImage.mBitmap, 0, 0));

        ArrayList<FloatingImage> detectedBitmaps = new ArrayList<>();
        for (Rect rect : mListRectangle) {
            FloatingImage floatingImage1 = new FloatingImage();
            floatingImage1.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
            floatingImage1.mMyShape = floatingImage.mMyShape;
            floatingImage1.mParentHeight = floatingImage.mParentHeight;
            floatingImage1.mParentWidth = floatingImage.mParentWidth;
            detectedBitmaps.add(floatingImage1);
        }

        /*ArrayList<FloatingImage> resultBitmaps = new ArrayList<>();
        for (FloatingImage f : detectedBitmaps) {
            resultBitmaps.addAll(getSegments(f));
            highLight(f.mBitmap,0, f.mBitmap.getWidth() - 1, (int) (f.mBitmap.getHeight() * 0.8d), f.mBitmap.getHeight() - 1);
            resultBitmaps.add(f);
        }*/

        /*ArrayList<FloatingImage> resultBitmaps = new ArrayList<>();
        for (FloatingImage f : detectedBitmaps) {
            resultBitmaps.addAll(bottomUpSegmenting(f));
        }*/
        callback.onRecognizeSuccess(/*resultBitmaps.isEmpty()?detectedBitmaps:resultBitmaps*/detectedBitmaps);

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

    private ArrayList<FloatingImage> topDownSegmenting(FloatingImage floatingImage) {
        ArrayList<FloatingImage> list = new ArrayList<>();
        Bitmap src = copyImage(floatingImage.mBitmap);
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
                FloatingImage f1 = new FloatingImage();
                f1.mBitmap = cropBitmap(floatingImage.mBitmap, 0, 0, point.x, floatingImage.mBitmap.getHeight());
                list.add(f1);
            }
            FloatingImage f2 = new FloatingImage(); f2.mBitmap = cropBitmap(floatingImage.mBitmap, point.x + 1,
                    0, floatingImage.mBitmap.getWidth() - point.x - 1, floatingImage.mBitmap.getHeight());
            list.add(f2);
            FloatingImage f3 = new FloatingImage(); f3.mBitmap = src; list.add(f3);
        }
        return list;
    }

    private ArrayList<FloatingImage> bottomUpSegmenting(FloatingImage floatingImage) {
        ArrayList<FloatingImage> list = new ArrayList<>();
        Bitmap src = copyImage(floatingImage.mBitmap);
        Point point = null;
        for (int row = 0; row < src.getHeight(); row++) {
            if (countRowSegments(row, src) == 2) {
                if (getInitialTopDownPoint(row, src) != null) {
                    point = getInitialBottomUpPoint(row, src);
                }
            }
        }

        if (point != null) {
            boolean moved = false;
            boolean end = false;
            while (!end) {
                int col = point.x;
                int row = point.y;
                src.setPixel(point.x, point.y, Color.RED);

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

        if (point != null) {
            if (point.x > 0 && point.y > 0) {
                FloatingImage f1 = new FloatingImage();
                f1.mBitmap = cropBitmap(floatingImage.mBitmap, 0, 0, point.x, floatingImage.mBitmap.getHeight());
                list.add(f1);
            }
            FloatingImage f2 = new FloatingImage(); f2.mBitmap = cropBitmap(floatingImage.mBitmap, point.x + 1,
                    0, floatingImage.mBitmap.getWidth() - point.x - 1, floatingImage.mBitmap.getHeight());
            list.add(f2);
            FloatingImage f3 = new FloatingImage(); f3.mBitmap = src; list.add(f3);
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

    private ArrayList<FloatingImage> getSegments(FloatingImage floatingImage) {
        ArrayList<FloatingImage> resultBitmaps = new ArrayList<>();
        Bitmap src = floatingImage.mBitmap;
        int height = src.getHeight(); double threshold = height * 0.8d;
        ArrayList<Rect> listRect = new ArrayList<>();
        boolean flag = true;
        for (int i = 0; i < src.getWidth() - 1; i++) {
            if (getColumnHeight(src, i) < threshold) {
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
                FloatingImage fImage = new FloatingImage();
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

    private int getColumnHeight(Bitmap bitmap, int col) {
        for (int row = 0; row < bitmap.getHeight(); row++) {
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

    private int getBothSideEndRow(Bitmap bitmap) {
        for (int i = bitmap.getHeight() - 1; i >= 0; i--) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getBothSideStartRow(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (countRowSegments(i, bitmap) == 2) {
                return i;
            }
        }
        return -1;
    }

    private int getThreeSideStartRow(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (countRowSegments(i, bitmap) == 3) {
                return i;
            }
        }
        return -1;
    }

    private int getBlankCounts(int y, Bitmap bitmap) {
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

    private Point getMiddleFirstBlackPoint(Bitmap bitmap, int row) {
        int mid = bitmap.getWidth() / 2;
        if (row < 0) {
            return null;
        }
        if (bitmap.getPixel(mid, row) != Color.WHITE) {
            return new Point(mid, row);
        }
        Point left = null;
        for (int i = mid - 1; i >= 0 && left == null; i--) {
            if (bitmap.getPixel(i, row) != Color.WHITE) {
                left = new Point(i, row);
            }
        }

        Point right = null;
        for (int i = mid + 1; mid < bitmap.getWidth() && right == null; i++) {
            if (bitmap.getPixel(i, row) != Color.WHITE) {
                right = new Point(i, row);
            }
        }

        if (left != null && right != null) {
            return (mid - left.x < right.x - mid) ? left : right;
        }

        return null;
    }

    private int triangleFromBot(FloatingImage floatingImage) {
        Point lastBlack = getLastBlackPixel(floatingImage.mBitmap);
        if (lastBlack == null) {
            return -1;
        }

        int bothStart = getBothSideStartRow(floatingImage.mBitmap);

        if (bothStart >= 0) {
            int width = getBlankCounts(bothStart, floatingImage.mBitmap);
            int decrease = 0, increase = 0;
            for (int i = bothStart; i <= lastBlack.y; i++) {
                int w = getBlankCounts(i, floatingImage.mBitmap);

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

    private boolean verticalClosed(Bitmap bitmap) {
        boolean closedTop = getBlankCounts(0, bitmap) > 0;
        boolean closedBot = getBlankCounts(bitmap.getHeight() - 1, bitmap) > 0;
        return closedTop || closedBot;
    }

    private int triangleFromTop(FloatingImage floatingImage) {
        if (verticalClosed(floatingImage.mBitmap)) {
            return -1;
        }
        Point firstBlack = getFirstBlackPixel(floatingImage.mBitmap);
        if (firstBlack == null) {
            return -1;
        }

        int bothEnd = getBothSideEndRow(floatingImage.mBitmap);

        if (bothEnd >= 0) {
            int width = getBlankCounts(bothEnd, floatingImage.mBitmap);
            int decrease = 0, increase = 0;
            for (int i = bothEnd - 1; i >= firstBlack.y; i--) {
                int w = getBlankCounts(i, floatingImage.mBitmap);

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

    private int connectedComponentTopBot(FloatingImage floatingImage) {
        int fragmentHeight = floatingImage.mBitmap.getHeight() / 3;
        int startAbove = 0;
        int startBelow = floatingImage.mBitmap.getHeight() - fragmentHeight;
        Bitmap fragmentAbove = cropBitmap(floatingImage.mBitmap, 0, startAbove, floatingImage.mBitmap.getWidth(), fragmentHeight);
        Bitmap fragmentBelow = cropBitmap(floatingImage.mBitmap, 0, startBelow, floatingImage.mBitmap.getWidth(), fragmentHeight);

        if (detectAreasOnBitmap(fragmentAbove, 0, 0).size() == 2 && detectAreasOnBitmap(fragmentBelow, 0, 0).size() == 2) {
            return 1;
        }
        return 0;
    }

    public String featureExtraction(FloatingImage floatingImage, String list) {
        for (int i = 0; i < list.length(); i++) {
            String string = list.substring(i, i + 1);
            switch (string) {
                case "A":
                    if (isA(floatingImage)) {
                        return "A";
                    }
                    break;

                case "B":
                    if (isB(floatingImage)) {
                        return "B";
                    }
                    break;

                case "C":
                    if (isC(floatingImage)) {
                        return "C";
                    }
                    break;

                case "D":
                    if (isD(floatingImage)) {
                        return "D";
                    }
                    break;

                case "F":
                    if (isF(floatingImage)) {
                        return "F";
                    }
                    break;

                case "G":
                    if (isG(floatingImage)) {
                        return "G";
                    }
                    break;

                case "H":
                    if (isH(floatingImage)) {
                        return "H";
                    }
                    break;

                case "I":
                    if (isI(floatingImage)) {
                        return "I";
                    }
                    break;

                case "J":
                    if (isJ(floatingImage)) {
                        return "J";
                    }
                    break;

                case "O":
                    if (isO(floatingImage)) {
                        return "O";
                    }
                    break;

                case "P":
                    if (isP(floatingImage)) {
                        return "P";
                    }
                    break;

                case "Q":
                    if (isQ(floatingImage)) {
                        return "Q";
                    }
                    break;

                case "R":
                    if (isR(floatingImage)) {
                        return "R";
                    }
                    break;

                case "S":
                    if (isS(floatingImage)) {
                        return "S";
                    }
                    break;

                case "V":
                    if (isV(floatingImage)) {
                        return "V";
                    }
                    break;

                case "T":
                    if (isT(floatingImage)) {
                        return "T";
                    }
                    break;

                case "W":
                    if (isW(floatingImage)) {
                        return "W";
                    }
                    break;

                case "X":
                    if (isX(floatingImage)) {
                        return "X";
                    }
                    break;

                case "Y":
                    if (isY(floatingImage)) {
                        return "Y";
                    }
                    break;

                default:
                    break;
            }
        }
        return list;
    }

    private boolean isA(FloatingImage floatingImage) {
        return !isW(floatingImage) && triangleFromTop(floatingImage) == 1 && connectedComponentTopBot(floatingImage) != 1;
    }

    private boolean isH(FloatingImage floatingImage) {
        if (triangleFromTop(floatingImage) != 1) {
            int fragmentHeight = floatingImage.mBitmap.getHeight() / 4;
            int startAbove = 0;
            int startBelow = floatingImage.mBitmap.getHeight() - fragmentHeight;
            Bitmap fragmentAbove = cropBitmap(floatingImage.mBitmap, 0, startAbove, floatingImage.mBitmap.getWidth(), fragmentHeight);
            Bitmap fragmentBelow = cropBitmap(floatingImage.mBitmap, 0, startBelow, floatingImage.mBitmap.getWidth(), fragmentHeight);

            ArrayList<Rect> listAbove = detectAreasOnBitmap(fragmentAbove, 0, 0);
            ArrayList<Rect> listBelow = detectAreasOnBitmap(fragmentBelow, 0, 0);

            if (listAbove.size() == 2 && listBelow.size() == 2) {
                boolean triangle = false;
                for (int i = 0; i < listAbove.size() && !triangle; i++) {
                    Rect rect = listAbove.get(i);
                    FloatingImage floatingImage1 = new FloatingImage();
                    floatingImage1.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                    if (triangleFromTop(floatingImage1) == 1 && triangleFromBot(floatingImage1) == 1) {
                        triangle = true;
                    }
                }
                if (triangle) {
                    return false;
                } else {
                    for (int i = 0; i < listBelow.size() && !triangle; i++) {
                        Rect rect = listBelow.get(i);
                        FloatingImage floatingImage1 = new FloatingImage();
                        floatingImage1.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                        if (triangleFromTop(floatingImage1) == 1 && triangleFromBot(floatingImage1) == 1) {
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

    private boolean isV(FloatingImage floatingImage) {
        return (connectedComponentTopBot(floatingImage) != 1 && triangleFromBot(floatingImage) == 1);
    }

    private boolean isW(FloatingImage floatingImage) {
        int threeStart = getThreeSideStartRow(floatingImage.mBitmap);
        Point mid = getMiddleFirstBlackPoint(floatingImage.mBitmap, threeStart);
        if (mid != null) {
            Bitmap left = cropBitmap(floatingImage.mBitmap, 0, threeStart, mid.x, floatingImage.mBitmap.getHeight() - threeStart);
            Bitmap right = cropBitmap(floatingImage.mBitmap, mid.x + 1, threeStart, floatingImage.mBitmap.getWidth() - mid.x - 1, floatingImage.mBitmap.getHeight() - threeStart);
            if (left != null && right != null) {
                FloatingImage leftImage = new FloatingImage();
                leftImage.mBitmap = left;
                FloatingImage rightImage = new FloatingImage();
                rightImage.mBitmap = right;

                return (isV(leftImage) && isV(rightImage));
            }
        }
        return false;
    }

    private boolean isF(FloatingImage floatingImage) {
        int mid = floatingImage.mBitmap.getWidth() / 2;
        Bitmap src = floatingImage.mBitmap;
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

    private boolean isR(FloatingImage floatingImage) {
        if (isB(floatingImage) || isS(floatingImage)) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean isT(FloatingImage floatingImage) {
        int offsetY = (int) (floatingImage.mBitmap.getHeight() * 0.3);
        Bitmap bitmap = floatingImage.mBitmap;
        Bitmap upper = cropBitmap(bitmap, 0, 0, bitmap.getWidth(), offsetY);
        Bitmap lower = cropBitmap(bitmap, 0, bitmap.getHeight() - offsetY, bitmap.getWidth(), offsetY);

        ArrayList<Rect> upperList = detectAreasOnBitmap(upper, 0, 0);
        ArrayList<Rect> lowerList = detectAreasOnBitmap(lower, 0, 0);

        return upperList.size() == 1 && lowerList.size() == 1 &&
                (((double) upperList.get(0).width() / bitmap.getWidth() > 0.9d) &&
                        (lowerList.get(0).width() / (double) upperList.get(0).width() < 0.5d));
    }

    private boolean isI(FloatingImage floatingImage) {
        int offsetY = (int) (floatingImage.mBitmap.getHeight() * 0.2);
        Bitmap bitmap = floatingImage.mBitmap;
        Bitmap upper = cropBitmap(bitmap, 0, 0, bitmap.getWidth(), offsetY);
        Bitmap lower = cropBitmap(bitmap, 0, bitmap.getHeight() - offsetY, bitmap.getWidth(), offsetY);

        ArrayList<Rect> upperList = detectAreasOnBitmap(upper, 0, 0);
        ArrayList<Rect> lowerList = detectAreasOnBitmap(lower, 0, 0);

        return upperList.size() == 1 && lowerList.size() == 1 &&
                (((double) upperList.get(0).width() / bitmap.getWidth() > 0.9d) &&
                        ((double) lowerList.get(0).width() / bitmap.getWidth() > 0.9d));
    }

    private boolean isP(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;

        int thinRows = 0;
        int fatRows = 0;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (getBlankCounts(i, bitmap) > 0) {
                fatRows++;
            } else {
                if (getBlankCounts(i, bitmap) == 0) {
                    thinRows++;
                }
            }
        }

        return ((double) fatRows / bitmap.getHeight() >= 0.2d) && ((double) fatRows / bitmap.getHeight() <= 0.8d)
                && ((double) thinRows / bitmap.getHeight() >= 0.2d);
    }

    private boolean isJ(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;
        int offsetY = (int) (bitmap.getHeight() * 0.2d);
        Bitmap upper = cropBitmap(bitmap, 0, 0, bitmap.getWidth(), offsetY);
        ArrayList<Rect> upperList = detectAreasOnBitmap(upper, 0, 0);
        if (upperList.size() == 1 && (double) upperList.get(0).width() / bitmap.getWidth() > 0.9d) {
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

    private boolean isD(FloatingImage floatingImage) {
        if (isB(floatingImage)) {
            return false;
        }
        Bitmap bitmap = floatingImage.mBitmap;

        int thinRows = 0;
        int fatRows = 0;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (getBlankCounts(i, bitmap) > 0) {
                fatRows++;
            } else {
                if (getBlankCounts(i, bitmap) == 0) {
                    thinRows++;
                }
            }
        }

        if (((double) thinRows / bitmap.getHeight() <= 0.15d) && ((double) fatRows / bitmap.getHeight() >= 0.85d)) {
            int split = (int) (bitmap.getWidth() * 0.7d);
            Bitmap bmp = cropBitmap(bitmap, split, 0, bitmap.getWidth() - split, bitmap.getHeight());
            if (detectAreasOnBitmap(bmp, 0, 0).size() == 1) {
                split = (int) (bitmap.getWidth() * 0.2);
                if (bmp != null) {
                    bmp = cropBitmap(bitmap, 0, 0, split, bitmap.getHeight());
                    int start = -1, end = -1;
                    for (int i = 0; i < bmp.getHeight(); i++) {
                        if (checkNotEmptyRow(i, 0, bmp.getWidth(), bmp)) {
                            if (start < 0) {
                                start = i;
                            }

                            end = i;
                        }
                    }

                    return (start >= 0 && end > start) && (((double) end - start) / bmp.getHeight() >= 0.9d);
                }
            }
        }
        return false;
    }

    private boolean isS(FloatingImage floatingImage) {
        int mid = floatingImage.mBitmap.getWidth() / 2;
        Bitmap src = floatingImage.mBitmap;
        Bitmap left = cropBitmap(floatingImage.mBitmap, 0, 0, mid, src.getHeight());
        Bitmap right = cropBitmap(floatingImage.mBitmap, mid, 0, src.getWidth() - mid, src.getHeight());

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

    private boolean isQ(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        int start = -1;
        int end = -1;
        for (int i = src.getHeight() / 2; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 3) {
                if (start < 0) {
                    start = i;
                }
                end = i;
            }
        }

        return (start >= 0 && end > start) && (start >= src.getHeight() / 2);
    }

    private boolean isG(FloatingImage floatingImage) {
        if (isS(floatingImage)) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
        int split = (int) (src.getWidth() * 0.4d);
        Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
        if (right != null) {
            ArrayList<Rect> listRect = detectAreasOnBitmap(right, 0, 0);
            if (listRect != null && listRect.size() == 2) {
                Rect rect1 = listRect.get(0);
                Bitmap right1 = cropBitmap(right, rect1.left, rect1.top, rect1.width(), rect1.height());
                int count1 = countBlack(right1);
                Rect rect2 = listRect.get(1);
                Bitmap right2 = cropBitmap(right, rect2.left, rect2.top, rect2.width(), rect2.height());
                int count2 = countBlack(right2);

                return (count1 >= 2 * count2 || count2 >= 2 * count1);
            }
        }
        return false;
    }

    private boolean isC(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        int split = (int) (src.getWidth() * 0.4d);
        Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
        if (right != null) {
            ArrayList<Rect> listRect = detectAreasOnBitmap(right, 0, 0);
            if (listRect != null && listRect.size() == 2) {
                Rect rect1 = listRect.get(0);
                Bitmap right1 = cropBitmap(right, rect1.left, rect1.top, rect1.width(), rect1.height());
                int count1 = countBlack(right1);
                Rect rect2 = listRect.get(1);
                Bitmap right2 = cropBitmap(right, rect2.left, rect2.top, rect2.width(), rect2.height());
                int count2 = countBlack(right2);

                if (count1 > count2) {
                    return (double) count1 / count2 <= 1.5d;
                } else {
                    if (count1 == count2) {
                        return true;
                    } else {
                        return (double) count2 / count1 <= 1.5d;
                    }
                }
            }
        }
        return false;
    }

    private boolean isB(FloatingImage floatingImage) {
        int countFourSegmentsCol = 0;
        Bitmap bitmap = floatingImage.mBitmap;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            if (countColumnSegments(i, bitmap) == 4) {
                countFourSegmentsCol++;
            }
        }
        return countFourSegmentsCol > 0;
    }

    private boolean isO(FloatingImage floatingImage) {
        if (isB(floatingImage)) {
            return false;
        }
        Bitmap bitmap = floatingImage.mBitmap;

        int thinRows = 0;
        int fatRows = 0;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            if (getBlankCounts(i, bitmap) > 0) {
                fatRows++;
            } else {
                if (getBlankCounts(i, bitmap) == 0) {
                    thinRows++;
                }
            }
        }

        if (((double) thinRows / bitmap.getHeight() <= 0.13d) && ((double) fatRows / bitmap.getHeight() >= 0.87d)) {
            int split = (int) (bitmap.getWidth() * 0.7d);
            Bitmap bmp = cropBitmap(bitmap, split, 0, bitmap.getWidth() - split, bitmap.getHeight());
            if (detectAreasOnBitmap(bmp, 0, 0).size() == 1) {
                split = (int) (bitmap.getWidth() * 0.2);
                if (bmp != null) {
                    bmp = cropBitmap(bitmap, 0, 0, split, bitmap.getHeight());
                    int start = -1, end = -1;
                    for (int i = 0; i < bmp.getHeight(); i++) {
                        if (checkNotEmptyRow(i, 0, bmp.getWidth(), bmp)) {
                            if (start < 0) {
                                start = i;
                            }

                            end = i;
                        }
                    }

                    return (start >= 0 && end > start) && (((double) end - start) / bmp.getHeight() <= 0.9d);
                }
            }
        }
        return false;
    }

    private boolean isX(FloatingImage floatingImage) {
        if (!isV(floatingImage) && !isW(floatingImage) && !isA(floatingImage) && !isY(floatingImage)) {
            Bitmap src = floatingImage.mBitmap;
            int row = -1;
            int start = src.getHeight() / 4;
            int end = src.getHeight() - start;
            for (int i = start; i <= end && row < 0; i++) {
                if (countRowSegments(i, src) == 1) {
                    row = i;
                }
            }

            if (row > 0) {
                FloatingImage top = new FloatingImage();
                top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row + 1);
                FloatingImage bot = new FloatingImage();
                bot.mBitmap = cropBitmap(src, 0, row + 2, src.getWidth(), src.getHeight() - row - 2);
                return triangleFromBot(top) == 1 && triangleFromTop(bot) == 1;
            }
        }
        return false;
    }

    private boolean isY(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        int row = -1;

        for (int i = src.getHeight() - 1; i >= 0 && row < 0; i--) {
            if (countRowSegments(i, src) == 2) {
                row = i;
            }
        }

        if (row > 0) {
            FloatingImage top = new FloatingImage();
            top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row + 1);
            return triangleFromBot(top) == 1;
        }
        return false;
    }

}
