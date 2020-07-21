import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElementTraverser;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.SummaryReport;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;

public class LoadTester{

    private StandardJMeterEngine jmeter;
    private Summariser summer;
    private ResultCollector logger; 

    String JMETER_HOME_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1";
    String JMETER_PROPERTY_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1/bin/jmeter.properties";
    String JMETER_LOG_FILE_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/Log/Test.jtl";
    String JMX_FILES_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/";

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

		summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		if (summariserName.length() > 0) {
		   summer = new Summariser(summariserName);
		}
		  
		String logFile = JMETER_LOG_FILE_PATH;
		logger = new ResultCollector(summer);
		logger.setFilename(logFile);

	}

	 public boolean ExecuteTransaction(String transactionName,int numOfThreads,int rampUpTime,int numOfLoops, QualityMeasures QM_transaction){

        
        HashTree testPlanTree; 
        boolean testPassed = false;        
        try{
           testPlanTree = SaveService.loadTree(new File (JMX_FILES_PATH+transactionName+".jmx"));

         //  testPlanTree.add(testPlanTree.getArray()[0], logger);
          // System.out.println(testPlanTree.toString());
          
           SearchByClass<ThreadGroup> threadGroups = new SearchByClass<>(ThreadGroup.class);
           testPlanTree.traverse(threadGroups);
           Collection<ThreadGroup> threadGroupsRes = threadGroups.getSearchResults();
           for (ThreadGroup threadGroup : threadGroupsRes) {
                      
               threadGroup.setNumThreads(numOfThreads);
               threadGroup.setRampUp(rampUpTime);
               ((LoopController)threadGroup.getSamplerController()).setLoops(numOfLoops);
               
               System.out.println("Transaction: "+ transactionName +", Workload (num of threads): "+threadGroup.getProperty("ThreadGroup.num_threads").toString()); 
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
                    myResultCollector.calculateAverageQualityMeasures(QM_transaction);
        

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