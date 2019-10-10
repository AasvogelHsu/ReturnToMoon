package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameView extends ScreenAdapter {

    Starter game;
    SpriteBatch batch;
    Texture bg1,bg2,rocketSheet,touchpadBg,touchpadKonb,
            asteroidASheet,GameOverView;
    float yMax,yCoordBg1,yCoordBg2,stateTime,lastAsteroid_time;
    int BACKGROUND_MOVE_SPEED = 80,ROCKET_SPEED = 5,
            ROCKET_DURABILITY = 5,STATE,DISTANCE=0;
    Animation<TextureRegion> rocketAnimation,asteroidAAnimation;
    final int ROCKET_FRAME_COLS = 4,ROCKET_FRAME_ROWS = 1,
            ASTEROID_FRAME_COLS = 8,ASTEROID_FRAME_ROWS = 1,
            START = 0,RUNING = 1,GAME_OVER=2;
    Rectangle Rocket;
    Touchpad touchpad;
    Stage stage,stageGameOver;
    TextureRegionDrawable padBG,padKnob;
    Touchpad.TouchpadStyle touchpadStyle;
    Array<Circle> Asteroids;
    ParticleEffect effect;
    BitmapFont font;
    boolean effectover = false;
    Skin skin;
    TextButton button;
    float initialY = 0;
    Color origin;


    public GameView(Starter game){
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage();
        STATE = START;
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
        Rocket.width = 100;
        Rocket.height = 150;
        Rocket.x = Constant.WIDTH/2-90;
        Rocket.y = Constant.HEIGHT/6;

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
        effect.allowCompletion();
        effect.start();

        //↓ScorePad
        font = new BitmapFont(Gdx.files.internal("fonts/jsfv1.fnt")
                ,Gdx.files.internal("fonts/jsfv1.png"),false);
        origin = font.getColor();
        //↓GAME_OVER VIEW　
        GameOverView = new Texture("gameover.png");
        skin = new Skin(Gdx.files.internal("uiskin-simple.json"));
        stageGameOver = new Stage();
        button = new TextButton("Try Again",skin,"default");
        button.setColor(Color.BLACK);
        button.setWidth(200);
        button.setHeight(50);
        button.setPosition(Constant.WIDTH/2-100,Constant.HEIGHT/4-10);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ROCKET_DURABILITY = 5;
                yCoordBg1 = yMax;
                yCoordBg2 = 0;
                Asteroids = new Array<Circle>();
                DISTANCE = 0;
                STATE = START;
            }
        });
        stageGameOver.addActor(button);

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        TextureRegion RocketcurrentFrame = rocketAnimation.getKeyFrame(stateTime,true);
        TextureRegion asteroidACurrentFrame = asteroidAAnimation.getKeyFrame(stateTime,true);

        switch (STATE){
            case START :
                Rocket.x = Constant.WIDTH/2-90;
                Rocket.y = Constant.HEIGHT/6;
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                batch.draw(bg1,0,0,Constant.WIDTH,Constant.HEIGHT);
                batch.draw(RocketcurrentFrame,Rocket.x+40,initialY,100,150);
                batch.end();
                initialY = initialY+3;
                if (initialY >= Rocket.y+40){
                    STATE = RUNING;
                    initialY = 0;
                    font.setColor(Color.ORANGE);
                }
                break;

            case RUNING :
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.input.setInputProcessor(stage);
                yCoordBg1 -= BACKGROUND_MOVE_SPEED*Gdx.graphics.getDeltaTime();
                yCoordBg2 = yCoordBg1 - yMax;
                if (yCoordBg1 <= 0){
                    yCoordBg1 = yMax;
                    yCoordBg2 = 0;
                }
                DISTANCE += delta*60;


                update();

                batch.begin();
                batch.draw(bg1,0,yCoordBg1,Constant.WIDTH,Constant.HEIGHT);
                batch.draw(bg2,0,yCoordBg2,Constant.WIDTH,Constant.HEIGHT);
                batch.draw(RocketcurrentFrame,Rocket.x+40,Rocket.y+40,100,150);
                for (Circle asteroid : Asteroids){
                    batch.draw(asteroidACurrentFrame,asteroid.x,asteroid.y);
                }
                font.draw(batch,"Durability : "+ROCKET_DURABILITY,100,100);
                font.draw(batch,"Distance : "+DISTANCE,500,100);
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
                    if (asteroid.y+Constant.ASTEROID_RADIUS<0){
                        iterator.remove();
                    }

                    if (Intersector.overlaps(asteroid,Rocket)){
                        System.out.println("crash!!");
                        Gdx.input.vibrate(500);
                        iterator.remove();
                        ROCKET_DURABILITY = ROCKET_DURABILITY - 1;

                    }
                    if (ROCKET_DURABILITY <= 0 ){

                        effect.setPosition(Rocket.x+Rocket.width,Rocket.y+Rocket.height);
                        effect.update(delta);

                        batch.begin();
                        effect.draw(batch);
                        batch.end();
                        if (effect.isComplete()) effectover = true;
                    }
                    if (effectover){
                        STATE = GAME_OVER;
                        effectover = !effectover;
                        effect.reset();
                    }

                }
                break;
            case GAME_OVER :
                Gdx.input.setInputProcessor(stageGameOver);

                batch.begin();
                batch.draw(bg1, 0, yCoordBg1, Constant.WIDTH, Constant.HEIGHT);
                batch.draw(bg2, 0, yCoordBg2, Constant.WIDTH, Constant.HEIGHT);
                batch.draw(GameOverView, Constant.WIDTH / 2 - GameOverView.getWidth() / 2,
                        Constant.HEIGHT / 2 - GameOverView.getHeight() / 2);
                font.setColor(Color.BLACK);
                font.draw(batch,"Distance : "+DISTANCE,Constant.WIDTH/2-100,Constant.HEIGHT/2);
                batch.end();
                stageGameOver.act();
                stageGameOver.draw();
                break;
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
        stageGameOver.dispose();
        stage.dispose();
        skin.dispose();


    }
    public void update(){
        if(touchpad.isTouched()&& Rocket.x>=-40 && Rocket.x<=Gdx.graphics.getWidth()-Rocket.width-40){
            Rocket.x += touchpad.getKnobPercentX()*ROCKET_SPEED;
        }else if(Rocket.x<-40){
            Rocket.x=-40;
        }else if (Rocket.x>Gdx.graphics.getWidth()-Rocket.width-40){
            Rocket.x=Gdx.graphics.getWidth()-Rocket.width-40;
        }

        if (touchpad.isTouched() && Rocket.y>=-40 && Rocket.y<=Gdx.graphics.getHeight()-Rocket.height-40){
            Rocket.y += touchpad.getKnobPercentY()*ROCKET_SPEED;
        }else if(Rocket.y<-40) {
            Rocket.y = -40;
        }else if(Rocket.y>Gdx.graphics.getHeight()-Rocket.height-40){
            Rocket.y = Gdx.graphics.getHeight()-Rocket.height-40;
        }

    }

    public void findAsteroid(){
        Circle asteroid = new Circle();
        asteroid.x = MathUtils.random(0,Constant.WIDTH-Constant.ASTEROID_RADIUS*2);
        asteroid.y = Constant.HEIGHT;
        asteroid.radius = Constant.ASTEROID_RADIUS;
        Asteroids.add(asteroid);
        lastAsteroid_time = TimeUtils.nanoTime();

    }

}
