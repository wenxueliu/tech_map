package org.wenxueliu.ds.visitor.define;

/**
 * Created by liuwenxue on 08/10/2017.
 *
 * 每个传递的对象都会调用该类，因此可以做一些统计工作
 */
public class Visitor2 implements IVisitor {
    @Override
    public void visitor(ConcreteElement1 e) {
        System.out.println("begin");
        e.doSth();
        System.out.println("end");
    }

    @Override
    public void visitor(ConcreteElement2 e) {
        System.out.println("begin");
        e.doSth();
        System.out.println("end");
    }
}
