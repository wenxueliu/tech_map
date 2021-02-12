package org.wenxueliu.ds.strategy.define;

import org.wenxueliu.ds.command.define.ConcreteCommand1;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Context {

    IStrategy strategy;
    Context(IStrategy strategy) {
        this.strategy = strategy;
    }

    void doSth() {
        this.strategy.doSth();
    }
}
