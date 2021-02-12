package org.wenxueliu.ds.builder.define;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Director {
    private Builder builder = new ConcreteProduct();

    public Product getAProduct() {
        builder.setPart();
        return builder.buildProduct();
    }
}
