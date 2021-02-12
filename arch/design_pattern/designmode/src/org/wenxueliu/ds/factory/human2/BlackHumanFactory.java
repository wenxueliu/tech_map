package org.wenxueliu.ds.factory.human2;

import org.wenxueliu.ds.factory.human2.AbstractHumanFactory;
import org.wenxueliu.ds.factory.human2.Human;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class BlackHumanFactory extends AbstractHumanFactory {
    @Override
    public Human createHuman() {
        return new BlackHuman();
    }
}
