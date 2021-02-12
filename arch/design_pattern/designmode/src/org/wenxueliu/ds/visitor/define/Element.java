package org.wenxueliu.ds.visitor.define;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public abstract class Element {
    public abstract void doSth();
    public abstract void accept(IVisitor visitor);
}
