package org.wenxueliu.ds.proxy.general;

import java.lang.reflect.InvocationHandler;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class SubjectDynamicProxy extends DynamicProxy {
    public static <T> T newProxyInstance(Subject subject) {
        ClassLoader loader = subject.getClass().getClassLoader();
        Class<?>[] interfaces = subject.getClass().getInterfaces();
        InvocationHandler hander = new MyInvocationHandler(subject);
        return newProxyInstance(loader, interfaces, hander);
    }
}
