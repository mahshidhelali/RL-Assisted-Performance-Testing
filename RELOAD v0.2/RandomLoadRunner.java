package org.deeplearning4j.rl4j.examples.advanced.QLearning;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class RandomLoadRunner {


    private SUT SUT_env ;
    private SUTstate curr_SUT_state;
    private int maxResposeTimeThreshold;
    private double maxErrorRateThreshold;
    private int maxStep;
    private int iterationNumber;
    private int iterationStep;
    private CsvWriter csvWriter;
    private Random random;
    private int episodeExecutionDelay;


    public RandomLoadRunner(int maxResposeTimeThreshold, double maxErrorRateThreshold, CsvWriter csvWriter, int episodeExecutionDelay) {
        SUT_env = new SUT();
        curr_SUT_state = SUT_env.getSUTState();
        this.maxResposeTimeThreshold = maxResposeTimeThreshold;
        this.maxErrorRateThreshold = maxErrorRateThreshold;
        iterationNumber = 1;
        iterationStep = 1;
        this.csvWriter = csvWriter;
        this.episodeExecutionDelay = episodeExecutionDelay;
        random = new Random();
    }


    public void execute(int maxIterationNumber){

        int currStep = 1;
        int action;
        //executing several episodes where in the end of each episode the SUT reaches the error rate or response time threshold
        while(iterationNumber <= maxIterationNumber){

            //this.maxResposeTimeThreshold += 100;
            //this.maxErrorRateThreshold += 0.01;

            SUT_env = new SUT();
            curr_SUT_state = SUT_env.getSUTState();
            //executing each step in each episode
            while(!isDone()){

                action = getRandomAction();
                SUT_env.applyAction(action);
                curr_SUT_state = SUT_env.getSUTState();

                logStatus(SUT_env.transactions[action].name);
                iterationStep = iterationStep+1;

            }
            currStep = currStep+iterationStep;
            iterationStep=1;
            iterationNumber = iterationNumber+1;
            //delaying the execution of next state to let the SUT go to a normal situation
            try { TimeUnit.MINUTES.sleep(episodeExecutionDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //returns true if the SUT has hit the error rate or response time and the episode is finished
    public boolean isDone() {
        boolean done = false;
        if (curr_SUT_state.qualityMeasures.errorRate > maxErrorRateThreshold || curr_SUT_state.qualityMeasures.responseTime > maxResposeTimeThreshold){
            done = true;
            csvWriter.writeRowInLog(iterationNumber,iterationStep-1,curr_SUT_state);
           // System.out.println("Mission ended");
        }

        return done;
    }


    public int getRandomAction(){
    	return random.nextInt(13);
    }

      public void logStatus(String transactionName) {
        System.out.println("iteration: " + iterationNumber + ", iterationStep: " + iterationStep);
        System.out.println(curr_SUT_state.toString());
        //this.logger.info(curr_SUT_state.toString());

        csvWriter.writeRowInFullLog(iterationNumber,iterationStep,transactionName,curr_SUT_state);
    }


}
