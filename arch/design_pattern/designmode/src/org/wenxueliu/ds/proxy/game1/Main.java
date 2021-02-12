package org.wenxueliu.ds.proxy.game1;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IGamePlayer proxy = new GamePlayerProxy("dota");
        proxy.login("admin", "admin");
        proxy.killBoss();
        proxy.upgrade();
    }
}
