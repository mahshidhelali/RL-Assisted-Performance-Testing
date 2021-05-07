package org.deeplearning4j.rl4j.examples.advanced.DQN1;


import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;

import org.apache.commons.io.FileUtils;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPAbstractImpl;
import org.apache.jmeter.protocol.http.sampler.HTTPHC4Impl;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.JMeterThread;


import java.io.File;
import java.util.Collection;

public class LoadTester{

    private StandardJMeterEngine jmeter;
    private Summariser summer;
    private ResultCollector logger;

    private String JMETER_HOME_PATH = "/Users/IEUser/Desktop/apache-jmeter-5.1";
    private String JMETER_PROPERTY_PATH = "/Users/IEUser/Desktop/apache-jmeter-5.1/bin/jmeter.properties";
    private String JMETER_LOG_FILE_PATH = "/Users/IEUser/Desktop/apache-jmeter-5.1/bin/transactions_rubis/all_transactions_local_server.jtl";
    private String JMX_FILES_PATH = "/Users/IEUser/Desktop/apache-jmeter-5.1/bin/transactions_rubis/";

//    private String JMETER_HOME_PATH = "/Users/erisa/OneDrive/Desktop/apache-jmeter-3.1/";
//    private String JMETER_PROPERTY_PATH = "/Users/erisa/OneDrive/Desktop/apache-jmeter-3.1/bin/jmeter.properties";
////    private String JMETER_LOG_FILE_PATH = "/Users/erisa/OneDrive/Desktop/RUBiS_Transactions/all_transactions.jtl";
//    private String JMETER_LOG_FILE_PATH = "/Users/erisa/OneDrive/Desktop/apache-jmeter-3.1/bin/transactions/All_PetStore.jtl";
//    private String JMX_FILES_PATH = "/Users/erisa/OneDrive/Desktop/apache-jmeter-3.1/bin/transactions/";


//    private String JMX_FILES_PATH = "/Users/erisa/OneDrive/Desktop/jmx_files/";

	public LoadTester(){
		Initialize();
	}

	private void Initialize(){

		// JMeter Engine
		jmeter = new StandardJMeterEngine();
		// Initialize Properties, logging, locale, etc.
		JMeterUtils.loadJMeterProperties(JMETER_PROPERTY_PATH);
		JMeterUtils.setJMeterHome(JMETER_HOME_PATH);
		// JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
		JMeterUtils.initLocale();
		// Initialize JMeter SaveService
		try{
			SaveService.loadProperties();
		}catch(Exception e){
			e.printStackTrace();
		}



	}

	//for seperate transactions
    /*
	 public boolean ExecuteTransaction(Transaction t,int rampUpTime,int numOfLoops){

        HashTree testPlanTree;
        boolean testPassed = false;
        try{
           testPlanTree = SaveService.loadTree(new File (JMX_FILES_PATH+t.name+".jmx"));

         //  testPlanTree.add(testPlanTree.getArray()[0], logger);
          // System.out.println(testPlanTree.toString());

           SearchByClass<ThreadGroup> threadGroups = new SearchByClass<>(ThreadGroup.class);
           testPlanTree.traverse(threadGroups);
           Collection<ThreadGroup> threadGroupsRes = threadGroups.getSearchResults();
           for (ThreadGroup threadGroup : threadGroupsRes) {

               threadGroup.setNumThreads(t.workLoad);
               threadGroup.setRampUp(rampUpTime);
               ((LoopController)threadGroup.getSamplerController()).setLoops(numOfLoops);

               System.out.println("Transaction: "+ t.name +", Workload (num of threads): "+threadGroup.getProperty("ThreadGroup.num_threads").toString());
               System.out.println("Ramp Up:"+threadGroup.getRampUp());
               System.out.println("Loop: "+ ((LoopController)threadGroup.getSamplerController()).getProperty("LoopController.loops"));
           }

           MyResultCollector myResultCollector = new MyResultCollector(summer);
           testPlanTree.add(testPlanTree.getArray()[0], myResultCollector);

           // Run JMeter Test
           jmeter.configure(testPlanTree);
           try{
                jmeter.run();
                testPassed = myResultCollector.allTestSamplesPassed();
                if(testPassed)
                    myResultCollector.calculateAverageQualityMeasures(t);


           }catch(Exception e){
               System.out.println("jmeter run");
               e.printStackTrace();
           }
        }catch(Exception e){
           System.out.println("testPlanTree");
           e.printStackTrace();
        }

        return testPassed;
    }*/

    public boolean ExecuteAllTransactions(Transaction[] transactions, int rampUpTime, int numOfLoops, QualityMeasures qualityMeasures){

        summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String logFile = JMETER_LOG_FILE_PATH;
        logger = new ResultCollector(summer);
        logger.setFilename(logFile);

        HashTree testPlanTree;
        boolean testPassed = false;
        String transactionName = "all_transactions";
        try{
            testPlanTree = SaveService.loadTree(new File (JMX_FILES_PATH+"all_transactions_local_server.jmx"));

            //  testPlanTree.add(testPlanTree.getArray()[0], logger);
            // System.out.println(testPlanTree.toString());

            SearchByClass<ThreadGroup> threadGroups = new SearchByClass<>(ThreadGroup.class);
            testPlanTree.traverse(threadGroups);
            Collection<ThreadGroup> threadGroupsRes = threadGroups.getSearchResults();
            for (ThreadGroup threadGroup : threadGroupsRes) {
                for (Transaction t : transactions) {
                    if((t.name+"_thread_group").equals(threadGroup.getName())) {

                        threadGroup.setNumThreads(t.workLoad);
                        threadGroup.setRampUp(rampUpTime);
                        ((LoopController) threadGroup.getSamplerController()).setLoops(numOfLoops);

                        //System.out.println("thread group: " + threadGroup.getName());
                        //System.out.println("Transaction: " + t.name + ", Workload (num of threads): " + threadGroup.getProperty("ThreadGroup.num_threads").toString());
                        //System.out.println("Ramp Up:" + threadGroup.getRampUp());
                        //System.out.println("Loop: " + ((LoopController) threadGroup.getSamplerController()).getProperty("LoopController.loops"));
                        break;
                    }
                }
            }
            System.out.println("Ramp Up:" + rampUpTime);


            MyResultCollector myResultCollector = new MyResultCollector(summer);
            testPlanTree.add(testPlanTree.getArray()[0], myResultCollector);

            // Run JMeter Test
            jmeter.configure(testPlanTree);
            try{
                jmeter.run();
                testPassed = myResultCollector.allTestSamplesPassed();
                if(testPassed)
                    myResultCollector.calculateAverageQualityMeasures(qualityMeasures);


            }catch(Exception e){
                System.out.println("jmeter run");
                e.printStackTrace();
            }
        }catch(Exception e){
            System.out.println("testPlanTree");
            e.printStackTrace();
        }

        return testPassed;
    }


}
