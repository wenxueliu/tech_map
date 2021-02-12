package org.wenxueliu.ds.decorator.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteDecorator1 extends Decorator {
    public ConcreteDecorator1(Component component) {
        super(component);
    }

    private void method1() {
        System.out.println("method1 decorator");
    }

    public void operator() {
        this.method1();
        super.operate();
    }
}
