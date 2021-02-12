package org.wenxueliu.ds.state.lift;

import org.wenxueliu.ds.visitor.define.ConcreteElement1;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Context context = new Context();
        context.setLiftState(Context.closeState);
        context.run();
        context.stop();
        context.open();
        context.close();
    }
}
