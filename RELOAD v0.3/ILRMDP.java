package org.deeplearning4j.rl4j.examples.advanced.DQN1;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.NeuralNetFetchable;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import java.lang.Enum;
import java.lang.Object;
import java.io.Serializable;
import java.lang.Comparable;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class ILRMDP implements MDP<QualityMeasures, Integer, DiscreteSpace>{



    private final Logger logger;
    private SUT SUT_env ;
    DiscreteSpace actionSpace;
    SUTObservationSpace observationSpace;
    SUTstate curr_SUT_state;
    SUTstate prev_SUT_state;
    int maxResposeTimeThreshold;
    double maxErrorRateThreshold;
    int episodeExecutionDelay;
    private NeuralNetFetchable<IDQN> fetchable;
    int episodeNumber;
    int episodeStep;
    CsvWriter csvWriter;


    public ILRMDP(int maxResposeTimeThreshold, double maxErrorRateThreshold, CsvWriter csvWriter, int episodeExecutionDelay) {
        logger = LoggerFactory.getLogger(this.getClass());
        SUT_env = new SUT();
        actionSpace = new DiscreteSpace(8);//defining actions
        observationSpace = new SUTObservationSpace(maxResposeTimeThreshold, maxErrorRateThreshold);//defining states
        curr_SUT_state = SUT_env.getSUTState();
        this.maxResposeTimeThreshold = maxResposeTimeThreshold;
        this.maxErrorRateThreshold = maxErrorRateThreshold;
        this.episodeExecutionDelay = episodeExecutionDelay;
        episodeNumber = 0;
        episodeStep = 0;
        this.csvWriter = csvWriter;
    }

    @Override
    public ObservationSpace<QualityMeasures> getObservationSpace() {
        return this.observationSpace;
    }

   @Override
    public DiscreteSpace getActionSpace() {
        return this.actionSpace;
    }

    //This function is called before executing each episode
    @Override
    public QualityMeasures reset() {

        csvWriter.writeRowInLog(episodeNumber,episodeStep-1,curr_SUT_state);

        //delaying between executing each two episodes
        try { TimeUnit.MINUTES.sleep(episodeExecutionDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        episodeStep = 1; //resetting episode step
        episodeNumber = episodeNumber+1; //updating epigsode number
        SUT_env = new SUT();
        SUT_env.applyAction_base(); //initializing the first step of the episode
        curr_SUT_state = SUT_env.getSUTState();
        return curr_SUT_state.qualityMeasures;
    }

    @Override
    public void close() {

    }

    //executing one step in one episode
    @Override
    public StepReply<QualityMeasures> step(Integer action) {

        // logging the ANN output
        if (this.fetchable != null) {
            INDArray input = Nd4j.create(1, (curr_SUT_state.qualityMeasures).toArray().length);
            input.putRow((long)1, Nd4j.create((curr_SUT_state.qualityMeasures).toArray()));

            INDArray output = ((IDQN)this.fetchable.getNeuralNet()).output(input);
            //logger.info(output.toString());
            System.out.println(output.toString());
        }

        prev_SUT_state = curr_SUT_state;
        LocalTime startTime = LocalTime.now();
        SUT_env.applyAction(action);
        LocalTime endTime = LocalTime.now();
        curr_SUT_state = SUT_env.getSUTState();

        String transactionName = SUT_env.transactions[action].name;
        logStatus(transactionName,startTime,endTime);
        episodeStep = episodeStep+1;

        double reward = CalculateReward();

        return new StepReply(curr_SUT_state.qualityMeasures, reward, this.isDone(), new JSONObject("{transaction:"+transactionName+"}"));
    }

    //reward function
    private double CalculateReward(){
        //double beta = 0.2;
        //double reward_Part1= (curr_SUT_state.qualityMeasures.responseTime-this.prev_SUT_state.qualityMeasures.responseTime)/curr_SUT_state.qualityMeasures.responseTime;
        //double reward_Part2 = (curr_SUT_state.qualityMeasures.errorRate-this.prev_SUT_state.qualityMeasures.errorRate)/curr_SUT_state.qualityMeasures.errorRate;
        //return Math.round((beta * reward_Part1 + (1-beta) * reward_Part2)*100.0)/100.0;

        double normalizedResponseTime = curr_SUT_state.qualityMeasures.responseTime*100 / maxResposeTimeThreshold;
        double normalizedErrorRate =   curr_SUT_state.qualityMeasures.errorRate*100 / maxErrorRateThreshold;
        return Math.pow(normalizedResponseTime,2)+Math.pow(normalizedErrorRate,2);

        //double reward = -1;
        //if (curr_SUT_state.qualityMeasures.errorRate >= maxErrorRateThreshold || curr_SUT_state.qualityMeasures.responseTime >= maxResposeTimeThreshold)
        //    reward = 100;
        //return reward;

    }

    // determining if the episode has finished or not based on checking if we have hit the response time of error rate threshold.
    @Override
    public boolean isDone() {
        boolean done = false;
        if (curr_SUT_state.qualityMeasures.errorRate > maxErrorRateThreshold || curr_SUT_state.qualityMeasures.responseTime > maxResposeTimeThreshold){
            done = true;
            //this.logger.info("Mission ended");
            System.out.println("Mission ended");
        }
        return done;
    }

    @Override
    public MDP<QualityMeasures, Integer, DiscreteSpace> newInstance() {
        return null;
    }

    // used for logging the output of the ANN which is the Qvalues of all actions of current state
    public void setFetchable(NeuralNetFetchable<IDQN> fetchable) {
        this.fetchable = fetchable;
    }

    public void logStatus(String transactionName, LocalTime startTime, LocalTime endTime) {
        System.out.println("episode: " + episodeNumber + ", episodeStep: " + episodeStep);
        System.out.println("StepReply: " + transactionName);
        System.out.println(curr_SUT_state.toString());
        //this.logger.info(curr_SUT_state.toString());

        csvWriter.writeRowInFullLog(episodeNumber,episodeStep,transactionName,curr_SUT_state,startTime,endTime);
    }

}


