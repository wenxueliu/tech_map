package org.wenxueliu.ds.adaptor.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class WhiteSwan implements Swan {

    @Override
    public void cry() {
        System.out.println("叫声是克鲁-克鲁-克鲁");
    }

    @Override
    public void fly() {
        System.out.println("能够飞行！");
    }

    @Override
    public void desAppaerance() {
        System.out.println("外表示纯白色，非常惹人喜欢！");
    }
}
