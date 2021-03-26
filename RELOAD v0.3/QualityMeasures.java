package org.deeplearning4j.rl4j.examples.advanced.DQN1;


import org.deeplearning4j.rl4j.space.Encodable;

public abstract class QualityMeasures implements Encodable {

    public double responseTime;
    public double errorRate;


    public QualityMeasures(double responseTime, double errorRate) {
        this.responseTime = responseTime;
        this.errorRate = errorRate;
    }

    public QualityMeasures() {
        responseTime = 0;
        errorRate = 0;
    }

    public void update(double responseTime, double errorRate) {
        this.responseTime = responseTime;
        this.errorRate = errorRate;
    }

    @Override
    public String toString() {
        String rt = "responseTime: " + responseTime;
        String er = "errorRate: " + errorRate;
        return rt + "\n" + er ;
    }

    @Override
    public double[] toArray() {
        double[] ar = new double[]{this.responseTime, this.errorRate};
        return ar;
    }
}
