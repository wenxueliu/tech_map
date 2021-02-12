package org.wenxueliu.ds.proxy.game2;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayerProxy implements IGamePlayer {
    private IGamePlayer gamePlayer = null;

    public GamePlayerProxy(IGamePlayer player) {
        this.gamePlayer = player;
    }

    @Override
    public void login(String user, String passwd) {
        this.gamePlayer.login(user, passwd);

    }

    @Override
    public void killBoss() {
        this.gamePlayer.killBoss();

    }

    @Override
    public void upgrade() {
        this.gamePlayer.upgrade();
    }

    @Override
    public IGamePlayer getProxy() {
        return this;
    }
}
