package org.wenxueliu.ds.command.define;

import com.sun.org.apache.regexp.internal.RE;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteCommand1 extends Command {

    Receiver receiver = null;

    ConcreteCommand1(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        this.receiver.doSth();
    }
}
