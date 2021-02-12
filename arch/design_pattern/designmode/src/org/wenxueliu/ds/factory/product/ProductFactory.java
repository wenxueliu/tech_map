package org.wenxueliu.ds.factory.product;

import java.util.HashMap;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class ProductFactory {

    private static final HashMap<String, Product> prMap = new HashMap<>();

    public static synchronized Product createProduct(String type) {
        Product p = null;
        if (prMap.containsKey(type)) {
            return prMap.get(type);
        } else {
            if (type.equals("Product1")) {
                p = new ConcreteProduct1();
            } else if (type.equals("Product2")) {
                p = new ConcreteProduct2();
            }
            prMap.put(type, p);
        }
        return p;
    }
}
