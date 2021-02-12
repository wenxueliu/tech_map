package org.wenxueliu.ds.adaptor.define1;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {

    public static void main(String[] args) {
        Target t1 = new ConcreteTarget();
        t1.request();
        Target t2 = new Adapter();
        t2.request();
    }
}
