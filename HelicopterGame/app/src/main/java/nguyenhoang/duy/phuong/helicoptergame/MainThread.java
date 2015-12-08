package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Phuong on 09/06/2015.
 */
public class MainThread extends Thread {
    private int FPS = 30;
    private double avarageFPS;
    private SurfaceHolder holder;
    private GamePanel gamePanel;
    private boolean running;
    private static Canvas canvas;

    public MainThread(SurfaceHolder sf, GamePanel gp){
        super();
        this.holder = sf;
        this.gamePanel = gp;
    }

    @Override
    public void run() {
        long startTime, timeMilis, waitTime, totalTime = 0, targetTime = 1000 / FPS;
        int frameCount = 0;
        while (running){
            startTime = System.nanoTime();
            canvas = null;

            //lock the canvas to pixel editing
            try{
                canvas = this.holder.lockCanvas();
                synchronized (holder){
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e){

            }
            finally {
                if(canvas != null){
                    try {
                        holder.unlockCanvasAndPost(canvas);
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
            if(frameCount == FPS){
                avarageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }
}
