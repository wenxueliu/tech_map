package org.wenxueliu.ds.singleton;

import java.lang.reflect.Constructor;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class SingletonFactory {

    private static Singleton singleton;

    static {
        try {
            Class cl = Class.forName(Singleton.class.getName());
            Constructor constructor = cl.getDeclaredConstructor();
            constructor.setAccessible(true);
            singleton = (Singleton)constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Singleton getSingleton() {
        return singleton;
    }
}
