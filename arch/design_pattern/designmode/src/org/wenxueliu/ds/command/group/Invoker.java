package org.wenxueliu.ds.command.group;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Invoker {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void action() {
        this.command.execute();
    }
}
