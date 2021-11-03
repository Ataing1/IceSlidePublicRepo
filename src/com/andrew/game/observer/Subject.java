package com.andrew.game.observer;

import com.andrew.game.enums.Event;

import java.util.ArrayList;

public class Subject {

    private final ArrayList<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notify(Event event) {
        for (Observer observer : observers) {
            observer.onNotify(event);
        }
    }
}


