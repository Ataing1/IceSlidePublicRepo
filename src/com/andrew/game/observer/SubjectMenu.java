package com.andrew.game.observer;

import com.andrew.game.enums.MenuEvent;

import java.util.ArrayList;

public class SubjectMenu {

    private final ArrayList<ObserverMenu> observers = new ArrayList<>();

    public void addObserver(ObserverMenu observer) {
        observers.add(observer);
    }

    public void removeObserver(ObserverMenu observer) {
        observers.remove(observer);
    }

    protected void notify(MenuEvent event) {
        for (ObserverMenu observer : observers) {
            observer.onNotify(event);
        }
    }
}
