package org.wenxueliu.ds.factory.product;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class ConcreteCreator extends Creator {

    @Override
    public <T extends Product> T createProduct(Class<T> c) {
        Product p = null;
        try {
            p = (T)c.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T)p;
    }
}
