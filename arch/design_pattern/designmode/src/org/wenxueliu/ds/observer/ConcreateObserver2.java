package org.wenxueliu.ds.observer;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class ConcreateObserver2 implements Observer {
    @Override
    public void update(String update) {
        System.out.println("observer2 observe : " + update);
    }
}
