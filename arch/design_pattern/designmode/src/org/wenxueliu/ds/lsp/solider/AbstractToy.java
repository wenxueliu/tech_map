package org.wenxueliu.ds.lsp.solider;

/**
 * Created by liuwenxue on 05/10/2017.
 *
 * 如果子类不能完整地实现父类的方法，或者父类的方法在子类中已经发生畸变，那么，建议断开父子关系，采用依赖，聚集，组合等关系
 * 代替继承
 */
public class AbstractToy {

    AbstractGun gun;
}
