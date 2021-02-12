package org.wenxueliu.ds.proxy.general;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class MyInvocationHandler implements InvocationHandler {

    private Object target;

    MyInvocationHandler(Object obj) {
        this.target = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.target, args);
    }
}
