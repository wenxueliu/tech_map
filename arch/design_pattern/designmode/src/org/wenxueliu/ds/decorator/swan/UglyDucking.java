package org.wenxueliu.ds.decorator.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class UglyDucking implements Swan {

    @Override
    public void fly() {
        System.out.println("不能飞行");
    }

    @Override
    public void cry() {
        System.out.println("叫声是克鲁-克鲁-克鲁");
    }

    @Override
    public void desAppaerance() {
        System.out.println("外形脏兮兮的白毛，毛茸茸大脑袋");
    }
}
