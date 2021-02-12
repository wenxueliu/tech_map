package org.wenxueliu.ds.memento;

import com.sun.tools.corba.se.idl.constExpr.Or;
import org.wenxueliu.ds.strategy.calculator1.calculator.Calculator;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Originator or = new Originator();
        CareTaker cr = new CareTaker();
        cr.setMemento(or.createMemento());
        or.restoreMemento(cr.getMemento());
    }
}
