package org.wenxueliu.ds.strategy.calculator1.calculator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Sub implements Calculator {
    @Override
    public int exec(int a, int b) {
        return a - b;
    }
}
