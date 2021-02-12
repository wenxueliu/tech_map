package org.wenxueliu.ds.respchain.woman;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {

    public static void main(String []args) {
        IWoman w1 = new Woman(1, "I want to go shopping");
        IWoman w2 = new Woman(2, "I want to go shopping");
        IWoman w3 = new Woman(3, "I want to go shopping");

        Handler father = new Father();
        Handler husband = new Husband();
        Handler son = new Son();
        father.setNextHandler(husband);
        husband.setNextHandler(son);

        father.handleMessage(w1);
        father.handleMessage(w2);
        father.handleMessage(w3);
    }
}
