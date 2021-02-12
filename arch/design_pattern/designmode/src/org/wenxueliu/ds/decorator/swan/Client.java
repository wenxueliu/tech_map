package org.wenxueliu.ds.decorator.swan;

/**
 * Created by liuwenxue on 03/06/2017.
 */
public class Client {
    public static void main(String []args) {
        Swan ducking = new UglyDucking();
        ducking.desAppaerance();
        ducking.fly();
        ducking.cry();

        ducking = new BeautifyApperance(ducking);
        ducking = new StrongBehavior(ducking);
        ducking.desAppaerance();
        ducking.cry();
        ducking.fly();
    }

}
