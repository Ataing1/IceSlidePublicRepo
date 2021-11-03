package com.andrew.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class DefaultScrollWheel extends Actor {
    private final Drawable wheelDrawable;
    private final Drawable wheelShading;
    private final Label label;

    private int unscaledScrollValueX = 0, scrollValueX = 0;
    private boolean isNotEdge;
    private int precision = 40;
    private int direction = 1;
    private int minValue = Integer.MIN_VALUE, maxValue = Integer.MAX_VALUE;
    // MANUAL SCROLL
    private int separator;
    private final int wheelWidth;
    // FLING
    private float flingTimer, flingTime = 1f;
    private float velocityX;

    public DefaultScrollWheel(Drawable wheelDrawable, Drawable wheelShading, Label label) {
        this.wheelDrawable = wheelDrawable;
        this.wheelShading = wheelShading;
        this.label = label;

        wheelWidth = (int) wheelDrawable.getMinWidth();
        separator = wheelWidth;

        setWidth(wheelDrawable.getMinWidth());
        setHeight(wheelDrawable.getMinHeight());

        // stops ScrollPane from overriding input events
        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };
        addListener(stopTouchDown);

        ActorGestureListener flickScrollListener = new ActorGestureListener() {
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                updateScroll(deltaX);
            }

            public void fling(InputEvent event, float x, float y, int button) {
                if (Math.abs(x) > 150) {
                    flingTimer = flingTime;
                    velocityX = x;
                }
            }

            public boolean handle(Event event) {
                if (super.handle(event)) {
                    if (((InputEvent) event).getType() == InputEvent.Type.touchDown) flingTimer = 0;
                    return true;
                }
                return false;
            }
        };
        addListener(flickScrollListener);
    }

    private void updateScroll(float delta) {
        unscaledScrollValueX += (delta * direction);
        scrollValueX = unscaledScrollValueX / precision;

        isNotEdge = true;
        if (scrollValueX <= minValue) {
            scrollValueX = minValue;
            unscaledScrollValueX = minValue * precision;
            isNotEdge = false;
        } else if (scrollValueX >= maxValue) {
            scrollValueX = maxValue;
            unscaledScrollValueX = maxValue * precision;
            isNotEdge = false;
        }
        if (isNotEdge) {
            separator += delta;
            if (separator <= 0) {
                separator = wheelWidth;
            } else if (separator >= wheelWidth) {
                separator = 0;
            }
        }

        updateLabel();
    }

    private void updateLabel() {
        label.setText("" + scrollValueX);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMinValueToNone() {
        minValue = Integer.MIN_VALUE;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMaxValueToNone() {
        minValue = Integer.MAX_VALUE;
    }

    public void setFlingTime(float flingTime) {
        this.flingTime = flingTime;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setRightPositiveDirection(boolean rightPositive) {
        direction = (rightPositive) ? 1 : -1;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        boolean animating = false;
        if (flingTimer > 0) {
            float alpha = flingTimer / flingTime;
            updateScroll(velocityX * alpha * delta);

            flingTimer -= delta;
            if (flingTimer <= 0) {
                velocityX = 0;
            }
            animating = true;
        }

        if (animating) {
            Stage stage = getStage();
            if (stage != null && stage.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.flush();
        if (clipBegin(getX(), getY(), getWidth(), getHeight())) {
            wheelDrawable.draw(batch, getX() + separator - wheelWidth, getY(), wheelDrawable.getMinWidth(), wheelDrawable.getMinHeight());
            wheelDrawable.draw(batch, getX() + separator, getY(), wheelDrawable.getMinWidth(), wheelDrawable.getMinHeight());
            wheelShading.draw(batch, getX(), getY(), wheelShading.getMinWidth(), wheelShading.getMinHeight());
            batch.flush();
            clipEnd();
        }
    }
}