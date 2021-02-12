package org.wenxueliu.ds.strategy.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IStrategy s1 = new ConcreteStrategy1();
        Context c1 = new Context(s1);
        c1.doSth();
    }
}
