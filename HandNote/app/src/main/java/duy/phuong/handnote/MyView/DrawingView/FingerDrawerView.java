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
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.RecognitionAPI.DetectCharacterAPI;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 23/11/2015.
 */
public class FingerDrawerView extends View {

    private ArrayList<ArrayList<Point>> mListPaths;

    private Bitmap mBitmap, mCacheBitmap;
    private float mCurrentWidth = 5f;
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
        mAPI = new DetectCharacterAPI(context);
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
                if (isReadyForRecognize && mStartRecognizeTime > 0) {
                    if (System.currentTimeMillis() - mStartRecognizeTime > 3000) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                }
                handler.postDelayed(this, 10);
            }
        };
        runnable.run();
    }

    private void detectCharacterWithAPI() {
        mAPI.onDetectCharacter(mCacheBitmap, new RecognitionCallback() {
            @Override
            public void onRecognizeSuccess(ArrayList<Bitmap> listBitmaps) {
                mListener.onRecognizeSuccess(listBitmaps);
                invalidate();
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isReadyForRecognize) {
            mCanvas.drawColor(Color.WHITE);
            mCacheCanvas.drawColor(Color.WHITE);
        }
        for (ArrayList<Point> listPoint : mListPaths) {
            Path path = new Path();
            boolean first = true;
            for (Point point : listPoint) {
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
                listPoint.add(new Point((int) x, (int) y));
                mListPaths.add(listPoint);
                mStartRecognizeTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_MOVE:
                mListPaths.get(mCurrentPath).add(new Point((int) x, (int) y));
                break;
            case MotionEvent.ACTION_UP:
                mListPaths.get(mCurrentPath).add(new Point((int) x, (int) y));
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
        tempPaint.setStrokeWidth(mCurrentWidth);
        return tempPaint;
    }

    private Paint createEraser(){
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setAlpha(0xFF);
        eraser.setColor(Color.RED);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setStrokeWidth(mCurrentWidth);
        eraser.setStrokeJoin(Paint.Join.ROUND);
        eraser.setXfermode(null);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return eraser;
    }
}
