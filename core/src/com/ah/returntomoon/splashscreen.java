package com.ah.returntomoon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class splashscreen implements Screen {

    private Starter game;
    private Texture texture = new Texture(Gdx.files.internal("splashScreen.png"));
    private Stage stage = new Stage();
    private Image image = new Image(texture);

    public splashscreen(Starter game){
        this.game = game;
    }

    @Override
    public void show() {
        stage.addActor(image);
            image.addAction(Actions.sequence(
                    Actions.alpha(0),
                    Actions.fadeIn(2f),
                    Actions.delay(1f),
                    Actions.fadeOut(2f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new MainMenu(game));
                        }
                    })
                    ));

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
        stage.dispose();
        texture.dispose();
    }
}
