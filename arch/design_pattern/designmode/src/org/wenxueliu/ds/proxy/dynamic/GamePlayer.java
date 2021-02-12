package org.wenxueliu.ds.proxy.dynamic;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayer implements IGamePlayer {
    private String name;
    private String user;
    private String passwd;

    GamePlayer(String name) {
       this.name = name;
    }

    @Override
    public void login(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
        System.out.println("user " + user + " passwd " + passwd + " login");
    }

    @Override
    public void killBoss() {
        System.out.println(user + " is killBoss");
    }

    @Override
    public void upgrade() {
        System.out.println(user + " is upgrading");
    }
}
