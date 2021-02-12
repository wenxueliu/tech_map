package org.wenxueliu.ds.state.lift;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public abstract class LiftState {
    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract void open();
    public abstract void run();
    public abstract void stop();
    public abstract void close();
}
