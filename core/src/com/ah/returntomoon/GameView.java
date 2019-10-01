package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameView extends ScreenAdapter {

    SpriteBatch batch;
    Texture bg1,bg2,rocketSheet;
    float yMax,yCoordBg1,yCoordBg2,stateTime;
    int BACKGROUND_MOVE_SPEED = 80;
    Animation<TextureRegion> rocket;
    final int FRAME_COLS = 4,FRAME_ROWS = 1;
    int RocketX,RocketY;


    public GameView(){}


    @Override
    public void show() {

        batch = new SpriteBatch();
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

        RocketX = Constant.WIDTH/2;
        RocketY = Constant.HEIGHT/8;

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

        batch.begin();
        batch.draw(bg1,0,yCoordBg1);
        batch.draw(bg2,0,yCoordBg2);
        batch.draw(currentFrame,RocketX-50,RocketY-75,100,150);
        batch.end();

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


    }

}
