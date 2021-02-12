package org.wenxueliu.ds.proxy.game;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IGamePlayer player = new GamePlayer("dota");
        IGamePlayer proxy = new GamePlayerProxy(player);
        proxy.login("admin", "admin");
        proxy.killBoss();
        proxy.upgrade();
    }
}
