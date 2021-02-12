package org.wenxueliu.ds.decorator.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Decorator extends Component {
    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    @Override
    public void operate() {
        this.component.operate();
    }
}
