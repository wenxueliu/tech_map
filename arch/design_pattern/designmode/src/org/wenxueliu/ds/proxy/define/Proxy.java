package org.wenxueliu.ds.proxy.define;

import org.wenxueliu.ds.builder.define.Product;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Proxy implements Subject {
    Subject subject;

    Proxy(Subject subject) {
        this.subject = subject;
    }
    public void before() {
        System.out.println("before proxy execute");
    }

    public void after() {
        System.out.println("after proxy execute");
    }

    @Override
    public void request() {
        this.before();
        this.subject.request();
        this.after();
    }
}
