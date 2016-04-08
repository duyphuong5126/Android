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
import duy.phuong.handnote.DTO.Label;
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
            for (Bitmap bitmap : findContour(f.mBitmap)) {
                FloatingImage fContour = new FloatingImage();
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
            FloatingImage f2 = new FloatingImage();
            f2.mBitmap = cropBitmap(floatingImage.mBitmap, point.x + 1,
                    0, floatingImage.mBitmap.getWidth() - point.x - 1, floatingImage.mBitmap.getHeight());
            list.add(f2);
            FloatingImage f3 = new FloatingImage();
            f3.mBitmap = src;
            list.add(f3);
        }
        return list;
    }

    private ArrayList<FloatingImage> bottomUpSegmenting(FloatingImage floatingImage) {
        ArrayList<FloatingImage> list = new ArrayList<>();
        Bitmap original = floatingImage.mBitmap;
        Bitmap src = copyImage(floatingImage.mBitmap);
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
                FloatingImage f1 = new FloatingImage();
                Bitmap bmp = /*cropBitmap(floatingImage.mBitmap, 0, 0, point.x, floatingImage.mBitmap.getHeight())*/
                        Bitmap.createBitmap(floatingImage.mBitmap.getWidth(), floatingImage.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
                        f1.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                    }
                }
                list.add(f1);
            }
            FloatingImage f2 = new FloatingImage(); /*f2.mBitmap = cropBitmap(floatingImage.mBitmap, point.x + 1,
                    0, floatingImage.mBitmap.getWidth() - point.x - 1, floatingImage.mBitmap.getHeight());*/
            Bitmap bmp =
                    Bitmap.createBitmap(floatingImage.mBitmap.getWidth(), floatingImage.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
                    f2.mBitmap = cropBitmap(floatingImage.mBitmap, rect.left, rect.top, rect.width(), rect.height());
                }
            }
            list.add(f2);
            FloatingImage f3 = new FloatingImage();
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

    private ArrayList<FloatingImage> getSegments(FloatingImage floatingImage) {
        ArrayList<FloatingImage> resultBitmaps = new ArrayList<>();
        Bitmap src = floatingImage.mBitmap;
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

            Log.d("Decrease", "" + decrease);
            Log.d("Increase", "" + increase);
            Log.d("Width", "" + width);

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

    private int triangleFromTop(FloatingImage floatingImage) {
        if (!verticalClosed(floatingImage.mBitmap)) {
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

            Log.d("Decrease", "" + decrease);
            Log.d("Increase", "" + increase);
            if ((((double) decrease / (decrease + increase)) >= 0.6d) && width <= 0) {
                return 1;
            }
        }
        return 0;
    }

    private int connectedComponentTop(FloatingImage floatingImage) {
        int fragmentHeight = floatingImage.mBitmap.getHeight() / 3;
        int startAbove = 0;
        Bitmap fragmentAbove = cropBitmap(floatingImage.mBitmap, 0, startAbove, floatingImage.mBitmap.getWidth(), fragmentHeight);
        if (fragmentAbove != null) {
            return detectAreasOnBitmap(fragmentAbove, 0, 0).size();
        }
        return 0;
    }

    private int connectedComponentBot(FloatingImage floatingImage) {
        int fragmentHeight = floatingImage.mBitmap.getHeight() / 3;
        int startBelow = floatingImage.mBitmap.getHeight() - fragmentHeight - 1;
        Bitmap fragmentBelow = cropBitmap(floatingImage.mBitmap, 0, startBelow, floatingImage.mBitmap.getWidth(), fragmentHeight);
        return detectAreasOnBitmap(fragmentBelow, 0, 0).size();
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

    public String featureExtraction(FloatingImage floatingImage, ArrayList<Label> list) {
        floatingImage.mListContours = findContour(floatingImage.mBitmap);
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i).getLabel();
            switch (string) {
                case "A":
                    if (isA(floatingImage)) {
                        return "A";
                    }
                    break;
                case "a":
                    if (isa(floatingImage)) {
                        return "a";
                    }
                    break;

                case "B":
                    if (isB(floatingImage)) {
                        return "B";
                    }
                    break;

                case "b":
                    if (isb(floatingImage)) {
                        return "b";
                    } else {
                        if (isb1(floatingImage)) {
                            return "b1";
                        }
                    }
                    break;

                case "b1":
                    if (isb1(floatingImage)) {
                        return "b1";
                    } else {
                        if (isk1(floatingImage)) {
                            return "k1";
                        }
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

                case "d":
                    if (isd(floatingImage)) {
                        return "d";
                    } else {
                        if (isU(floatingImage)) {
                            return "U";
                        }
                        if (isb1(floatingImage)) {
                            return "b1";
                        }
                    }
                    break;

                case "E":
                    if (isE(floatingImage)) {
                        return "E";
                    }
                    break;

                case "e":
                    if (ise(floatingImage)) {
                        return "e";
                    } else {
                        if (isC(floatingImage)) {
                            return "C";
                        }
                    }
                    break;

                case "F":
                    if (isF(floatingImage)) {
                        return "F";
                    }
                    break;

                case "f":
                    if (isf(floatingImage)) {
                        return "f";
                    } else {
                        if (isb1(floatingImage)) {
                            return "b1";
                        }
                    }
                    break;

                case "G":
                    if (isG(floatingImage)) {
                        return "G";
                    }
                    break;

                case "g":
                    if (isg(floatingImage)) {
                        return "g";
                    }
                    break;

                case "H":
                    if (isH(floatingImage)) {
                        return "H";
                    }
                    break;

                case "h":
                    if (ish(floatingImage)) {
                        return "h";
                    }
                    break;

                case "I":
                    if (isI(floatingImage)) {
                        return "I";
                    }
                    break;

                case "i":
                    if (isi(floatingImage)) {
                        return "i";
                    }
                    break;

                case "J":
                    if (isJ(floatingImage)) {
                        return "J";
                    }
                    break;

                case "j":
                    if (isj(floatingImage)) {
                        return "j";
                    } else {
                        if (isU(floatingImage)) {
                            return "U";
                        }
                    }
                    break;

                case "K":
                    if (isK(floatingImage)) {
                        return "K";
                    } else {
                        if (isk(floatingImage)) {
                            return "k";
                        } else {
                            if (isk1(floatingImage)) {
                                return "k1";
                            }
                        }
                    }
                    break;

                case "k":
                    if (isk(floatingImage)) {
                        return "k";
                    }
                    break;

                case "k1":
                    if (isk1(floatingImage)) {
                        return "k1";
                    }
                    break;

                case "L":
                    if (isL(floatingImage)) {
                        return "L";
                    }
                    break;

                case "l":
                    if (isl(floatingImage)) {
                        return "l";
                    }
                    break;

                case "M":
                    if (isM(floatingImage)) {
                        return "M";
                    }
                    break;

                case "m":
                    if (ism(floatingImage)) {
                        return "m";
                    }
                    break;

                case "N":
                    if (isN(floatingImage)) {
                        return "N";
                    }
                    break;

                case "n":
                    if (isn(floatingImage)) {
                        return "n";
                    }
                    break;

                case "O":
                    if (isO(floatingImage)) {
                        return "O";
                    } else {
                        if (is0(floatingImage)) {
                            return "0";
                        }
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

                case "q":
                    if (isq(floatingImage)) {
                        return "q";
                    }
                    break;

                case "R":
                    if (isR(floatingImage)) {
                        return "R";
                    }
                    break;

                case "r":
                    if (isr(floatingImage)) {
                        return "r";
                    }
                    break;

                case "S":
                    if (isS(floatingImage)) {
                        return "S";
                    }
                    break;

                case "U":
                    if (isU(floatingImage)) {
                        return "U";
                    }
                    break;

                case "u":
                    if (isu(floatingImage)) {
                        return "u";
                    } else if (isa(floatingImage)) {
                        return "a";
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

                case "t":
                    if (ist(floatingImage)) {
                        return "t";
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

                case "y":
                    if (isy(floatingImage)) {
                        return "y";
                    } else {
                        if (isb1(floatingImage)) {
                            return "b1";
                        }
                    }
                    break;

                case "Z":
                    if (isZ(floatingImage)) {
                        return "Z";
                    }
                    break;

                case "1":
                    if (is1(floatingImage)) {
                        return "1";
                    }
                    break;

                case "2":
                    if (is2(floatingImage)) {
                        return "2";
                    }
                    break;

                case "3":
                    if (is3(floatingImage)) {
                        return "3";
                    }
                    break;

                case "4":
                    if (is4(floatingImage)) {
                        return "4";
                    }
                    break;

                case "5":
                    if (is5(floatingImage)) {
                        return "5";
                    }
                    break;

                case "6":
                    if (is6(floatingImage)) {
                        return "6";
                    }
                    break;

                case "7":
                    if (is7(floatingImage)) {
                        return "7";
                    }
                    break;

                case "8":
                    if (is8(floatingImage)) {
                        return "8";
                    }
                    break;

                case "9":
                    if (is9(floatingImage)) {
                        return "9";
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
        return s;
    }

    private boolean isA(FloatingImage floatingImage) {
        ArrayList<Bitmap> listRect = floatingImage.mListContours;
        if (listRect != null) {
            return (listRect.size() == 2) && !isW(floatingImage) && triangleFromTop(floatingImage) == 1
                    && connectedComponentTop(floatingImage) == 1 && connectedComponentBot(floatingImage) == 2;
        }
        return false;
    }

    private boolean isa(FloatingImage floatingImage) {
        ArrayList<Bitmap> bitmaps = floatingImage.mListContours;
        Bitmap src = floatingImage.mBitmap;
        if (bitmaps.size() == 2) {
            ArrayList<Rect> rect1 = detectAreasOnBitmap(bitmaps.get(0), 0, 0);
            ArrayList<Rect> rect2 = detectAreasOnBitmap(bitmaps.get(1), 0, 0);

            if (rect1 != null && rect2 != null) {
                if (rect1.size() == 1 && rect2.size() == 1) {
                    Rect rSmall = (rect1.get(0).width() < rect2.get(0).width()) ? rect1.get(0) : rect2.get(0);
                    Rect rBig = (rect1.get(0).width() >= rect2.get(0).width()) ? rect1.get(0) : rect2.get(0);
                    if (rSmall != null && rBig != null) {
                        Bitmap right = cropBitmap(src, rSmall.right, 0, src.getWidth() - rSmall.right, src.getHeight());
                        FloatingImage f = new FloatingImage();
                        f.mBitmap = right;
                        f.mListContours = findContour(right);
                        boolean isi = isi(f), isL = isL(f);
                        return isi || isL;
                    }
                }
            }
        }
        return false;
    }

    private boolean isB(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list.size() == 2) {
            int countFourSegmentsCol = 0, countThreeSegmentsCol = 0;
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
                if (countColumnSegments(i, bitmap) == 4) {
                    countFourSegmentsCol++;
                }
                if (countColumnSegments(i, bitmap) == 3) {
                    countThreeSegmentsCol++;
                }
            }
            return countFourSegmentsCol > 0 && countThreeSegmentsCol > 0;
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

    private boolean isb(FloatingImage floatingImage) {
        ArrayList<Bitmap> contourImage = floatingImage.mListContours;
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
        }
        return false;
    }

    private boolean isb1(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rect = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
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
                                if (interested != null) {/*
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

                                        return ((double) big) / small >= 1.3d;
                                    }*/

                                    FloatingImage f = new FloatingImage();
                                    f.mBitmap = interested;
                                    f.mListContours = findContour(interested);
                                    return !isi(f) && !isL(f);

                                }
                            }
                        }
                    }
                }
            } else {
                if (list.size() == 3) {
                    ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                    ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                    ArrayList<Rect> r3 = detectAreasOnBitmap(list.get(2), 0, 0);

                    if (r1.size() == 1 && r2.size() == 1 && r3.size() == 1) {
                        if (r1.get(0).width() != r2.get(0).width() && r1.get(0).width() != r3.get(0).width() && r3.get(0).width() != r2.get(0).width()) {
                            Rect rect = null;
                            Rect rSmall = null;
                            if (r1.get(0).width() > r2.get(0).width() && r2.get(0).width() > r3.get(0).width()) {
                                rect = r2.get(0);
                                rSmall = r3.get(0);
                            }
                            if (r1.get(0).width() > r3.get(0).width() && r3.get(0).width() > r2.get(0).width()) {
                                rect = r3.get(0);
                                rSmall = r2.get(0);
                            }
                            if (r2.get(0).width() > r1.get(0).width() && r1.get(0).width() > r3.get(0).width()) {
                                rect = r1.get(0);
                                rSmall = r3.get(0);
                            }
                            if (r2.get(0).width() > r3.get(0).width() && r3.get(0).width() > r1.get(0).width()) {
                                rect = r3.get(0);
                                rSmall = r1.get(0);
                            }
                            if (r3.get(0).width() > r1.get(0).width() && r1.get(0).width() > r2.get(0).width()) {
                                rect = r1.get(0);
                                rSmall = r2.get(0);
                            }
                            if (r3.get(0).width() > r2.get(0).width() && r2.get(0).width() > r1.get(0).width()) {
                                rect = r2.get(0);
                                rSmall = r1.get(0);
                            }

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
            }
        }
        return false;
    }

    private boolean isC(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
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

    private boolean isD(FloatingImage floatingImage) {
        if (isB(floatingImage)) {
            return false;
        }
        Bitmap bitmap = floatingImage.mBitmap;/*
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
        }*/
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

                    int countOne = 0, countTwo = 0, firstOne = -1, lastOne = -1;
                    boolean end = false;
                    int count = 0;
                    for (int i = 0; i < src.getWidth(); i++) {
                        if (countColumnSegments(i, src) == 1) {
                            countOne++;
                        }
                        if (countColumnSegments(i, src) == 2) {
                            countTwo++;
                        }
                    }

                    Log.d("Total", "" + (countOne + countTwo));
                    Log.d("Width", "" + src.getWidth());
                    if (countOne + countTwo >= src.getWidth() * 0.95d) {
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
                        Log.d("One", "first: " + firstOne + ", last: " + lastOne);
                        Log.d("Rect", "top: " + rSmall.top + ", bot: " + rSmall.bottom);
                        if (firstOne >= 0 && lastOne >= 0) {
                            return firstOne <= rSmall.top && lastOne >= rSmall.bottom;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isd(FloatingImage floatingImage) {
        /*ArrayList<Bitmap> contourImage = floatingImage.mListContours;
        if (contourImage.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(contourImage.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(contourImage.get(1), 0, 0);
            if (r1.size() == 1 && r2.size() == 1) {
                Rect area1 = r1.get(0), area2 = r2.get(0);
                Point p1 = getFirstPixel(contourImage.get(0), Color.RED);
                Point p2 = getFirstPixel(contourImage.get(1), Color.RED);
                if (p1 != null && p2 != null) {
                    if (p1.y > p2.y) {
                        return p2.x > contourImage.get(1).getWidth() / 2 &&
                                Math.abs(area1.bottom - contourImage.get(0).getHeight()) <= 2 * FingerDrawerView.CurrentPaintSize;
                    } else {
                        if (p2.y > p1.y) {
                            return p1.x > contourImage.get(0).getWidth() / 2 &&
                                    Math.abs(area2.bottom - contourImage.get(1).getHeight()) <= 2 * FingerDrawerView.CurrentPaintSize;
                        }
                    }
                }
            }
        }*/
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rSmall = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        Rect rBig = (r1.get(0).width() >= r2.get(0).width()) ? r1.get(0) : r2.get(0);

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
                                FloatingImage f = new FloatingImage();
                                f.mBitmap = interested;
                                f.mListContours = findContour(interested);
                                return isi(f) || isL(f);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isE(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() == 1) {
            int split = (int) (floatingImage.mBitmap.getWidth() * 0.3d);
            Bitmap src = floatingImage.mBitmap;
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

    private boolean ise(FloatingImage floatingImage) {
        ArrayList<Bitmap> list = floatingImage.mListContours;
        Bitmap src = floatingImage.mBitmap;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

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

    private boolean isf(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), (int) (src.getHeight() * 0.2d));
        if (detectAreasOnBitmap(top, 0, 0).size() >= 2) {
            return false;
        }
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rect = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        if (rect != null) {
                            boolean closeToBot = Math.abs(rect.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                            int count = 0;
                            int increase = 0;
                            boolean stop = false;
                            for (int i = 0; i < src.getHeight() && !stop; i++) {
                                int blank = getBlankCounts(i, src);
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
            }
        }
        return false;
    }

    private boolean isG(FloatingImage floatingImage) {
        if (isS(floatingImage)) {
            return false;
        }
        if (floatingImage.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean isg(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 3) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                ArrayList<Rect> r3 = detectAreasOnBitmap(list.get(2), 0, 0);

                if (r1 != null && r2 != null && r3 != null) {
                    if (r1.size() == 1 && r2.size() == 1 && r3.size() == 1) {
                        Rect rMin = null;
                        Rect r = null;
                        if (r1.get(0).width() > r2.get(0).width() && r2.get(0).width() > r3.get(0).width()) {
                            r = r2.get(0);
                            rMin = r3.get(0);
                        }
                        if (r1.get(0).width() > r3.get(0).width() && r3.get(0).width() > r2.get(0).width()) {
                            r = r3.get(0);
                            rMin = r2.get(0);
                        }
                        if (r2.get(0).width() > r1.get(0).width() && r1.get(0).width() > r3.get(0).width()) {
                            r = r1.get(0);
                            rMin = r3.get(0);
                        }
                        if (r2.get(0).width() > r3.get(0).width() && r3.get(0).width() > r1.get(0).width()) {
                            r = r3.get(0);
                            rMin = r1.get(0);
                        }
                        if (r3.get(0).width() > r1.get(0).width() && r1.get(0).width() > r2.get(0).width()) {
                            r = r1.get(0);
                            rMin = r2.get(0);
                        }
                        if (r3.get(0).width() > r2.get(0).width() && r2.get(0).width() > r1.get(0).width()) {
                            r = r2.get(0);
                            rMin = r1.get(0);
                        }

                        if (r != null && rMin != null) {
                            boolean closeToBot = Math.abs(rMin.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                            boolean tall = rMin.width() <= rMin.height();
                            boolean fat = r.width() >= r.height();
                            return closeToBot && tall && fat;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isH(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        Bitmap mid = cropBitmap(src, 0, (int) (src.getHeight() * 0.2d), src.getWidth(), (int) (src.getHeight() * 0.6d));
        if (detectAreasOnBitmap(mid, 0, 0).size() != 1) {
            return false;
        }
        if (triangleFromTop(floatingImage) != 1) {
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
                    FloatingImage floatingImage1 = new FloatingImage();
                    floatingImage1.mBitmap = cropBitmap(src, rect.left, rect.top, rect.width(), rect.height());
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
                        floatingImage1.mBitmap = cropBitmap(src, rect.left, rect.top, rect.width(), rect.height());
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

    private boolean ish(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rect = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
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
                                    int count = 0;
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

    private boolean isI(FloatingImage floatingImage) {
        Bitmap src = rotateBitmap(floatingImage.mBitmap, 90);
        FloatingImage f = new FloatingImage();
        f.mBitmap = src;
        return isH(f);
    }

    private boolean isi(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 1) {
                int split = (int) (src.getWidth() * 0.5d);
                Bitmap right = cropBitmap(src, split, 0, src.getWidth() - split, src.getHeight());
                ArrayList<Rect> rect = detectAreasOnBitmap(right, 0, 0);
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
                return countOne >= 0.95d * src.getHeight() || firstTwo >= src.getHeight() * 0.3d;
            }
        }
        return false;
    }

    private boolean isJ(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;
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

    private boolean isj(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
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

    private boolean isK(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
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

                    return (getBlankCounts(y, src) >= 2 * FingerDrawerView.CurrentPaintSize);
                }
            }
        }
        return false;
    }

    private boolean isk(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
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

                    return (getBlankCounts(y, src) < 2d * FingerDrawerView.CurrentPaintSize);
                }
            }
        }
        return false;
    }

    private boolean isk1(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() < 3) {
                return false;
            } else {
                if (list.size() == 3) {
                    ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                    ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                    ArrayList<Rect> r3 = detectAreasOnBitmap(list.get(2), 0, 0);

                    if (r1.size() == 1 && r2.size() == 1 && r3.size() == 1) {
                        if (r1.get(0).width() != r2.get(0).width() && r1.get(0).width() != r3.get(0).width() && r3.get(0).width() != r2.get(0).width()) {
                            Rect rBig = null;
                            Rect rTop = null;
                            Rect rMid = null;
                            if (r1.get(0).width() > r2.get(0).width() && r1.get(0).width() > r3.get(0).width()) {
                                Log.d("Rect", "1");
                                rBig = r1.get(0);
                                if (Math.abs(r1.get(0).top - r2.get(0).top) < Math.abs(r1.get(0).top - r3.get(0).top)) {
                                    rTop = r2.get(0);
                                    rMid = r3.get(0);
                                } else {
                                    rTop = r3.get(0);
                                    rMid = r2.get(0);
                                }
                            }
                            if (r2.get(0).width() > r1.get(0).width() && r2.get(0).width() > r3.get(0).width()) {
                                Log.d("Rect", "2");
                                rBig = r2.get(0);
                                if (Math.abs(r2.get(0).top - r1.get(0).top) < Math.abs(r2.get(0).top - r3.get(0).top)) {
                                    rTop = r1.get(0);
                                    rMid = r3.get(0);
                                } else {
                                    rTop = r3.get(0);
                                    rMid = r1.get(0);
                                }
                            }
                            if (r3.get(0).width() > r1.get(0).width() && r3.get(0).width() > r2.get(0).width()) {
                                Log.d("Rect", "3");
                                rBig = r3.get(0);
                                if (Math.abs(r3.get(0).top - r1.get(0).top) < Math.abs(r3.get(0).top - r2.get(0).top)) {
                                    rTop = r1.get(0);
                                    rMid = r2.get(0);
                                } else {
                                    rTop = r2.get(0);
                                    rMid = r1.get(0);
                                }
                            }

                            if (rTop != null && rMid != null && rBig != null) {
                                int offsetY = rMid.bottom;
                                Log.d("Bottom", offsetY + "");
                                Log.d("Height", src.getHeight() + "");
                                int offsetX = -1;
                                for (int i = 0; i < src.getWidth() && offsetX < 0; i++) {
                                    if (getColumnHeightFromTop(src, i) < rMid.bottom) {
                                        offsetX = i;
                                    }
                                }

                                if (offsetY >= 0) {
                                    Bitmap interested = cropBitmap(src, offsetX, offsetY, src.getWidth() - offsetX, src.getHeight() - offsetY);
                                    if (interested != null) {
                                        int two = 0;
                                        for (int i = 0; i < interested.getHeight(); i++) {
                                            if (countRowSegments(i, src) == 2) {
                                                two++;
                                            }
                                        }

                                        return two > 0;
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

    private boolean isL(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
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

    private boolean isl(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rect = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
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
                                /*int two = 0, one = 0;
                                for (int i = 0; i < interested.getHeight(); i++) {
                                    if (countRowSegments(i, interested) == 2) {
                                        two++;
                                    }
                                    if (countRowSegments(i, interested) == 1) {
                                        one++;
                                    }
                                }
                                return ((double) one / interested.getHeight()) >= 0.8d && ((double) two / interested.getHeight()) <= 0.1d;*/

                                FloatingImage f = new FloatingImage();
                                f.mBitmap = interested;
                                f.mListContours = findContour(interested);
                                return isi(f) || isL(f);
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean isM(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
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
                    FloatingImage f = new FloatingImage();
                    f.mBitmap = center;
                    return isV(f);
                }
            }
        }
        return false;
    }

    private boolean ism(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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
                FloatingImage fLeft = new FloatingImage();
                fLeft.mBitmap = left;
                FloatingImage fRight = new FloatingImage();
                fRight.mBitmap = r;
                return isN(fLeft) && isN(fRight);
            }
        }
        return false;
    }

    private boolean isN(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        int mid = src.getHeight() / 2;
        Point point = null;
        for (int i = (int) (src.getWidth() * 0.7d); i >= 0 && point == null; i--) {
            int height = getColumnHeightFromTop(src, i);
            int segments = countColumnSegments(i, src);
            if (height > mid && segments == 1) {
                point = new Point(i, height);
            }
        }

        if (point != null) {
            Log.d("Point", "not null");
            Bitmap leftAbove = cropBitmap(src, 0, 0, point.x, point.y);
            Bitmap rightBelow = cropBitmap(src, point.x + 1, point.y + 1, src.getWidth() - point.x - 1, src.getHeight() - point.y - 1);
            if (leftAbove != null && rightBelow != null) {
                FloatingImage fLeftAbove = new FloatingImage();
                fLeftAbove.mBitmap = leftAbove;
                FloatingImage fRightBelow = new FloatingImage();
                fRightBelow.mBitmap = rightBelow;
                return isV_Inverse(fLeftAbove) && isV(fRightBelow);
            }
        }
        return false;
    }

    private boolean isn(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
        int one = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                one++;
            }
        }
        int split = (int) (src.getHeight() * 0.7d);
        Bitmap bot = cropBitmap(src, 0, split, src.getWidth(), src.getHeight() - split);
        return detectAreasOnBitmap(bot, 0, 0).size() == 2 && ((double) one) / src.getHeight() <= 0.15d;
    }

    private boolean isO(FloatingImage floatingImage) {
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

                    int offsetTop = Math.abs(rBig.top - rSmall.top);
                    int offsetBot = Math.abs(rBig.bottom - rSmall.bottom);
                    int offsetLeft = Math.abs(rBig.left - rSmall.left);
                    int offsetRight = Math.abs(rBig.right - rSmall.right);
                    boolean circleHorizontal = rBig.width() >= rBig.height();

                    return (offsetBot == offsetTop) && (offsetBot == offsetRight) && (offsetBot == offsetLeft) && circleHorizontal;
                }
            }
        }
        return false;
    }

    private boolean isP(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rSmall = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        Rect rBig = (r1.get(0).width() >= r2.get(0).width()) ? r1.get(0) : r2.get(0);

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
            }
        }
        /*int thinRows = 0;
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
                && ((double) thinRows / bitmap.getHeight() >= 0.2d);*/
        return false;
    }

    private boolean isQ(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
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
        ArrayList<Bitmap> bitmaps = floatingImage.mListContours;
        if (bitmaps.size() == 2) {
            ArrayList<Rect> rect1 = detectAreasOnBitmap(bitmaps.get(0), 0, 0);
            ArrayList<Rect> rect2 = detectAreasOnBitmap(bitmaps.get(1), 0, 0);

            if (rect1 != null && rect2 != null) {
                Log.d("Size", "1: " + rect1.size() + ", 2: " + rect2.size());
                if (rect1.size() == 1 && rect2.size() == 1) {
                    Rect rSmall = (rect1.get(0).width() < rect2.get(0).width()) ? rect1.get(0) : rect2.get(0);
                    Rect rBig = (rect1.get(0).width() >= rect2.get(0).width()) ? rect1.get(0) : rect2.get(0);
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
            }
        }
        return false;
    }

    private boolean isq(FloatingImage floatingImage) {
        Bitmap bitmap = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rSmall = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);

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

    private boolean isr(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(0), 0, 0);

                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rect = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        Rect rBounder = (r1.get(0).width() >= r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        boolean closeToTopLeft = Math.abs(rBounder.top - rect.top) == Math.abs(rBounder.left - rect.left);
                        int split = (int) (src.getHeight() * 0.7d);
                        Bitmap bitmap = cropBitmap(src, 0, split, src.getWidth(), src.getHeight() - split);
                        return closeToTopLeft && detectAreasOnBitmap(bitmap, 0, 0).size() == 2;
                    }
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

    private boolean isT(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() >= 2 || isZ(floatingImage)) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean ist(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() >= 2) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean isU(FloatingImage floatingImage) {
        int countThree = 0;
        for (int i = 0; i < floatingImage.mBitmap.getHeight(); i++) {
            if (countRowSegments(i, floatingImage.mBitmap) == 3) {
                countThree++;
            }
        }
        if (countThree == 0) {
            return (floatingImage.mListContours.size() == 1) && connectedComponentBot(floatingImage) == 1 &&
                    connectedComponentTop(floatingImage) == 2;
        }
        return false;
    }

    private boolean isu(FloatingImage floatingImage) {
        return (findContour(floatingImage.mBitmap).size() == 1);
    }

    private boolean isV_Inverse(FloatingImage floatingImage) {
        int connectedTop = connectedComponentTop(floatingImage);
        int connectedBot = connectedComponentBot(floatingImage);
        int triangleTop = triangleFromTop(floatingImage);
        return (connectedTop == 1 && connectedBot == 2 && triangleTop == 1);
    }

    private boolean isV(FloatingImage floatingImage) {
        int connectedTop = connectedComponentTop(floatingImage);
        int connectedBot = connectedComponentBot(floatingImage);
        int triangleBot = triangleFromBot(floatingImage);
        Bitmap src = floatingImage.mBitmap;
        int countOne = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            if (countRowSegments(i, src) == 1) {
                countOne++;
            }
        }
        return (connectedTop == 2 && connectedBot == 1 && triangleBot == 1 && countOne <= 0.3d * src.getHeight());
    }

    private boolean isW(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() >= 2) {
            return false;
        }

        Bitmap src = floatingImage.mBitmap;
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
                FloatingImage f = new FloatingImage();
                f.mBitmap = center;
                return isV_Inverse(f);
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
            if (row + 5 < src.getHeight()) {
                FloatingImage top = new FloatingImage();
                top.mBitmap = cropBitmap(src, 0, 0, src.getWidth(), row + 5);
                return isV(top);
            }
        }
        return false;
    }

    private boolean isy(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
        Bitmap top = cropBitmap(src, 0, 0, src.getWidth(), (int) (src.getHeight() * 0.2d));
        if (detectAreasOnBitmap(top, 0, 0).size() < 2) {
            return false;
        }
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list != null) {
            if (list.size() == 2) {
                ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
                ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);
                if (r1 != null && r2 != null) {
                    if (r1.size() == 1 && r2.size() == 1) {
                        Rect rect = (r1.get(0).width() < r2.get(0).width()) ? r1.get(0) : r2.get(0);
                        if (rect != null) {
                            boolean closeToBot = Math.abs(rect.bottom - (src.getHeight() - 1)) <= 2.5 * FingerDrawerView.CurrentPaintSize;
                            int count = 0;
                            int increase = 0;
                            boolean stop = false;
                            for (int i = 0; i < src.getHeight() && !stop; i++) {
                                int blank = getBlankCounts(i, src);
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
            }
        }
        return false;
    }

    private boolean isZ(FloatingImage floatingImage) {
        Bitmap src = rotateBitmap(floatingImage.mBitmap, 90);
        FloatingImage f = new FloatingImage();
        f.mBitmap = src;
        return isN(f);
    }

    private boolean is0(FloatingImage floatingImage) {
        ArrayList<Bitmap> list = floatingImage.mListContours;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

                    int offsetTop = Math.abs(rBig.top - rSmall.top);
                    int offsetBot = Math.abs(rBig.bottom - rSmall.bottom);
                    int offsetLeft = Math.abs(rBig.left - rSmall.left);
                    int offsetRight = Math.abs(rBig.right - rSmall.right);
                    boolean circleVertical = rBig.width() <= rBig.height();

                    return (offsetBot == offsetTop) && (offsetBot == offsetRight) && (offsetBot == offsetLeft) && circleVertical;
                }
            }
        }
        return false;
    }

    private boolean is1(FloatingImage floatingImage) {
        Bitmap src = floatingImage.mBitmap;
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
                FloatingImage f = new FloatingImage();
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

    private boolean is2(FloatingImage floatingImage) {
        ArrayList<Bitmap> list = floatingImage.mListContours;
        Bitmap src = floatingImage.mBitmap;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

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
                }
            }
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

    private boolean is3(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
        int split = (int) (src.getWidth() * 0.3d);
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

    private boolean is4(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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
            FloatingImage f = new FloatingImage();
            f.mBitmap = interested;
            f.mListContours = findContour(interested);
            return isL(f) && openTop;
        }
        return false;
    }

    private boolean is5(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() > 1) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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
                        FloatingImage f = new FloatingImage();
                        f.mBitmap = rotated;
                        f.mListContours = findContour(rotated);
                        return isL(f) && countFatRow > 0;
                    }
                }
            }

        }
        return false;
    }

    private boolean is6(FloatingImage floatingImage) {
        ArrayList<Bitmap> list = floatingImage.mListContours;
        Bitmap src = floatingImage.mBitmap;
        if (list.size() == 2) {
            ArrayList<Rect> r1 = detectAreasOnBitmap(list.get(0), 0, 0);
            ArrayList<Rect> r2 = detectAreasOnBitmap(list.get(1), 0, 0);

            if (r1 != null && r2 != null) {
                if (r1.size() == 1 && r2.size() == 1) {
                    Rect rSmall = (r1.get(0).width() > r2.get(0).width()) ? r2.get(0) : r1.get(0);
                    Rect rBig = (r1.get(0).width() <= r2.get(0).width()) ? r2.get(0) : r1.get(0);

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
            }
        }

        return false;
    }

    private boolean is7(FloatingImage floatingImage) {

        if (floatingImage.mListContours.size() >= 2 || isZ(floatingImage)) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean is8(FloatingImage floatingImage) {
        if (floatingImage.mListContours.size() != 3) {
            return false;
        }
        Bitmap src = floatingImage.mBitmap;
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

    private boolean is9(FloatingImage floatingImage) {
        Bitmap rotated = rotateBitmap(floatingImage.mBitmap, 180);
        FloatingImage f = new FloatingImage();
        f.mBitmap = rotated;
        f.mListContours = findContour(rotated);
        return is6(f);
    }
}
