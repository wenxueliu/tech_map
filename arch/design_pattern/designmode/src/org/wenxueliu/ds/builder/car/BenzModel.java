package org.wenxueliu.ds.builder.car;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class BenzModel extends CarModel {
    @Override
    void start() {
        System.out.println("benz start");
    }

    @Override
    void stop() {
        System.out.println("benz stop");
    }

    @Override
    void alarm() {
        System.out.println("benz alarm");
    }

    @Override
    void engineBoom() {
        System.out.println("benz engineBoom");
    }
}
