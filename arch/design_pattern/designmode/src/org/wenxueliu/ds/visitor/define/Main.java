package org.wenxueliu.ds.visitor.define;

import org.wenxueliu.ds.command.define.ConcreteCommand1;
import org.wenxueliu.ds.visitor.employee.CommonEmployee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        IVisitor v = new Visitor1();
        ConcreteElement1 c1 = new ConcreteElement1();
        ConcreteElement2 c2 = new ConcreteElement2();
        c1.accept(v);
        c2.accept(v);
    }
}
