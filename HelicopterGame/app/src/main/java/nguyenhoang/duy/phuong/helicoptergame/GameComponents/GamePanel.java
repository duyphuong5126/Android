package nguyenhoang.duy.phuong.helicoptergame.GameComponents;

/**
 * Created by Phuong on 09/06/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameEffects.Explosion;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameEffects.SmokePuff;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers.Controller;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers.Enemy;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers.NPC;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers.Player;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits.Bullet;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits.Gold;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits.Item;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits.Missile;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits.SpecialMissile;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils.Background;
import nguyenhoang.duy.phuong.helicoptergame.MainThread;
import nguyenhoang.duy.phuong.helicoptergame.R;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final int MOVE_SPEED = -5;

    private MainThread mThread;
    private int mLevel;
    private Background mBackground;
    private Player mPlayer;
    private Enemy mEnemy;
    private ArrayList<SmokePuff> mSmokes;
    private ArrayList<Missile> mMissiles;
    private ArrayList<SpecialMissile> mEnemyMissiles;
    private ArrayList<Gold> mGolds;
    private ArrayList<Item> mItems;
    private ArrayList<Bullet> mBullets;
    private ArrayList<Explosion> mMiniExplosions;
    private ArrayList<NPC> mNPCs;
    private long mSmokeStartTime;
    private long mEnemyStartTime;
    private long mMissileStartTime;
    private long mGoldsStartTime;
    private long mItemsStartTime;

    private Controller fire_controller;

    private int Result;

    private int NumOfCoins;
    private int NumOfItems;
    private int NunOfNPC;
    private int score;
    private int availableSingleBullets;
    private int availableMultiBullets;

    private boolean newGameCreated;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean started;
    private boolean destroyNPC;

    private Random rd = new Random();

    public GamePanel(Context context) {
        super(context);
        //ad the callback method to surfaceholder to intercept the event
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mLevel = 1;

        //init the mBackground
        mBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg01));

        //setting Up the controller
        fire_controller = new Controller(30, HEIGHT - 150, 80, 80, 1);
        fire_controller.initImage(new BitmapFactory().decodeResource(getResources(), R.drawable.target4), 0, 0, 80, 80);

        //setting Up for mPlayer
        mPlayer = new Player(65, 25);
        mPlayer.initSprites(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 3);
        mPlayer.setX(100);
        mPlayer.setY(GamePanel.HEIGHT / 2);


        //init the result
        Result = 0;
        availableSingleBullets = 0;
        availableMultiBullets = 0;

        //setting Up for mEnemy
        mEnemy = new Enemy(138, 86);
        Bitmap[] images = new Bitmap[4];
        Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.bee1);
        images[0] = Bitmap.createBitmap(res, 0, 0, mEnemy.getWidth(), mEnemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee2);
        images[1] = Bitmap.createBitmap(res, 0, 0, mEnemy.getWidth(), mEnemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee3);
        images[2] = Bitmap.createBitmap(res, 0, 0, mEnemy.getWidth(), mEnemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee4);
        images[3] = Bitmap.createBitmap(res, 0, 0, mEnemy.getWidth(), mEnemy.getHeight());
        mEnemy.initSprites(images);
        mEnemy.setX(GamePanel.WIDTH - mEnemy.getWidth() - 10);
        mEnemy.setY(GamePanel.HEIGHT / 2);
        mEnemy.setPlaying(true);
        mEnemy.setDisappear(true);
        if(mLevel == 1){
            //init number of mGolds
            NumOfCoins = 5;
        }
        if(mLevel == 2) {
            mEnemy.setHealthPoints(4);
            mEnemy.setHealthPointsPool(4);
            //init number of mGolds
            NumOfCoins = 5;
            NumOfItems = 3;
        }
        if(mLevel == 3) {
            mEnemy.setHealthPoints(5);
            mEnemy.setHealthPointsPool(5);
            //init number of mGolds
            NumOfCoins = 7;
            NumOfItems = 5;
            NunOfNPC = 5;
        }
        mSmokes = new ArrayList<SmokePuff>();
        mMissiles = new ArrayList<Missile>();
        mEnemyMissiles = new ArrayList<SpecialMissile>();
        mGolds = new ArrayList<Gold>();
        mItems = new ArrayList<Item>();
        mBullets = new ArrayList<Bullet>();
        mMiniExplosions = new ArrayList<Explosion>();
        mNPCs = new ArrayList<NPC>();
        mMissileStartTime = System.nanoTime();
        mSmokeStartTime = System.nanoTime();
        mEnemyStartTime = System.nanoTime();
        mGoldsStartTime = System.nanoTime();
        mItemsStartTime = System.nanoTime();

        started = false;
        destroyNPC = false;

        mThread = new MainThread(getHolder(), this);
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000){
            try {
                counter++;
                mThread.setRunning(false);
                mThread.join();
                retry = false;
                mThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDefaultWidth(){
        return WIDTH;
    }
    public int getDefaultHeight(){
        return HEIGHT;
    }

    public void newMissile(int x, int y, int w, int h, int score, int frames, Bitmap res){
        Missile bullet = new Missile(x, y, w, h);
        bullet.initSprites(score, frames, res);
        mMissiles.add(bullet);
    }

    public void newBullet(int x, int y, int w, int h, int score, int frames, Bitmap res){
        Bullet bullet = new Bullet(x, y, w, h, true, true);
        bullet.initSprites(score, frames, res);
        mBullets.add(bullet);
    }

    public void newGold(int x, int y, int w, int h, int score, int frames, Bitmap res){
        Gold coin = new Gold(x, y, w, h);
        coin.initSprites(score, frames, res);
        int count = 0;
        for(Missile bull: mMissiles){
            if(this.collision(bull, coin)){
                count++;
            }
        }
        if(count == 0){
            mGolds.add(coin);
            NumOfCoins--;
            mGoldsStartTime = System.nanoTime();
        }
    }

    public void newNPC(int x, int y, int w, int h, int speed, int frames, Bitmap res){
        NPC npc = new NPC(w, h, y, x);
        npc.initSprites(res, frames);
        npc.setDisappear(false);
        npc.setSpeed(speed);
        mNPCs.add(npc);
    }

    public void newSpecialMissile(int x, int y, int w, int h, String kind, int speed, int score, int frames, Bitmap res){
        SpecialMissile triple_bullet = new SpecialMissile(x, y, w, h, kind, speed);
        triple_bullet.initSprites(score, frames, res);
        mEnemyMissiles.add(triple_bullet);
    }

    public NPC newNPC(int w, int h, int x, int y, int speed, Bitmap res, int frames){
        NPC npc = new NPC(w, h, x, y);
        npc.initSprites(res, 8);
        npc.setSpeed(speed);
        npc.setDisappear(false);
        return npc;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float scaleFactorX = getWidth() / (WIDTH*1.f);
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if(action == MotionEvent.ACTION_DOWN){
            if(mEnemy.isPlaying() && fire_controller.getX() < (event.getX()/scaleFactorX) &&
                    fire_controller.getX() + fire_controller.getWidth() > (event.getX()/scaleFactorX)
                    && fire_controller.getY() < (event.getY()/scaleFactorY)
                    && fire_controller.getY() + fire_controller.getHeight() > (event.getY()/scaleFactorY)){
                int x = mPlayer.getX(); int y = mPlayer.getY();
                int w = mPlayer.getWidth(); int h = mPlayer.getHeight();
                int b_width = 31; int b_height = 10;
                if(availableMultiBullets > 0){
                    newBullet(x + w, mPlayer.getY()+ mPlayer.getHeight()/2, b_width, b_height,
                            mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    newBullet(x + w/2, (mPlayer.getY()+ mPlayer.getHeight()/2) - b_height - 5, b_width, b_height,
                            mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    newBullet(x + w/2, (mPlayer.getY()+ mPlayer.getHeight()/2) + b_height + 5, b_width, b_height,
                            mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    availableMultiBullets--;
                    score -= 3;
                    if(availableMultiBullets == 0) mPlayer.setStatus(1);
                }
                else{
                    if(score > 0){
                        newBullet(x + w/2, mPlayer.getY()+ mPlayer.getHeight()/2, b_width, b_height,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                        score--;
                        availableSingleBullets--;
                    }
                }
            }
            if(!mPlayer.isPlaying()&&newGameCreated&&reset){
                mPlayer.setPlaying(true);
                mEnemy.setPlaying(true);
                mPlayer.setUp(true);
            }
            if(mPlayer.isPlaying()){
                reset = false;
                mPlayer.setUp(true);
            }
            return true;
        }
        if(action == MotionEvent.ACTION_UP){
            mPlayer.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update(){
        switch(mLevel){
            case 1:{
                levelOne();
                break;
            }
            case 2:{
                levelTwo();
                break;
            }
            case 3:{
                levelThree();
                break;
            }
        }
    }

    public void levelOne(){
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if (mPlayer.isPlaying() && mEnemy.isPlaying()) {
            mBackground.update();
            mPlayer.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()- mMissileStartTime)/1000000;
            if(missilesEslapsed > (1000 - mPlayer.getScore()/2) && NumOfCoins > 0){
                //first missile alway goes down the middle
                if(mMissiles.size()==0)
                    newMissile(WIDTH+10, HEIGHT/2, 45, 15, mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                else
                    newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15, mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                //reset timer
                mMissileStartTime = System.nanoTime();
                if((System.nanoTime() - mGoldsStartTime)/1000000 > 10000){
                    newGold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32,
                            mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                }
            }


            //update the mBullets
            if(mEnemy.isPlaying()){
                for(Bullet bullet: mBullets){
                    bullet.update();
                    if(bullet.getX() > WIDTH) bullet.setLive(false);
                }
            }

            if(mPlayer.getY()+ mPlayer.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                mPlayer.setPlaying(false);
                Result = -1;
            }

            //check for collision between all of mMissiles and mPlayer
            for(Missile ms: mMissiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, mPlayer)){
                        mMissiles.remove(ms);
                        if(!started) started = true;
                        mPlayer.setPlaying(false);
                        Result = -1;
                        break;
                    }
                    if(ms.getX() <-100){
                        mMissiles.remove(ms);
                        break;
                    }
                }
            }

            //when mGolds and mMissiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                mEnemy.setPlaying(true);
                mEnemy.setDisappear(false);
                //update mEnemy position on timer
                long enemy_eslapsed = (System.nanoTime() - mEnemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && mEnemy.isPlaying()){
                    mEnemy.update(this);
                    if(rd.nextInt(50) == 1){
                        newMissile(mEnemy.getX()+5, mEnemy.getY()+(mEnemy.getHeight()/2) - 8, 45, 15,
                                mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                    }
                }

                if(score == 0&& mGolds.size() == 0&& mBullets.size() == 0&&!mEnemy.isDisappear()&& mEnemy.isPlaying()){
                    mPlayer.setPlaying(false);
                    Result = -1;
                }

                for(Bullet bullet: mBullets){
                    if(collision(bullet, mEnemy)){
                        mEnemy.setPlaying(false);
                        started = true;
                        break;
                    }
                    if(bullet.getX() > WIDTH)
                        bullet.setLive(false);
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - mSmokeStartTime)/1000000;
            if(eslapsed > 120){
                mSmokes.add(new SmokePuff(mPlayer.getX(), mPlayer.getY() + 10));
                mSmokeStartTime = System.nanoTime();
            }
            for(int i=0; i< mSmokes.size(); i++){
                mSmokes.get(i).update();
                if(mSmokes.get(i).getX() < -10) mSmokes.remove(i);
            }
        }
        else{ //the mPlayer has destroyed mEnemy plane or has been destroyed
            mPlayer.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!mPlayer.isPlaying()){
                    mPlayer.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                            mPlayer.getY() - 30, 100, 100, 25);
                    for(Gold coin: mGolds){
                        coin.setSpeed(0);
                    }
                }
                if(!mEnemy.isPlaying()){
                    mEnemy.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mEnemy.getX(),
                            mEnemy.getY() - 30, 100, 100, 25);
                    mPlayer.setPlaying(false);
                    Result = 1;
                }
                mGolds.clear();
                score = 0;
            }
            explosion.update();
            long resetEslapsed = (System.nanoTime() - startReset)/1000000;
            if(resetEslapsed > 2500 && !newGameCreated){
                newGame();
            }
        }
    }

    public void levelTwo(){
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if (mPlayer.isPlaying() && mEnemy.isPlaying()) {
            mBackground.update();
            mPlayer.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()- mMissileStartTime)/1000000;
            if(missilesEslapsed > (1000 - mPlayer.getScore()/2) && (NumOfCoins > 0 || NumOfItems > 0)){
                //first missile alway goes down the middle
                if(mMissiles.size()==0) newMissile(WIDTH+10, HEIGHT/2, 45, 15, mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                else newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15,
                        mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));

                //reset timer
                mMissileStartTime = System.nanoTime();
                if((System.nanoTime() - mGoldsStartTime)/1000000 > 10000 && NumOfCoins > 0)
                    newGold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32,
                            mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                if((System.nanoTime() - mItemsStartTime)/1000000 > 15000 && NumOfItems > 0){
                    Item item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 30, 39, 3);
                    item.initSprites(mPlayer.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.triple2));
                    int count = 0;
                    for(Missile bull: mMissiles){
                        if(this.collision(bull, item)){
                            count++;
                        }
                    }
                    for(Gold coin: mGolds){
                        if(this.collision(coin, item)){
                            count++;
                        }
                    }
                    if(count == 0){
                        mItems.add(item);
                        NumOfItems--;
                        mItemsStartTime = System.nanoTime();
                    }
                }
            }

            //update the mBullets
            if(mEnemy.isPlaying()){
                for(Bullet bullet: mBullets){
                    bullet.update();
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }
            }

            if(mPlayer.getY()+ mPlayer.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                mPlayer.setPlaying(false);
                Result = -1;
            }

            for (Explosion e: mMiniExplosions){
                e.update();
            }

            //check for collision between all of mMissiles and mPlayer
            for(Missile ms: mMissiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, mPlayer)){
                        mMissiles.remove(ms);
                        if(!started) started = true;
                        mPlayer.setPlaying(false);
                        Result = -1;
                        break;
                    }
                    if(ms.getX() <-100){
                        mMissiles.remove(ms);
                        break;
                    }
                }
            }

            for(SpecialMissile spm: mEnemyMissiles){
                if(collision(spm, mPlayer)){
                    mEnemyMissiles.remove(spm);
                    if(!started) started = true;
                    mPlayer.setPlaying(false);
                    Result = -1;
                    break;
                }
                if(spm.getX() <-100){
                    mEnemyMissiles.remove(spm);
                    break;
                }
            }

            //when mGolds and mMissiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                mEnemy.setPlaying(true);
                mEnemy.setDisappear(false);
                //update mEnemy position on timer
                long enemy_eslapsed = (System.nanoTime() - mEnemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && mEnemy.isPlaying()){
                    mEnemy.update(this);
                    if(rd.nextInt(50) == 1) newMissile(mEnemy.getX()+5, mEnemy.getY()+(mEnemy.getHeight()/2) - 8, 45, 15,
                            mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                    if(rd.nextInt(60) == 1){
                        int x = mEnemy.getX(); int y = mEnemy.getY();
                        int ene_h = mEnemy.getHeight();
                        int w = 32; int h = 32;
                        int speed = rd.nextInt(10) + 10;
                        newSpecialMissile(x, y + ene_h/2, w, h, "Normal", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Top_Left", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Bot_Left", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                    }
                }

                if(score == 0&& mGolds.size() == 0&& mBullets.size() == 0&&!mEnemy.isDisappear()&& mEnemy.isPlaying()){
                    mPlayer.setPlaying(false);
                    Result = -1;
                }

                for(Bullet bullet: mBullets){
                    if(collision(bullet, mEnemy)){
                        if(mEnemy.getHealthPoints() == 0){
                            mEnemy.setPlaying(false);
                            started = true;
                        }
                        else {
                            if(bullet.isLive()){
                                mEnemy.setHealthPoints(mEnemy.getHealthPoints() - 1);
                                bullet.setLive(false);
                            }
                            Explosion e = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.miniexplosion), bullet.getX(),
                                    bullet.getY(), 50, 50, 25);
                            mMiniExplosions.add(e);
                        }
                        break;
                    }
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }

                for (SpecialMissile spm: mEnemyMissiles){
                    if(spm.getNew() == 0) spm.setNew(1);
                    else spm.update();
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - mSmokeStartTime)/1000000;
            if(eslapsed > 120){
                mSmokes.add(new SmokePuff(mPlayer.getX(), mPlayer.getY() + 10));
                mSmokeStartTime = System.nanoTime();
            }
            for(int i=0; i< mSmokes.size(); i++){
                mSmokes.get(i).update();
                if(mSmokes.get(i).getX() < -10) mSmokes.remove(i);
            }
        }
        else{ //the mPlayer has destroyed mEnemy plane or has been destroyed
            mPlayer.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!mPlayer.isPlaying()){
                    mPlayer.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                            mPlayer.getY() - 30, 100, 100, 25);
                    for(Gold coin: mGolds){
                        coin.setSpeed(0);
                    }
                    for(Item item: mItems){
                        item.setSpeed(0);
                    }
                }
                if(!mEnemy.isPlaying()){
                    mEnemy.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mEnemy.getX(),
                            mEnemy.getY() - 30, 100, 100, 25);
                    mPlayer.setPlaying(false);
                    Result = 1;
                }
                mGolds.clear();
                score = 0;
            }
            explosion.update();
            long resetEslapsed = (System.nanoTime() - startReset)/1000000;
            if(resetEslapsed > 2500 && !newGameCreated){
                newGame();
            }
        }
    }

    public void levelThree(){
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if (mPlayer.isPlaying() && mEnemy.isPlaying()) {
            mBackground.update();
            mPlayer.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()- mMissileStartTime)/1000000;
            if(missilesEslapsed > (1000 - mPlayer.getScore()/2) && (NumOfCoins > 0 || NumOfItems > 0)){
                //first missile alway goes down the middle
                if(mMissiles.size()==0){
                    newMissile(WIDTH+10, HEIGHT/2, 45, 15, mPlayer.getScore(),
                            13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));

                }else{
                    newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15, mPlayer.getScore(),
                            13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                }
                if(rd.nextInt(5)==1&& NunOfNPC > 0){
                    boolean flag = false;
                    for (NPC n: mNPCs){
                        if(!n.isDisappear()) flag = true;
                    }
                    if(!flag){
                        int npcWidth = 64, npcHeight = 64;
                        int x = WIDTH-64;
                        int npcSpeed = 15 + rd.nextInt(20);
                        int num = rd.nextInt(10);
                        if(num == 0||num == 2){
                            int y = rd.nextInt(HEIGHT - (npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            mNPCs.add(newNPC(npcWidth, npcHeight, x, y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC--;
                        }
                        if(num == 1||num == 3){
                            int y = rd.nextInt(HEIGHT - (2*npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            mNPCs.add(newNPC(npcWidth, npcHeight, x, y - npcHeight - 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            mNPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth / 2), y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC-=2;
                        }
                        if(num == 7){
                            int y = rd.nextInt(HEIGHT - (3*npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            mNPCs.add(newNPC(npcWidth, npcHeight, x, y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            mNPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth / 2), y + npcHeight + 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            mNPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth / 2), y - npcHeight - 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC-=3;
                        }
                    }
                }

                //reset timer
                mMissileStartTime = System.nanoTime();
                if((System.nanoTime() - mGoldsStartTime)/1000000 > 10000 && NumOfCoins > 0){
                    Gold coin = new Gold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32);
                    coin.initSprites(mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                    int count = 0;
                    for(Missile bull: mMissiles){
                        if(this.collision(bull, coin)){
                            count++;
                        }
                    }
                    if(count == 0){
                        mGolds.add(coin);
                        NumOfCoins--;
                        mGoldsStartTime = System.nanoTime();
                    }
                }
                if((System.nanoTime() - mItemsStartTime)/1000000 > 15000 && NumOfItems > 0){
                    int KindOfItem = 3;
                    if(rd.nextInt(3)==2) KindOfItem = 2;
                    Item item;
                    if(KindOfItem == 3){
                        item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 30, 39, KindOfItem);
                        item.initSprites(mPlayer.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.triple2));
                    }
                    else{
                        item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32, KindOfItem);
                        item.initSprites(mPlayer.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.item));
                    }
                    int count = 0;
                    for(Missile bull: mMissiles){
                        if(this.collision(bull, item)){
                            count++;
                        }
                    }
                    for(Gold coin: mGolds){
                        if(this.collision(coin, item)){
                            count++;
                        }
                    }
                    if(count == 0){
                        mItems.add(item);
                        NumOfItems--;
                        mItemsStartTime = System.nanoTime();
                    }
                }
            }

            for(Bullet bullet: mBullets){
                for (NPC n: mNPCs){
                    if(collision(n, bullet)&&!n.isDisappear()){
                        n.setDisappear(true);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), n.getX(),
                                n.getY() - 30, 100, 100, 25);
                        bullet.setLive(false);
                        explosion.setMove(true);
                        mMiniExplosions.add(explosion);
                    }
                }
            }

            //update the mBullets
            if(mEnemy.isPlaying()){
                for(Bullet bullet: mBullets){
                    bullet.update();
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }
            }

            if(mPlayer.getY()+ mPlayer.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                mPlayer.setPlaying(false);
                mPlayer.setLife(1);
                Result = -1;
            }

            for (Explosion e: mMiniExplosions){
                e.update();
            }

            for (NPC n: mNPCs){
                if(!n.isDisappear()) n.update(this);
                if(n.getX() < -n.getWidth()) n.setDisappear(true);
                if(collision(n, mPlayer)&&!n.isDisappear()){
                    n.setDisappear(true);
                    if(mPlayer.getLife() <= 1){
                        if(!started) started = true;
                        mPlayer.setPlaying(false);
                        Result = -1;
                    }
                    else {
                        mPlayer.setLife(mPlayer.getLife() - 1);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                                mPlayer.getY() - 30, 100, 100, 25);
                        explosion.setMove(true);
                        mMiniExplosions.add(explosion);
                    }
                }
            }

            //check for collision between all of mMissiles and mPlayer
            for(Missile ms: mMissiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, mPlayer)){
                        ms.setLive(false);
                        if(mPlayer.getLife() <= 1){
                            if(!started) started = true;
                            mPlayer.setPlaying(false);
                            Result = -1;
                        }
                        else {
                            mPlayer.setLife(mPlayer.getLife() - 1);
                            explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                                    mPlayer.getY() - 30, 100, 100, 25);
                            explosion.setMove(true);
                            mMiniExplosions.add(explosion);
                        }
                    }
                    if(ms.getX() <-100){
                        ms.setLive(false);
                        break;
                    }
                }
            }

            for(SpecialMissile spm: mEnemyMissiles){
                if(collision(spm, mPlayer)&&spm.isLive()){
                    spm.setLive(false);
                    if(mPlayer.getLife() <= 1){
                        if(!started) started = true;
                        mPlayer.setPlaying(false);
                        Result = -1;
                    }
                    else {
                        mPlayer.setLife(mPlayer.getLife() - 1);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                                mPlayer.getY() - 30, 100, 100, 25);
                        explosion.setMove(true);
                        mMiniExplosions.add(explosion);
                    }
                }
                if(spm.getX() <-100){
                    spm.setLive(false);
                }
            }

            //when mGolds and mMissiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                mEnemy.setPlaying(true);
                mEnemy.setDisappear(false);
                //update mEnemy position on timer
                long enemy_eslapsed = (System.nanoTime() - mEnemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && mEnemy.isPlaying()){
                    mEnemy.update(this);
                    if(rd.nextInt(50) == 1){
                        Missile bullet = new Missile(mEnemy.getX()+5, mEnemy.getY()+(mEnemy.getHeight()/2) - 8, 45, 15);
                        bullet.initSprites(mPlayer.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                        bullet.setSpeed(10 + rd.nextInt(10));
                        mMissiles.add(bullet);
                    }
                    if(rd.nextInt(60) == 1){
                        int x = mEnemy.getX(); int y = mEnemy.getY();
                        int ene_h = mEnemy.getHeight();
                        int w = 32; int h = 32;
                        int speed = rd.nextInt(10) + 15;
                        newSpecialMissile(x, y + ene_h/2, w, h, "Normal", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Top_Left", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Bot_Left", speed,
                                mPlayer.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                    }
                }

                if(score == 0&& mGolds.size() == 0&& mBullets.size() == 0&&!mEnemy.isDisappear()&& mEnemy.isPlaying()){
                    mPlayer.setPlaying(false);
                    mPlayer.setLife(1);
                    Result = -1;
                }

                for(Bullet bullet: mBullets){
                    if(collision(bullet, mEnemy)){
                        if(mEnemy.getHealthPoints() == 0){
                            mEnemy.setPlaying(false);
                            started = true;
                        }
                        else {
                            if(bullet.isLive()){
                                mEnemy.setHealthPoints(mEnemy.getHealthPoints() - 1);
                                bullet.setLive(false);
                            }
                            Explosion e = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.miniexplosion), bullet.getX(),
                                    bullet.getY(), 50, 50, 25);
                            mMiniExplosions.add(e);
                        }
                        break;
                    }
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }

                for (SpecialMissile spm: mEnemyMissiles){
                    if(spm.getNew() == 0) spm.setNew(1);
                    else spm.update();
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - mSmokeStartTime)/1000000;
            if(eslapsed > 120){
                mSmokes.add(new SmokePuff(mPlayer.getX(), mPlayer.getY() + 10));
                mSmokeStartTime = System.nanoTime();
            }
            for(int i=0; i< mSmokes.size(); i++){
                mSmokes.get(i).update();
                if(mSmokes.get(i).getX() < -10) mSmokes.remove(i);
            }
        }
        else{ //the mPlayer has destroyed mEnemy plane or has been destroyed
            mPlayer.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!mPlayer.isPlaying()){
                    mPlayer.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mPlayer.getX(),
                            mPlayer.getY() - 30, 100, 100, 25);
                    for(Gold coin: mGolds){
                        coin.setSpeed(0);
                    }
                    for(Item item: mItems){
                        item.setSpeed(0);
                    }
                }
                if(!mEnemy.isPlaying()){
                    mEnemy.setDisappear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), mEnemy.getX(),
                            mEnemy.getY() - 30, 100, 100, 25);
                    mPlayer.setPlaying(false);
                    Result = 1;
                }
                mGolds.clear();
                score = 0;
            }
            explosion.update();
            long resetEslapsed = (System.nanoTime() - startReset)/1000000;
            if(resetEslapsed > 2500 && !newGameCreated){
                newGame();
            }
        }
    }

    public void showCoinValue(Canvas canvas, String value){
        Paint paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setTextSize(30);
        paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("+"+value, mPlayer.getX() + mPlayer.getWidth(), mPlayer.getY(), paint2);
    }

    public boolean collision(GameObject a, GameObject b){
        if(Rect.intersects(a.getRect(), b.getRect())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth() / (WIDTH*1.f);
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if (canvas != null){
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            mBackground.draw(canvas);
            drawText(canvas);
            for(Missile missile: mMissiles){
                if(missile.isLive()) missile.draw(canvas);
            }
            for(Gold gold: mGolds){
                gold.update();
                gold.draw(canvas);
                if(collision(gold, mPlayer)){
                    score++;
                    availableSingleBullets++;
                    mGolds.remove(gold);
                    if(mPlayer.getStatus() == 0) mPlayer.setStatus(1);
                    showCoinValue(canvas, "1");
                    break;
                }
                if(gold.getX() < 0){
                    mGolds.remove(gold);
                    break;
                }
            }
            for(Item item: mItems){
                item.update();
                item.draw(canvas);
                if(collision(item, mPlayer)){
                    if(item.getKind() != 2){
                        availableMultiBullets++;
                        score+=item.getKind();
                        mPlayer.setStatus(item.getKind());
                        showCoinValue(canvas, String.valueOf(item.getKind()));
                    }
                    else{
                        mPlayer.setLife(mPlayer.getLife() + 1);
                        showCoinValue(canvas, "1 life");
                    }
                    mItems.remove(item);
                    break;
                }
                if(item.getX() < 0){
                    mItems.remove(item);
                    break;
                }
            }
            fire_controller.draw(canvas);
            if(!mPlayer.isDisappear()){
                mPlayer.draw(canvas);
                for(SmokePuff sp: mSmokes){
                    sp.draw(canvas);
                }
                //draw NPC
                for (NPC n: mNPCs){
                    if(!n.isDisappear()){
                        n.draw(canvas);
                    }
                    else mNPCs.remove(n);
                }

                if(mPlayer.isPlaying() && mEnemy.isPlaying() && !mEnemy.isDisappear()){
                    if(mLevel > 1){
                        Paint paint = new Paint();
                        int x = mEnemy.getX()+10; int y = mEnemy.getY();
                        int w = mEnemy.getHealthPoints()*40; int h = 10;
                        if(mEnemy.getPecentagesOfHP() > 0.5)
                            paint.setColor(Color.GREEN);
                        else{
                            if(mEnemy.getPecentagesOfHP() >= 0.3)
                                paint.setColor(Color.YELLOW);
                            else paint.setColor(Color.RED);
                        }
                        paint.setStrokeWidth(1);
                        canvas.drawRect(x - 2, (y - h * 2) - 2, x + w, y - h, paint);
                    }
                    mEnemy.setPlaying(true);
                    mEnemy.draw(canvas);
                }
            }
            //draw the mBullets
            if(mEnemy.isPlaying()){
                for(Bullet bullet: mBullets){
                    if(bullet.isLive()) bullet.draw(canvas);
                    else mBullets.remove(bullet);
                }
                for (SpecialMissile spm: mEnemyMissiles){
                    if(spm.isLive()) spm.draw(canvas);
                }
            }
            if(started){
                explosion.draw(canvas);
            }
            for(Explosion e: mMiniExplosions){
                if(!e.isDisappear()) e.draw(canvas);
            }
            canvas.restoreToCount(savedState);
        }
    }


    public void newGame(){
        mEnemyMissiles.clear(); mItems.clear(); mMissiles.clear(); mSmokes.clear();
        mBullets.clear(); mMiniExplosions.clear(); mNPCs.clear();
        mPlayer.setDisappear(false); mEnemy.setDisappear(true);
        mPlayer.resetScore(); mPlayer.setY(HEIGHT / 2); mPlayer.resetDY();
        if(Result == 1){
            mLevel++;
            if(mLevel > 3) mLevel = 3;
        }
        switch(mLevel){
            case 1:{
                NumOfCoins = 5;
                mEnemy.setHealthPoints(4);
                mEnemy.setHealthPointsPool(4);
                break;
            }
            case 2:{
                NumOfCoins = 5;
                NumOfItems = 3;
                break;
            }
            case 3:{
                NumOfCoins = 7;
                NumOfItems = 5;
                NunOfNPC = 5;
                mEnemy.setHealthPoints(5);
                mEnemy.setHealthPointsPool(5);
                break;
            }
        }
        Result = 0;
        availableSingleBullets = 0;
        availableMultiBullets = 0;
        newGameCreated = true;
        started = false;
    }

    public void drawText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if(mPlayer.getStatus() > 1){
            canvas.drawText("CURRENT BULLET: MULTIPLE", 150, HEIGHT - 10, paint);
            canvas.drawText("Number: " + availableMultiBullets, WIDTH - 215, HEIGHT - 10, paint);
        }
        else{
            if(mPlayer.getStatus() == 1){
                canvas.drawText("CURRENT BULLET: SINGLE", 150, HEIGHT - 10, paint);
                canvas.drawText("Number: " + availableSingleBullets, WIDTH - 215, HEIGHT - 10, paint);
            }
            else{
                canvas.drawText("NO BULLET!", 150, HEIGHT - 10, paint);
            }
        }

        if(Result == -1) canvas.drawText("You lose", (WIDTH/2) - 10, (HEIGHT/2) - 10, paint);
        if(Result == 1) canvas.drawText("You win", (WIDTH/2) - 10, (HEIGHT/2) - 10, paint);

        paint.setTextSize(30);
        paint.setColor(Color.BLUE);
        canvas.drawText("Level: "+ mLevel, 30, 50, paint);

        if(!mPlayer.isPlaying()&&newGameCreated&&reset){
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("TOUCH TO START!", WIDTH/2 - 50, HEIGHT/2, paint);

            paint.setTextSize(20);
            canvas.drawText("TOUCH AND HOLD TO GO UP!", WIDTH/2 - 50, HEIGHT/2 + 30, paint);
            canvas.drawText("RELEASE TO GO DOWN!", WIDTH/2 - 50, HEIGHT/2 + 50, paint);
        }
    }

}
