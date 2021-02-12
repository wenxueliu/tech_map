package org.wenxueliu.ds.decorator.beaverage;

/**
 * Created by liuwenxue on 18/04/2018.
 */
public class BlackTea extends Beverage {
    public String getDescription() {
        return "红茶";
    }
    public double cost() {
        return 10;
    }
}
