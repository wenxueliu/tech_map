package org.wenxueliu.ds.cof.common;

/**
 * Created by liuwenxue on 08/07/2017.
 */
public class Client {

    public static void main(String []args ) {
        Context context = new Context();
        Response res = context.handler(new Request());
    }
}
