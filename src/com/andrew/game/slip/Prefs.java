package com.andrew.game.slip;

public enum Prefs {


    MUSIC("music"),
    SOUND_FX("soundFX"),
    VIBRATE("vibrate"),
    //todo lives, gamemode, and teh casual and challenge things need to be reset if the user logs, out, or somehow, I need to store those in the firestore as well.
    //  *maybe it's time to tie the game to people's google play, and apple becuase this is an issue
    CHALLENGE_SEED("challengeLevel"),
    CASUAL_SEED("casualLevel"),
    GAME_MODE("gameMode1"),
    LOGGED_OUT("loggedOut"),

    EMAIL("email");


    public final String label;

    Prefs(String label) {
        this.label = label;
    }

}
