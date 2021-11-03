package com.andrew.game.screens;

import com.andrew.game.observer.SplashScreenListener;
import com.andrew.game.screens.menu.MainMenuScreen;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen, SplashScreenListener {

    private Slip game;
    private Texture texture;
    private SpriteBatch spriteBatch;

    public SplashScreen(Slip game) {
        this.game = game;

        System.out.println("splash screen constructor called");
    }

    @Override
    public void show() {
        System.out.println("splahs screen show called");
        texture = new Texture(Gdx.files.internal("images/temp splash screen.png"));
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.290196078f,
                0.31372549f,
                0.262745098f
                , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
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

    }

    @Override
    public void dispose() {
        System.out.println("Splashscreen dispose start");

        texture.dispose();

        spriteBatch.dispose();
        System.out.println("Splash screen dispose end");
    }

    @Override
    public void onComplete() {
        game.setScreen(new MainMenuScreen(game));
        dispose();

    }


}
