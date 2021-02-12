package org.wenxueliu.ds.decorator.define;

import org.wenxueliu.ds.command.house.Command;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteComponent extends Component {

    @Override
    public void operate() {
        System.out.println("do sth");
    }
}
