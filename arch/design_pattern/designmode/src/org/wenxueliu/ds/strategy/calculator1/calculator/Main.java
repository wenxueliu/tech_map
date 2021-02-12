package org.wenxueliu.ds.strategy.calculator1.calculator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Calculator c1 = new Add();
        Calculator c2 = new Sub();

        Context context = new Context(c1);
        context.exec(1, 2);
    }
}
