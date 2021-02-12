package org.wenxueliu.ds.proxy.define;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class RealSubject implements Subject {
    public void request() {
       System.out.println("real request");
    }
}
