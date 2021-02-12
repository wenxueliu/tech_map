package org.wenxueliu.ds.decorator.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class BeautifyApperance extends Decorator {

    public BeautifyApperance(Swan swan) {
        super(swan);
    }

    @Override
    public void desAppaerance() {
        System.out.println("外表示纯白色，非常惹人喜欢！");
    }
}
