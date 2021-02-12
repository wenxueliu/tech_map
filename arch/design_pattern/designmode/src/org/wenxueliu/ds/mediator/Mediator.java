package org.wenxueliu.ds.mediator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Mediator {
    protected ConcreteColleague1 c1;
    protected ConcreteColleague2 c2;

    public ConcreteColleague1 getC1() {
        return this.c1;
    }

    public void setC1(ConcreteColleague1 c1) {
        this.c1 = c1;
    }

    public ConcreteColleague2 getC2() {
        return this.c2;
    }

    public void setC2(ConcreteColleague2 c2) {
        this.c2 = c2;
    }

    public abstract void doSth1();
    public abstract void doSth2();
}
