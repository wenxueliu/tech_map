package org.wenxueliu.ds.builder.car;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String []args) {
        Director director = new Director();
        for (int i = 0; i < 10; i++) {
            director.getABenzModel();
        }
        for (int i = 0; i < 10; i++) {
            director.getBBenzModel();
        }
        for (int i = 0; i < 10; i++) {
            director.getABWMModel();
        }
    }
}
