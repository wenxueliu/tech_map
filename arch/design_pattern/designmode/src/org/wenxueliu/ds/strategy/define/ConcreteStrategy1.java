package org.wenxueliu.ds.strategy.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteStrategy1 implements IStrategy {
    @Override
    public void doSth() {
        System.out.println("strategy 1");
    }
}
