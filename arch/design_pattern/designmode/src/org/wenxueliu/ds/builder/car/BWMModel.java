package org.wenxueliu.ds.builder.car;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class BWMModel extends CarModel {
    @Override
    void start() {
        System.out.println("bwm start");
    }

    @Override
    void stop() {
        System.out.println("bwm stop");
    }

    @Override
    void alarm() {
        System.out.println("bwm alarm");
    }

    @Override
    void engineBoom() {
        System.out.println("bwm engineBoom");
    }
}
