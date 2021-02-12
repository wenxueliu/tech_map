package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Invoker {

    private Command cmd;

    void setCommand(Command cmd) {
        this.cmd = cmd;
    }

    void action() {
        this.cmd.execute();
    }
}
