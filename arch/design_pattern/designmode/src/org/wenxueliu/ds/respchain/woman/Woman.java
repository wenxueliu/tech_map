package org.wenxueliu.ds.respchain.woman;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Woman implements IWoman {

    private int type;
    private String request;
    Woman(int type, String request) {
        this.type = type;
        this.request = request;
        switch (this.type) {
            case Handler.FATHER_LEVEL_REQUEST:
                this.request = "daughter's requeset is " + this.request;
                break;
            case Handler.HUSBAND_LEVEL_REQUEST:
                this.request = "wife's request is " + this.request;
                break;
            case Handler.SON_LEVEL_REQUEST:
                this.request = "mother's request is " + this.request;
                break;
        }
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public String getRequest() {
        return this.request;
    }
}
