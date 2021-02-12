package org.wenxueliu.ds.proxy.general;

import java.lang.reflect.InvocationHandler;

/**
 * Created by liuwenxue on 07/10/2017.
 *
 * TODO 对于动态dialing还没有立即
 */
public class Main {
    public static void main(String []args) {
        Subject subject = new RealSubject();
        Subject proxy = SubjectDynamicProxy.newProxyInstance(subject);
        proxy.request();
    }
}
