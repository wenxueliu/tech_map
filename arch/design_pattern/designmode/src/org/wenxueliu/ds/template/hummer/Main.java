package org.wenxueliu.ds.template.hummer;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String[] args) {
        HummerModel modelH1 = new HummerH1Model();
        modelH1.run();

        HummerModel modelH2 = new HummerH2Model();
        modelH2.run();
    }
}
