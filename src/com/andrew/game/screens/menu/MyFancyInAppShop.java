package com.andrew.game.screens.menu;

import com.andrew.game.slip.Slip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class MyFancyInAppShop extends Dialog {

    // public static final String MY_ENTITLEMENT = "entitlement_sku";
    // public static final String MY_CONSUMABLE = "consumable_sku";
    public static final String AD_FREE_199 = "ad_free_199";

    private final Slip game;
    private final Skin mySkin;
    private final TextButton restoreButton;

    private boolean restorePressed;
    // private IapButton buyEntitlement;
    // private IapButton buyConsumable;
    private IapButton buyAdFree;

    private BaseMenuScreen baseMenuScreen;

    public MyFancyInAppShop(final Slip game, Skin mySkin, final BaseMenuScreen baseMenuScreen) {
        super("", mySkin);
        this.game = game;
        this.mySkin = mySkin;
        this.baseMenuScreen = baseMenuScreen;
        pad(BaseMenuScreen.DEFAULT_UNIT);
        getContentTable().pad(BaseMenuScreen.DEFAULT_UNIT);
        getContentTable().defaults().pad(BaseMenuScreen.DEFAULT_UNIT).grow();

        getButtonTable().pad(BaseMenuScreen.DEFAULT_UNIT);
        getButtonTable().defaults().pad(BaseMenuScreen.DEFAULT_UNIT).grow();
        TextButton close = new TextButton("Close", mySkin);
        close.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                baseMenuScreen.buttonClickedSound();
            }
        });
        button(close);

        restoreButton = new TextButton("Restore", mySkin);
        restoreButton.setDisabled(true);
        restoreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                baseMenuScreen.buttonClickedSound();
                restorePressed = true;
                restoreButton.setDisabled(true);
                game.purchaseManager.purchaseRestore();
            }
        });


        getButtonTable().add(restoreButton);

        //
        //Build GUI like this for now
        fillContent(baseMenuScreen);

        // den Init lostreten so früh es geht, aber nicht bevor die GUI-Referenzen existieren :-)
        initPurchaseManager();
    }

    private void fillContent(BaseMenuScreen baseMenuScreen) {
        Table contentTable = getContentTable();
        contentTable.pad(10);
        Label title = new Label("Shop", mySkin);
        title.setAlignment(Align.center);
        contentTable.add(title);
        contentTable.row();
        Table iapTable = new Table();
        iapTable.defaults().fillX().uniform().expandX();
        //buyEntitlement = new IapButton(MY_ENTITLEMENT, 179);
        //iapTable.add(buyEntitlement);
        //iapTable.row();
        //buyConsumable = new IapButton(MY_CONSUMABLE, 349);
        //iapTable.add(buyConsumable);
        //iapTable.row();
        buyAdFree = new IapButton(AD_FREE_199, 199);
        iapTable.add(buyAdFree).grow();


        contentTable.add(iapTable);
    }

    private void initPurchaseManager() {
        // the purchase manager config here in the core project works if your SKUs are the same in every
        // payment system. If this is not the case, inject them like the PurchaseManager is injected
        PurchaseManagerConfig pmc = new PurchaseManagerConfig();
        //pmc.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(MY_ENTITLEMENT));
        //pmc.addOffer(new Offer().setType(OfferType.CONSUMABLE).setIdentifier(MY_CONSUMABLE));
        pmc.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(AD_FREE_199));

        game.purchaseManager.install(new MyPurchaseObserver(), pmc, true);
    }

    private void updateGuiWhenPurchaseManInstalled(String errorMessage) {
        // einfüllen der Infos
        //buyEntitlement.updateFromManager();
        //buyConsumable.updateFromManager();
        buyAdFree.updateFromManager();

        if (game.purchaseManager.installed() && errorMessage == null) {
            restoreButton.setDisabled(false);
        } else {
            errorMessage = (errorMessage == null ? "Error instantiating the purchase system" : errorMessage);

            //TODO show dialog here (happens when no internet connection available)
        }

    }

    private class IapButton extends TextButton {
        private final String sku;
        private final int usdCents;

        public IapButton(String sku, int usdCents) {
            super("Ad Free", mySkin);
            this.sku = sku;
            this.usdCents = usdCents;

            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    baseMenuScreen.buttonClickedSound();
                    buyItem();
                }
            });
        }

        private void buyItem() {
            game.purchaseManager.purchase(sku);
        }

        public void setBought(boolean fromRestore) {
            setDisabled(true);
        }

        public void updateFromManager() {
            Information skuInfo = game.purchaseManager.getInformation(sku);

            if (skuInfo == null || skuInfo.equals(Information.UNAVAILABLE)) {
                setDisabled(true);
                setText("Not available");
            } else {
                if (sku.equals(AD_FREE_199)) {
                    setText("Ad Free " + skuInfo.getLocalPricing());
                }
                //setText(skuInfo.getLocalName() + " " + skuInfo.getLocalPricing());
            }
        }
    }

    private class MyPurchaseObserver implements PurchaseObserver {

        @Override
        public void handleInstall() {
            Gdx.app.log("IAP", "Installed");

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    updateGuiWhenPurchaseManInstalled(null);
                }
            });
        }

        @Override
        public void handleInstallError(final Throwable e) {
            Gdx.app.error("IAP", "Error when trying to install PurchaseManager", e);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    updateGuiWhenPurchaseManInstalled(e.getMessage());
                }
            });
        }

        @Override
        public void handleRestore(Transaction[] transactions) {
            if (transactions != null && transactions.length > 0)
                for (Transaction t : transactions) {
                    handlePurchase(t, true);
                }
            else if (restorePressed)
                showErrorOnMainThread("Nothing to restore");
        }

        @Override
        public void handleRestoreError(Throwable e) {
            if (restorePressed)
                showErrorOnMainThread("Error restoring purchases: " + e.getMessage());
        }

        @Override
        public void handlePurchase(Transaction transaction) {
            handlePurchase(transaction, false);
        }

        protected void handlePurchase(final Transaction transaction, final boolean fromRestore) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (transaction.isPurchased()) {
                        //if (transaction.getIdentifier().equals(MY_ENTITLEMENT))
                        //    buyEntitlement.setBought(fromRestore);
                        //else if (transaction.getIdentifier().equals(MY_CONSUMABLE))
                        //    buyConsumable.setBought(fromRestore);
                        if (transaction.getIdentifier().equals(AD_FREE_199)) {
                            buyAdFree.setBought(fromRestore);//todo how do you prevent people from sending their account to one person who has purchased adfree and restores it onto everyone elses account?
                            game.accountManager.setAdFree(true);
                        }

                    }
                }
            });
        }

        @Override
        public void handlePurchaseError(Throwable e) {
            showErrorOnMainThread("Error on buying:\n" + e.getMessage());
        }

        @Override
        public void handlePurchaseCanceled() {

        }

        private void showErrorOnMainThread(String message) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    // show a dialog here...
                }
            });
        }
    }
}
