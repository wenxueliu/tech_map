package org.wenxueliu.ds.template.define;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public abstract class AbstractClass {
    protected abstract void doSomething();
    protected abstract void doAnything();
    //注意这里一般为 final
    final public void templateMethod() {
        this.doSomething();
        this.doAnything();
    }
}
