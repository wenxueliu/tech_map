package org.wenxueliu.ds.respchain.oa;


/**
 * Created by liuwenxue on 07/10/2017.
 */
public class BHandler extends Handler {

    BHandler() {
        super(Level.B);
    }

    @Override
    public Response response(Request request) {
        System.out.println("B pass request " + request.getLevel() + " with " + request.getContent());
        return new Response(true);
    }

}
