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

import duy.phuong.handnote.DTO.FloatingImage;
import duy.phuong.handnote.Listener.RecognitionCallback;

/**
 * Created by Phuong on 26/11/2015.
 */

/**
 * (Pre-)Processing bitmap and segmenting character areas. Features extraction after clustering
 */
public class BitmapProcessor {

    private ArrayList<Rect> mListRectangle;

    public BitmapProcessor() {
        mListRectangle = new ArrayList<>();
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

        ArrayList<FloatingImage> resultBitmaps = new ArrayList<>();
        for (Rect rect : mListRectangle) {
            FloatingImage floatingImage1 = new FloatingImage();
            floatingImage1.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
            floatingImage1.mMyShape = floatingImage.mMyShape;
            floatingImage1.mParentHeight = floatingImage.mParentHeight;
            floatingImage1.mParentWidth = floatingImage.mParentWidth;
            resultBitmaps.add(floatingImage1);
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

    private int countBlack(Bitmap bitmap) {
        int count = 0;
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                count += (bitmap.getPixel(j, i) == Color.WHITE) ? 0 : 1;
            }
        return count;
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
        Log.d("Seg", "" + segments);
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

    private int triangleFromTop(FloatingImage floatingImage) {
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
                    } /*else {
                        if (isR(floatingImage)) {
                            return "R";
                        }
                    }*/
                    break;

                case "C":
                    if (isC(floatingImage)) {
                        return "C";
                    }
                    break;

                case "D":
                    if (isD(floatingImage)) {
                        return "D";
                    } /*else {
                        if (isR(floatingImage)) {
                            return "R";
                        }
                    }*/
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
                    } else {
                        if (isV(floatingImage)) {
                            return "V";
                        }
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
                    } /*else {
                        if (isB(floatingImage)) {
                            return "B";
                        }
                    }*/
                    break;

                case "S":
                    if (isS(floatingImage)) {
                        return "S";
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
            int fragmentHeight = floatingImage.mBitmap.getHeight() / 3;
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
                    Log.d("Row", "" + i);
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
}
