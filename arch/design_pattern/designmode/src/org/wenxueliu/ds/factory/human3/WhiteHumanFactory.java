package org.wenxueliu.ds.factory.human3;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class WhiteHumanFactory implements HumanFactoryV2 {
    @Override
    public Human createFemaleHuman() {
        return new FemaleWhileHuman();
    }

    @Override
    public Human createMaleHuman() {
        return new MaleWhiteHuman();
    }
}
