package com.andrew.game.observer;

import com.andrew.game.enums.MenuEvent;

public interface ObserverMenu {

    void onNotify(MenuEvent event);

}
