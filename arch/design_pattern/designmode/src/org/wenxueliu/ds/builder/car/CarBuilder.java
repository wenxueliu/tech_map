package org.wenxueliu.ds.builder.car;

import java.util.ArrayList;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public abstract class CarBuilder {
    public abstract void setSequence(ArrayList<String> sequence);
    public abstract CarModel getCarModel();
}
