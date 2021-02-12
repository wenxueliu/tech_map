package org.wenxueliu.ds.respchain.oa;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Invoker {

    static void processRequest(Request request) {
        Handler a = new AHandler();
        Handler b = new BHandler();
        Handler c = new CHandler();
        Handler d = new DHandler();
        if (request.getType().equals(Type.LEAVE)) {
            a.setNextHandler(b);
            b.setNextHandler(c);
            c.setNextHandler(d);
        } else if (request.getType().equals(Type.REIMBURSE)) {
            a.setNextHandler(b);
            b.setNextHandler(c);
        }
        a.handleMessage(request);
    }
}
