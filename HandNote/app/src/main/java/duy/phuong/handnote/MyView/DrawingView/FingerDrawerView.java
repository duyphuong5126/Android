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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import duy.phuong.handnote.DTO.FloatingImage;
import duy.phuong.handnote.RecognitionAPI.BitmapProcessor;

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

    private Paint mPaint;

    private long mStartRecognizeTime = -1;
    private boolean isReadyForRecognize = false;

    private static int CurrentWidth = 0;
    private static int CurrentHeight = 0;

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
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

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
        mPaint = createPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isReadyForRecognize) {
            mCanvas.drawColor(Color.WHITE);
            mCacheCanvas.drawColor(Color.WHITE);
        }
        for (MyPath myPath : mListPaths) {
            Path path = new Path();
            boolean first = true;
            ArrayList<Point> points = myPath.getListPoint();
            if (points.size() == 2) {
                if (points.get(0).equals(points.get(1))) {
                    Point point = points.get(0);
                    mCanvas.drawPoint(point.x, point.y, mPaint);
                    mCacheCanvas.drawPoint(point.x, point.y, mPaint);
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

                mCanvas.drawPath(path, mPaint);
                mCacheCanvas.drawPath(path, mPaint);
            }
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetStack();
                ArrayList<Point> listPoint = new ArrayList<>();
                MyPath myPath = new MyPath(listPoint);
                listPoint.add(new Point((int) x, (int) y));
                mListPaths.add(myPath);
                mStartRecognizeTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_MOVE:
                mListPaths.get(mCurrentPath).getListPoint().add(new Point((int) x, (int) y));
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
            mUndoRedoStack.add(mListPaths.remove(mListPaths.size() - 1));
            mCurrentPath--;
            detectCharacters();
            invalidate();
        }
    }

    public void redo() {
        if (mUndoRedoStack.size() > 0) {
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
                if (!path.isChecked() && path.isIntersect(myPath, CurrentWidth, CurrentHeight, mPaint)) {
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
        ArrayList<MyShape> listShapes = new ArrayList<>();

        for (int i = 0; i< mListPaths.size(); i++) {
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
        }

        final ArrayList<FloatingImage> bitmaps = new ArrayList<>();

        if (!listShapes.isEmpty()) {
            for (MyShape myShape : listShapes) {
                final FloatingImage floatingImage = new FloatingImage();
                floatingImage.mBitmap = Bitmap.createBitmap(mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                floatingImage.mMyShape = myShape;
                floatingImage.mParentWidth = CurrentWidth;
                floatingImage.mParentHeight = CurrentHeight;

                Canvas canvas = new Canvas(floatingImage.mBitmap);
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

                    canvas.drawPath(path, mPaint);
                }

                mBitmapProcessor.onDetectCharacter(floatingImage, new BitmapProcessor.RecognitionCallback() {
                    @Override
                    public void onRecognizeSuccess(ArrayList<FloatingImage> listBitmaps) {
                        bitmaps.addAll(listBitmaps);
                    }
                });
            }
        }
        mListener.onRecognizeSuccess(bitmaps);
    }

    private Paint createPaint(){
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
        invalidate();
    }

    public boolean isEmpty() {
        return mListPaths.isEmpty();
    }

    private void updatePaintWidth() {
        DisplayMetrics metrics = mDisplayListener.getScreenResolution();
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
