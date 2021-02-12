package org.wenxueliu.ds.visitor.define;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Visitor1 implements IVisitor {
    @Override
    public void visitor(ConcreteElement1 e) {
        e.doSth();
    }

    @Override
    public void visitor(ConcreteElement2 e) {
        e.doSth();
    }
}
