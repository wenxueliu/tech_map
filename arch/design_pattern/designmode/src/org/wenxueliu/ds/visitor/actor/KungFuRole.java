package org.wenxueliu.ds.visitor.actor;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class KungFuRole implements Role {
    @Override
    public void accept(AbsActor actor) {
        actor.act(this);
    }
}
