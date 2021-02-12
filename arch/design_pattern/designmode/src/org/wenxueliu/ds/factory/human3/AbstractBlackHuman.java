package org.wenxueliu.ds.factory.human3;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public abstract class AbstractBlackHuman implements Human {
    @Override
    public void getColor() {
        System.out.println("black color");
    }

    @Override
    public void talk() {
        System.out.println("black talk");
    }
}
