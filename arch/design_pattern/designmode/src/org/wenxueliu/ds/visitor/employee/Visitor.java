package org.wenxueliu.ds.visitor.employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Visitor implements IVisitor {
    private int commonSalary;
    private int managerSalary;

    @Override
    public void visitor(CommonEmployee e) {
        getBaseInfo(e);
        this.commonSalary += e.getSalary();
    }

    @Override
    public void visitor(Manager m) {
        getManagerInfo(m);
        this.managerSalary += m.getSalary();
    }

    @Override
    public int getTotalSalary() {
        return this.commonSalary + this.managerSalary;
    }

    public void getBaseInfo(Employee e) {
        System.out.println(" name : " + e.getName());
        System.out.println(" sex : " + e.getSex());
        System.out.println(" salary : " + e.getSalary());
    }

    public void getCommonEmployee(CommonEmployee e) {
        getBaseInfo(e);
        System.out.println("job : " + e.getJob());
    }

    public void getManagerInfo(Manager m) {
        getBaseInfo(m);
        System.out.println(" performance: " + m.getPerformance());
    }
}
