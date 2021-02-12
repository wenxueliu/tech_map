package org.wenxueliu.ds.state.define;

/**
 * Created by liuwenxue on 09/10/2017.
 */
public class CreateState extends State {
    @Override
    public void create() {
        System.out.println("create");
    }

    @Override
    public void start() {
        super.context.setState(Context.start);
        super.context.getState().start();
    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void close() {

    }

    @Override
    public void destory() {

    }
}
