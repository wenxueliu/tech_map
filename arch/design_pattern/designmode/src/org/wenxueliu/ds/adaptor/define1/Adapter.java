package org.wenxueliu.ds.adaptor.define1;

import org.wenxueliu.ds.adaptor.define.Adaptee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Adapter implements Target {
    Adaptee1 ad1;
    Adaptee2 ad2;
    @Override
    public void request() {
        ad1.doSth();
        ad2.doSth();
    }
}
