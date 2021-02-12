package org.wenxueliu.ds.proxy.game;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayerProxy implements IGamePlayer {
    private IGamePlayer gamePlayer = null;

    public GamePlayerProxy(IGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
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
}
