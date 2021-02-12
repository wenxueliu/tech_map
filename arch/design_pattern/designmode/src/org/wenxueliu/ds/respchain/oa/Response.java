package org.wenxueliu.ds.respchain.oa;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Response {

    private boolean pass = false;
    Response(boolean pass) {
        this.pass = pass;
    }

    boolean getPass() {
       return pass;
    }
}
