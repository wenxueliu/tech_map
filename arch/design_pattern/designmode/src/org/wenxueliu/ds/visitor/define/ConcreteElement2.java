package org.wenxueliu.ds.visitor.define;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class ConcreteElement2 extends Element {
    @Override
    public void doSth() {
        System.out.println("concrete element 2 do sth");
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitor(this);
    }
}
