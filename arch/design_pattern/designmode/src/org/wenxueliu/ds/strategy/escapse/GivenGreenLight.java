package org.wenxueliu.ds.strategy.escapse;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class GivenGreenLight implements IStrategy {
    @Override
    public void operate() {
        System.out.println("give green light");
    }
}
