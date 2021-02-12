package org.wenxueliu.ds.factory.product;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class Client {

    public static void main(String []args) {
        Creator c = new ConcreteCreator();
        Product p1 = c.createProduct(ConcreteProduct1.class);
        Product p2 = c.createProduct(ConcreteProduct2.class);
    }
}
