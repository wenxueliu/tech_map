package org.wenxueliu.ds.builder.car;

import java.util.ArrayList;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class BenzBuilder extends CarBuilder {
    private BenzModel benz = new BenzModel();
    @Override
    public void setSequence(ArrayList<String> sequence) {
        this.benz.setSequence(sequence);
    }

    @Override
    public CarModel getCarModel() {
        return this.benz;
    }
}
