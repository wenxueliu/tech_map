package org.wenxueliu.ds.state.lift;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class RunningState extends LiftState {
    @Override
    public void open() {
        throw new IllegalStateException("running cannot open");
    }

    @Override
    public void run() {
        System.out.println("running");
    }

    @Override
    public void stop() {
        super.context.setLiftState(Context.stopState);
        super.context.getLiftState().stop();
    }

    @Override
    public void close() {
        throw new IllegalStateException("running cannot open");
    }
}
