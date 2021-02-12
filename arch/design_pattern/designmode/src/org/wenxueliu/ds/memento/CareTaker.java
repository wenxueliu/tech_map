package org.wenxueliu.ds.memento;

import org.wenxueliu.ds.strategy.calculator1.calculator.Calculator;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class CareTaker {
    private Memento memento;

    public Memento getMemento() {
        return memento;
    }

    public void setMemento(Memento memento) {
        this.memento = memento;
    }
}
