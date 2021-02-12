package org.wenxueliu.ds.proxy.general;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class BeforeAdvice implements IAdvice {
    @Override
    public void exec() {
        System.out.println("before exec");
    }
}
