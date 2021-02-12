package org.wenxueliu.ds.singleton;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Singleton {
    private static final Singleton singleton = new Singleton();

    private Singleton() {

    }

    public Singleton getSingleton() {
        return singleton;
    }

    //类中其他方法尽量是 static
    public static void doSth() {

    }
}
