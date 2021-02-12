package org.wenxueliu.ds.builder.car;

import java.util.ArrayList;

/**
 * Created by liuwenxue on 06/10/2017.
 */
public abstract class CarModel {
    private ArrayList<String> sequence = new ArrayList<>();
    abstract void start();
    abstract void stop();
    abstract void alarm();
    abstract void engineBoom();
    void run() {
        for (String step : this.sequence) {
            if (step.equals("start")) {
                this.start();
            } else if (step.equals("stop")) {
                this.stop();
            } else if (step.equals("alarm")) {
                this.alarm();
            } else if (step.equals("engineerBoom")) {
                this.engineBoom();
            } else {
                throw new IllegalStateException("not support step");
            }

        }
    }
    void setSequence(ArrayList sequence) {
        this.sequence = sequence;
    }
}
