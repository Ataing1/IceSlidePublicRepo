package com.andrew.game.screens.menu;

import com.andrew.game.enums.NavigationEvent;
import com.andrew.game.slip.Slip;

public class StoreScreen extends BaseMenuScreen {

    public StoreScreen(Slip game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        new MyFancyInAppShop(game, mySkin, this).show(stage);
    }

    @Override
    protected void switchScreen(NavigationEvent navigationEvent) {
        if (navigationEvent == NavigationEvent.STORE) {
            new MyFancyInAppShop(game, mySkin, this).show(stage);
            return;
        }

        super.switchScreen(navigationEvent);
    }
}
