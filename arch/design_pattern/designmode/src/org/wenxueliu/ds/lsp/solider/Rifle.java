package org.wenxueliu.ds.lsp.solider;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Rifle extends AbstractGun {

    void zoomOut() {
        System.out.println("rifle zoom out");
    }

    @Override
    void shoot() {
       System.out.println("rifle gun shoot");
    }
}
