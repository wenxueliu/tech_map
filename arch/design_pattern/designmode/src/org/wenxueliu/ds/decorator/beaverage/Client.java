package org.wenxueliu.ds.decorator.beaverage;

/**
 * Created by liuwenxue on 18/04/2018.
 */
public class Client {
    public static void main(String[] args) {
        // 首先，我们需要一个基础饮料，红茶、绿茶或咖啡
        Beverage beverage = new GreenTea();
        // 开始装饰
        beverage = new Lemon(beverage); // 先加一份柠檬
        beverage = new Mango(beverage); // 再加一份芒果

        System.out.println(beverage.getDescription() + " 价格：￥" + beverage.cost());
        //"绿茶, 加柠檬, 加芒果 价格：￥16"
    }
}
