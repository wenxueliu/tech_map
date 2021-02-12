package org.wenxueliu.ds.decorator.beaverage;

/**
 * Created by liuwenxue on 18/04/2018.
 */
public class Mango extends Condiment {
    private Beverage bevarage;
    public Mango(Beverage bevarage) {
        this.bevarage = bevarage;
    }
    public String getDescription() {
        return bevarage.getDescription() + ", 加芒果";
    }
    public double cost() {
        return this.bevarage.cost() + 3; // 加芒果需要 3 元
    }
}
