package org.wenxueliu.ds.visitor.define;

import org.wenxueliu.ds.visitor.employee.Employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class ConcreteElement1 extends Element {
    @Override
    public void doSth() {
        System.out.println("concrete element 1 do sth");
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitor(this);
    }
}
