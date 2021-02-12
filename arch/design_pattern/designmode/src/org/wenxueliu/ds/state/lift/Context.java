package org.wenxueliu.ds.state.lift;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Context {
    public final static OpenningState openState = new OpenningState();
    public final static RunningState runState = new RunningState();
    public final static StoppingState stopState = new StoppingState();
    public final static CloseingState closeState = new CloseingState();

    private LiftState liftState;

    public LiftState getLiftState() {
        return liftState;
    }

    public void setLiftState(LiftState liftState) {
        this.liftState = liftState;
        this.liftState.setContext(this);
    }

    public void open() {
        this.liftState.open();
    }

    public void stop() {
        this.liftState.stop();
    }

    public void close() {
        this.liftState.close();
    }

    public void run() {
        this.liftState.run();
    }
}
