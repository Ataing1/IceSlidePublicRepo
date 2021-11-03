package com.andrew.game.slip;

import java.util.concurrent.ThreadLocalRandom;

public class InterstitialManager {

    private int gamesPlayed;
    private final int gamesTillNextAd;

    public InterstitialManager() {
        gamesPlayed = 0;
        gamesTillNextAd = ThreadLocalRandom.current().nextInt(3, 6);
    }

    public boolean updateAndShow() {
        gamesPlayed++;
        if (gamesPlayed >= gamesTillNextAd) {
            gamesPlayed = 0;
            return true;
        }
        return false;
    }

}
