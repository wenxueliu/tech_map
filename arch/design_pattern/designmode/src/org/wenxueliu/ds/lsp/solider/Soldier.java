package org.wenxueliu.ds.lsp.solider;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Soldier {

    AbstractGun gun;

    void setGun(AbstractGun gun) {
        this.gun = gun;
    }

    void killEnemy() {
        System.out.println("begin to shoot ...");
        this.gun.shoot();
    }
}
