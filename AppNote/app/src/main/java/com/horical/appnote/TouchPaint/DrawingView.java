package com.horical.appnote.TouchPaint;

import com.horical.appnote.R;
import com.horical.appnote.Supports.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Phuong on 15/07/2015.
 */
public class DrawingView extends View {
    private ArrayList<Stuff> pairDrawers;
    private Bitmap bitmap, cacheBitmap;
    private Path path;
    private Paint paint;
    private Stuff tempStuff;
    private boolean EraseMode = false;

    private static float centerX, centerY;

    private int UndoRedoPath = -1;

    private static int realBitmapWidth = 0;
    private static int realBitmapHeight = 0;

    private Canvas canvasCache;

    private ColorUtil currentPaintColor;
    private float currentWidth = 6f;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint drawBitMapPaint = new Paint();

        pairDrawers = new ArrayList<>();

        drawBitMapPaint.setColor(Color.RED);


        path = new Path();
        paint = new Paint();
        tempStuff = new Stuff();

        cacheBitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);

        canvasCache = new Canvas(cacheBitmap);

        bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.defaultbitmap));

        currentPaintColor = new ColorUtil();
    }



    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvasCache = new Canvas(cacheBitmap);

        centerX = getWidth() / 2; centerY = getHeight() / 2;

        if(bitmap != null) canvas.drawBitmap(bitmap, centerX - (bitmap.getWidth()/2), centerY - (bitmap.getHeight()/2), null);
        for(Stuff stuff:pairDrawers){
            if(stuff.isVisible()){
                canvasCache.drawPath(stuff.getPath(), stuff.getPaint());
            }
        }
        canvas.drawBitmap(cacheBitmap, 0, 0, null);
    }

    public void setBitmap(Bitmap temp){
        realBitmapWidth = temp.getWidth();
        realBitmapHeight = temp.getHeight();
        if (temp.getWidth() > getWidth() || temp.getHeight() > getHeight()){
            bitmap = Bitmap.createBitmap(resizeBitmap(temp, getWidth(), getHeight()), 0, 0, getWidth(), getHeight());
        } else {
            bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight());
        }
        invalidate();
    }

    private Bitmap loadImageFromSDCard(String imageDirectory, String imageName, String imageExtension){
        Bitmap tempBitmap;
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()+imageDirectory);
        File tempFile = new File(directory, imageName+imageExtension);
        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(tempFile);
        } catch (FileNotFoundException e) {
            Log.d("Error", "File error");
        }
        tempBitmap = BitmapFactory.decodeStream(streamIn);
        return tempBitmap;
    }

    public String saveImageIntoSDCard(Context context, String bitmapName) {
        String rootPath = FileUtils.getApplicationDirectory("TouchPaint");
        File myFileDir = new File(rootPath);
        myFileDir.mkdir();
        File myFile = new File(myFileDir, bitmapName+".png");
        if(myFile.exists()) {
            myFile.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(myFile);
            this.mergeBitmap().compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context, new String[]{myFile.toString()}, null, null);
        return myFile.getAbsolutePath();
    }

    public Bitmap mergeBitmap(){
        if(bitmap == null) return null;
        Bitmap mergeBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mergeCanvas = new Canvas(mergeBitmap);

        mergeCanvas.drawColor(Color.WHITE);
        mergeCanvas.drawBitmap(bitmap, centerX - (bitmap.getWidth() / 2), centerY - (bitmap.getHeight() / 2), null);
        mergeCanvas.drawBitmap(cacheBitmap, 0, 0, null);
        mergeCanvas.save(Canvas.ALL_SAVE_FLAG);
        mergeCanvas.restore();

        return mergeBitmap;
}

    private Bitmap resizeBitmap(Bitmap src, int newWidth, int newHeight){
        int width = src.getWidth(); int height = src.getHeight();
        float scaleFactorX = (newWidth*1.f)/width;
        float scaleFactorY = (newHeight*1.f)/height;

        Matrix resizeMatrix = new Matrix();
        resizeMatrix.postScale(scaleFactorX, scaleFactorY);

        return Bitmap.createBitmap(src, 0, 0, width, height, resizeMatrix, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                tempStuff = new Stuff();
                paint = new Paint();
                paint = (EraseMode)?createEraser():createPaint();
                path = new Path();
                pairDrawers.add(tempStuff);
                path.moveTo(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                UndoRedoPath++;
                break;
        }
        tempStuff.setPath(path);
        tempStuff.setPaint(paint);
        invalidate();
        return super.onTouchEvent(event);
    }

    private Paint createPaint(){
        Paint tempPaint = new Paint();
        tempPaint.setColor(currentPaintColor.getRGB());
        tempPaint.setStyle(Paint.Style.STROKE);
        tempPaint.setStrokeCap(Paint.Cap.ROUND);
        tempPaint.setStrokeJoin(Paint.Join.ROUND);
        tempPaint.setAntiAlias(true);
        tempPaint.setDither(true);
        tempPaint.setStrokeWidth(currentWidth);
        return tempPaint;
    }

    private Paint createEraser(){
        Log.d("Eraser", "abc");
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setAlpha(0xFF);
        eraser.setColor(Color.RED);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setStrokeWidth(currentWidth);
        eraser.setStrokeJoin(Paint.Join.ROUND);
        eraser.setXfermode(null);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return eraser;
    }

    public void ActiveEraser(boolean mode){
        EraseMode = mode;
    }

    public int changeColor(int value, int channel){
        return currentPaintColor.changeColorByChannel(value, channel);
    }

    public int changeColor(String strColor){
        int rgb = Color.parseColor(strColor);
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        currentPaintColor.setRGB(r, g, b);
        return currentPaintColor.getRGB();
    }

    public int changeColor(int Color){
        int r = (Color >> 16) & 0xff;
        int g = (Color >> 8) & 0xff;
        int b = (Color) & 0xff;
        currentPaintColor.setRGB(r, g, b);
        return currentPaintColor.getRGB();
    }

    public int getCurrentPaintColor(){
        return currentPaintColor.getRGB();
    }

    public ColorUtil getCurrentPaintRGB(){
        return currentPaintColor;
    }

    public ColorUtil getCurrentPaintColorUtil(){
        return this.currentPaintColor;
    }

    public float getCurrentWidth() {
        return currentWidth;
    }

    public void setCurrentWidth(float currentWidth) {
        this.currentWidth = currentWidth;
    }

    public void Redo(){
        if(pairDrawers.isEmpty()) return;
        UndoRedoPath++;
        UndoRedoPath = (UndoRedoPath < pairDrawers.size())?UndoRedoPath:pairDrawers.size()-1;
        pairDrawers.get(UndoRedoPath).setVisible(true);
        invalidate();
    }

    public void Undo(){
        if(pairDrawers.isEmpty()) return;
        pairDrawers.get(UndoRedoPath).setVisible(false);
        UndoRedoPath--;
        UndoRedoPath = (UndoRedoPath >=0 )?UndoRedoPath:0;
        invalidate();
    }

    public void EmptyCanvas(){
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        pairDrawers.clear();
        invalidate();
    }
}
