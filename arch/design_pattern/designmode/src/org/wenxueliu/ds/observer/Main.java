package org.wenxueliu.ds.observer;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Subject1 s = new Subject1();
        Observer b1 = new ConcreateObserver1();
        Observer b2 = new ConcreateObserver2();
        Observer b3 = new ConcreateObserver3();
        s.addObserver(b1);
        s.addObserver(b2);
        s.addObserver(b3);
        s.doSth();
    }
}
