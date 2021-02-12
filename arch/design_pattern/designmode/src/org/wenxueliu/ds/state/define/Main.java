package org.wenxueliu.ds.state.define;

import org.wenxueliu.ds.visitor.define.ConcreteElement1;

/**
 * Created by liuwenxue on 09/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Context context = new Context();
        context.setState(Context.craete);
        context.create();
        context.start();
        context.stop();
        context.close();
        context.destory();
    }
}
