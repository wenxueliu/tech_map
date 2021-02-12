package org.wenxueliu.ds.factory.human2;

import org.wenxueliu.ds.factory.human2.AbstractHumanFactory;
import org.wenxueliu.ds.factory.human2.BlackHuman;
import org.wenxueliu.ds.factory.human2.Human;
import org.wenxueliu.ds.factory.human2.BlackHumanFactory;
import org.wenxueliu.ds.factory.human2.WhiteHumanFactory;
import org.wenxueliu.ds.factory.human2.YellowHumanFactory;

/**
 * Created by liuwenxue on 05/10/2017.
 *
 * 如果将 AbstractHumanFactory 去掉，则变为简单工厂类
 */
public class NvWa {

    public static void main(String []args) {

        Human whiteHuman = new WhiteHumanFactory().createHuman();
        whiteHuman.getColor();
        whiteHuman.talk();

        Human blackHuman = new BlackHumanFactory().createHuman();
        blackHuman.getColor();
        blackHuman.talk();

        Human yellowHuman = new YellowHumanFactory().createHuman();
        yellowHuman.getColor();
        yellowHuman.talk();
    }
}
