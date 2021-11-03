package com.andrew.game.UI;

import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.screens.menu.BaseMenuScreen;
import com.andrew.game.slip.Slip;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Locale;

public class DailyRewardWindow extends Dialog {

    private final Skin mySkin;
    private final float width;
    private final float height;
    private final MyCallbackEmpty myCallbackEmpty;
    final DailyRewardWindow dailyRewardWindow = this;

    public DailyRewardWindow(Slip game, Skin mySkin, MyCallbackEmpty myCallbackEmpty) {
        super("", mySkin);
        this.mySkin = mySkin;
        this.myCallbackEmpty = myCallbackEmpty;
        //validate();
        setModal(true);
        setMovable(false);
        pad(BaseMenuScreen.DEFAULT_UNIT);

        fillContent(mySkin, game.accountManager.getAccountObject().getStreak());
        width = super.getPrefWidth();
        height = super.getPrefHeight();
    }

    @Override
    public float getPrefWidth() {
        return width;
    }

    @Override
    public float getPrefHeight() {
        return height;
    }

    private void fillContent(Skin mySkin, int streak) {
        final ScrollWheel scrollWheel = new ScrollWheel(mySkin.getDrawable("dailyrewardspinner crystal"), mySkin.getDrawable("gradient-2"));
        getButtonTable().defaults().grow().pad(BaseMenuScreen.DEFAULT_UNIT);
        Table contentTable = getContentTable();
        contentTable.defaults().pad(BaseMenuScreen.DEFAULT_UNIT).grow();
        //contentTable.pad(BaseMenuScreen.DEFAULT_UNIT);
        contentTable.add(new Label(String.format(Locale.US, "Daily reward streak: %d", streak), mySkin, "titleFont"));
        contentTable.row();
        contentTable.add(scrollWheel);
        contentTable.row();
        TextButton textButton = new TextButton("Spin", this.mySkin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scrollWheel.setFlingTime(5f);
                scrollWheel.fling(5000f, new MyCallbackEmpty() {
                    @Override
                    public void onCallback() {
                        myCallbackEmpty.onCallback();
                        dailyRewardWindow.hide();

                    }
                });

            }
        });
        contentTable.add(textButton).grow();
        TextButton closeButton = new TextButton("Close", mySkin);
        button(closeButton);

    }

}
