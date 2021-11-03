package com.andrew.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class ParallaxBackground extends Actor {

    private int scroll;
    private final Array<Texture> layers;
    private final int LAYER_SPEED_DIFFERENCE = 3;

    final float x;
    final float y;
    final float width;
    final float heigth;
    final float scaleX;
    final float scaleY;
    final int originX;
    final int originY;
    final int rotation;
    int srcX;
    final int srcY;
    final boolean flipX;
    final boolean flipY;

    private int speed;

    public ParallaxBackground(Array<Texture> textures) {
        layers = textures;
        for (int i = 0; i < textures.size; i++) {
            layers.get(i).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        }
        scroll = 0;
        speed = 0;

        x = y = originX = originY = rotation = srcY = 0;
        width = Gdx.graphics.getWidth();
        heigth = Gdx.graphics.getHeight();
        scaleX = scaleY = 1;
        flipX = flipY = false;
    }

    public void setSpeed(int newSpeed) {
        speed = newSpeed;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        scroll += speed;
        for (int i = 0; i < layers.size; i++) {
            /*srcX = scroll + i * LAYER_SPEED_DIFFERENCE * scroll;*/
            srcX = (int) (scroll + i * LAYER_SPEED_DIFFERENCE * scroll * .05f);
            batch.draw(layers.get(i), x, y, originX, originY, width, heigth, scaleX, scaleY, rotation, srcX, srcY, layers.get(i).getWidth(), layers.get(i).getHeight(), flipX, flipY);
        }
    }
}