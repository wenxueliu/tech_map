package org.wenxueliu.ds.lsp.solider;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Snipper {

    Rifle gun;

    public void setGun(Rifle rifle) {
        this.gun = rifle;

    }

    public void killEmeny() {
        this.gun.zoomOut();
        this.gun.shoot();
    }
}
