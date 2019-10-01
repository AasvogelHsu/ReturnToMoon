package com.ah.returntomoon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameView extends ScreenAdapter {

    SpriteBatch batch;
    Texture bg1,bg2;
    float yMax,yCoordBg1,yCoordBg2;
    int BACKGROUND_MOVE_SPEED = 100;

    public GameView(){}


    @Override
    public void show() {

        batch = new SpriteBatch();

        bg1 = new Texture(Gdx.files.internal("bg_space01.jpg"));
        bg2 = new Texture(Gdx.files.internal("bg_space01r.jpg"));
        yMax = 1280;
        yCoordBg1 = yMax;
        yCoordBg2 = 0;

    }

    @Override
    public void render(float delta) {
        System.out.println(delta+"");
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        yCoordBg1 -= BACKGROUND_MOVE_SPEED*Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 - yMax;
        if (yCoordBg1 <= 0){
            yCoordBg1 = yMax;
            yCoordBg2 = 0;
        }

        batch.begin();
        batch.draw(bg1,0,yCoordBg1);
        batch.draw(bg2,0,yCoordBg2);
        batch.end();

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

    }

}
