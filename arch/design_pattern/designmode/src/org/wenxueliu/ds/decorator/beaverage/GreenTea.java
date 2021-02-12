package org.wenxueliu.ds.decorator.beaverage;

/**
 * Created by liuwenxue on 18/04/2018.
 */
public class GreenTea extends Beverage {
    public String getDescription() {
        return "绿茶";
    }
    public double cost() {
        return 11;
    }
}
