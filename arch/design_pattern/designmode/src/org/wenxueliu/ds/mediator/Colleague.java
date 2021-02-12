package org.wenxueliu.ds.mediator;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Colleague {
    protected Mediator mediator;
    public Colleague(Mediator mediator){
        this.mediator = mediator;
    }
}
