package org.wenxueliu.ds.template.hummer;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public abstract class HummerModel {
    abstract void start();
    abstract void stop();
    abstract void alarm();
    abstract void engineBoom();
    void run() {
        this.start();
        this.engineBoom();
        this.alarm();
        this.stop();
    }
}
