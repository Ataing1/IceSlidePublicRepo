package com.andrew.game.observer;

import com.andrew.game.enums.Event;

public interface Observer {

    void onNotify(Event event);
}
