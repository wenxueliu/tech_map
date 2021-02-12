package org.wenxueliu.ds.respchain.woman;

import sun.jvm.hotspot.opto.HaltNode;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Handler implements IHandler {
    public final static int FATHER_LEVEL_REQUEST = 1;
    public final static int HUSBAND_LEVEL_REQUEST = 2;
    public final static int SON_LEVEL_REQUEST = 3;

    private Handler nextHandler;
    private int level;

    Handler(int level) {
        this.level = level;
    }

    public void handleMessage(IWoman woman) {
        if (woman.getType() == this.level) {
            this.response(woman);
        } else {
            if (this.nextHandler != null) {
                this.nextHandler.response(woman);
            } else {
                throw new IllegalStateException("cannot process request");
            }
        }
    }

    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    public abstract void response(IWoman woman);
}
