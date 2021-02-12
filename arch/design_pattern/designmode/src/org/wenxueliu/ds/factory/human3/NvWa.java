package org.wenxueliu.ds.factory.human3;

/**
 * Created by liuwenxue on 05/10/2017.
 *
 * 如果将 AbstractHumanFactory 去掉，则变为简单工厂类
 */
public class NvWa {

    public static void main(String []args) {

        Human whiteFemaleHuman = new WhiteHumanFactory().createFemaleHuman();
        whiteFemaleHuman.getColor();
        whiteFemaleHuman.talk();

        Human whiteMaleHuman = new WhiteHumanFactory().createMaleHuman();
        whiteMaleHuman.getColor();
        whiteMaleHuman.talk();

        Human blackFemaleHuman = new BlackHumanFactory().createFemaleHuman();
        blackFemaleHuman.getColor();
        blackFemaleHuman.talk();

        Human yellowFemaleHuman = new YellowHumanFactory().createFemaleHuman();
        yellowFemaleHuman.getColor();
        yellowFemaleHuman.talk();
    }
}
