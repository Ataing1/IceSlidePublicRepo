package com.andrew.game.interfaces;

public interface FireBaseAnalytics {

    void logLevelEvent(int level, boolean success);

    /**
     * @param value    amount of inGameCurrency earned
     * @param currency type of ingameCurrency earned
     */
    void logEarnVirtualCurrency(int value, String currency);

    /**
     * @param item     what you spent currency on
     * @param value    how much it cost
     * @param currency type of currency
     */
    void logSpendVirtualCurrency(String item, int value, String currency);

    void lifeUpdate(int gainOrLoss);
}
