package org.wenxueliu.ds.decorator.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class Decorator implements Swan {

    private Swan swan;

    public Decorator(Swan swan) {
       this.swan = swan;
    }

    @Override
    public void cry() {
        swan.cry();
    }

    @Override
    public void desAppaerance() {
        swan.desAppaerance();
    }

    @Override
    public void fly() {
        swan.fly();
    }
}
