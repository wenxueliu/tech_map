package org.wenxueliu.ds.component.transparent;

import java.util.List;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public abstract class Component {
    public abstract void doSth();
    public abstract void add(Component com);
    public abstract void remove(Component com);
    public abstract List<Component> getChildren();
    public abstract void setParent(Component com);
    public abstract Component getParent();
    public abstract void setNext(Component com);
    public abstract Component getNext();
    public abstract void setPrev(Component com);
    public abstract Component getPrev();
}
