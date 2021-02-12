package org.wenxueliu.ds.decorator.runner;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class RunnerWithJet implements IRunner {

    private IRunner runner;

    public RunnerWithJet(IRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        System.out.println("加快运动速度，为运动员增加喷气装置");
        runner.run();
    }
}
