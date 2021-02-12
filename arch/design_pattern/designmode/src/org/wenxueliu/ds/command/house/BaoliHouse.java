package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class BaoliHouse extends House {
    @Override
    void canSell() {
        System.out.println("baoli can sell");
    }

    @Override
    void forbidSell() {
        System.out.println("baoli stop sell");
    }
}
