package org.wenxueliu.ds.facade.define;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Facade {

    private ClassA a = new ClassA();
    private ClassB b = new ClassB();
    private ClassC c = new ClassC();

    public void doSth1() {
        a.doSth();
        b.doSth();
        c.doSth();
    }

    public void doSth2() {
        a.doSth();
    }

    public void doSth3() {
        c.doSth();
    }
}
