package org.wenxueliu.ds.component.transparent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Composite extends Component {
    Component parent;
    Component prev;
    Component next;
    List<Component> children = new ArrayList<>();

    @Override
    public void add(Component com) {
        this.children.add(com);
    }

    @Override
    public void remove(Component com) {
        this.children.remove(com);
    }

    @Override
    public List<Component> getChildren() {
        return this.children;
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

    @Override
    public void doSth() {
        System.out.println("composite ");
    }
}
