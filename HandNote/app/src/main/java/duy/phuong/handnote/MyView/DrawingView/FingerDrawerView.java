package duy.phuong.handnote.MyView.DrawingView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Recognizer.BitmapProcessor;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 23/11/2015.
 */
public class FingerDrawerView extends View {
    private ArrayList<MyPath> mListPaths;
    private Stack<MyPath> mUndoRedoStack;

    private Bitmap mBitmap, mCacheBitmap;
    public static float CurrentPaintSize = 5f;
    private Canvas mCanvas, mCacheCanvas;

    private int mCurrentPath = 0;

    private Paint mPaint, mPrivatePaint;

    private long mStartRecognizeTime = -1;
    private boolean isReadyForRecognize = false;

    private static int CurrentWidth = 0;
    private static int CurrentHeight = 0;

    private boolean mUndoRedo;

    private HashMap<MyPath, Integer> mMapColor;

    private ArrayList<Line> mLines;
    private ArrayList<Character> mCharacters;

    public interface GetDisplayListener {
        DisplayMetrics getScreenResolution();
    }

    private BitmapProcessor.RecognitionCallback mListener;
    private GetDisplayListener mDisplayListener;

    private BitmapProcessor mBitmapProcessor;

    public FingerDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListPaths = new ArrayList<>();
        mUndoRedoStack = new Stack<>();

        mBitmapProcessor = new BitmapProcessor();
        mLines = new ArrayList<>();
        mCharacters = new ArrayList<>();
        mPaint = createPaint(); mPrivatePaint = createPaint();
    }

    public void setListener(BitmapProcessor.RecognitionCallback callback) {
        this.mListener = callback;
    }


    public void setDisplayListener(GetDisplayListener listener) {
        mDisplayListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePaintWidth();
        CurrentWidth = w;
        CurrentHeight = h;
        try {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            Log.d("Out of memory", "bitmap size exceeds VM budget");
            Log.d("Size", "w: " + w + ", h: " + h);
        }

        mCanvas = new Canvas(mBitmap);
        mCacheCanvas = new Canvas(mCacheBitmap);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (isReadyForRecognize) {
                    super.handleMessage(msg);
                    detectCharacters();
                    isReadyForRecognize = false;
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isReadyForRecognize && mStartRecognizeTime > 0 && System.currentTimeMillis() - mStartRecognizeTime > 3000) {
                    handler.sendMessage(handler.obtainMessage());
                }
                handler.postDelayed(this, 10);
            }
        };
        runnable.run();
        int mLineHeight = mCacheBitmap.getHeight() / 6;
        if (mLines.isEmpty()) {
            Line line = new Line(); line.mTop = 0; line.mBottom = mLineHeight;
            line.mMinTop = line.mTop; line.mMaxBottom = line.mBottom;
            mLines.add(line);
        }
        for (int i = mLineHeight; i < mCacheBitmap.getHeight(); i += mLineHeight) {
            if (i + mLineHeight < mCacheBitmap.getHeight()) {
                Line line = new Line(); line.mTop = i; line.mBottom = i + mLineHeight;
                line.mMinTop = line.mTop; line.mMaxBottom = line.mBottom;
                mLines.add(line);
            }
        }

        mMapColor = new HashMap<>();
    }

    public Bitmap getContent() {
        return mBitmap;
    }

    public ArrayList<Line> getLines() {
        return mLines;
    }

    public void setSplit() {
        mBitmapProcessor.setSplit();
    }

    public void setFindContours() {
        mBitmapProcessor.setFindContours();
    }

    public void setFindVerticalProjectionProfile() {
        mBitmapProcessor.setFindVerticalProjectionProfile();
    }

    public void setFindHorizontalProjectionProfile() {
        mBitmapProcessor.setFindHorizontalProjectionProfile();
    }

    public void setProfile() {
        mBitmapProcessor.setProfile();
    }

    public void setDefault() {
        mBitmapProcessor.setDefault();
    }

    public void setSplitTopDown() {
        mBitmapProcessor.setTopDown();
    }

    public void setSplitBottomUp() {
        mBitmapProcessor.setBottomUp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCacheCanvas.drawColor(Color.WHITE);
        if (!mLines.isEmpty()) {
            for (Line line : mLines) {
                for (int j = 10; j <= mCacheBitmap.getWidth() - 10; j += 6) {
                    mCacheBitmap.setPixel(j, line.mTop, Color.BLACK);
                    if (j + 1 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 1, line.mTop, Color.BLACK);
                    }
                    if (j + 2 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 2, line.mTop, Color.BLACK);
                    }
                    if (j + 3 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 3, line.mTop, Color.BLACK);
                    }
                }
                for (int j = 10; j <= mCacheBitmap.getWidth() - 10; j += 6) {
                    mCacheBitmap.setPixel(j, line.mBottom, Color.BLACK);
                    if (j + 1 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 1, line.mBottom, Color.BLACK);
                    }
                    if (j + 2 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 2, line.mBottom, Color.BLACK);
                    }
                    if (j + 3 < mCacheBitmap.getWidth()) {
                        mCacheBitmap.setPixel(j + 3, line.mBottom, Color.BLACK);
                    }
                }
            }
        }
        mCanvas.drawBitmap(mCacheBitmap, 0, 0, mPaint);
        for (MyPath myPath : mListPaths) {
            Paint paint = createPaint(); paint.setColor(mMapColor.get(myPath));
            Path path = new Path();
            boolean first = true;
            ArrayList<Point> points = myPath.getListPoint();
            if (points.size() == 2) {
                if (points.get(0).equals(points.get(1))) {
                    Point point = points.get(0);
                    mCanvas.drawPoint(point.x, point.y, paint);
                }
            } else {
                for (Point point : myPath.getListPoint()) {
                    if (first) {
                        first = false;
                        path.moveTo(point.x, point.y);
                    } else {
                        path.lineTo(point.x, point.y);
                    }
                }

                mCanvas.drawPath(path, paint);
            }
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetStack();
                ArrayList<Point> listPoint = new ArrayList<>();
                MyPath myPath = new MyPath(listPoint);
                mMapColor.put(myPath, mPaint.getColor());
                listPoint.add(new Point((int) x, (int) y));
                mListPaths.add(myPath);
                mStartRecognizeTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_MOVE:
                mListPaths.get(mCurrentPath).getListPoint().add(new Point((int) x, (int) y));
                mStartRecognizeTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                mListPaths.get(mCurrentPath).getListPoint().add(new Point((int) x, (int) y));
                mListPaths.get(mCurrentPath).initRect();
                mCurrentPath++;
                isReadyForRecognize = true;
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    public void undo() {
        if (mListPaths.size() > 0) {
            mUndoRedo = true;
            MyPath myPath = mListPaths.remove(mListPaths.size() - 1);
            mUndoRedoStack.push(myPath);
            mCurrentPath--;
            detectCharacters();
            invalidate();
        }
    }

    public void redo() {
        if (mUndoRedoStack.size() > 0) {
            mUndoRedo = true;
            mListPaths.add(mUndoRedoStack.pop());
            mCurrentPath++;
            detectCharacters();
            invalidate();
        }
    }

    private void resetStack() {
        mUndoRedoStack = new Stack<>();
    }

    private ArrayList<MyPath> doDFS(ArrayList<MyPath> list, MyPath myPath) {
        if (hasMoreEdges()) {
            ArrayList<MyPath> listPaths = new ArrayList<>();
            for (MyPath path : mListPaths) {
                if (!path.isChecked() && path.isIntersect(myPath, CurrentWidth, CurrentHeight, mPrivatePaint)) {
                    listPaths.add(path);
                    path.setChecked(true);
                }
            }

            if (!listPaths.isEmpty()) {
                list.addAll(listPaths);
                for (MyPath path : listPaths) {
                    doDFS(list, path);
                }
            } else {
                return list;
            }
        }
        return list;
    }

    private boolean hasMoreEdges() {
        for (MyPath path : mListPaths) {
            if (!path.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void detectCharacters() {
        if (mUndoRedo || mBitmapProcessor.getMode() != BitmapProcessor.DEFAULT) {
            mCharacters.clear();
            mUndoRedo = false;
        }
        if (mCharacters.isEmpty()) {
            ArrayList<MyShape> listShapes = new ArrayList<>();

            for (int i = 0; i < mListPaths.size(); i++) {
                MyPath myPath = mListPaths.get(i);
                if (!myPath.isChecked()) {
                    ArrayList<MyPath> list = new ArrayList<>();
                    ArrayList<MyPath> paths = new ArrayList<>();
                    list.add(myPath);
                    myPath.setChecked(true);
                    list.addAll(doDFS(paths, myPath));

                    if (!list.isEmpty()) {
                        MyShape myShape = new MyShape(list);
                        listShapes.add(myShape);
                    }
                }
            }

            for (int i = 0; i < mListPaths.size(); i++) {
                mListPaths.get(i).setChecked(false);
                mListPaths.get(i).setSettled(true);
            }

            if (!listShapes.isEmpty()) {
                for (MyShape myShape : listShapes) {
                    final Character character = new Character();
                    character.mBitmap = Bitmap.createBitmap(mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    character.mMyShape = myShape;
                    character.mParentWidth = CurrentWidth;
                    character.mParentHeight = CurrentHeight;

                    Canvas canvas = new Canvas(character.mBitmap);
                    canvas.drawColor(Color.WHITE);
                    for (MyPath myPath : myShape.getListPaths()) {
                        Path path = new Path();
                        boolean first = true;
                        for (Point point : myPath.getListPoint()) {
                            if (first) {
                                first = false;
                                path.moveTo(point.x, point.y);
                            } else {
                                path.lineTo(point.x, point.y);
                            }
                        }

                        canvas.drawPath(path, mPrivatePaint);
                    }

                    mBitmapProcessor.onDetectCharacter(character, new BitmapProcessor.RecognitionCallback() {
                        @Override
                        public void onRecognizeSuccess(ArrayList<Character> listCharacters) {
                            for (Character c : listCharacters) {
                                c.isSettled = true;
                            }
                            mCharacters.addAll(listCharacters);
                        }
                    });
                }
            }
        } else {
            ArrayList<MyShape> listShapes = new ArrayList<>();
            for (MyPath myPath : mListPaths) {
                if (!myPath.isSettled() && !myPath.isChecked()) {
                    ArrayList<MyPath> list = new ArrayList<>();
                    ArrayList<MyPath> paths = new ArrayList<>();
                    list.add(myPath);
                    myPath.setChecked(true);
                    list.addAll(doDFS(paths, myPath));

                    boolean related = false;
                    ArrayList<Character> characters = new ArrayList<>();
                    for (Character character : mCharacters) {
                        ArrayList<MyPath> myPaths = character.mMyShape.getListPaths();
                        boolean contain = false;
                        for (int j = 0; j < list.size() && !contain; j++)  {
                            if (myPaths.contains(list.get(j))) {
                                contain = true;
                                related = true;
                            }
                        }
                        if (contain) {
                            for (int j = 0; j < list.size(); j++)  {
                                MyPath path = list.get(j);
                                if (!myPaths.contains(path)) {
                                    myPaths.add(path);
                                    myPath.setSettled(true);
                                    character.isSettled = false;
                                }
                                Bitmap bitmap = Bitmap.createBitmap(CurrentWidth, CurrentHeight, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                canvas.drawColor(Color.WHITE);
                                for (MyPath myPath1 : character.mMyShape.getListPaths()) {
                                    Path p = new Path();
                                    boolean first = true;
                                    for (Point point : myPath1.getListPoint()) {
                                        if (first) {
                                            first = false;
                                            p.moveTo(point.x, point.y);
                                        } else {
                                            p.lineTo(point.x, point.y);
                                        }
                                    }

                                    canvas.drawPath(p, mPrivatePaint);
                                }
                                Rect rect = mBitmapProcessor.detectAreasOnBitmap(bitmap, 0, 0).get(0);
                                character.mRect = new Rect(rect.left, rect.top, rect.right, rect.bottom);
                                character.mBitmap = BitmapProcessor.cropBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
                                if (character.mListContours != null) {
                                    character.mListContours.clear();
                                }
                                if (character.mListContours == null) {
                                    character.mListContours = new ArrayList<>();
                                }
                                character.mListContours.addAll(mBitmapProcessor.findContour(character.mBitmap));
                            }
                            characters.add(character);
                            character.mAlphabet = null;
                        }
                    }
                    if (!characters.isEmpty()) {
                        if (characters.size() > 1) {
                            while (characters.size() > 1) {
                                int index = characters.size() - 1;
                                mCharacters.remove(characters.remove(index));
                            }
                        }
                    }
                    if (!related) {
                        if (!list.isEmpty()) {
                            MyShape myShape = new MyShape(list);
                            listShapes.add(myShape);
                        }
                    }
                }
            }
            if (!listShapes.isEmpty()) {
                for (MyShape shape : listShapes) {
                    final Character character = new Character();
                    character.mBitmap = Bitmap.createBitmap(mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    character.mMyShape = shape;
                    character.mParentWidth = CurrentWidth;
                    character.mParentHeight = CurrentHeight;

                    Canvas canvas = new Canvas(character.mBitmap);
                    canvas.drawColor(Color.WHITE);
                    for (MyPath path : shape.getListPaths()) {
                        Path p = new Path();
                        boolean first = true;
                        for (Point point : path.getListPoint()) {
                            if (first) {
                                first = false;
                                p.moveTo(point.x, point.y);
                            } else {
                                p.lineTo(point.x, point.y);
                            }
                        }

                        canvas.drawPath(p, mPrivatePaint);
                    }

                    mBitmapProcessor.onDetectCharacter(character, new BitmapProcessor.RecognitionCallback() {
                        @Override
                        public void onRecognizeSuccess(ArrayList<Character> listCharacters) {
                            mCharacters.addAll(listCharacters);
                        }
                    });
                }
            }
        }

        for (MyPath myPath : mListPaths) {
            myPath.setChecked(false);
        }
        mListener.onRecognizeSuccess(mCharacters);
    }

    private Paint createPaint() {
        Paint tempPaint = new Paint();
        tempPaint.setColor(Color.BLACK);
        tempPaint.setPathEffect(new CornerPathEffect(10));
        tempPaint.setStyle(Paint.Style.STROKE);
        tempPaint.setStrokeCap(Paint.Cap.ROUND);
        tempPaint.setStrokeJoin(Paint.Join.ROUND);
        tempPaint.setAntiAlias(true);
        tempPaint.setDither(true);
        tempPaint.setStrokeWidth(CurrentPaintSize);
        return tempPaint;
    }

    public void emptyDrawer() {
        mListPaths.clear();
        mCurrentPath = 0;
        mCharacters.clear();
        mMapColor.clear();
        invalidate();
    }

    public boolean isEmpty() {
        return mListPaths.isEmpty();
    }

    private void updatePaintWidth() {
        if (mDisplayListener != null) {
            DisplayMetrics metrics = mDisplayListener.getScreenResolution();
            if (metrics != null) {
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;
                if (height >= 480) {
                    if (height <= 800) {
                        FingerDrawerView.CurrentPaintSize = 10f;
                    } else {
                        if (height <= 854) {
                            FingerDrawerView.CurrentPaintSize = 11f;
                        } else {
                            if (height <= 960) {
                                FingerDrawerView.CurrentPaintSize = 14f;
                            } else {
                                if (height <= 1024) {
                                    FingerDrawerView.CurrentPaintSize = 15f;
                                } else {
                                    if (height <= 1280) {
                                        switch (width) {
                                            case 800:
                                                FingerDrawerView.CurrentPaintSize = 18f;
                                                break;
                                            case 768:
                                                FingerDrawerView.CurrentPaintSize = 17f;
                                                break;
                                            default:
                                                FingerDrawerView.CurrentPaintSize = 16f;
                                                break;
                                        }
                                    } else {
                                        if (height <= 1400) {
                                            FingerDrawerView.CurrentPaintSize = 20f;
                                        } else {
                                            if (height <= 1824) {
                                                FingerDrawerView.CurrentPaintSize = 22f;
                                            } else {
                                                if (height <= 1920) {
                                                    FingerDrawerView.CurrentPaintSize = (width <= 1200) ? 24f : 25f;
                                                } else {
                                                    FingerDrawerView.CurrentPaintSize = 30f;
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
        }
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
