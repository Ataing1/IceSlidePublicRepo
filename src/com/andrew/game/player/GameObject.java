package com.andrew.game.player;

import com.andrew.game.MazeJson;
import com.andrew.game.StringPoint;
import com.andrew.game.enums.Event;
import com.andrew.game.observer.Subject;
import com.andrew.game.statemachines.PlayerState;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameObject extends Subject implements GestureDetector.GestureListener {

    private static UpState upState;
    private static DownState downState;
    private static LeftState leftState;
    private static RightState rightState;
    private final Sprite iceSprite;
    private final Sprite blockSprite;
    private final Sprite exitSprite;
    private boolean gameOver = false;
    public boolean exit1 = false;
    public boolean exit2 = false;
    protected int dx, dy;
    protected final int speed;
    protected final int blockWH;
    protected final int gameWidth;
    protected final int gameHeight;
    protected int steps;
    protected Sprite playerSprite;
    protected Rectangle player;
    protected Rectangle exit;
    protected Array.ArrayIterator<Rectangle> blockIterator;
    private Array.ArrayIterator<Rectangle> iceIterator;
    private PlayerState playerState;


    public GameObject(TextureAtlas textureAtlas, int spriteID, int gameWidth, int gameHeight, int blockWH, MazeJson mazeJson, int high, int wide) {
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
        this.blockWH = blockWH;

        upState = new UpState(textureAtlas, spriteID);
        downState = new DownState(textureAtlas, spriteID);
        leftState = new LeftState(textureAtlas, spriteID);
        rightState = new RightState(textureAtlas, spriteID);

        iceSprite = textureAtlas.createSprite("ice-tile-2 X128");
        blockSprite = textureAtlas.createSprite("ice-block-3");
        exitSprite = textureAtlas.createSprite("ladder-hole-1.0 X128");

        dx = 0;
        dy = 0;
        speed = blockWH * 5;
        readMazeJson(mazeJson, high, wide);
        setPlayerState(downState);

    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        this.playerState.init(this);
    }

    public void animateExit(float delta) {
        if (!exit1) {
            if (playerState.animateExitFirst(this, delta)) {
                exit1 = true;
                setPlayerState(upState);
            }
        } else if (!exit2) {
            if (playerState.animateExitSecond(this, delta)) {
                exit2 = true;
            }
        }
    }

    public void handleInput() {
        playerState.handleInput(this);
    }

    private boolean isMoving() {
        return dx != 0 || dy != 0;
    }

    public int getSteps() {
        return steps;
    }

    public void addExtraSteps() {
        steps -= 5;
    }

    public void update(float delta) {
        playerState.update(this, delta);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        blockIterator.reset();
        iceIterator.reset();
        for (Rectangle ice : iceIterator) {
            spriteBatch.draw(iceSprite, ice.x, ice.y, blockWH, blockWH);
        }
        for (Rectangle block : blockIterator) {
            spriteBatch.draw(blockSprite, block.x, block.y, blockWH, blockWH);
        }
        spriteBatch.draw(exitSprite, exit.x, exit.y, blockWH, blockWH);
        spriteBatch.draw(playerSprite, player.x, player.y, blockWH, blockWH);
        spriteBatch.end();
    }

    public void event(Event... a) {
        for (Event event : a) {
            notify(event);
        }
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (isMoving() || gameOver) {
            return true; //don't do anything with the fling event if the player is already moving
        }
        System.out.println("fling");
        int SWIPE_VELOCITY_THRESHOLD = 100;
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (velocityX > 0) {
                    setPlayerState(rightState);
                } else {
                    setPlayerState(leftState);
                }
                handleInput();
                return true;
            }
        } else {
            if (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (velocityY > 0) {
                    setPlayerState(downState);
                } else {
                    setPlayerState(upState);
                }
                handleInput();
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void readMazeJson(MazeJson mazeJson, int NUMBER_OF_BLOCKS_HIGH, int NUMBER_OF_BLOCKS_WIDE) {
        System.out.println("reading maze json");
        int x, y;
        x = Integer.parseInt(mazeJson.endPoint.x);
        y = NUMBER_OF_BLOCKS_HIGH - 1 - Integer.parseInt(mazeJson.endPoint.y); //coordinates are from teh top left not bottom left
        System.out.println("spawnExit " + x + " " + y);
        exit = new Rectangle(x * blockWH, y * blockWH, blockWH, blockWH);
        x = Integer.parseInt(mazeJson.startPoint.x);
        y = NUMBER_OF_BLOCKS_HIGH - 1 - Integer.parseInt(mazeJson.startPoint.y);//coordinates are from teh top left not bottom left
        System.out.println("spawnPlayer " + x + " " + y);
        player = new Rectangle(x * blockWH, y * blockWH, blockWH, blockWH);
        Array<Rectangle> array = new Array<>();
        for (StringPoint stringPoint : mazeJson.blockCoords) {
            x = Integer.parseInt(stringPoint.x);
            y = Integer.parseInt(stringPoint.y); //already calculated during txt file parsing
            array.add(new Rectangle(x * blockWH, y * blockWH, blockWH, blockWH));

        }
        blockIterator = new Array.ArrayIterator<>(array);
        Array<Rectangle> ice = new Array<>();
        for (int iceX = 0; iceX < NUMBER_OF_BLOCKS_WIDE; iceX++) {
            for (int iceY = 0; iceY < NUMBER_OF_BLOCKS_HIGH; iceY++) {
                ice.add(new Rectangle(iceX * blockWH, iceY * blockWH, blockWH, blockWH));

            }
        }
        iceIterator = new Array.ArrayIterator<>(ice);
    }
}

