package org.wenxueliu.ds.template.hummer;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class HummerH2Model extends HummerModel {
    @Override
    void start() {
       System.out.println("H2 start");
    }

    @Override
    void stop() {
        System.out.println("H2 stop");
    }

    @Override
    void alarm() {
        System.out.println("H2 alarm");
    }

    @Override
    void engineBoom() {
        System.out.println("H2 engineBoom");
    }
}
