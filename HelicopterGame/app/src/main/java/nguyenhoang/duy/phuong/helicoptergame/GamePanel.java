package nguyenhoang.duy.phuong.helicoptergame;

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

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;


    private MainThread thread;
    private int level;
    private Background background;
    private Player player;
    private Enemy enemy;
    private ArrayList<Smokepuff> smokes;
    private ArrayList<Missile> missiles;
    private ArrayList<SpecialMissile> enemymissiles;
    private ArrayList<Gold> coins;
    private ArrayList<Item> items;
    private ArrayList<Bullet> bullets;
    private ArrayList<Explosion> miniExplosions;
    private ArrayList<NPC> NPCs;
    private long smokeStartTime;
    private long enemyStartTime;
    private long missileStartTime;
    private long coinsStartTime;
    private long itemsStartTime;
    private long bulletsStartTime;

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
        level = 1;

        //init the background
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg01));

        //setting up the controller
        fire_controller = new Controller(30, HEIGHT - 150, 80, 80, 1);
        fire_controller.initImage(new BitmapFactory().decodeResource(getResources(), R.drawable.target4), 0, 0, 80, 80);

        //setting up for player
        player = new Player(65, 25);
        player.initSprites(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 3);
        player.setX(100);
        player.setY(GamePanel.HEIGHT/2);


        //init the result
        Result = 0;
        availableSingleBullets = 0;
        availableMultiBullets = 0;

        //setting up for enemy
        enemy = new Enemy(138, 86);
        Bitmap[] images = new Bitmap[4];
        Bitmap res = BitmapFactory.decodeResource(getResources(), R.drawable.bee1);
        images[0] = Bitmap.createBitmap(res, 0, 0, enemy.getWidth(), enemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee2);
        images[1] = Bitmap.createBitmap(res, 0, 0, enemy.getWidth(), enemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee3);
        images[2] = Bitmap.createBitmap(res, 0, 0, enemy.getWidth(), enemy.getHeight());
        res = BitmapFactory.decodeResource(getResources(), R.drawable.bee4);
        images[3] = Bitmap.createBitmap(res, 0, 0, enemy.getWidth(), enemy.getHeight());
        enemy.initSprites(images);
        enemy.setX(GamePanel.WIDTH - enemy.getWidth() - 10);
        enemy.setY(GamePanel.HEIGHT / 2);
        enemy.setPlaying(true);
        enemy.setDissapear(true);
        if(level == 1){
            //init number of coins
            NumOfCoins = 5;
        }
        if(level == 2) {
            enemy.setHealthPoints(4);
            enemy.setHealthPointsPool(4);
            //init number of coins
            NumOfCoins = 5;
            NumOfItems = 3;
        }
        if(level == 3) {
            enemy.setHealthPoints(5);
            enemy.setHealthPointsPool(5);
            //init number of coins
            NumOfCoins = 7;
            NumOfItems = 5;
            NunOfNPC = 5;
        }
        smokes = new ArrayList<Smokepuff>();
        missiles = new ArrayList<Missile>();
        enemymissiles = new ArrayList<SpecialMissile>();
        coins = new ArrayList<Gold>();
        items = new ArrayList<Item>();
        bullets = new ArrayList<Bullet>();
        miniExplosions = new ArrayList<Explosion>();
        NPCs = new ArrayList<NPC>();
        missileStartTime = System.nanoTime();
        smokeStartTime = System.nanoTime();
        enemyStartTime = System.nanoTime();
        coinsStartTime = System.nanoTime();
        itemsStartTime = System.nanoTime();
        bulletsStartTime = System.nanoTime();

        started = false;
        destroyNPC = false;

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
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
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
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
        missiles.add(bullet);
    }

    public void newBullet(int x, int y, int w, int h, int score, int frames, Bitmap res){
        Bullet bullet = new Bullet(x, y, w, h, true, true);
        bullet.initSprites(score, frames, res);
        bullets.add(bullet);
    }

    public void newGold(int x, int y, int w, int h, int score, int frames, Bitmap res){
        Gold coin = new Gold(x, y, w, h);
        coin.initSprites(score, frames, res);
        int count = 0;
        for(Missile bull:missiles){
            if(this.collision(bull, coin)){
                count++;
            }
        }
        if(count == 0){
            coins.add(coin);
            NumOfCoins--;
            coinsStartTime = System.nanoTime();
        }
    }

    public void newNPC(int x, int y, int w, int h, int speed, int frames, Bitmap res){
        NPC npc = new NPC(w, h, y, x);
        npc.initSprites(res, frames);
        npc.setDissapear(false);
        npc.setSpeed(speed);
        NPCs.add(npc);
    }

    public void newSpecialMissile(int x, int y, int w, int h, String kind, int speed, int score, int frames, Bitmap res){
        SpecialMissile triple_bullet = new SpecialMissile(x, y, w, h, kind, speed);
        triple_bullet.initSprites(score, frames, res);
        enemymissiles.add(triple_bullet);
    }

    public NPC newNPC(int w, int h, int x, int y, int speed, Bitmap res, int frames){
        NPC npc = new NPC(w, h, x, y);
        npc.initSprites(res, 8);
        npc.setSpeed(speed);
        npc.setDissapear(false);
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
            if(enemy.isPlaying() && fire_controller.getX() < (event.getX()/scaleFactorX) &&
                    fire_controller.getX() + fire_controller.getWidth() > (event.getX()/scaleFactorX)
                    && fire_controller.getY() < (event.getY()/scaleFactorY)
                    && fire_controller.getY() + fire_controller.getHeight() > (event.getY()/scaleFactorY)){
                int x = player.getX(); int y = player.getY();
                int w = player.getWidth(); int h = player.getHeight();
                int b_width = 31; int b_height = 10;
                if(availableMultiBullets > 0){
                    newBullet(x + w, player.getY()+player.getHeight()/2, b_width, b_height,
                            player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    newBullet(x + w/2, (player.getY()+player.getHeight()/2) - b_height - 5, b_width, b_height,
                            player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    newBullet(x + w/2, (player.getY()+player.getHeight()/2) + b_height + 5, b_width, b_height,
                            player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                    availableMultiBullets--;
                    score -= 3;
                    if(availableMultiBullets == 0) player.setStatus(1);
                }
                else{
                    if(score > 0){
                        newBullet(x + w/2, player.getY()+player.getHeight()/2, b_width, b_height,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.fireball));
                        score--;
                        availableSingleBullets--;
                    }
                }
            }
            if(!player.isPlaying()&&newGameCreated&&reset){
                player.setPlaying(true);
                enemy.setPlaying(true);
                player.setUp(true);
            }
            if(player.isPlaying()){
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(action == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update(){
        switch(level){
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
        if (player.isPlaying() && enemy.isPlaying()) {
            background.update();
            player.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missilesEslapsed > (1000 - player.getScore()/2) && NumOfCoins > 0){
                //first missile alway goes down the middle
                if(missiles.size()==0)
                    newMissile(WIDTH+10, HEIGHT/2, 45, 15, player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                else
                    newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15, player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                //reset timer
                missileStartTime = System.nanoTime();
                if((System.nanoTime() - coinsStartTime)/1000000 > 10000){
                    newGold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32,
                            player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                }
            }


            //update the bullets
            if(enemy.isPlaying()){
                for(Bullet bullet:bullets){
                    bullet.update();
                    if(bullet.getX() > WIDTH) bullet.setLive(false);
                }
            }

            if(player.getY()+player.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                player.setPlaying(false);
                Result = -1;
            }

            //check for collision between all of missiles and player
            for(Missile ms:missiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, player)){
                        missiles.remove(ms);
                        if(!started) started = true;
                        player.setPlaying(false);
                        Result = -1;
                        break;
                    }
                    if(ms.getX() <-100){
                        missiles.remove(ms);
                        break;
                    }
                }
            }

            //when coins and missiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                enemy.setPlaying(true);
                enemy.setDissapear(false);
                //update enemy position on timer
                long enemy_eslapsed = (System.nanoTime() - enemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && enemy.isPlaying()){
                    enemy.update(this);
                    if(rd.nextInt(50) == 1){
                        newMissile(enemy.getX()+5, enemy.getY()+(enemy.getHeight()/2) - 8, 45, 15,
                                player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                    }
                }

                if(score == 0&&coins.size() == 0&&bullets.size() == 0&&!enemy.isDissapear()&&enemy.isPlaying()){
                    player.setPlaying(false);
                    Result = -1;
                }

                for(Bullet bullet:bullets){
                    if(collision(bullet, enemy)){
                        enemy.setPlaying(false);
                        started = true;
                        break;
                    }
                    if(bullet.getX() > WIDTH)
                        bullet.setLive(false);
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(eslapsed > 120){
                smokes.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }
            for(int i=0; i<smokes.size(); i++){
                smokes.get(i).update();
                if(smokes.get(i).getX() < -10) smokes.remove(i);
            }
        }
        else{ //the player has destroyed enemy plane or has been destroyed
            player.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!player.isPlaying()){
                    player.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                            player.getY() - 30, 100, 100, 25);
                    for(Gold coin:coins){
                        coin.setSpeed(0);
                    }
                }
                if(!enemy.isPlaying()){
                    enemy.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), enemy.getX(),
                            enemy.getY() - 30, 100, 100, 25);
                    player.setPlaying(false);
                    Result = 1;
                }
                coins.clear();
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
        if (player.isPlaying() && enemy.isPlaying()) {
            background.update();
            player.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missilesEslapsed > (1000 - player.getScore()/2) && (NumOfCoins > 0 || NumOfItems > 0)){
                //first missile alway goes down the middle
                if(missiles.size()==0) newMissile(WIDTH+10, HEIGHT/2, 45, 15, player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                else newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15,
                        player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));

                //reset timer
                missileStartTime = System.nanoTime();
                if((System.nanoTime() - coinsStartTime)/1000000 > 10000 && NumOfCoins > 0)
                    newGold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32,
                            player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                if((System.nanoTime() - itemsStartTime)/1000000 > 15000 && NumOfItems > 0){
                    Item item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 30, 39, 3);
                    item.initSprites(player.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.triple2));
                    int count = 0;
                    for(Missile bull:missiles){
                        if(this.collision(bull, item)){
                            count++;
                        }
                    }
                    for(Gold coin:coins){
                        if(this.collision(coin, item)){
                            count++;
                        }
                    }
                    if(count == 0){
                        items.add(item);
                        NumOfItems--;
                        itemsStartTime = System.nanoTime();
                    }
                }
            }

            //update the bullets
            if(enemy.isPlaying()){
                for(Bullet bullet:bullets){
                    bullet.update();
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }
            }

            if(player.getY()+player.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                player.setPlaying(false);
                Result = -1;
            }

            for (Explosion e:miniExplosions){
                e.update();
            }

            //check for collision between all of missiles and player
            for(Missile ms:missiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, player)){
                        missiles.remove(ms);
                        if(!started) started = true;
                        player.setPlaying(false);
                        Result = -1;
                        break;
                    }
                    if(ms.getX() <-100){
                        missiles.remove(ms);
                        break;
                    }
                }
            }

            for(SpecialMissile spm:enemymissiles){
                if(collision(spm, player)){
                    enemymissiles.remove(spm);
                    if(!started) started = true;
                    player.setPlaying(false);
                    Result = -1;
                    break;
                }
                if(spm.getX() <-100){
                    enemymissiles.remove(spm);
                    break;
                }
            }

            //when coins and missiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                enemy.setPlaying(true);
                enemy.setDissapear(false);
                //update enemy position on timer
                long enemy_eslapsed = (System.nanoTime() - enemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && enemy.isPlaying()){
                    enemy.update(this);
                    if(rd.nextInt(50) == 1) newMissile(enemy.getX()+5, enemy.getY()+(enemy.getHeight()/2) - 8, 45, 15,
                            player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                    if(rd.nextInt(60) == 1){
                        int x = enemy.getX(); int y = enemy.getY();
                        int ene_h = enemy.getHeight();
                        int w = 32; int h = 32;
                        int speed = rd.nextInt(10) + 10;
                        newSpecialMissile(x, y + ene_h/2, w, h, "Normal", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Top_Left", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Bot_Left", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile));
                    }
                }

                if(score == 0&&coins.size() == 0&&bullets.size() == 0&&!enemy.isDissapear()&&enemy.isPlaying()){
                    player.setPlaying(false);
                    Result = -1;
                }

                for(Bullet bullet:bullets){
                    if(collision(bullet, enemy)){
                        if(enemy.getHealthPoints() == 0){
                            enemy.setPlaying(false);
                            started = true;
                        }
                        else {
                            if(bullet.isLive()){
                                enemy.setHealthPoints(enemy.getHealthPoints() - 1);
                                bullet.setLive(false);
                            }
                            Explosion e = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.miniexplosion), bullet.getX(),
                                    bullet.getY(), 50, 50, 25);
                            miniExplosions.add(e);
                        }
                        break;
                    }
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }

                for (SpecialMissile spm:enemymissiles){
                    if(spm.getNew() == 0) spm.setNew(1);
                    else spm.update();
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(eslapsed > 120){
                smokes.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }
            for(int i=0; i<smokes.size(); i++){
                smokes.get(i).update();
                if(smokes.get(i).getX() < -10) smokes.remove(i);
            }
        }
        else{ //the player has destroyed enemy plane or has been destroyed
            player.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!player.isPlaying()){
                    player.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                            player.getY() - 30, 100, 100, 25);
                    for(Gold coin:coins){
                        coin.setSpeed(0);
                    }
                    for(Item item:items){
                        item.setSpeed(0);
                    }
                }
                if(!enemy.isPlaying()){
                    enemy.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), enemy.getX(),
                            enemy.getY() - 30, 100, 100, 25);
                    player.setPlaying(false);
                    Result = 1;
                }
                coins.clear();
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
        if (player.isPlaying() && enemy.isPlaying()) {
            background.update();
            player.update(this);

            //add missile on timer
            long missilesEslapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missilesEslapsed > (1000 - player.getScore()/2) && (NumOfCoins > 0 || NumOfItems > 0)){
                //first missile alway goes down the middle
                if(missiles.size()==0){
                    newMissile(WIDTH+10, HEIGHT/2, 45, 15, player.getScore(),
                            13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));

                }else{
                    newMissile(WIDTH+10, rd.nextInt(HEIGHT - 90), 45, 15, player.getScore(),
                            13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                }
                if(rd.nextInt(5)==1&& NunOfNPC > 0){
                    boolean flag = false;
                    for (NPC n:NPCs){
                        if(!n.isDissapear()) flag = true;
                    }
                    if(!flag){
                        int npcWidth = 64, npcHeight = 64;
                        int x = WIDTH-64;
                        int npcSpeed = 15 + rd.nextInt(20);
                        int num = rd.nextInt(10);
                        if(num == 0||num == 2){
                            int y = rd.nextInt(HEIGHT - (npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            NPCs.add(newNPC(npcWidth, npcHeight, x, y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC--;
                        }
                        if(num == 1||num == 3){
                            int y = rd.nextInt(HEIGHT - (2*npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            NPCs.add(newNPC(npcWidth, npcHeight, x, y - npcHeight - 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth/2), y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC-=2;
                        }
                        if(num == 7){
                            int y = rd.nextInt(HEIGHT - (3*npcHeight + 100));
                            if(y < npcHeight + 5) y = npcHeight + 5;
                            NPCs.add(newNPC(npcWidth, npcHeight, x, y, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth/2), y + npcHeight + 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NPCs.add(newNPC(npcWidth, npcHeight, x + (npcWidth/2), y - npcHeight - 5, npcSpeed,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.minienemy), 8));
                            NunOfNPC-=3;
                        }
                    }
                }

                //reset timer
                missileStartTime = System.nanoTime();
                if((System.nanoTime() - coinsStartTime)/1000000 > 10000 && NumOfCoins > 0){
                    Gold coin = new Gold(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32);
                    coin.initSprites(player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                    int count = 0;
                    for(Missile bull:missiles){
                        if(this.collision(bull, coin)){
                            count++;
                        }
                    }
                    if(count == 0){
                        coins.add(coin);
                        NumOfCoins--;
                        coinsStartTime = System.nanoTime();
                    }
                }
                if((System.nanoTime() - itemsStartTime)/1000000 > 15000 && NumOfItems > 0){
                    int KindOfItem = 3;
                    if(rd.nextInt(3)==2) KindOfItem = 2;
                    Item item;
                    if(KindOfItem == 3){
                        item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 30, 39, KindOfItem);
                        item.initSprites(player.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.triple2));
                    }
                    else{
                        item = new Item(WIDTH + 50, rd.nextInt(HEIGHT - 100), 32, 32, KindOfItem);
                        item.initSprites(player.getScore(), 1, BitmapFactory.decodeResource(getResources(), R.drawable.item));
                    }
                    int count = 0;
                    for(Missile bull:missiles){
                        if(this.collision(bull, item)){
                            count++;
                        }
                    }
                    for(Gold coin:coins){
                        if(this.collision(coin, item)){
                            count++;
                        }
                    }
                    if(count == 0){
                        items.add(item);
                        NumOfItems--;
                        itemsStartTime = System.nanoTime();
                    }
                }
            }

            for(Bullet bullet:bullets){
                for (NPC n:NPCs){
                    if(collision(n, bullet)&&!n.isDissapear()){
                        n.setDissapear(true);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), n.getX(),
                                n.getY() - 30, 100, 100, 25);
                        bullet.setLive(false);
                        explosion.setMove(true);
                        miniExplosions.add(explosion);
                    }
                }
            }

            //update the bullets
            if(enemy.isPlaying()){
                for(Bullet bullet:bullets){
                    bullet.update();
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }
            }

            if(player.getY()+player.getHeight() >= (getHeight() - 80)/scaleFactorY){
                if(!started) started = true;
                player.setPlaying(false);
                player.setLife(1);
                Result = -1;
            }

            for (Explosion e:miniExplosions){
                e.update();
            }

            for (NPC n:NPCs){
                if(!n.isDissapear()) n.update(this);
                if(n.getX() < -n.getWidth()) n.setDissapear(true);
                if(collision(n, player)&&!n.isDissapear()){
                    n.setDissapear(true);
                    if(player.getLife() <= 1){
                        if(!started) started = true;
                        player.setPlaying(false);
                        Result = -1;
                    }
                    else {
                        player.setLife(player.getLife() - 1);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                                player.getY() - 30, 100, 100, 25);
                        explosion.setMove(true);
                        miniExplosions.add(explosion);
                    }
                }
            }

            //check for collision between all of missiles and player
            for(Missile ms:missiles){
                if(ms.isLive()){
                    ms.update();
                    if(collision(ms, player)){
                        ms.setLive(false);
                        if(player.getLife() <= 1){
                            if(!started) started = true;
                            player.setPlaying(false);
                            Result = -1;
                        }
                        else {
                            player.setLife(player.getLife() - 1);
                            explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                                    player.getY() - 30, 100, 100, 25);
                            explosion.setMove(true);
                            miniExplosions.add(explosion);
                        }
                    }
                    if(ms.getX() <-100){
                        ms.setLive(false);
                        break;
                    }
                }
            }

            for(SpecialMissile spm:enemymissiles){
                if(collision(spm, player)&&spm.isLive()){
                    spm.setLive(false);
                    if(player.getLife() <= 1){
                        if(!started) started = true;
                        player.setPlaying(false);
                        Result = -1;
                    }
                    else {
                        player.setLife(player.getLife() - 1);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                                player.getY() - 30, 100, 100, 25);
                        explosion.setMove(true);
                        miniExplosions.add(explosion);
                    }
                }
                if(spm.getX() <-100){
                    spm.setLive(false);
                }
            }

            //when coins and missiles didn't come out anymore, the boss appear
            if(NumOfCoins == 0){
                enemy.setPlaying(true);
                enemy.setDissapear(false);
                //update enemy position on timer
                long enemy_eslapsed = (System.nanoTime() - enemyStartTime)/1000000;
                if(enemy_eslapsed > 200 && enemy.isPlaying()){
                    enemy.update(this);
                    if(rd.nextInt(50) == 1){
                        Missile bullet = new Missile(enemy.getX()+5, enemy.getY()+(enemy.getHeight()/2) - 8, 45, 15);
                        bullet.initSprites(player.getScore(),13, BitmapFactory.decodeResource(getResources(), R.drawable.missile));
                        bullet.setSpeed(20 + rd.nextInt(10));
                        missiles.add(bullet);
                    }
                    if(rd.nextInt(60) == 1){
                        int x = enemy.getX(); int y = enemy.getY();
                        int ene_h = enemy.getHeight();
                        int w = 32; int h = 32;
                        int speed = rd.nextInt(10) + 15;
                        newSpecialMissile(x, y + ene_h/2, w, h, "Normal", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Top_Left", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                        newSpecialMissile(x+5, y + ene_h/2, w, h, "Bot_Left", speed,
                                player.getScore(), 8, BitmapFactory.decodeResource(getResources(), R.drawable.enemymissile1));
                    }
                }

                if(score == 0&&coins.size() == 0&&bullets.size() == 0&&!enemy.isDissapear()&&enemy.isPlaying()){
                    player.setPlaying(false);
                    player.setLife(1);
                    Result = -1;
                }

                for(Bullet bullet:bullets){
                    if(collision(bullet, enemy)){
                        if(enemy.getHealthPoints() == 0){
                            enemy.setPlaying(false);
                            started = true;
                        }
                        else {
                            if(bullet.isLive()){
                                enemy.setHealthPoints(enemy.getHealthPoints() - 1);
                                bullet.setLive(false);
                            }
                            Explosion e = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.miniexplosion), bullet.getX(),
                                    bullet.getY(), 50, 50, 25);
                            miniExplosions.add(e);
                        }
                        break;
                    }
                    if(bullet.getX()>WIDTH) bullet.setLive(false);
                }

                for (SpecialMissile spm:enemymissiles){
                    if(spm.getNew() == 0) spm.setNew(1);
                    else spm.update();
                }
            }

            //add smoke puff on timer
            long eslapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(eslapsed > 120){
                smokes.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }
            for(int i=0; i<smokes.size(); i++){
                smokes.get(i).update();
                if(smokes.get(i).getX() < -10) smokes.remove(i);
            }
        }
        else{ //the player has destroyed enemy plane or has been destroyed
            player.resetDY();
            if(!reset){
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                if(!player.isPlaying()){
                    player.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                            player.getY() - 30, 100, 100, 25);
                    for(Gold coin:coins){
                        coin.setSpeed(0);
                    }
                    for(Item item:items){
                        item.setSpeed(0);
                    }
                }
                if(!enemy.isPlaying()){
                    enemy.setDissapear(true);
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), enemy.getX(),
                            enemy.getY() - 30, 100, 100, 25);
                    player.setPlaying(false);
                    Result = 1;
                }
                coins.clear();
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
        canvas.drawText("+"+value, player.getX() + player.getWidth(), player.getY(), paint2);
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
            background.draw(canvas);
            drawText(canvas);
            for(Missile missile:missiles){
                if(missile.isLive()) missile.draw(canvas);
            }
            for(Gold gold:coins){
                gold.update();
                gold.draw(canvas);
                if(collision(gold, player)){
                    score++;
                    availableSingleBullets++;
                    coins.remove(gold);
                    if(player.getStatus() == 0) player.setStatus(1);
                    showCoinValue(canvas, "1");
                    break;
                }
                if(gold.getX() < 0){
                    coins.remove(gold);
                    break;
                }
            }
            for(Item item:items){
                item.update();
                item.draw(canvas);
                if(collision(item, player)){
                    if(item.getKind() != 2){
                        availableMultiBullets++;
                        score+=item.getKind();
                        player.setStatus(item.getKind());
                        showCoinValue(canvas, String.valueOf(item.getKind()));
                    }
                    else{
                        player.setLife(player.getLife() + 1);
                        showCoinValue(canvas, "1 life");
                    }
                    items.remove(item);
                    break;
                }
                if(item.getX() < 0){
                    items.remove(item);
                    break;
                }
            }
            fire_controller.draw(canvas);
            if(!player.isDissapear()){
                player.draw(canvas);
                for(Smokepuff sp:smokes){
                    sp.draw(canvas);
                }
                //draw NPC
                for (NPC n:NPCs){
                    if(!n.isDissapear()){
                        n.draw(canvas);
                    }
                    else NPCs.remove(n);
                }

                if(player.isPlaying() && enemy.isPlaying() && !enemy.isDissapear()){
                    if(level > 1){
                        Paint paint = new Paint();
                        int x = enemy.getX()+10; int y = enemy.getY();
                        int w = enemy.getHealthPoints()*40; int h = 10;
                        if(enemy.getPecentagesOfHP() > 0.5)
                            paint.setColor(Color.GREEN);
                        else{
                            if(enemy.getPecentagesOfHP() >= 0.3)
                                paint.setColor(Color.YELLOW);
                            else paint.setColor(Color.RED);
                        }
                        paint.setStrokeWidth(1);
                        canvas.drawRect(x - 2, (y - h * 2) - 2, x + w, y - h, paint);
                    }
                    enemy.setPlaying(true);
                    enemy.draw(canvas);
                }
            }
            //draw the bullets
            if(enemy.isPlaying()){
                for(Bullet bullet:bullets){
                    if(bullet.isLive()) bullet.draw(canvas);
                    else bullets.remove(bullet);
                }
                for (SpecialMissile spm:enemymissiles){
                    if(spm.isLive()) spm.draw(canvas);
                }
            }
            if(started){
                explosion.draw(canvas);
            }
            for(Explosion e:miniExplosions){
                if(!e.isDisappear()) e.draw(canvas);
            }
            canvas.restoreToCount(savedState);
        }
    }


    public void newGame(){
        enemymissiles.clear(); items.clear(); missiles.clear(); smokes.clear();
        bullets.clear(); miniExplosions.clear(); NPCs.clear();
        player.setDissapear(false); enemy.setDissapear(true);
        player.resetScore(); player.setY(HEIGHT/2); player.resetDY();
        if(Result == 1){
            level++;
            if(level > 3) level = 3;
        }
        switch(level){
            case 1:{
                NumOfCoins = 5;
                enemy.setHealthPoints(4);
                enemy.setHealthPointsPool(4);
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
                enemy.setHealthPoints(5);
                enemy.setHealthPointsPool(5);
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
        if(player.getStatus() > 1){
            canvas.drawText("CURRENT BULLET: MULTIPLE", 150, HEIGHT - 10, paint);
            canvas.drawText("Number: " + availableMultiBullets, WIDTH - 215, HEIGHT - 10, paint);
        }
        else{
            if(player.getStatus() == 1){
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
        canvas.drawText("Level: "+level, 30, 50, paint);

        if(!player.isPlaying()&&newGameCreated&&reset){
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
