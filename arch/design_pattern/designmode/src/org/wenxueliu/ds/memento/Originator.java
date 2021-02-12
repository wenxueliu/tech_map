package org.wenxueliu.ds.memento;

import java.beans.Introspector;
import java.lang.reflect.Member;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Originator {

    private String state;

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Memento createMemento() {
        return new Memento(this.state);
    }

    public void restoreMemento(Memento memento) {
        this.setState(memento.getState());
    }
}
