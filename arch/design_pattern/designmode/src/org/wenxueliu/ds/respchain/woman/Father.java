package org.wenxueliu.ds.respchain.woman;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Father extends Handler {

    Father() {
        super(Handler.FATHER_LEVEL_REQUEST);
    }
    @Override
    public void response(IWoman woman) {
        System.out.println("daughter's request is " + woman.getRequest());
        System.out.println("father's answer is yes");
    }
}
