package com.andrew.game.player;

import com.andrew.game.enums.Event;
import com.andrew.game.statemachines.PlayerState;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.ThreadLocalRandom;

public class UpState implements PlayerState {
    private final Array<Sprite> up;
    private float slowdown = 1f;


    public UpState(TextureAtlas atlas, int spriteID) {
        up = atlas.createSprites(spriteID + "-up");
    }


    @Override
    public void init(GameObject gameObject) {
        gameObject.playerSprite = up.first();
    }

    @Override
    public void handleInput(GameObject gameObject) {
        if (gameObject.player.y + gameObject.blockWH <= gameObject.gameHeight - gameObject.blockWH) {
            Rectangle tempRect = new Rectangle(gameObject.player);
            tempRect.y += gameObject.blockWH;
            gameObject.blockIterator.reset();
            for (Rectangle x : gameObject.blockIterator) {
                if (x.overlaps(tempRect)) {
                    System.out.println("hitting block up state");
                    return;
                }
            }

            gameObject.dy = gameObject.speed;
            gameObject.playerSprite = ThreadLocalRandom.current().nextBoolean() ? up.get(1) : up.get(2);
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
        } else if (gameObject.player.y >= gameObject.gameHeight - gameObject.blockWH) {
            gameObject.player.y = gameObject.gameHeight - gameObject.blockWH;
            gameObject.dy = 0;
            gameObject.event(Event.IMPACT, Event.PLAYER_STOPPED);
            gameObject.playerSprite = up.first();
            //do something when the player is past the right wall
        } else {
            gameObject.blockIterator.reset();
            for (Rectangle rect : gameObject.blockIterator) {
                if (rect.overlaps(gameObject.player)) {
                    System.out.println("hitting block right state");
                    gameObject.dy = 0;
                    gameObject.player.y -= gameObject.player.y % gameObject.blockWH;
                    gameObject.event(Event.IMPACT, Event.PLAYER_STOPPED);
                    gameObject.playerSprite = up.first();
                    //do something if the player overlaps a block
                    return;
                }
            }
        }
    }

    @Override
    public boolean animateExitFirst(GameObject gameObject, float delta) {
        gameObject.player.y += gameObject.blockWH * delta * slowdown;
        slowdown = Math.max(.5f, slowdown - delta);
        return gameObject.player.y >= gameObject.exit.y;
    }

    @Override
    public boolean animateExitSecond(GameObject gameObject, float delta) {
        if (!(gameObject.player.y >= gameObject.exit.y + gameObject.blockWH / 2f)) {
            gameObject.player.y += gameObject.blockWH * .5f * delta;
            return false;
        }
        gameObject.event(Event.EXIT_ANIM_FINISHED);
        return true;
    }
}
