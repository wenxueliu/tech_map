package org.wenxueliu.ds.builder.car;

import java.util.ArrayList;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public class Director {
    private BenzBuilder benzBuilder = new BenzBuilder();
    private BWMBuilder bwmBuiler = new BWMBuilder();

    public BenzModel getABenzModel() {
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add("start");
        sequence.add("stop");
        this.benzBuilder.setSequence(sequence);
        return (BenzModel) this.benzBuilder.getCarModel();
    }

    public BenzModel getBBenzModel() {
        ArrayList<String> sequence = new ArrayList<>();
        sequence.clear();
        sequence.add("start");
        sequence.add("engineer boom");
        sequence.add("stop");
        this.benzBuilder.setSequence(sequence);
        return (BenzModel) this.benzBuilder.getCarModel();
    }

    public BWMModel getABWMModel() {
        ArrayList<String> sequence = new ArrayList<>();
        sequence.add("start");
        sequence.add("engineer boom");
        sequence.add("stop");
        this.bwmBuiler.setSequence(sequence);
        return (BWMModel)this.bwmBuiler.getCarModel();
    }
}
