package org.wenxueliu.ds.component.transparent;

import java.util.List;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Leaf extends Component {
    Component parent;
    Component prev;
    Component next;

    @Override
    public void doSth() {
        System.out.println("leaf");
    }

    @Deprecated
    @Override
    public void add(Component com) {
        throw new UnsupportedOperationException("leaf doesn't support add");
    }

    @Deprecated
    @Override
    public void remove(Component com) {
        throw new UnsupportedOperationException("leaf doesn't support remove");
    }

    @Deprecated
    @Override
    public List<Component> getChildren() {
        throw new UnsupportedOperationException("leaf doesn't support getChildren");
    }

    @Override
    public void setParent(Component com) {
        this.parent = com;
    }

    @Override
    public Component getParent() {
        return this.parent;
    }

    @Override
    public void setNext(Component com) {
        this.next = com;
    }

    @Override
    public Component getNext() {
        return this.next;
    }

    @Override
    public void setPrev(Component com) {
        this.prev = com;
    }

    @Override
    public Component getPrev() {
        return this.prev;
    }
}
