package org.wenxueliu.ds.state.lift;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class OpenningState extends LiftState {
    @Override
    public void open() {
        System.out.println("lift open");
    }

    @Override
    public void run() {
        throw new IllegalStateException("open cannot run");
    }

    @Override
    public void stop() {
        throw new IllegalStateException("open cannot stop");
    }

    @Override
    public void close() {
        super.context.setLiftState(Context.closeState);
        super.context.getLiftState().close();
    }
}
