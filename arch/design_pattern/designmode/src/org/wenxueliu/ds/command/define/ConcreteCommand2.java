package org.wenxueliu.ds.command.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteCommand2 extends Command {

    Receiver receiver = null;

    ConcreteCommand2(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        this.receiver.doSth();
    }
}
