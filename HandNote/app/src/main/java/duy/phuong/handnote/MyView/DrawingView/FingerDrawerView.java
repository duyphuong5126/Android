package duy.phuong.handnote.MyView.DrawingView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.RecognitionAPI.DetectCharacterAPI;

/**
 * Created by Phuong on 23/11/2015.
 */
public class FingerDrawerView extends View {

    private ArrayList<MyPath> mListPaths;

    private Bitmap mBitmap, mCacheBitmap;
    public static float CurrentPaintSize = 5f;
    private Canvas mCanvas, mCacheCanvas;

    private int mCurrentPath = 0;

    private Paint mPaint;
    private Paint mRectPaint;

    private long mStartRecognizeTime = -1;
    private boolean isReadyForRecognize = false;

    private static int CurrentWidth = 0;
    private static int CurrentHeight = 0;

    private RecognitionCallback mListener;

    private DetectCharacterAPI mAPI;

    public FingerDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListPaths = new ArrayList<>();
        mPaint = createPaint();

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.RED);
        mRectPaint.setStrokeWidth(1);
        mAPI = new DetectCharacterAPI();
    }

    public void setListener(RecognitionCallback callback) {
        this.mListener = callback;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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
                    detectCharacterWithAPI();
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
    }

    private ArrayList<MyPath> doDFS(ArrayList<MyPath> list, MyPath myPath) {
        boolean flag = false;
        if (hasMoreEdges()) {
            for (MyPath path : mListPaths) {
                if (!path.isChecked() && path.isIntersect(myPath, CurrentWidth, CurrentHeight, mPaint)) {
                    list.add(path);
                    path.setChecked(true);
                    flag = true;
                }
            }
            if (flag) {
                ArrayList<MyPath> listPaths = new ArrayList<>();
                listPaths.addAll(list);
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

    private void detectCharacterWithAPI() {
        ArrayList<MyShape> listShapes = new ArrayList<>();

        for (int i = 0; i< mListPaths.size(); i++) {
            MyPath myPath = mListPaths.get(i);
            if (!myPath.isChecked()) {
                ArrayList<MyPath> list = new ArrayList<>();
                list.add(myPath);
                myPath.setChecked(true);
                list.addAll(doDFS(list, myPath));

                if (!list.isEmpty()) {
                    MyShape myShape = new MyShape(list);
                    myShape.mergeRect();
                    listShapes.add(myShape);
                }
            }
        }

        for (int i = 0; i < mListPaths.size(); i++) {
            mListPaths.get(i).setChecked(false);
        }

        final ArrayList<Bitmap> bitmaps = new ArrayList<>();

        if (!listShapes.isEmpty()) {
            for (MyShape myShape : listShapes) {
                Bitmap bitmap = Bitmap.createBitmap(mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
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

                mAPI.onDetectCharacter(bitmap, new RecognitionCallback() {
                    @Override
                    public void onRecognizeSuccess(ArrayList<Bitmap> listBitmaps) {
                        bitmaps.addAll(listBitmaps);
                    }
                });
            }
        }
        mListener.onRecognizeSuccess(bitmaps);

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
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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

    private Paint createEraser(){
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setAlpha(0xFF);
        eraser.setColor(Color.WHITE);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setStrokeWidth(CurrentPaintSize);
        eraser.setStrokeJoin(Paint.Join.ROUND);
        eraser.setXfermode(null);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return eraser;
    }

    public void emptyDrawer() {
        mListPaths.clear();
        mCurrentPath = 0;
        invalidate();
    }
}
