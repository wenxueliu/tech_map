package org.wenxueliu.ds.builder.car;

import java.util.ArrayList;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class BWMBuilder extends CarBuilder {
    private BWMModel bwm = new BWMModel();
    @Override
    public void setSequence(ArrayList<String> sequence) {
        this.bwm.setSequence(sequence);
    }

    @Override
    public CarModel getCarModel() {
        return this.bwm;
    }
}
