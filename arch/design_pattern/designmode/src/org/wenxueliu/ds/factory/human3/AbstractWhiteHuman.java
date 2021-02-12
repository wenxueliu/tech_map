package org.wenxueliu.ds.factory.human3;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public abstract class AbstractWhiteHuman implements Human {

    @Override
    public void getColor() {
        System.out.println("white color");
    }

    @Override
    public void talk() {
        System.out.println("white talk");
    }
}
