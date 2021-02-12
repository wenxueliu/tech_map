package org.wenxueliu.ds.state.define;

/**
 * Created by liuwenxue on 09/10/2017.
 */
public abstract class State {
    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract void create();
    public abstract void start();
    public abstract void run();
    public abstract void stop();
    public abstract void close();
    public abstract void destory();
}
