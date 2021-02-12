package org.wenxueliu.ds.adaptor.swan;

import org.wenxueliu.ds.decorator.swan.Swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class UglyDucking extends WhiteSwan implements Duck {

    @Override
    public void cry() {
        super.cry();
    }

    @Override
    public void desAppaerance() {
        super.desAppaerance();
    }

    @Override
    public void desBehavior() {
        System.out.println("会游泳");
        super.fly();
    }
}
