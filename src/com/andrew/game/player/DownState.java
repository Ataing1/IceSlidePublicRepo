package com.andrew.game.player;

import com.andrew.game.enums.Event;
import com.andrew.game.statemachines.PlayerState;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

public class DownState implements PlayerState {
    private final Array<Sprite> down;
    private float slowdown = 1f;

    public DownState(TextureAtlas atlas, int spriteID) {
        down = atlas.createSprites(spriteID + "-down");
    }

    @Override
    public void init(GameObject gameObject) {
        gameObject.playerSprite = down.first();
    }

    @Override
    public void handleInput(GameObject gameObject) {

        if (gameObject.player.y - gameObject.blockWH >= 0) {
            Rectangle tempRect = new Rectangle(gameObject.player);
            tempRect.y -= gameObject.blockWH;
            gameObject.blockIterator.reset();
            for (Rectangle rect : gameObject.blockIterator) {
                if (rect.overlaps(tempRect)) {
                    System.out.println("hitting block Down state");
                    return;
                }
            }

            gameObject.dy = -gameObject.speed;
            gameObject.playerSprite = ThreadLocalRandom.current().nextBoolean() ? down.get(1) : down.get(2);
            gameObject.steps++;
            gameObject.event(Event.LEGAL_MOVE);
        }
    }

    @Override
    public void update(GameObject gameObject, float delta) {
        if (gameObject.dy == 0) return;
        gameObject.player.y += gameObject.dy * delta;
        if (gameObject.player.overlaps(gameObject.exit)) {
            gameObject.event(Event.HIT_EXIT);
            //do something when the player overlaps exit
        } else if (gameObject.player.y < 0) {
            gameObject.player.y = 0;
            gameObject.dy = 0;
            System.out.println("notify down wall");
            gameObject.event(Event.PLAYER_STOPPED, Event.IMPACT);
            gameObject.playerSprite = down.first();
            //do something when the player is past the right wall
        } else {
            gameObject.blockIterator.reset();
            for (Rectangle rect : gameObject.blockIterator) {
                if (rect.overlaps(gameObject.player)) {
                    System.out.println("hitting block right state");
                    gameObject.dy = 0;
                    gameObject.player.y += (gameObject.blockWH - (gameObject.player.y % gameObject.blockWH));
                    gameObject.event(Event.PLAYER_STOPPED, Event.IMPACT);
                    gameObject.playerSprite = down.first();
                    //do something if the player overlaps a block
                    return;
                }
            }
        }


    }

    @Override
    public boolean animateExitFirst(GameObject gameObject, float delta) {
        gameObject.player.y -= gameObject.blockWH * delta * slowdown;
        slowdown = Math.max(.5f, slowdown - delta);
        return gameObject.player.y <= gameObject.exit.y;
    }

    @Override
    public boolean animateExitSecond(GameObject gameObject, float delta) {
        return true;
    }
}
