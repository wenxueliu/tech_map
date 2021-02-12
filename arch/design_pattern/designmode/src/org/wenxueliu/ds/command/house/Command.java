package org.wenxueliu.ds.command.house;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Command {

    List<House> houses = new ArrayList<>();

    Command() {
        House wanke = new WankeHouse();
        House jindi = new JindiHouse();
        House baoli = new MayaMediator(new BaoliHouse());
        House hengda = new LianjiaMediator(new HengdaHouse());

        houses.add(wanke);
        houses.add(jindi);
        houses.add(baoli);
        houses.add(hengda);
    }

    public abstract void execute();
}
