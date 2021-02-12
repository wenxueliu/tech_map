package org.wenxueliu.ds.proxy.game2;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayer implements IGamePlayer {
    private GamePlayerProxy proxy = null;
    private String name;
    private String user;
    private String passwd;

    GamePlayer(String name) {
        this.name = name;
    }

    private boolean isProxy() {
        if (this.proxy == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void login(String user, String passwd) {
        if (!this.isProxy()) {
            System.out.println("please use proxy");
        } else {
            this.user = user;
            this.passwd = passwd;
            System.out.println("user " + user + " passwd " + passwd + " login");
        }
    }

    @Override
    public void killBoss() {
        if (!this.isProxy()) {
            System.out.println("please use proxy");
        } else {
            System.out.println(user + " is killBoss");
        }
    }

    @Override
    public void upgrade() {
        if (!this.isProxy()) {
            System.out.println("please use proxy");
        } else {
            System.out.println(user + " is upgrading");
        }
    }

    @Override
    public IGamePlayer getProxy() {
        this.proxy = new GamePlayerProxy(this);
        return this.proxy;
    }
}
