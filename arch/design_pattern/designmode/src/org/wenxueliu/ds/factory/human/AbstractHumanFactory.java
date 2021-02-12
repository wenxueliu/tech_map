package org.wenxueliu.ds.factory.human;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public abstract class AbstractHumanFactory {
    public abstract <T extends Human> T createHuman(Class<T> c);
}
