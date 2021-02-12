package org.wenxueliu.ds.state.lift;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class CloseingState extends LiftState {
    @Override
    public void open() {
        super.context.setLiftState(Context.openState);
        super.context.getLiftState().open();
    }

    @Override
    public void run() {
        super.context.setLiftState(Context.runState);
        super.context.getLiftState().run();
    }

    @Override
    public void stop() {
        super.context.setLiftState(Context.stopState);
        super.context.getLiftState().stop();
    }

    @Override
    public void close() {
        System.out.println("closing");
    }
}
