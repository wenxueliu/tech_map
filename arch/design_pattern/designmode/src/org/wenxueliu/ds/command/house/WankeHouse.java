package org.wenxueliu.ds.command.house;

import java.util.HashMap;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class WankeHouse extends House {
    @Override
    void canSell() {
        System.out.println("wanke can sell");
    }

    @Override
    void forbidSell() {
        System.out.println("wanke stop sell");
    }
}
