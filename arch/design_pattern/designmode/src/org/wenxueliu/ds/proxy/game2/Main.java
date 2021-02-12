package org.wenxueliu.ds.proxy.game2;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IGamePlayer player = new GamePlayer("dota");
        player.login("adin", "admin");
        player.killBoss();
        player.upgrade();

        IGamePlayer proxy = new GamePlayerProxy(player);
        proxy.login("admin", "admin");
        proxy.killBoss();
        proxy.upgrade();

        IGamePlayer rproxy = player.getProxy();
        rproxy.login("adim", "admin");
        rproxy.killBoss();
        rproxy.upgrade();
    }
}
