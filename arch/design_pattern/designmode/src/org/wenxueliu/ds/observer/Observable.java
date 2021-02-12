package org.wenxueliu.ds.observer;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public interface Observable {
    void addObserver(Observer observer);
    void deleteObserver(Observer observer);
    void notifyObserver(String update);
}
