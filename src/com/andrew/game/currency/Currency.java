package com.andrew.game.currency;

import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.slip.Assets;
import com.andrew.game.statemachines.CoinState;
import com.andrew.game.statemachines.GemState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;



public class Currency extends Actor {

    private int coins;

    private CoinState coinState;
    private GemState gemState;
    private Label coinLabel;


    public Currency(int coins) {
        this.coins = coins;

        coinState = new DefaultCoinState();

    }

    public void resetCoinState(int coins) {
        this.coins = coins;
        coinState = new DefaultCoinState();
    }


    public void addCoins(int amount) {
        coinState.flush(coinLabel, this);
        coinState = new AddCoinState(coins, amount);
    }

    public void subCoins(int amount) {
        coinState.flush(coinLabel, this);
        coinState = new SubtractCoinState(coins, amount);
    }


    public void flush() {
        coinState.flush(coinLabel, this);
    }

    @Override
    public void act(float delta) {

        coinState.update(coinLabel, this, delta);
        super.act(delta);

    }

    public Table getCurrencyTable(Skin mySkin, Assets assets) {
        flush(); //finishes adding coins, if coins were stil being added on the previous screen

        Image image = new Image(mySkin.getDrawable(Assets.COIN_IMAGE));
        coinLabel = new Label(String.valueOf(coins), new Label.LabelStyle(assets.buttonLightFont, Color.WHITE));
        coinLabel.setAlignment(Align.center);
        coinLabel.setWrap(false);
        image.setScaling(Scaling.fit);
        Table coinTable = new Table();
        coinTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT);
        coinTable.left();
        coinTable.add(image).size(BaseMenuScreen.BUTTON_SIZE);
        Container<Label> container = new Container<>(coinLabel);
        container.setBackground(mySkin.getDrawable("NoTitlePurplePink64"));
        coinTable.add(container).width(82.78095238f * Gdx.graphics.getDensity());
        //coinTable.setBackground(mySkin.getDrawable("purple window"));
        return coinTable;


    }

}
