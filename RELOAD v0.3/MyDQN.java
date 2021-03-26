package org.deeplearning4j.rl4j.examples.advanced.DQN1;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPAbstractImpl;
import org.apache.jmeter.protocol.http.sampler.HTTPHC4Impl;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.JMeterThread;

import org.apache.commons.io.FileUtils;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;

public class MyDQN {

    private static int maxEpocStep = 30;
    private static int maxStep = 450;
    private static int expRepMaxSize = maxStep;

    public static DqnProperties dqnProperties =
        new DqnProperties(
            123,   //Random seed
            maxEpocStep,//Max step per epoch
            maxStep, //Max step, training will finish after this number of steps
            expRepMaxSize, //Max size of experience replay
            1,    //size of batches
            10,   //target update (hard), Regular Q-learning can overestimate the action values under certain conditions. Double Q-learning adds stability to the learning. The main idea of double DQN is to freeze the network after every M number of updates or smoothly average for every M number of updates. The value of M is referred to as targetDqnUpdateFreq
            1,     //num step noop warmup
            0.1,  //reward scaling
            0.5,  //gamma, discount factor
            10.0,  //td-error clipping
            0.1f,  //min epsilon
            400,  //num step for eps greedy anneal
            true   //double DQN
        );

    public static QLearning.QLConfiguration LOAD_TEST_QL =
        new QLearning.QLConfiguration(
            dqnProperties.seed,
            dqnProperties.maxEpochStep,
            dqnProperties.maxStep,
            dqnProperties.expRepMaxSize,
            dqnProperties.batchSize,
            dqnProperties.targetDqnUpdateFreq,
            dqnProperties.updateStart,
            dqnProperties.rewardFactor,
            dqnProperties.gamma,
            dqnProperties.errorClamp,
            dqnProperties.minEpsilon,
            dqnProperties.epsilonNbStep,
            dqnProperties.doubleDQN
        );

    //configuring the neural net
    public static DQNFactoryStdDense.Configuration LOAD_TEST_NET =
        DQNFactoryStdDense.Configuration.builder()
            .l2(0.01).updater(new Adam(1e-2)).numLayer(3).numHiddenNodes(16).build();

    public static void main(String[] args) throws IOException {
        loadTest();
    }


    public static void loadTest() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager();

        SUT SUT_env = new SUT();

        int maxResponseTimeThreshold = 1500;
        double maxErrorRateThreshold = 0.2;
        int episodeExecutionDelay = 5;
        //logging configuration
        CsvWriter csvWriter = new CsvWriter(dqnProperties, maxResponseTimeThreshold, maxErrorRateThreshold,episodeExecutionDelay);
        //create the Intelligent Load Runner MDP
        ILRMDP mdp = new ILRMDP(maxResponseTimeThreshold, maxErrorRateThreshold, csvWriter,episodeExecutionDelay);

        //define the training method
        Learning<QualityMeasures, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<QualityMeasures>(mdp, LOAD_TEST_NET, LOAD_TEST_QL, manager);

        //enable some logging for debug purposes on toy mdp
        mdp.setFetchable(dql);

        //start the training
        dql.train();

        //useless on toy but good practice!
        mdp.close();

    }


}
