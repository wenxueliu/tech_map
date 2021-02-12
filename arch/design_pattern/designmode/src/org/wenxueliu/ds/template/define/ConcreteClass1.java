package org.wenxueliu.ds.template.define;

import org.wenxueliu.ds.factory.human3.AbstractBlackHuman;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class ConcreteClass1 extends AbstractClass {
    @Override
    protected void doSomething() {
        System.out.println("c1 do something");
    }

    @Override
    protected void doAnything() {
        System.out.println("c1 do anything");
    }
}
