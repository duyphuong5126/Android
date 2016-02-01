package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePanel;

/**
 * Created by Phuong on 09/06/2015.
 */
public class MainThread extends Thread {
    private int mFPS = 30;
    private double mAverageFPS;
    private SurfaceHolder mHolder;
    private GamePanel mGamePanel;
    private boolean mRunning;

    private static Canvas CANVAS;

    public MainThread(SurfaceHolder sf, GamePanel gp){
        super();
        this.mHolder = sf;
        this.mGamePanel = gp;
    }

    @Override
    public void run() {
        long startTime, timeMilis, waitTime, totalTime = 0, targetTime = 1000 / mFPS;
        int frameCount = 0;
        while (mRunning){
            startTime = System.nanoTime();
            CANVAS = null;

            //lock the CANVAS to pixel editing
            try{
                CANVAS = this.mHolder.lockCanvas();
                synchronized (mHolder){
                    this.mGamePanel.update();
                    this.mGamePanel.draw(CANVAS);
                }
            } catch (Exception e){

            }
            finally {
                if(CANVAS != null){
                    try {
                        mHolder.unlockCanvasAndPost(CANVAS);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            timeMilis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMilis;

            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == mFPS){
                mAverageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    public void setRunning(boolean running){
        this.mRunning = running;
    }
}
