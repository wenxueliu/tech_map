package org.wenxueliu.ds.mediator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteColleague2 extends Colleague {
    public ConcreteColleague2(Mediator mediator) {
        super(mediator);
    }

    public void selfMethod1() {

    }
    public void selfMethod2() {

    }

    public void depMethod2() {
        super.mediator.doSth2();
    }
}
