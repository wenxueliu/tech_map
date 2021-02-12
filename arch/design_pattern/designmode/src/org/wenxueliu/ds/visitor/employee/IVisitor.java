package org.wenxueliu.ds.visitor.employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public interface IVisitor {
    public void visitor(CommonEmployee e) ;
    public void visitor(Manager m) ;
    public int getTotalSalary();
}
