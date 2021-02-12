package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Invoker invoker = new Invoker();
        invoker.setCommand(new CanSellCommand());
        invoker.action();
    }
}
