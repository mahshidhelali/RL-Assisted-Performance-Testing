package org.deeplearning4j.rl4j.examples.advanced.QLearning;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class DummyLoadRunner {


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


    public DummyLoadRunner(int maxResposeTimeThreshold, double maxErrorRateThreshold, CsvWriter csvWriter,int episodeExecutionDelay) {
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

        while(iterationNumber <= maxIterationNumber){

            this.maxResposeTimeThreshold += 100;
            this.maxErrorRateThreshold += 0.01;

            SUT_env = new SUT();
            SUT_env.applyAction_base();
            curr_SUT_state = SUT_env.getSUTState();
            logStatus();


            while(!isDone()){

                SUT_env.applyAction();
                curr_SUT_state = SUT_env.getSUTState();

                logStatus();
                iterationStep = iterationStep+1;

            }
            currStep = currStep+iterationStep;
            iterationStep=1;
            iterationNumber = iterationNumber+1;
            try { TimeUnit.MINUTES.sleep(episodeExecutionDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

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
    	return random.nextInt()%13;
    }

      public void logStatus() {
        System.out.println("iteration: " + iterationNumber + ", iterationStep: " + iterationStep);
        System.out.println(curr_SUT_state.toString());
        //this.logger.info(curr_SUT_state.toString());

        csvWriter.writeRowInFullLog(iterationNumber,iterationStep,"all",curr_SUT_state);
    }


}
