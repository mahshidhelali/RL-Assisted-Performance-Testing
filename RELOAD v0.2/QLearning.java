package org.deeplearning4j.rl4j.examples.advanced.QLearning;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;



public class QLearning{


    private SUT SUT_env ;
    private SUTstate curr_SUT_state;
    private SUTstate prev_SUT_state;
    private int maxResposeTimeThreshold;
    private int responseTimeThreshold;
    private double maxErrorRateThreshold;
    private double errorRateThreshold;
    private int maxStep;
    private int episodeNumber;
    private int episodeStep;
    private CsvWriter csvWriter;
    private  double alpha;
    private  double gamma;
    private double epsilon;
    private  stateAction[][] Qtable = new stateAction[6][];
    private String agentStateDescription;
    private int episodeExecutionDelay;



    public QLearning(double alpha, double gamma, double maxErrorRateThreshold, int maxResposeTimeThreshold,CsvWriter csvWriter, int episodeExecutionDelay){

        SUT_env = new SUT();
        curr_SUT_state = SUT_env.getSUTState();
        this.maxResposeTimeThreshold = maxResposeTimeThreshold;
        this.maxErrorRateThreshold = maxErrorRateThreshold;
        //responseTimeThreshold = maxResposeTimeThreshold/2;
        responseTimeThreshold = 1300;
        //errorRateThreshold = maxErrorRateThreshold/2;
        errorRateThreshold = 0.05;
        episodeNumber = 1;
        episodeStep = 1;
        this.csvWriter = csvWriter;
        this.alpha= alpha;
        this.gamma = gamma;
        epsilon = 1.0;

        for (int x = 0; x < Qtable.length; x++) {
                    Qtable[x] = new stateAction[12];
                    for (int y=0; y< Qtable[x].length ; y++)
                        Qtable[x][y]= new stateAction();
        }
    }


    public  void Learn (int maxEpisodeNumber) throws Exception
    {
        epsilon = 1.0;
        int currStep = 1;
        //executing several episodes where in the end of each episode the SUT reaches the error rate or response time threshold
        while(episodeNumber <= maxEpisodeNumber){

            //if(episodeNumber == (maxEpisodeNumber+1)){
            //    csvWriter.saveQTable(Qtable);
            //}
            //if(episodeNumber > maxEpisodeNumber){
            //    this.maxResposeTimeThreshold += 100;
            //    this.maxErrorRateThreshold += 0.01;
            //}

            //decaying epsilon
            epsilon = 1.0 - (double)episodeNumber/(double)maxEpisodeNumber;
            if (epsilon<=0.1) epsilon = 0.1;

            //decaying alpha
            //alpha = 0.8*(1.0 - (double)episodeNumber/(double)maxEpisodeNumber);

            SUT_env = new SUT();
            //initializing first state
            SUT_env.applyAction_base();
            curr_SUT_state = SUT_env.getSUTState();
            int IndexofAgentsCurrentState = DetectState();
            //executing each step in each episode
            do{
                IndexofAgentsCurrentState = Step(IndexofAgentsCurrentState, epsilon);
                episodeStep += 1;
                currStep += 1;
            }while(!isDone());
            episodeStep=1;
            episodeNumber = episodeNumber+1;
            //delaying the execution of next state to let the SUT go to a normal situation
            try { TimeUnit.MINUTES.sleep(episodeExecutionDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public  int Step (int IndexofAgentsCurrentState, double epsilon) throws Exception
    {

       //Select An Action
        int action = chooseAnAction(IndexofAgentsCurrentState, epsilon);


        prev_SUT_state = curr_SUT_state;
        SUT_env.applyAction(action);
        curr_SUT_state = SUT_env.getSUTState();

        String transactionName = SUT_env.transactions[action].name;
        int IndexofDetectedNewState = this.DetectState();

        logStatus(transactionName);

        double reward = CalculateReward();

        //Updating Qvalue
        Qtable[IndexofAgentsCurrentState][action].Q_value = ((1 - alpha) * Qtable[IndexofAgentsCurrentState][action].Q_value) + alpha * (reward + gamma * Maximum(IndexofDetectedNewState, false));

       //Setting the Next State as the Current State for the next lo
       return IndexofDetectedNewState;
   }

    public int DetectState()
    {
      //Detecting the state based on the quality measurements including average response time & error rate.
    	int IndexofDetectedState=-1;


    	double averageResponseTime = curr_SUT_state.qualityMeasures.responseTime;
    	double averageErrorRate = curr_SUT_state.qualityMeasures.errorRate;

        //error rate: Low, response time: Low
        if( averageErrorRate <= errorRateThreshold && averageResponseTime < (responseTimeThreshold - responseTimeThreshold * 0.05)){
            IndexofDetectedState = 0;
            agentStateDescription = "LL";
        }
        //error rate: Low, response time: Normal
        else if( averageErrorRate <= errorRateThreshold && (responseTimeThreshold - (responseTimeThreshold * 0.05)) <= averageResponseTime && averageResponseTime <= (responseTimeThreshold + (responseTimeThreshold * 0.05))){
            IndexofDetectedState = 1;
            agentStateDescription = "LN";
        }
        //error rate: Low, response time: High
        else if( averageErrorRate <= errorRateThreshold && averageResponseTime > (responseTimeThreshold + responseTimeThreshold * 0.05)){
            IndexofDetectedState = 2;
            agentStateDescription = "LH";
        }
        //error rate: High, response time: Low
        else if( averageErrorRate > errorRateThreshold && averageResponseTime < (responseTimeThreshold - responseTimeThreshold * 0.05)){
            IndexofDetectedState = 3;
            agentStateDescription = "HL";

        }
        //error rate: High, response time: Normal
        else if( averageErrorRate > errorRateThreshold && (responseTimeThreshold - (responseTimeThreshold * 0.05)) <= averageResponseTime && averageResponseTime <= (responseTimeThreshold + (responseTimeThreshold * 0.05))){
            IndexofDetectedState = 4;
            agentStateDescription = "HN";
        }
        //error rate: High, response time: High
        else if( averageErrorRate > errorRateThreshold && averageResponseTime > (responseTimeThreshold + (responseTimeThreshold * 0.05))){
            IndexofDetectedState = 5;
            agentStateDescription = "HH";
        }


        return IndexofDetectedState;

    }

public  int chooseAnAction(int IndexofAgentsCurrentState, double epsilon){


        double randomNumber = 0;
        boolean choiceIsValid = false;
        int possibleAction =0;

        while (choiceIsValid == false) {
            // Get a random value between 0(inclusive) and 1(exclusive).
            randomNumber = new Random().nextDouble();
            //choosing a random action
            if (randomNumber<epsilon){
                possibleAction = new Random().nextInt(Qtable[0].length);
                if (Qtable[IndexofAgentsCurrentState][possibleAction].Q_value > -1) {
                choiceIsValid = true;
                System.out.println("Random action");
                }
            }
            //choosing best action
            else {
                possibleAction = (int) Maximum(IndexofAgentsCurrentState, true);
                if (Qtable[IndexofAgentsCurrentState][possibleAction].Q_value > -1) {
                choiceIsValid = true;
                System.out.println("action with maximum Qvalue");
               }
            }
        }
        return possibleAction;
    }


    public  double Maximum(int IndexofState, final boolean ReturnIndexOnly) {
        int winner = 0;
        boolean foundNewWinner = false;
        boolean done = false;

        while (!done) {
            foundNewWinner = false;
            for (int i = 0; i < Qtable[0].length; i++) {
                if (i != winner) {             // Avoid self-comparison.
                    if (Qtable[IndexofState][i].Q_value > Qtable[IndexofState][winner].Q_value) {
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if (foundNewWinner == false) {
                done = true;
            }
        }

        if (ReturnIndexOnly == true) {
            return winner;
        } else {
            return Qtable[IndexofState][winner].Q_value;

        }
    }





    public  Double CalculateReward()
    {
        double normalizedResponseTime = curr_SUT_state.qualityMeasures.responseTime*100 / maxResposeTimeThreshold;
        double normalizedErrorRate =   curr_SUT_state.qualityMeasures.errorRate*100 / maxErrorRateThreshold;
        double reward = Math.pow(normalizedResponseTime,2)+Math.pow(normalizedErrorRate,2);
        System.out.println("reward: "+reward);
        return reward;

        //double reward = -1;
        //if (curr_SUT_state.qualityMeasures.errorRate >= maxErrorRateThreshold || curr_SUT_state.qualityMeasures.responseTime >= maxResposeTimeThreshold)
        //    reward = 100;
        //return reward;
    }

    //returns true if the SUT has hit the error rate or response time and the episode is finished
    public boolean isDone() {
        boolean done = false;
        if (curr_SUT_state.qualityMeasures.errorRate > maxErrorRateThreshold || curr_SUT_state.qualityMeasures.responseTime > maxResposeTimeThreshold){
            done = true;
            //this.logger.info("Mission ended");
            System.out.println("Mission ended");
            csvWriter.writeRowInLog(episodeNumber,episodeStep-1,curr_SUT_state);

        }

        return done;
    }

    public void logStatus(String transactionName) {
        System.out.println("episode: " + episodeNumber + ", episodeStep: " + episodeStep);
        System.out.println("StepReply: " + transactionName);
        System.out.println(curr_SUT_state.toString());
        //this.logger.info(curr_SUT_state.toString());

        csvWriter.writeRowInFullLog(episodeNumber,episodeStep,transactionName,curr_SUT_state,this.agentStateDescription,epsilon,alpha);
    }


}
