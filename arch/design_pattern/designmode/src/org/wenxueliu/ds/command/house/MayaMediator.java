package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class MayaMediator extends House {

    House house;
    MayaMediator(House house) {
        this.house = house;
    }

    @Override
    void canSell() {
        this.house.canSell();
    }

    @Override
    void forbidSell() {
        this.house.forbidSell();
    }
}
