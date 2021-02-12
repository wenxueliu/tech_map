package org.wenxueliu.ds.proxy.dynamic;

import java.lang.reflect.Method;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayIH {
    Class cls = null;
    Object obj = null;

    public GamePlayIH(Object obj) {
        this.obj = obj;
    }

    public Object invoke(Object proxy, Method method, Object []args) throws Throwable {
        Object result = method.invoke(this.obj, args);
        if (method.getName().equals("login")) {
            System.out.println("someone else use your account");
        }
        return result;
    }
}
