package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class JindiHouse extends House {
    @Override
    void canSell() {
        System.out.println("jindi can sell");
    }

    @Override
    void forbidSell() {
        System.out.println("jindi stop sell");
    }
}
