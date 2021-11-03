package com.andrew.game.player;

import com.andrew.game.enums.Event;
import com.andrew.game.statemachines.PlayerState;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

public class LeftState implements PlayerState {
    private final Array<Sprite> left;
    private float slowdown = 1f;

    public LeftState(TextureAtlas atlas, int spriteID) {
        left = atlas.createSprites(spriteID + "-left");
    }

    @Override
    public void init(GameObject gameObject) {
        gameObject.playerSprite = left.first();
    }

    @Override
    public void handleInput(GameObject gameObject) {
        gameObject.playerSprite = left.first();

        if (gameObject.player.x - gameObject.blockWH >= 0) {
            Rectangle tempRect = new Rectangle(gameObject.player);
            tempRect.x -= gameObject.blockWH;
            gameObject.blockIterator.reset();
            for (Rectangle rect : gameObject.blockIterator) {
                if (rect.overlaps(tempRect)) {
                    System.out.println("hitting block right state");
                    return;
                }
            }

            gameObject.dx = -gameObject.speed;
            gameObject.playerSprite = ThreadLocalRandom.current().nextBoolean() ? left.get(1) : left.get(2);
            gameObject.steps++;
            gameObject.event(Event.LEGAL_MOVE);
        }
    }

    @Override
    public void update(GameObject gameObject, float delta) {
        if (gameObject.dx == 0) return;
        gameObject.player.x += gameObject.dx * delta;
        if (gameObject.player.overlaps(gameObject.exit)) {
            gameObject.event(Event.HIT_EXIT);
            //do something when the player overlaps exit
        } else if (gameObject.player.x < 0) {
            gameObject.player.x = 0;
            gameObject.dx = 0;
            gameObject.event(Event.IMPACT, Event.PLAYER_STOPPED);
            gameObject.playerSprite = left.first();
        } else {
            gameObject.blockIterator.reset();
            for (Rectangle rect : gameObject.blockIterator) {
                if (rect.overlaps(gameObject.player)) {
                    System.out.println("hitting block right state");
                    gameObject.dx = 0;
                    gameObject.player.x += (gameObject.blockWH - (gameObject.player.x % gameObject.blockWH));
                    gameObject.event(Event.IMPACT, Event.PLAYER_STOPPED);
                    gameObject.playerSprite = left.first();
                    return;
                }
            }
        }


    }

    @Override
    public boolean animateExitFirst(GameObject gameObject, float delta) {
        gameObject.player.x -= gameObject.blockWH * delta * slowdown;
        slowdown = Math.max(.5f, slowdown - delta);
        return gameObject.player.x <= gameObject.exit.x;
    }

    @Override
    public boolean animateExitSecond(GameObject gameObject, float delta) {
        return true;
    }
}
