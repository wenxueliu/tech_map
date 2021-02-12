package org.wenxueliu.ds.respchain.woman;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Son extends Handler {

    Son() {
       super(Handler.SON_LEVEL_REQUEST);
    }

    @Override
    public void response(IWoman woman) {
        System.out.println("mother's request is " + woman.getRequest());
        System.out.println("son's answer is yes");
    }
}
