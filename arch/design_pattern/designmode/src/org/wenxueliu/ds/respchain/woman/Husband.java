package org.wenxueliu.ds.respchain.woman;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Husband extends Handler {

    Husband() {
        super(Handler.HUSBAND_LEVEL_REQUEST);
    }
    @Override
    public void response(IWoman woman) {
        System.out.println("wife's request is " + woman.getRequest());
        System.out.println("husband's answer is yes");
    }
}
