package org.wenxueliu.ds.respchain.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Handler {
    private Handler nextHandler;

    //注意这里是 final
    public final Response handleMessage(Request request) {
        Response response = null;
        if (this.getHandlerLevel().equals(request.getRequestLevel())) {
            this.echo(request);
        } else {
            if (this.nextHandler != null) {
                this.nextHandler.handleMessage(request);
            } else {
                throw new IllegalStateException("cannot process");
            }
        }
        return response;
    }

    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    protected abstract Level getHandlerLevel();
    protected abstract Response echo(Request request);
}
