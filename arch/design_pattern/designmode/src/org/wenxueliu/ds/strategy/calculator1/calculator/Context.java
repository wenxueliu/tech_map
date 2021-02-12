package org.wenxueliu.ds.strategy.calculator1.calculator;

import org.wenxueliu.ds.command.define.ConcreteCommand1;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Context {

    Calculator cal;

    Context(Calculator cal) {
        this.cal = cal;
    }

    public int exec(int a, int b) {
        return this.cal.exec(a, b);
    }
}
