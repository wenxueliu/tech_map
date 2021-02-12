package org.wenxueliu.ds.lsp.solider;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class AUG extends Rifle {

    @Override
    void zoomOut() {
        System.out.println("zoom out by AUG");
    }

    @Override
    void shoot() {
        System.out.println("AUG shoot");
    }
}
