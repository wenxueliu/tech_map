package org.wenxueliu.ds.visitor.employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Manager extends Employee {

    private String performance;
    Manager(String name, int sex) {
        super(name, sex);
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitor(this);
    }
}
