package com.andrew.game.observer;

import com.andrew.game.enums.SlipMessage;

public class SlipNotifier {

    private final SlipListener slipListener;

    public SlipNotifier(SlipListener slipListener) {
        this.slipListener = slipListener;
    }

    public void sendMessage(SlipMessage slipMessage) {
        slipListener.onEventReceived(slipMessage);
    }
}
