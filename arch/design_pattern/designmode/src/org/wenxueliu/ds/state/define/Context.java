package org.wenxueliu.ds.state.define;

/**
 * Created by liuwenxue on 09/10/2017.
 */
public class Context {
    static final State craete = new CreateState();
    static final State start = new StartState();
    static final State stop = new StopState();
    static final State run = new RunState();
    static final State close = new CloseState();
    static final State destory = new DestoryState();

    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        //此处是难点
        this.state.setContext(this);
    }

    public void create() {
        this.state.create();
    }

    public void start() {
        this.state.start();
    }

    public void run() {
        this.state.run();
    }

    public void stop() {
        this.state.stop();
    }

    public void close() {
        this.state.close();
    }

    public void destory() {
        this.state.destory();
    }
}
