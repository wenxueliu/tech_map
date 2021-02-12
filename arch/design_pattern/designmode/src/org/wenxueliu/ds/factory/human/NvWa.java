package org.wenxueliu.ds.factory.human;

/**
 * Created by liuwenxue on 05/10/2017.
 *
 * 如果将 AbstractHumanFactory 去掉，则变为简单工厂类
 */
public class NvWa {

    public static void main(String []args) {
        AbstractHumanFactory yinYanglu = new HumanFactory();

        Human whiteHuman = yinYanglu.createHuman(WhiteHuman.class);
        whiteHuman.getColor();
        whiteHuman.talk();

        Human blackHuman = yinYanglu.createHuman(BlackHuman.class);
        blackHuman.getColor();
        blackHuman.talk();

        Human yellowHuman = yinYanglu.createHuman(YellowHuman.class);
        yellowHuman.getColor();
        yellowHuman.talk();
    }
}
