package org.wenxueliu.ds.strategy.escapse;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IStrategy s1 = new BackDoor();
        IStrategy s2 = new GivenGreenLight();
        IStrategy s3 = new BlockEnemy();

        Context c1 = new Context(s1);
        c1.operate();

        Context c2 = new Context(s2);
        c2.operate();

        Context c3 = new Context(s3);
        c3.operate();
    }
}
