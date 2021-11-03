package com.andrew.game.slip;

import com.andrew.game.AccountObject;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;



public class Trophy {

    private Label trophyLabel;
    private AccountObject accountObject;
    private Slip game;

    public Trophy(AccountObject accountObject, Slip game) {
        this.accountObject = accountObject;
        this.game = game;
        trophyLabel = new Label(String.valueOf(accountObject.getTrophy()), new Label.LabelStyle(game.getAssets().buttonLightFont, Color.WHITE));
    }

    public Table getTrophyTable(Skin mySkin, Assets assets) {

        Image image = new Image(mySkin.getDrawable("trophy Image"));

        trophyLabel.setAlignment(Align.center);
        trophyLabel.setWrap(false);
        image.setScaling(Scaling.fit);
        Table coinTable = new Table();
        coinTable.defaults().space(BaseMenuScreen.DEFAULT_UNIT);
        coinTable.left();
        coinTable.add(image).size(BaseMenuScreen.BUTTON_SIZE);
        Container<Label> container = new Container<>(trophyLabel);
        container.setBackground(mySkin.getDrawable("NoTitlePurpleBlue64"));
        coinTable.add(container).width(82.78095238f * Gdx.graphics.getDensity());

        return coinTable;


    }

    public void update() {
        trophyLabel.setText(String.valueOf(accountObject.getTrophy()));
    }

}
