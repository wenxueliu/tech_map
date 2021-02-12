package org.wenxueliu.ds.memento;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Memento {

    private String state;

    Memento(String state) {
        this.state = state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
