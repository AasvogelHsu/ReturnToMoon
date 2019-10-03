package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameView extends ScreenAdapter {

    SpriteBatch batch;
    Texture bg1,bg2,rocketSheet,touchpadBg,touchpadKonb;
    float yMax,yCoordBg1,yCoordBg2,stateTime;
    int BACKGROUND_MOVE_SPEED = 80,ROCKET_SPEED = 5;
    Animation<TextureRegion> rocket;
    final int FRAME_COLS = 4,FRAME_ROWS = 1;
    int RocketX,RocketY;
    Touchpad touchpad;
    Stage stage;
    TextureRegionDrawable padBG,padKnob;
    Touchpad.TouchpadStyle touchpadStyle;



    public GameView(){}


    @Override
    public void show() {

        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        //↓背景捲動
        bg1 = new Texture("bg_space01.jpg");
        bg2 = new Texture("bg_space01r.jpg");
        yMax = 1280;
        yCoordBg1 = yMax;
        yCoordBg2 = 0;

        //↓Rocket動畫
        rocketSheet = new Texture("spaceShipSprite.png");
        TextureRegion[][] tmp = TextureRegion.split(rocketSheet,
                rocketSheet.getWidth()/FRAME_COLS,rocketSheet.getHeight()/FRAME_ROWS);
        TextureRegion[] RocketFrame = new TextureRegion[FRAME_COLS*FRAME_ROWS];
        int index = 0;
        for (int i=0 ;i<FRAME_ROWS ;i++){
            for (int j=0 ;j<FRAME_COLS ;j++){
                RocketFrame[index++] = tmp[i][j];
            }
        }
        rocket = new Animation<TextureRegion>(0.15f,RocketFrame);
        stateTime =0f;

        //↓Touchpad
        touchpadBg = new Texture("touchpad_bg.png");
        touchpadKonb = new Texture("touchpad_knob.png");
        padBG = new TextureRegionDrawable(touchpadBg);
        padKnob = new TextureRegionDrawable(touchpadKonb);
        touchpadStyle = new Touchpad.TouchpadStyle(padBG,padKnob);
        touchpad = new Touchpad(20f,touchpadStyle);
        touchpad.setBounds(Constant.WIDTH/2-75,Constant.HEIGHT/10-75,150,150);
        stage.addActor(touchpad);


        RocketX = Constant.WIDTH/2-50;
        RocketY = Constant.HEIGHT/8-75;

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
        TextureRegion currentFrame =rocket.getKeyFrame(stateTime,true);

        update();

        batch.begin();
        batch.draw(bg1,0,yCoordBg1);
        batch.draw(bg2,0,yCoordBg2);
        batch.draw(currentFrame,RocketX,RocketY,100,150);
        batch.end();

        stage.act();
        stage.draw();
        System.out.println(RocketX+" : "+RocketY);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        bg1.dispose();
        bg2.dispose();
        rocketSheet.dispose();
        touchpadBg.dispose();
        touchpadKonb.dispose();

    }
    public void update(){
        if(touchpad.isTouched()&& RocketX>=0 && RocketX<=Gdx.graphics.getWidth()-100){
            RocketX += touchpad.getKnobPercentX()*ROCKET_SPEED;
        }else if(RocketX<0){
            RocketX=0;
        }else if (RocketX>Gdx.graphics.getWidth()-100){
            RocketX=Gdx.graphics.getWidth()-100;
        }

        if (touchpad.isTouched() && RocketY>=0 && RocketY<=Gdx.graphics.getHeight()-150){
            RocketY += touchpad.getKnobPercentY()*ROCKET_SPEED;
        }else if(RocketY<0) {
            RocketY = 0;
        }else if(RocketY>Gdx.graphics.getHeight()-150){
            RocketY = Gdx.graphics.getHeight()-150;
        }

    }

}
