package org.wenxueliu.ds.proxy.game1;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayerProxy implements IGamePlayer {
    private IGamePlayer gamePlayer = null;

    public GamePlayerProxy(String name) {
        this.gamePlayer = new GamePlayer(this, name);
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
