package org.deeplearning4j.rl4j.examples.advanced.DQN1;

public class DqnProperties {

    Integer seed;
    int maxEpochStep;
    int maxStep;
    int expRepMaxSize;
    int batchSize;
    int targetDqnUpdateFreq;
    int updateStart;
    double rewardFactor;
    double gamma;
    double errorClamp;
    float minEpsilon;
    int epsilonNbStep;
    boolean doubleDQN;

    public DqnProperties(Integer seed, int maxEpochStep, int maxStep, int expRepMaxSize, int batchSize, int targetDqnUpdateFreq, int updateStart, double rewardFactor, double gamma, double errorClamp, float minEpsilon, int epsilonNbStep, boolean doubleDQN) {
        this.seed = seed;
        this.maxEpochStep = maxEpochStep;
        this.maxStep = maxStep;
        this.expRepMaxSize = expRepMaxSize;
        this.batchSize = batchSize;
        this.targetDqnUpdateFreq = targetDqnUpdateFreq;
        this.updateStart = updateStart;
        this.rewardFactor = rewardFactor;
        this.gamma = gamma;
        this.errorClamp = errorClamp;
        this.minEpsilon = minEpsilon;
        this.epsilonNbStep = epsilonNbStep;
        this.doubleDQN = doubleDQN;
    }

    public String toCsvString(){
        String str = "DqnProperties:"+",seed,maxEpochStep,maxStep,expRepMaxSize,batchSize,targetDqnUpdateFreq,updateStart,rewardFactor,gamma,errorClamp,minEpsilon,epsilonNbStep,doubleDQN\n";
        str += ","+seed+","+maxEpochStep+","+maxStep+","+expRepMaxSize+","+batchSize+","+targetDqnUpdateFreq+","+updateStart+","+rewardFactor+","+gamma+","+errorClamp+","+minEpsilon+","+epsilonNbStep+","+doubleDQN+"\n";
        return str;
    }
}
