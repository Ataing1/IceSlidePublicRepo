package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackEmpty;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ScrollWheel extends Actor {
    private final Drawable wheelDrawable;
    private final Drawable wheelShading;

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
    private MyCallbackEmpty myCallbackEmpty;

    public ScrollWheel(Drawable wheelDrawable, Drawable wheelShading) {
        this.wheelDrawable = wheelDrawable;
        this.wheelShading = wheelShading;


        wheelWidth = (int) wheelDrawable.getMinWidth();
        separator = wheelWidth;

        setWidth(wheelShading.getMinWidth());
        setHeight(wheelDrawable.getMinHeight());

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

    /**
     * @param velocity        velocity at which to fling the wheel
     * @param myCallbackEmpty call back used to notify daily reward window when the scroll stops spinning
     */
    public void fling(float velocity, MyCallbackEmpty myCallbackEmpty) {

        flingTimer = flingTime;
        velocityX = velocity;
        this.myCallbackEmpty = myCallbackEmpty;
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
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
        } else {
            if (myCallbackEmpty != null) {
                myCallbackEmpty.onCallback();
                myCallbackEmpty = null;
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