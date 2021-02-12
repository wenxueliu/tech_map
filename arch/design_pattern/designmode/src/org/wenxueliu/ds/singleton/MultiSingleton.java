package org.wenxueliu.ds.singleton;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by liuwenxue on 05/10/2017.
 */
public class MultiSingleton {

    private static int maxNumOfSingleton = 2;
    private int currentIndex = 0;
    private String name;

    private static ArrayList<MultiSingleton> slist = new ArrayList<>();
    static {
        for (int i= 0;i < maxNumOfSingleton; i++) {
            slist.add(new MultiSingleton("the " + i + "th"));
        }
    }

    private MultiSingleton() {
    }
    private MultiSingleton(String name) {

    }

    public MultiSingleton getInstance() {
        Random random = new Random();
        currentIndex = random.nextInt(maxNumOfSingleton);
        return slist.get(currentIndex);
    }

}
