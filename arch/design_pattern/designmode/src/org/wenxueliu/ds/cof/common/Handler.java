package org.wenxueliu.ds.cof.common;

/**
 * Created by liuwenxue on 08/07/2017.
 *
 * 1. 这里 response 和 request 可以进一步实现为接口类，提高系统的可扩展性
 */
public abstract class Handler {
    private Handler nextHandler;

    public final Response handleMessage(Request req) {
        Response response = null;
        if (this.getHandlerLevel().equals(req.getRequestLevel())) {
            response = this.echo(req);
        } else {
            if (this.nextHandler != null) {
                response = this.nextHandler.handleMessage(req);
            } else {
                throw new IllegalStateException("cannot process this message");
            }
        }
        return response;
    }

    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    protected abstract Level getHandlerLevel();

    protected abstract Response echo(Request req);
}
