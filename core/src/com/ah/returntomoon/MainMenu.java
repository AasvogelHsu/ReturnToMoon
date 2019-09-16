package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class MainMenu implements Screen {


    private Texture texture;
    private Image bg,title;
    private Button button;
    private Skin skin;
    private Stage stage;

    public MainMenu(){

    }

    @Override
    public void show() {

        texture = new Texture(Gdx.files.internal("background.png"));
        bg = new Image(texture);
        skin = new Skin(Gdx.files.internal("menuelements.json")
                ,new TextureAtlas("menuelements.atlas"));
        title = new Image(skin,"title");
        title.setWidth(300);
        title.setHeight(400);
        title.setPosition(280,500);
        button = new Button(skin,"SpaceShipButton");
        button.setWidth(150);
        button.setHeight(150);

        button.setPosition(240,360);



        stage = new Stage();
        stage.addActor(bg);
        stage.addActor(title);
        stage.addActor(button);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act();
        stage.draw();



    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
        texture.dispose();




    }
}
