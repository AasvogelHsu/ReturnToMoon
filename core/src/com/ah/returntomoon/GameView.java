package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class GameView extends ScreenAdapter {

    Starter game;
    SpriteBatch batch;
    Texture bg1,bg2,rocketSheet,touchpadBg,touchpadKonb,asteroidASheet;
    float yMax,yCoordBg1,yCoordBg2,stateTime,lastAsteroid_time;
    int BACKGROUND_MOVE_SPEED = 80,ROCKET_SPEED = 5;
    Animation<TextureRegion> rocketAnimation,asteroidAAnimation;
    final int ROCKET_FRAME_COLS = 4,ROCKET_FRAME_ROWS = 1,ASTEROID_FRAME_COLS = 6,ASTEROID_FRAME_ROWS = 1;
    Rectangle Rocket;
    Touchpad touchpad;
    Stage stage;
    TextureRegionDrawable padBG,padKnob;
    Touchpad.TouchpadStyle touchpadStyle;
    Array<Rectangle> Asteroids;


    public GameView(Starter game){
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Asteroids = new Array<Rectangle>();
        //↓背景捲動
        bg1 = new Texture("bg_space01.jpg");
        bg2 = new Texture("bg_space01r.jpg");
        yMax = 1280;
        yCoordBg1 = yMax;
        yCoordBg2 = 0;

        //↓Rocket動畫
        rocketSheet = new Texture("spaceShipSprite.png");
        TextureRegion[][] Rtmp = TextureRegion.split(rocketSheet,
                rocketSheet.getWidth()/ROCKET_FRAME_COLS,rocketSheet.getHeight()/ROCKET_FRAME_ROWS);
        TextureRegion[] RocketFrame = new TextureRegion[ROCKET_FRAME_COLS*ROCKET_FRAME_ROWS];
        int Rindex = 0;
        for (int i=0 ;i<ROCKET_FRAME_ROWS ;i++){
            for (int j=0 ;j<ROCKET_FRAME_COLS ;j++){
                RocketFrame[Rindex++] = Rtmp[i][j];
            }
        }
        rocketAnimation = new Animation<TextureRegion>(0.15f,RocketFrame);
        stateTime =0f;
        Rocket = new Rectangle();
        Rocket.x = Constant.WIDTH/2-50;
        Rocket.y = Constant.HEIGHT/6-75;
        Rocket.width = 100;
        Rocket.height = 150;

        //↓Touchpad
        touchpadBg = new Texture("touchpad_bg.png");
        touchpadKonb = new Texture("touchpad_knob.png");
        padBG = new TextureRegionDrawable(touchpadBg);
        padKnob = new TextureRegionDrawable(touchpadKonb);
        touchpadStyle = new Touchpad.TouchpadStyle(padBG,padKnob);
        touchpad = new Touchpad(20f,touchpadStyle);
        touchpad.setBounds(Constant.WIDTH/2-75,Constant.HEIGHT/10-75,150,150);
        stage.addActor(touchpad);

        //↓animation of asteroid
        asteroidASheet = new Texture("asteroidA.png");
        TextureRegion[][] atmpA = TextureRegion.split(asteroidASheet,
                asteroidASheet.getWidth()/ASTEROID_FRAME_COLS,asteroidASheet.getHeight()/ASTEROID_FRAME_ROWS);
        TextureRegion[] AsteroidAFrame = new TextureRegion[ASTEROID_FRAME_COLS*ASTEROID_FRAME_ROWS];
        int Aindex = 0;
        for (int i=0 ;i<ASTEROID_FRAME_ROWS ;i++){
            for (int j=0 ;j<ASTEROID_FRAME_COLS ;j++){
                AsteroidAFrame[Aindex++] = atmpA[i][j];
            }
        }
        asteroidAAnimation = new Animation<TextureRegion>(0.15f,AsteroidAFrame);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        yCoordBg1 -= BACKGROUND_MOVE_SPEED*Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 - yMax;
        if (yCoordBg1 <= 0){
            yCoordBg1 = yMax;
            yCoordBg2 = 0;
        }

        stateTime += delta;
        TextureRegion RocketcurrentFrame = rocketAnimation.getKeyFrame(stateTime,true);
        TextureRegion asteroidACurrentFrame = asteroidAAnimation.getKeyFrame(stateTime,true);

        update();

        batch.begin();
        batch.draw(bg1,0,yCoordBg1);
        batch.draw(bg2,0,yCoordBg2);
        batch.draw(RocketcurrentFrame,Rocket.x,Rocket.y,Rocket.width,Rocket.height);
        for (Rectangle asteroid : Asteroids){
            batch.draw(asteroidACurrentFrame,asteroid.x,asteroid.y);
        }
        batch.end();

        stage.act();
        stage.draw();

        if (delta > lastAsteroid_time){
            findAsteroid();
        }
        Iterator<Rectangle> iterator = Asteroids.iterator();
        while (iterator.hasNext()){
            Rectangle asteroid = iterator.next();
            asteroid.y -= 200*delta;
            if (asteroid.y+Constant.ASTEROID_HEIGHT<0){
                iterator.remove();
            }
            if (asteroid.overlaps(Rocket)){
                System.out.println("crash!!");
                iterator.remove();
            }
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bg1.dispose();
        bg2.dispose();
        rocketSheet.dispose();
        touchpadBg.dispose();
        touchpadKonb.dispose();
        asteroidASheet.dispose();



    }
    public void update(){
        if(touchpad.isTouched()&& Rocket.x>=0 && Rocket.x<=Gdx.graphics.getWidth()-Rocket.width){
            Rocket.x += touchpad.getKnobPercentX()*ROCKET_SPEED;
        }else if(Rocket.x<0){
            Rocket.x=0;
        }else if (Rocket.x>Gdx.graphics.getWidth()-Rocket.width){
            Rocket.x=Gdx.graphics.getWidth()-100;
        }

        if (touchpad.isTouched() && Rocket.y>=0 && Rocket.y<=Gdx.graphics.getHeight()-Rocket.height){
            Rocket.y += touchpad.getKnobPercentY()*ROCKET_SPEED;
        }else if(Rocket.y<0) {
            Rocket.y = 0;
        }else if(Rocket.y>Gdx.graphics.getHeight()-Rocket.height){
            Rocket.y = Gdx.graphics.getHeight()-Rocket.height;
        }

    }

    public void findAsteroid(){
        Rectangle asteroid = new Rectangle();
        asteroid.x = MathUtils.random(0,Constant.WIDTH-Constant.ASTEROID_WIDTH);
        asteroid.y = Constant.HEIGHT;
        asteroid.width = Constant.ASTEROID_WIDTH;
        asteroid.height = Constant.ASTEROID_HEIGHT;
        Asteroids.add(asteroid);
        lastAsteroid_time = MathUtils.random(0.015555f,0.017555f);

    }

}
