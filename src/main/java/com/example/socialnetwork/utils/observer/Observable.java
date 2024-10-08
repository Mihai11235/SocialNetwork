package com.example.socialnetwork.utils.observer;

import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.utils.events.Event;

import java.util.ArrayList;
import java.util.List;

public class Observable<E extends Event> {

    protected List<Observer<E>> observers = new ArrayList<>();
    public void addObserver(Observer<E> o){
        observers.add(o);
    }

    public void removeObserver(Observer<E> e){
        observers.remove(e);
    }

    public void notifyObservers(E t){
        observers.forEach(o -> o.update(t));
    }
}
