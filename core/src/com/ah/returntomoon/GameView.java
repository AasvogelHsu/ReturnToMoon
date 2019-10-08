package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameView extends ScreenAdapter {

    Starter game;
    SpriteBatch batch;
    Texture bg1,bg2,rocketSheet,touchpadBg,touchpadKonb,asteroidASheet;
    float yMax,yCoordBg1,yCoordBg2,stateTime,lastAsteroid_time;
    int BACKGROUND_MOVE_SPEED = 80,ROCKET_SPEED = 5,ROCKET_DURABILITY = 5;
    Animation<TextureRegion> rocketAnimation,asteroidAAnimation;
    final int ROCKET_FRAME_COLS = 4,ROCKET_FRAME_ROWS = 1,
            ASTEROID_FRAME_COLS = 8,ASTEROID_FRAME_ROWS = 1;
    Rectangle Rocket;
    Touchpad touchpad;
    Stage stage;
    TextureRegionDrawable padBG,padKnob;
    Touchpad.TouchpadStyle touchpadStyle;
    Array<Circle> Asteroids;
    ParticleEffect effect;
    BitmapFont font;


    public GameView(Starter game){
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Asteroids = new Array<Circle>();
        //↓背景捲動
        bg1 = new Texture("bg_space.jpg");
        bg2 = new Texture("bg_space.jpg");
        yMax = Constant.HEIGHT;
        yCoordBg1 = yMax;
        yCoordBg2 = 0;

        //↓Rocket動畫
        rocketSheet = new Texture("spaceShipSprite.png");
        TextureRegion[][] Rtmp = TextureRegion.split(rocketSheet,
                rocketSheet.getWidth()/ROCKET_FRAME_COLS,
                rocketSheet.getHeight()/ROCKET_FRAME_ROWS);
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
        asteroidASheet = new Texture("asteroidD.png");
        TextureRegion[][] atmpA = TextureRegion.split(asteroidASheet,
                asteroidASheet.getWidth()/ASTEROID_FRAME_COLS,
                asteroidASheet.getHeight()/ASTEROID_FRAME_ROWS);
        TextureRegion[] AsteroidAFrame = new TextureRegion[ASTEROID_FRAME_COLS*ASTEROID_FRAME_ROWS];
        int Aindex = 0;
        for (int i=0 ;i<ASTEROID_FRAME_ROWS ;i++){
            for (int j=0 ;j<ASTEROID_FRAME_COLS ;j++){
                AsteroidAFrame[Aindex++] = atmpA[i][j];
            }
        }
        asteroidAAnimation = new Animation<TextureRegion>(0.15f,AsteroidAFrame);

        //↓particle Effect of explosion
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("explosion.p"),Gdx.files.internal(""));
        effect.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        effect.start();

        //↓ScorePad
        font = new BitmapFont(Gdx.files.internal("fonts/jsfv1.fnt")
                ,Gdx.files.internal("fonts/jsfv1.png"),false);



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
        effect.update(delta);

        batch.begin();
        batch.draw(bg1,0,yCoordBg1,Constant.WIDTH,Constant.HEIGHT);
        batch.draw(bg2,0,yCoordBg2,Constant.WIDTH,Constant.HEIGHT);
        batch.draw(RocketcurrentFrame,Rocket.x,Rocket.y,Rocket.width,Rocket.height);
        for (Circle asteroid : Asteroids){
            batch.draw(asteroidACurrentFrame,asteroid.x,asteroid.y);
        }
        effect.draw(batch);
        font.draw(batch,"Durability : "+ROCKET_DURABILITY,100,100);
        font.draw(batch,"Distance :",500,100);
        batch.end();

        stage.act();
        stage.draw();

        if (TimeUtils.nanoTime() - lastAsteroid_time > 1000000000){
            findAsteroid();
        }
        Iterator<Circle> iterator = Asteroids.iterator();
        while (iterator.hasNext()){
            Circle asteroid = iterator.next();
            asteroid.y -= 200*delta;
            if (asteroid.y+Constant.ASTEROID_HEIGHT<0){
                iterator.remove();
            }

            if (Intersector.overlaps(asteroid,Rocket)){
                System.out.println("crash!!");
                Gdx.input.vibrate(500);
                iterator.remove();
                ROCKET_DURABILITY = ROCKET_DURABILITY - 1;
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
        effect.dispose();
        font.dispose();

    }
    public void update(){
        if(touchpad.isTouched()&& Rocket.x>=0 && Rocket.x<=Gdx.graphics.getWidth()-Rocket.width){
            Rocket.x += touchpad.getKnobPercentX()*ROCKET_SPEED;
        }else if(Rocket.x<0){
            Rocket.x=0;
        }else if (Rocket.x>Gdx.graphics.getWidth()-Rocket.width){
            Rocket.x=Gdx.graphics.getWidth()-Rocket.width;
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
        Circle asteroid = new Circle();
        asteroid.x = MathUtils.random(0,Constant.WIDTH-Constant.ASTEROID_WIDTH);
        asteroid.y = Constant.HEIGHT;
        asteroid.radius = Constant.ASTEROID_RADIUS;
        //asteroid.width = Constant.ASTEROID_WIDTH;
        //asteroid.height = Constant.ASTEROID_HEIGHT;
        Asteroids.add(asteroid);
        lastAsteroid_time = TimeUtils.nanoTime();

    }

}
