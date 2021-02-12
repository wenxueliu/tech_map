package org.wenxueliu.ds.lsp.solider;

import org.wenxueliu.ds.cof.common.Handler;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Main {

    public static void main(String[] args) {
        Soldier soldier = new Soldier();
        soldier.setGun(new Handgun());
        soldier.killEnemy();
        soldier.setGun(new Rifle());
        soldier.killEnemy();
    }
}
