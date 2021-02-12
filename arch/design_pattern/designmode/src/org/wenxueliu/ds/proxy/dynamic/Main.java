package org.wenxueliu.ds.proxy.dynamic;

import java.lang.reflect.*;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Main {
    public static void main(String []args) {
        IGamePlayer player = new GamePlayer("dota");
        InvocationHandler handler = (InvocationHandler) new GamePlayIH(player);
        ClassLoader cl = player.getClass().getClassLoader();
        IGamePlayer proxy = (IGamePlayer)Proxy.newProxyInstance(cl, new Class[] {IGamePlayer.class}, handler);
        proxy.login("admin", "admin");
        proxy.killBoss();
        proxy.upgrade();
    }
}
