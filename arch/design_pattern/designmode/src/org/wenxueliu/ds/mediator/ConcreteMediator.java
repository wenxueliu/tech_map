package org.wenxueliu.ds.mediator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteMediator extends Mediator {

    @Override
    public void doSth1() {
        super.c1.selfMethod1();
        super.c2.selfMethod1();
    }

    @Override
    public void doSth2() {
        super.c1.selfMethod2();
        super.c2.selfMethod2();
    }
}
