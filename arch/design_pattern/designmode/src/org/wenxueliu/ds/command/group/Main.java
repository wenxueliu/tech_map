package org.wenxueliu.ds.command.group;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Invoker invoker = new Invoker();
        Command cmd = new AddRequirementCommand(new RequirementGroup());
        invoker.setCommand(cmd);
        invoker.action();
    }
}
