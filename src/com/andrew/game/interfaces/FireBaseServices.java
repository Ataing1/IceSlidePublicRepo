package com.andrew.game.interfaces;

import com.andrew.game.AccountObject;

import java.util.Date;

public interface FireBaseServices {

    //Ad starts

    //void showDailyRewardAd(MyCallbackAd myCallbackAd);

    void showExtraMovesAd(MyCallbackAd myCallbackAd);

    void showSkipLevelAd(MyCallbackAd myCallbackAd);

    void showExtraLivesAd(MyCallbackAd myCallbackAd);

    void showInterstitialAd(MyCallbackAd myCallbackAd);

    void loadExtraMovesRewardedAd();

    //void loadDailyRewardAd();

    void loadSkipLevelAd();

    void loadExtraLivesAd();

    void loadInterstitialAd();

    //Ad End

    //Firebase Start
    void getLauncherStatus(MyCallbackIntent myCallbackIntent);

    void signInAnonymously(MyCallbackBoolean myCallbackBoolean);

    boolean hasUser();

    boolean isAnonymous();

    void anonymousToPermanentEmail(String email, String password, MyCallbackBoolean myCallbackBoolean);

    void loginUserWithEmailAndPassword(String email, String password, MyCallbackBoolean myCallbackBoolean);

    void SendEmailSignUp(String email, MyCallbackBoolean myCallbackBoolean);

    void ProcessDeepLink(String email, MyCallbackBoolean myCallbackBoolean);

    void usernameExists(String username, MyCallbackBoolean myCallbackBoolean);

    void loadAccountObject(MyCallbackAccountData myCallbackAccountData);

    void updateAccountObject(AccountObject accountObject);

    void resetPassword(String email, MyCallbackBoolean myCallbackBoolean);

    void logOut(MyCallbackBoolean myCallbackBoolean);

    void saveData();

    //Firebase End

    //Android Utility Start

    int checkDaily(Date date);

    void toast(String message);

    void test();

    //Android Utility End


}
