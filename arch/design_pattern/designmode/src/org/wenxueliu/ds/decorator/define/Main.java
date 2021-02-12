package org.wenxueliu.ds.decorator.define;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Component component = new ConcreteComponent();
        component = new ConcreteDecorator1(component);
        component = new ConcreteDecorator2(component);
        component.operate();
    }
}
