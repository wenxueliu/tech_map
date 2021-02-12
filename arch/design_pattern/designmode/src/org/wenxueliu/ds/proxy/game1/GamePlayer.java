package org.wenxueliu.ds.proxy.game1;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class GamePlayer implements IGamePlayer {
    private String name;
    private String user;
    private String passwd;

    GamePlayer(IGamePlayer gamePlayer, String name) {
        //这里为什么要传入 IGgmePlayer
        if (gamePlayer == null) {
            throw new IllegalStateException("cannot create");
        }
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
