package org.wenxueliu.ds.factory.human3;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class FemaleFactory implements HumanFactory {
    @Override
    public Human createYellowHuman() {
        return new FemaleYellowHuman();
    }

    @Override
    public Human createBlackHuman() {
        return new FemaleBlackHuman();
    }

    @Override
    public Human createWhiteHuman() {
        return new FemaleWhileHuman();
    }
}
