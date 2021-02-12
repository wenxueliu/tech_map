package org.wenxueliu.ds.prototype;

import org.wenxueliu.ds.builder.define.Product;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class PrototypeClass implements Cloneable {
    @Override
    public PrototypeClass  clone() {
        PrototypeClass prototypeClass = null;
        try {
            prototypeClass = (PrototypeClass)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return prototypeClass;
    }
}
