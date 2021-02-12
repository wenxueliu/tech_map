package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class HengdaHouse extends House {
    @Override
    void canSell() {
        System.out.println("hengda can sell");
    }

    @Override
    void forbidSell() {
        System.out.println("hengda stop sell");
    }
}
