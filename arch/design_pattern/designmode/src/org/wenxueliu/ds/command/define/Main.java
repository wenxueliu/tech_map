package org.wenxueliu.ds.command.define;

import java.lang.reflect.InvocationHandler;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Invoker invoker = new Invoker();
        Receiver receiver =  new ConcreteReceiver1();
        Command cmd1 = new ConcreteCommand1(receiver);
        invoker.setCommand(cmd1);
        invoker.action();
    }
}
