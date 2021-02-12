package org.wenxueliu.ds.mediator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteColleague1 extends Colleague {
    public ConcreteColleague1(Mediator mediator) {
        super(mediator);
    }

    public void selfMethod1() {

    }

    public void selfMethod2() {

    }

    public void depMethod1() {
        super.mediator.doSth1();
    }
}
