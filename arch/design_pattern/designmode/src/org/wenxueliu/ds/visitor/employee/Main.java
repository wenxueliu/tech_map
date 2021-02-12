package org.wenxueliu.ds.visitor.employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {

    public static void main(String[] args) {
        IVisitor v = new Visitor();
        CommonEmployee e1 = new CommonEmployee("e1", 0);
        e1.setSalary(1);
        CommonEmployee e2 = new CommonEmployee("e2", 0);
        e2.setSalary(1);
        Manager m1 = new Manager("m1", 0);
        m1.setSalary(2);
        Manager m2 = new Manager("m1", 1);
        m2.setSalary(2);

        e1.accept(v);
        e2.accept(v);
        m1.accept(v);
        m2.accept(v);

        System.out.println("total salary is " + v.getTotalSalary());
    }
}
