package org.deeplearning4j.rl4j.examples.advanced.QLearning;

public class Transaction {
    protected String name;
    protected int workLoad;
    //for seperate transactions
    //protected QualityMeasures qualityMeasures = new QualityMeasures();

    public Transaction(String name, int workLoad) {
        this.name = name;
        this.workLoad = workLoad;
    }
    @Override
    public String toString(){
        return this.name+": "+workLoad;
    }
}
