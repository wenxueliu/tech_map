package org.wenxueliu.ds.cof.common;

/**
 * Created by liuwenxue on 08/07/2017.
 *
 * 避免链太长，增加长度限制。
 */
public class Context {

    Handler handler1 = new FirstHandler();
    Handler handler2 = new SecondHandler();
    Handler handler3 = new ThirdHandler();

    Context() {
        handler1.setNextHandler(handler2);
        handler2.setNextHandler(handler3);
    }

    public Response handler(Request req) {
        return handler1.handleMessage(req);
    }
}
