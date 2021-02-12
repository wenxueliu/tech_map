package org.wenxueliu.ds.template.define;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String []args) {
        AbstractClass c1 = new ConcreteClass1();
        c1.templateMethod();
        AbstractClass c2 = new ConcreteClass2();
        c2.templateMethod();
    }
}
