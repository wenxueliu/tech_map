package org.wenxueliu.ds.factory.human2;

import org.wenxueliu.ds.factory.human2.Human;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class YellowHuman implements Human {
    @Override
    public void getColor() {
        System.out.println("yellow color");
    }

    @Override
    public void talk() {
        System.out.println("yellow talk");
    }
}
