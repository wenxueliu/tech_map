package org.wenxueliu.ds.respchain.oa;


/**
 * Created by liuwenxue on 07/10/2017.
 */
public class CHandler extends Handler {

    CHandler() {
        super(Level.C);
    }

    @Override
    public Response response(Request request) {
        System.out.println("C pass request " + request.getLevel() + " with " + request.getContent());
        return new Response(true);
    }
}
