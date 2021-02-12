package org.wenxueliu.ds.adaptor.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class Ducking implements Duck {

    @Override
    public void desBehavior() {
        System.out.println("会游泳");
    }

    @Override
    public void cry() {
        System.out.println("叫声是嘎-嘎-嘎");
    }

    @Override
    public void desAppaerance() {
        System.out.println("外形是黄白相间，嘴长");
    }
}
