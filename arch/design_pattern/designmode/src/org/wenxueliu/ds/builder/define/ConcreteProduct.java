package org.wenxueliu.ds.builder.define;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class ConcreteProduct extends Builder {
    private Product product = new Product();

    public void setPart() {

    }

    public Product buildProduct() {
        return product;
    }
}
