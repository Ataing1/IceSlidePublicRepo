package com.andrew.game.observer;

import com.andrew.game.enums.SlipMessage;

public interface SlipListener {

    void onEventReceived(SlipMessage slipMessage);
}
