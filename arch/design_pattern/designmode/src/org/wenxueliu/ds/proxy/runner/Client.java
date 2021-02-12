package org.wenxueliu.ds.proxy.runner;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class Client {

    public static void main(String [] args) {
        IRunner liu = new Runner();
        IRunner agent = new RunnerAget(liu);
        agent.run();
    }
}
