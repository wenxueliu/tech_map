package org.wenxueliu.ds.factory.product;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public abstract class Creator {

    public abstract <T extends Product> T createProduct(Class<T> c);
}
