package org.wenxueliu.ds.component.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Composite extends Component {
    List<Component> components = new ArrayList<>();

    public void add(Component com) {
        this.components.add(com);
    }

    public void remive(Component com) {
        this.components.remove(com);
    }

    public List<Component> getChildren() {
        return this.components;
    }

    @Override
    public void doSth() {
        System.out.println("composite ");
    }
}
