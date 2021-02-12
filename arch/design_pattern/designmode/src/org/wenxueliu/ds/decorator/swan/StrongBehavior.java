package org.wenxueliu.ds.decorator.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class StrongBehavior extends Decorator {

    public StrongBehavior(Swan swan) {
        super(swan);
    }

    @Override
    public void fly() {
        System.out.println("会飞行了！");
    }
}
