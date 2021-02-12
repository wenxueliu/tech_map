package org.wenxueliu.ds.command.group;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class RequirementGroup extends Group {
    @Override
    public void find() {
        System.out.println("find requirement group");
    }

    @Override
    public void add() {
        System.out.println("add function requirement group");
    }

    @Override
    public void delete() {
        System.out.println("delete hfunction requirement group");
    }

    @Override
    public void change() {
        System.out.println("change hfunction requirement group");
    }

    @Override
    public void plan() {
        System.out.println("plan function requirement group");
    }
}
