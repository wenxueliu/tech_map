package org.wenxueliu.ds.visitor.employee;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class CommonEmployee extends Employee {

    private String job;
    CommonEmployee(String name, int sex) {
        super(name, sex);
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitor(this);
    }
}
