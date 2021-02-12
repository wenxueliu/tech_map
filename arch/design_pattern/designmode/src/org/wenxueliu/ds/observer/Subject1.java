package org.wenxueliu.ds.observer;

import com.sun.org.apache.bcel.internal.generic.ISUB;
import org.wenxueliu.ds.strategy.define.IStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Subject1 implements ISubject, Observable {

    List<Observer> observers = new ArrayList<>();

    @Override
    public void doSth() {
        System.out.println("do sth");
        this.notifyObserver("do sth");
    }

    @Override
    public void addObserver(Observer observer) {
       this.observers.add(observer);
    }

    @Override
    public void deleteObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObserver(String update) {
        for(Observer ob : this.observers) {
            ob.update(update);
        }
    }
}
