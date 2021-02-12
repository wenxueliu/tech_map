package org.wenxueliu.ds.respchain.oa;


/**
 * Created by liuwenxue on 07/10/2017.
 */
public class AHandler extends Handler {

    AHandler() {
        super(Level.A);
    }

    @Override
    public Response response(Request request) {
        System.out.println("A pass request " + request.getLevel() + " with " + request.getContent());
        return new Response(true);
    }
}
