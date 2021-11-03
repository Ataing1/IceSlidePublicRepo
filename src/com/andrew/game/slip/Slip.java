package com.andrew.game.slip;

import com.andrew.game.AccountObject;
import com.andrew.game.enums.IntentStatus;
import com.andrew.game.interfaces.FireBaseAnalytics;
import com.andrew.game.interfaces.FireBaseServices;
import com.andrew.game.interfaces.MyCallbackAccountData;
import com.andrew.game.interfaces.MyCallbackBoolean;
import com.andrew.game.interfaces.MyCallbackEmpty;
import com.andrew.game.interfaces.MyCallbackIntent;
import com.andrew.game.interfaces.ServiceInterface;
import com.andrew.game.screens.SplashScreen;
import com.andrew.game.screens.gameplay.TutorialScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.PurchaseManager;

/**
 * TODO implement achievments like get a perfect score on 3 mazes or something like that
 */
public class Slip extends Game {

    public final FireBaseServices fireBaseServices;
    public final FireBaseAnalytics fireBaseAnalytics;
    public final ServiceInterface serviceInterface;
    public final AccountManager accountManager;
    public final InterstitialManager interstitialManager;
    public PurchaseManager purchaseManager;



    private Assets assets; //can't be final because Gdx is ot initiallzed yet, so you can't call getwidth
    private Preferences preferences; //same reason: Gdx.* hasn't been instantiated yet
    private final Slip game = this;
    private SplashScreen splashScreen;


    //todo splash screen?
    public Slip(FireBaseServices fireBaseServices, FireBaseAnalytics fireBaseAnalytics, ServiceInterface serviceInterface) {
        this.fireBaseServices = fireBaseServices;
        this.fireBaseAnalytics = fireBaseAnalytics;
        this.serviceInterface = serviceInterface;
        accountManager = new AccountManager(this);
        interstitialManager = new InterstitialManager();
    }


    public void create() {
        //todo what happens if the user starts offline?
        preferences = Gdx.app.getPreferences("My Preferences");
        assets = new Assets();
        System.out.println(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + " DPI: " + Gdx.graphics.getDensity());

        //FOR EMULATORS ONLY
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //accountManager.setAccountObject(new AccountObject());
        //setScreen(new MainMenuScreen(game));
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //todo game crashes if user logs out, then types a valid but incorrect email, and clicks the link from a previous successful login attempt ( can't log out if you aren't logged in).
        //  if email and preference email don't match, return user to tutorial and tell them "the emailed link email and the user entered email are not the same"

        fireBaseServices.getLauncherStatus(new MyCallbackIntent() {
            @Override
            public void onCallback(IntentStatus intentStatus) {
                switch (intentStatus) {
                    case DYNAMIC_LINK:
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                splashScreen = new SplashScreen(game);
                                setScreen(splashScreen);
                                fireBaseServices.ProcessDeepLink(preferences.getString(Prefs.EMAIL.label), new MyCallbackBoolean() {
                                    @Override
                                    public void onCallback(boolean status) {
                                        if (status) {
                                            loadAccountObject();
                                        } else {
                                            Gdx.app.postRunnable(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setScreen(new TutorialScreen(game, 1, true));
                                                    splashScreen.dispose(); //disposes splashscreen
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    case MAIN_LAUNCHER:
                        if (fireBaseServices.hasUser()) {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    splashScreen = new SplashScreen(game);
                                    setScreen(splashScreen);
                                    loadAccountObject();
                                }
                            });
                        } else {
                            System.out.println("brand new User, sending to tutorial Screen");
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    setScreen(new TutorialScreen(game, 1));
                                }
                            });
                        }
                        break;
                }
            }
        });
    }


    public void loadAccountObject() {
        System.out.println("load called");
        fireBaseServices.loadAccountObject(new MyCallbackAccountData() {
            @Override
            public void onCallBack(final AccountObject MaccountObject) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Loaded Data\n" + MaccountObject);
                        if (MaccountObject == null) {
                            System.out.println("no account object saved, making  a new one");
                            accountManager.setAccountObject(new AccountObject());
                        } else {
                            System.out.println("loading saved account object");
                            accountManager.setAccountObject(MaccountObject);
                        }
                        if (game.accountManager.getAccountObject().getEmail() == null && !game.fireBaseServices.isAnonymous()) { //only add an email if the user is not anonymous
                            game.accountManager.getAccountObject().setEmail(game.preferences.getString(Prefs.EMAIL.label));
                            game.accountManager.updateAccountObject("email");
                        }
                        if (splashScreen != null) {
                            splashScreen.onComplete();
                            splashScreen = null;
                        }
                    }
                });
            }
        });
    }

    public void loadAccountObject(final MyCallbackEmpty myCallbackEmpty) {
        System.out.println("load called");
        fireBaseServices.loadAccountObject(new MyCallbackAccountData() {
            @Override
            public void onCallBack(final AccountObject MaccountObject) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Loaded Data" + MaccountObject);
                        if (MaccountObject == null) {
                            System.out.println("no account object saved, making  a new one");
                            accountManager.setAccountObject(new AccountObject());
                        } else {
                            System.out.println("loading saved account object");
                            accountManager.setAccountObject(MaccountObject);
                        }
                        myCallbackEmpty.onCallback();
                    }
                });
            }
        });
    }

    @Override
    public void dispose() {
        System.out.println("game dispose called");
        assets.dispose();

    }

    public Assets getAssets() {
        return assets;
    }


    public Preferences getPreferences() {
        return preferences;
    }


    @Override
    public void pause() {
        super.pause();

    }

    @Override
    public void resume() {
        super.resume();
    }


}

