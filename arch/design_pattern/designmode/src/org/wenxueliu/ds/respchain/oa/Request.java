package org.wenxueliu.ds.respchain.oa;

import com.sun.deploy.security.ValidationState;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Request {
    public abstract Level getLevel();
    public abstract Type getType();
    public abstract String getContent();
}
