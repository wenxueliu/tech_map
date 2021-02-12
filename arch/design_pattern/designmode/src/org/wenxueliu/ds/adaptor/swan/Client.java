package org.wenxueliu.ds.adaptor.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class Client {


    public static void main(String []args) {
        Duck duck = new Ducking();
        duck.cry();
        duck.desAppaerance();
        duck.desBehavior();

        Duck uglyDucking = new UglyDucking();
        uglyDucking.cry();
        uglyDucking.desBehavior();
        uglyDucking.desAppaerance();
    }
}
