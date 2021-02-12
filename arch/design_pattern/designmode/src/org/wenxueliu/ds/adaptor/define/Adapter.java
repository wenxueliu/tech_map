package org.wenxueliu.ds.adaptor.define;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Adapter extends Adaptee implements Target {
    @Override
    public void request() {
        super.doSth();
    }
}
