package com.example.snakee;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    Integer[] appleGrid = new Integer[] {20,40,60,80,100,120,140,160,180,200,220,240,260,280,300,320,340,360,380,400,420,440,460,480,500,520,540,560,580,600,620,640,660,680,700,720,740,760,780,800,820,840,860,880,900};
    ArrayList<Integer> headHistoryX = new ArrayList<>();
    ArrayList<Integer> bodyHistoryX = new ArrayList<>();
    float pHeadXh;
    float pHeadYh;

    int appleCount = 1;
    int checkUp = 1, checkDown = 1, checkLeft = 1, checkRight = 1;
    //GAME SETUP
    private Timer timer = new Timer();       //     GameLoop
    private Handler handler = new Handler();   //    GameLoop
    private FrameLayout game_window;  //    GameWindow

    //PLAYER
    private Timer snakeTimer = new Timer(); //Snake Independent Timer
    private Handler snakeHandler = new Handler(); //Snake Independent Handler
    private ImageView playerHead;
    private Drawable playerHeadL, playerHeadR, playerHeadD, playerHeadU;//   Resource Head Picture
    private ImageView playerTail;
    private Drawable playerTailL, playerTailR, playerTailD, playerTailU;  //   Resource Tail Picture
    private ImageView playerBody;
    private Drawable playerBodyL, playerBodyR, playerBodyD, playerBodyU;  //   Resource Tail Picture
    private float pHeadX, pHeadY; //    Head Position
    private float pTailX, pTailY;
    private float pBodyX, pBodyY;
    private  boolean up, down, left, right;  // Movement

    //APPLE
    private ImageView apple; //    Define Apple
    private float appleX, appleY;   //    Apple Position
    private Drawable appleRes; //   Resource Apple Picture

    //OTHER
    private TextView scoreField;
    private int points = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headHistoryX.add(0,0);
        headHistoryX.add(1,0);
        headHistoryX.add(2,0);
        headHistoryX.add(3,0);
        bodyHistoryX.add(0,0);
        bodyHistoryX.add(1,0);
        bodyHistoryX.add(2,0);
        bodyHistoryX.add(3,0);

        //DEFINE OBJECTS
        game_window = (FrameLayout)findViewById(R.id.gameWindow);

        playerHead = (ImageView)findViewById(R.id.playerHead);
        playerHeadL = getResources().getDrawable(R.drawable.headleft);
        playerHeadR = getResources().getDrawable(R.drawable.headright);
        playerHeadD = getResources().getDrawable(R.drawable.headdown);
        playerHeadU = getResources().getDrawable(R.drawable.headup);

        playerTail = (ImageView)findViewById(R.id.playerTail);
        playerTailL = getResources().getDrawable(R.drawable.body);
        playerTailR = getResources().getDrawable(R.drawable.body);
        playerTailD = getResources().getDrawable(R.drawable.body);
        playerTailU = getResources().getDrawable(R.drawable.body);

        playerBody = (ImageView)findViewById(R.id.playerBody);
        playerBodyL = getResources().getDrawable(R.drawable.body);
        playerBodyR = getResources().getDrawable(R.drawable.body);
        playerBodyD = getResources().getDrawable(R.drawable.body);
        playerBodyU = getResources().getDrawable(R.drawable.body);

        appleRes = getResources().getDrawable(R.drawable.apple);
        apple = (ImageView)findViewById(R.id.appleObject);

        scoreField = (TextView)findViewById(R.id.scoreField);

        //BUTTON LISTENERS
        findViewById(R.id.upButton).setOnTouchListener(this);
        findViewById(R.id.downButton).setOnTouchListener(this);
        findViewById(R.id.leftButton).setOnTouchListener(this);
        findViewById(R.id.rightButton).setOnTouchListener(this);

        //---------\\
        //GAME LOOP\\
        //---------\\
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //playerCode();
                        //appleCode();
                        appleCollision();
                        //zrazkaNepriatel();
                    }
                });
            }
        },3000,20); // zacne za 3000 ms a snimka sa bude opakovať každých 20ms
        snakeTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                snakeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        playerCode();
                        bodymove();
                        tailmove();
                        //playerCodeTail();
                        //playerCodeBody();
                    }
                });
            }
        },3000,500);

    }

    public void playerCode() {
//HEAD
        pHeadX = playerHead.getX();
        pHeadY = playerHead.getY();

        //pohyb hore
        if(up) {pHeadY -= game_window.getWidth()/15;
            playerHead.setImageDrawable(playerHeadU);}
        //pohyb dole
        if (down){pHeadY += game_window.getWidth()/15;
            playerHead.setImageDrawable(playerHeadD);}
        //pohyb vlavo
        if(left){pHeadX -= game_window.getWidth()/15;
            playerHead.setImageDrawable(playerHeadL);}
        //pohyb vpravo
        if(right){pHeadX += game_window.getWidth()/15;
            playerHead.setImageDrawable(playerHeadR);}

        // treba doriesit, aby davac zostal v hernom okne
        if (pHeadY < 0) {pHeadY = game_window.getHeight() - playerHead.getHeight();}

        if (pHeadY > game_window.getHeight() - playerHead.getHeight()){
            pHeadY = 0;
        }

        if (pHeadX < 0) {pHeadX = game_window.getWidth() - playerHead.getWidth();}

        if (pHeadX > game_window.getWidth() - playerHead.getWidth()){
            pHeadX = 0;
        }

        //zapise zmenu umiestnenia
        playerHead.setX(pHeadX);
        playerHead.setY(pHeadY);


//TAIL
        /*
        pTailX = playerTail.getX();
        pTailY = playerTail.getY();

        //pohyb hore
        if(up) {pTailY = pHeadY+90; pTailX = pHeadX;
            playerTail.setImageDrawable(playerTailU);}
        //pohyb dole
        if (down){pTailY = pHeadY-90; pTailX = pHeadX;
            playerTail.setImageDrawable(playerTailD);}
        //pohyb vlavo
        if(left){pTailX = pHeadX+90; pTailY = pHeadY;
            playerTail.setImageDrawable(playerTailL);}
        //pohyb vpravo
        if(right){pTailX = pHeadX-90; pTailY = pHeadY;
            playerTail.setImageDrawable(playerTailR);}

        // treba doriesit, aby davac zostal v hernom okne
        if (pTailY < 0) {pTailY = game_window.getWidth() - 2*playerTail.getWidth();}

        if (pTailY > game_window.getHeight() - playerTail.getHeight()){pTailY = 0;}

        if (pTailX < 0) {pTailX = game_window.getWidth() - playerTail.getWidth();}

        if (pTailX > game_window.getWidth() - playerTail.getWidth()) {pTailX = 0; }

        //zapise zmenu umiestnenia
        playerTail.setX(pTailX);
        playerTail.setY(pTailY);

*/
//BODY
        /*
        pBodyX = playerBody.getX();
        pBodyY = playerBody.getY();

        //pohyb hore
        if(up) {pBodyY = pHeadYh+90; pBodyX = pHeadX;
            playerBody.setImageDrawable(playerBodyU);}
        //pohyb dole
        if (down){pBodyY = pHeadYh-90; pBodyX = pHeadX;
            playerBody.setImageDrawable(playerBodyD);}
        //pohyb vlavo
        if(left){pBodyX = pHeadXh+90; pBodyY = pHeadY;
            playerBody.setImageDrawable(playerBodyL);}
        //pohyb vpravo
        if(right){pBodyX = pHeadXh-90; pBodyY = pHeadY;
            playerBody.setImageDrawable(playerBodyR);}

        // treba doriesit, aby davac zostal v hernom okne
        //if (pBodyY < 0) {pBodyY = game_window.getWidth() - 2*playerBody.getWidth();}

        //if (pBodyY > game_window.getHeight() - playerBody.getHeight()){pBodyY = 0;}

        //if (pBodyX < 0) {pBodyX = game_window.getWidth() - playerBody.getWidth();}

        //if (pBodyX > game_window.getWidth() - playerBody.getWidth()) {pBodyX = 0; }

        //zapise zmenu umiestnenia
        playerBody.setX(pBodyX);
        playerBody.setY(pBodyY);
*/




    }
/*
    public void playerCodeTail() {

        pTailX = playerTail.getX();
        pTailY = playerTail.getY();

        //pohyb hore
        if(up) {pTailY = pBodyY+30; pTailX = pBodyX;
            playerTail.setImageDrawable(playerTailU);}
        //pohyb dole
        if (down){pTailY = pBodyY-30; pTailX = pBodyX;
            playerTail.setImageDrawable(playerTailD);}
        //pohyb vlavo
        if(left){pTailX = pBodyX+30; pTailY = pBodyY;
            playerTail.setImageDrawable(playerTailL);}
        //pohyb vpravo
        if(right){pTailX = pBodyX-30; pTailY = pBodyY;
            playerTail.setImageDrawable(playerTailR);}

        // treba doriesit, aby davac zostal v hernom okne
        if (pTailY < 0) {pTailY = game_window.getWidth() - 2*playerTail.getWidth();}

        if (pTailY > game_window.getHeight() - playerTail.getHeight()){pTailY = 0;}

        if (pTailX < 0) {pTailX = game_window.getWidth() - playerTail.getWidth();}

        if (pTailX > game_window.getWidth() - playerTail.getWidth()) {pTailX = 0; }

        //zapise zmenu umiestnenia
        playerTail.setX(pTailX);
        playerTail.setY(pTailY);
    }

    public void playerCodeBody() {

        pBodyX = playerBody.getX();
        pBodyY = playerBody.getY();

        //pohyb hore
        if(up) {pBodyY = pHeadY+90; pBodyX = pHeadX;
            playerBody.setImageDrawable(playerBodyU);}
        //pohyb dole
        if (down){pBodyY = pHeadY-90; pBodyX = pHeadX;
            playerBody.setImageDrawable(playerBodyD);}
        //pohyb vlavo
        if(left){pBodyX = pHeadX+90; pBodyY = pHeadY;
            playerBody.setImageDrawable(playerBodyL);}
        //pohyb vpravo
        if(right){pBodyX = pHeadX-90; pBodyY = pHeadY;
            playerBody.setImageDrawable(playerBodyR);}

        // treba doriesit, aby davac zostal v hernom okne
        if (pBodyY < 0) {pBodyY = game_window.getWidth() - 2*playerBody.getWidth();}

        if (pBodyY > game_window.getHeight() - playerBody.getHeight()){pBodyY = 0;}

        if (pBodyX < 0) {pBodyX = game_window.getWidth() - playerBody.getWidth();}

        if (pBodyX > game_window.getWidth() - playerBody.getWidth()) {pBodyX = 0; }

        //zapise zmenu umiestnenia
        playerBody.setX(pBodyX);
        playerBody.setY(pBodyY);
    }
*/
    private void bodymove() {
        headHistoryX.add(1,(int)playerHead.getX());
        headHistoryX.add(3,(int)playerHead.getY());

        playerBody.setX(headHistoryX.get(0));
        playerBody.setY(headHistoryX.get(2));

        scoreField.setText("x="+headHistoryX.get(0)+" "+headHistoryX.get(1)+"y="+headHistoryX.get(2)+" "+headHistoryX.get(3));
        
        headHistoryX.remove(0);
        headHistoryX.remove(1);

        if (playerHead.getDrawable() == playerHeadU) { playerBody.setImageDrawable(playerBodyU); }
        if (playerHead.getDrawable() == playerHeadD) { playerBody.setImageDrawable(playerBodyD); }
        if (playerHead.getDrawable() == playerHeadL) { playerBody.setImageDrawable(playerBodyL); }
        if (playerHead.getDrawable() == playerHeadR) { playerBody.setImageDrawable(playerBodyR); }

    }

    private void tailmove() {
        bodyHistoryX.add(1,(int)playerBody.getX());
        bodyHistoryX.add(3,(int)playerBody.getY());

        playerTail.setX(bodyHistoryX.get(0));
        playerTail.setY(bodyHistoryX.get(2));

        bodyHistoryX.remove(0);
        bodyHistoryX.remove(1);

        if (playerBody.getDrawable() == playerBodyU) { playerTail.setImageDrawable(playerTailU); }
        if (playerBody.getDrawable() == playerBodyD) { playerTail.setImageDrawable(playerTailD); }
        if (playerBody.getDrawable() == playerBodyL) { playerTail.setImageDrawable(playerTailL); }
        if (playerBody.getDrawable() == playerBodyR) { playerTail.setImageDrawable(playerTailR); }

    }

    private void appleCode() {
        if (appleCount == 1) {
            int oX = (int) (Math.random() * appleGrid.length);
            int oY = (int) (Math.random() * appleGrid.length);
            appleX = appleGrid[oX];
            appleY = appleGrid[oY];
            apple.setImageDrawable(appleRes);
            appleCount += 1;
        }
        apple.setX(appleX); // zapisem  hodnotu  na os X
        apple.setY(appleY); //zapisem  hodnotu  na os Y
    }

    private void appleCollision() {
        appleCode();
        float appleMiddleX = appleX + apple.getWidth() / 2;
        float appleMiddleY = appleY + apple.getHeight() / 2;

        if ( appleMiddleX  >= pHeadX && appleMiddleX  <= pHeadX+ playerHead.getWidth()
                && appleMiddleY >= pHeadY && appleMiddleY <= pHeadY+ playerHead.getHeight() )
        {
            appleCount -= 1;
            points += 1;
            scoreField.setText("Score : " + points);
        }
    }


    //private void zrazkaNepriatel() {

    //    float nepriatelStredX = nepriatelX + nepriatel.getWidth() / 2;
    //    float nepriatelStredY = nepriatelY + nepriatel.getHeight() / 2;

    //    if ( nepriatelStredX  >= davacX && nepriatelStredX  <= davacX+ davac.getWidth()
    //            && nepriatelStredY >= davacY && nepriatelStredY <= davacY+ davac.getHeight()) {



            // Game Over!!
    //        if (casovac != null) {
    //            casovac.cancel();
    //            casovac = null;
    //        }




    //    }}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            switch (v.getId()){
                case R.id.upButton:
                    if(checkDown != 1){
                        up=true;
                        down=false;
                        left=false;
                        right=false;
                    }
                    checkUp = 1;
                    checkLeft += 1;
                    checkRight += 1;
                    break;
                case R.id.downButton:
                    if(checkUp != 1){
                        up=false;
                        down=true;
                        left=false;
                        right=false;
                    }
                    checkDown = 1;
                    checkLeft += 1;
                    checkRight += 1;
                    break;
                case R.id.leftButton:
                    if(checkRight != 1){
                        up=false;
                        down=false;
                        left=true;
                        right=false;
                    }
                    checkLeft = 1;
                    checkUp += 1;
                    checkDown += 1;
                    break;
                case R.id.rightButton:
                    if(checkLeft != 1){
                        up=false;
                        down=false;
                        left=false;
                        right=true;
                    }
                    checkRight = 1;
                    checkUp += 1;
                    checkDown += 1;
                    break;
            }
        }
        return false;
    }
}