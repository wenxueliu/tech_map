package org.wenxueliu.ds.decorator.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ConcreteDecorator2 extends Decorator {
    public ConcreteDecorator2(Component component) {
        super(component);
    }

    private void method2() {
        System.out.println("method2 decorator");
    }

    public void operator() {
        this.method2();
        super.operate();
    }
}
