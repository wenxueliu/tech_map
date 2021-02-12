package org.wenxueliu.ds.proxy.game2;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public interface IGamePlayer {
    void login(String user, String passwd);
    void killBoss();
    void upgrade();
    IGamePlayer getProxy();
}
