package org.wenxueliu.ds.respchain.oa;


/**
 * Created by liuwenxue on 07/10/2017.
 */
public class DHandler extends Handler {

    DHandler() {
        super(Level.D);
    }

    @Override
    public Response response(Request request) {
        System.out.println("D pass request " + request.getLevel() + " with " + request.getContent());
        return new Response(true);
    }
}
