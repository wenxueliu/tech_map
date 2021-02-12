package org.wenxueliu.ds.observer;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class ConcreateObserver1 implements Observer {
    @Override
    public void update(String update) {
        System.out.println("observer1 observe : " + update);
    }
}
