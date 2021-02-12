package org.wenxueliu.ds.proxy.runner;

import java.util.Random;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class RunnerAget implements IRunner {

    private IRunner runner;

    public RunnerAget(IRunner runner) {
        this.runner  = runner;
    }

    @Override
    public void run() {
        Random rand = new Random();

        if (rand.nextBoolean()) {
            System.out.println("代理人同意安排运动员跑步");
        } else {
            System.out.println("代理人心情不好，不同意安排运动员跑步");
        }
    }
}
