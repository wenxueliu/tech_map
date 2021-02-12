package org.wenxueliu.ds.respchain.oa;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Handler {

    Handler nextHandler;
    Level level;

    Handler(Level level) {
        this.level = level;
    }

    public Response handleMessage(Request request) {
        Response response = null;
        if (request.getLevel().equals(this.level)) {
            response = this.response(request);
        } else {
            if (this.nextHandler != null) {
                response = this.nextHandler.handleMessage(request);
            } else {
                throw new IllegalStateException("cannot process");
            }
        }
        return response;
    }

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract Response response(Request request);
}
