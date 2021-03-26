import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

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

public class ReinforcementLearning {
	
	private  stateAction[][] Qtable = new stateAction[6][];
    private  double alpha;
    private  double gamma; 
    public double[] qualityMeasures_curr = new double [4];
    public double[] qualityMeasures_pre = new double [4];
    public LinkedList<double[]> qualityMeasuresTrans  = new LinkedList<double[]>();;
    public int[] transactionsWorkload = new int [11];
    public String stateDescription;
    private double Requirement_ResTimes;
    private double errorRateThreshold;
    Map<String, String> transactionsMap = new HashMap<String, String>();
    
    
    String JMETER_HOME_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1";
    String JMETER_PROPERTY_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1/bin/jmeter.properties";
    String JMETER_LOG_FILE_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/Log/Test.jtl";
    String JMX_FILES_PATH = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/";
    
    
     
    public  void Initializing(double alpha, double gamma, double Requirement_ResTimes, double errorRateThreshold) {
    for (int x = 0; x < Qtable.length; x++) {
                Qtable[x] = new stateAction[11];
                for (int y=0; y< Qtable[x].length ; y++)
                {  Qtable[x][y]= new stateAction();
                   
                }
                
            }
     this.alpha= alpha;
     this.gamma = gamma;
     this.Requirement_ResTimes = Requirement_ResTimes;
     this.errorRateThreshold = errorRateThreshold;
     
     transactionsMap.put("0", "home");
     transactionsMap.put("1","register_page");
     transactionsMap.put("2","register");
     transactionsMap.put("3", "login_page");
     transactionsMap.put("4","login");
     transactionsMap.put("5", "search_page");
     transactionsMap.put("6", "select_product");
     transactionsMap.put("7", "add_to_card");
     transactionsMap.put("8", "payment");
     transactionsMap.put("9", "confirm");
     transactionsMap.put("10","log_out");
    
    }
    
    public  int Learn (int IndexofCurrentState, double epsilon) throws Exception
    {
       //Save the Quality measures of previous state
    	
    	double QM_Pre_responseTime;
    	double QM_Pre_errorRate;
    	double QM_Pre_latency;
    	double QM_Pre_maxErrorRate;
    	QM_Pre_responseTime = this.qualityMeasures_curr[0];
    	QM_Pre_latency = this.qualityMeasures_curr[1];
    	QM_Pre_errorRate= this.qualityMeasures_curr[2];
    	QM_Pre_maxErrorRate =this.qualityMeasures_curr[3];
    	this.qualityMeasures_pre[0]=QM_Pre_responseTime;
    	this.qualityMeasures_pre[1]=QM_Pre_latency;
    	this.qualityMeasures_pre[2]=QM_Pre_errorRate;
    	this.qualityMeasures_pre[3]=QM_Pre_maxErrorRate; 
    	
    	
        System.out.println("----------------------------------------------");
       //Select An Action
        int action=0;
        boolean success= false;
        while (success== false){
        action = chooseAnAction(IndexofCurrentState, epsilon);
         
         //Applying The Selected Action
          
       
        success = ApplyAction(action);
         
        if (success== true )
            System.out.println("Action "+action+": "+transactionsMap.get(Integer.toString(action))+" successful");
        else
            System.out.println("Action "+action+": "+transactionsMap.get(Integer.toString(action))+" failed");     
        
        System.out.println("----------------------------------------------");
        
        
                
        }
        
        // Detection of new state
        
        int IndexofDetectedNewState = 0;
        IndexofDetectedNewState = this.DetectState();
        
        System.out.println("New State after action: "+IndexofDetectedNewState + "   "+ this.stateDescription);
        
         // Calculating The Reward
        Double Reward= 0.00;
        Reward = CalculateReward();
        
       
        
        //Updating Qvalue
        Qtable[IndexofCurrentState][action].Q_value = ((1 - alpha) * Qtable[IndexofCurrentState][action].Q_value) + alpha * (Reward + gamma * Maximum(IndexofDetectedNewState, false));
       
       //Setting the Next State as the Current State for the next loop
        
        
       return IndexofDetectedNewState;

   }
    
    public int DetectState()
    { 
      //Detecting the state based on the quality measurements including average response time & error rate.
    	int IndexofDetectedState=3;
    	
    	double responseTime=0;
    	double latency= 0;
    	double errorRate =0;
    	double averageResponseTime = 0;
    	double averageLatency =0;
    	double averageErrorRate = 0;
    	double maxErrorRate =0;
    	double temp=0;
    	
    	maxErrorRate = qualityMeasuresTrans.get(0)[2];
    	for (int i=0; i<qualityMeasuresTrans.size(); i++)
    		
    	{
    		responseTime = responseTime + qualityMeasuresTrans.get(i)[0];
    		latency = latency + qualityMeasuresTrans.get(i)[1];
    		errorRate = errorRate + qualityMeasuresTrans.get(i)[2];
    		
    		if (qualityMeasuresTrans.get(i)[2]> maxErrorRate)
    			{temp = (double) qualityMeasuresTrans.get(i)[2];
    			maxErrorRate =temp;
    			}
    		
    		
    	}
    	
    	averageResponseTime = responseTime / qualityMeasuresTrans.size();
    	averageErrorRate = errorRate / qualityMeasuresTrans.size();
    	averageLatency = latency / qualityMeasuresTrans.size();
    	
    	qualityMeasures_curr[0] = Math.round(averageResponseTime);
    	qualityMeasures_curr[1] = Math.round(averageLatency);
    	qualityMeasures_curr [2] = (double)Math.round(averageErrorRate*100)/100.00;
    	qualityMeasures_curr [3] = (double)Math.round(maxErrorRate*100)/100.00;
    	
    	System.out.println("averageResponseTime: " + averageResponseTime );
    	System.out.println("averageLatency: " + averageLatency);
    	System.out.println("averageErrorRate: " +averageErrorRate);
    	
    	System.out.println("averageResponseTime: " + qualityMeasures_curr[0] );
    	System.out.println("averageLatency: " + qualityMeasures_curr[1]);
    	System.out.println("averageErrorRate: " +qualityMeasures_curr [2]);
    	System.out.println("maxErrorRate: " +qualityMeasures_curr [3] );
    	
    	
    	 
        //error rate: Low, response time: Low
        if( (averageErrorRate <= errorRateThreshold) && (averageResponseTime < (Requirement_ResTimes - (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 0;
            stateDescription = "LL";
        }
        //error rate: Low, response time: Normal
        else if( (averageErrorRate <= errorRateThreshold) && ((Requirement_ResTimes - (Requirement_ResTimes * 0.05)) <= averageResponseTime) && (averageResponseTime <= (Requirement_ResTimes + (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 1;
            stateDescription = "LN";
        }
        //error rate: Low, response time: High
        else if( (averageErrorRate <= errorRateThreshold) && (averageResponseTime > (Requirement_ResTimes + (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 2;
            stateDescription = "LH";
        }
        //error rate: High, response time: Low
        else if( (averageErrorRate > errorRateThreshold) && (averageResponseTime < (Requirement_ResTimes - (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 3;
            stateDescription = "HL";
           
        }
        //error rate: High, response time: Normal
        else if( (averageErrorRate > errorRateThreshold) && ((Requirement_ResTimes - (Requirement_ResTimes * 0.05)) <= averageResponseTime) && (averageResponseTime <= (Requirement_ResTimes + (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 4;
            stateDescription = "HN";
        }
        //error rate: High, response time: High
        else if( (averageErrorRate > errorRateThreshold) && (averageResponseTime > (Requirement_ResTimes + (Requirement_ResTimes * 0.05)))){
            IndexofDetectedState = 5;
            stateDescription = "HH";
        }
 
        
        return IndexofDetectedState;
       
    }
    
public  int chooseAnAction(int IndexofCurrentState, double epsilon){
        
        // Randomly choose a possible action connected to the current state.
        double randomNumber = 0;
        boolean choiceIsValid = false;
        int possibleAction =0;
        
        // Randomly choose a possible action connected to the current state.
        while (choiceIsValid == false) {
            // Get a random value between 0(inclusive) and 1(exclusive).

            randomNumber = new Random().nextDouble();
            if (randomNumber<epsilon){
                possibleAction = new Random().nextInt(Qtable[0].length);   
                if (Qtable[IndexofCurrentState][possibleAction].Q_value > -1) {
                choiceIsValid = true;
                System.out.println("Random action");
                }
                //choiceIsValid = true;
                
            }
            else {
                 
                possibleAction = (int) Maximum(IndexofCurrentState, true);
                if (Qtable[IndexofCurrentState][possibleAction].Q_value > -1) {
                choiceIsValid = true;
                System.out.println("action with maximum Qvalue");
               }
               // choiceIsValid = true; 
            }
       
    }
        return possibleAction;
    }


public  double Maximum(int IndexofState, final boolean ReturnIndexOnly) {
    // If ReturnIndexOnly = True, the Q matrix index is returned.
    // If ReturnIndexOnly = False, the Q matrix value is returned.
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

//List of Actions. Modifying the load of different transactions 

public  boolean ApplyAction_base() //Modifying load of Transaction #1: Access to home page (Changing Num of threads in the associated jmx)
{
    boolean Success= false;
    
    //body of action
    qualityMeasuresTrans.clear();
    
    try {
		ExecutingExtTestPlan();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return Success;
	}
   
    Success = true;
    
    return Success;
}


public  boolean ApplyAction(int action) //Modifying load of Transaction #1: Access to home page (Changing Num of threads in the associated jmx)
{
    boolean success= false;
     
    //body of action
    String transactionName = transactionsMap.get(Integer.toString(action));
    qualityMeasuresTrans.clear();
    //qualityMeasuresTrans = new LinkedList<double[]>();
    try {
        success = ExecutingExtTestPlan(transactionName, action);
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
            
    return success;
}



public  Double CalculateReward()
{
	Double Reward_Part1=0.0;
    Double Reward_Part2=0.0;
    Double Beta=0.2;
	/*
    
    Double LowerBoundAcceptReg = VM.Requirement_ResTime - (VM.Acceptolerance * VM.Requirement_ResTime) ;
    Double UpperBoundAcceptReg = VM.Requirement_ResTime + (VM.Acceptolerance * VM.Requirement_ResTime);
    
    Double Reward_Part1=0.0;
    Double Reward_Part2;
    
    if (VM.ResponseTime <= VM.Requirement_ResTime)
     Reward_Part1=0.0;
    else if (VM.ResponseTime > VM.Requirement_ResTime)
      Reward_Part1 = (double) ((VM.ResponseTime-VM.Requirement_ResTime)/(UpperBoundAcceptReg-VM.Requirement_ResTime));
      
    Reward_Part2 = VM.VM_SensitivityValues[0]* VM.VM_CPUtil + VM.VM_SensitivityValues[1]* VM.VM_MemUtil + VM.VM_SensitivityValues[2]*VM.VM_DiskUtil;
    
    Double Reward =Math.round((Beta * Reward_Part1 + (1-Beta) * Reward_Part2)*100.0)/100.0;
    
    System.out.println("Reward:"+Reward);
    
    return Reward;
   
    */
    
    Reward_Part1= (this.qualityMeasures_curr[0]-this.qualityMeasures_pre[0])/this.qualityMeasures_curr[0];
    Reward_Part2 = (this.qualityMeasures_curr[3]-this.qualityMeasures_pre[3])/this.qualityMeasures_curr[3];
    Double Reward= Math.round((Beta * Reward_Part1 + (1-Beta) * Reward_Part2)*100.0)/100.0;
	return Reward;
}



private boolean ExecuteTransaction(StandardJMeterEngine jmeter, String transactionName,int numOfThreads,int rampUpTime,int numOfLoops){
	 
    Summariser summer = null;
    String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
    if (summariserName.length() > 0) {
       summer = new Summariser(summariserName);
    }
       
    String logFile = JMETER_LOG_FILE_PATH;
    ResultCollector logger = new ResultCollector(summer);
    logger.setFilename(logFile);

    HashTree testPlanTree;
    boolean testPassed = false;
    double [] qualityMeasure = new double[3];
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
            {   myResultCollector.calculateAverageQualityMeasures(qualityMeasure);
                qualityMeasuresTrans.add(qualityMeasure);
            }
            
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


// Executing test plans with a modified Num of threads for targeted transaction and unchanged Nums of threads for the rest

public boolean ExecutingExtTestPlan (String transaction, Integer key) throws Exception{
     
    // JMeter Engine
   StandardJMeterEngine jmeter = new StandardJMeterEngine();
   // Initialize Properties, logging, locale, etc.
   JMeterUtils.loadJMeterProperties(JMETER_PROPERTY_PATH);
   JMeterUtils.setJMeterHome(JMETER_HOME_PATH);
  // JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
   JMeterUtils.initLocale();
   // Initialize JMeter SaveService
   SaveService.loadProperties();
       
   //Running jmx files of other transactions (except the target transaction in the action)
   for (int i=0; i<=10; i++){ 
        if (i!= key) {

           String transactionName = transactionsMap.get(Integer.toString(i)); 
           int numOfThreads = transactionsWorkload[i];
           boolean success = false;
           while(success == false)
           {   int rampup =(int) Math.round((double)numOfThreads/3.00); 
        	   success = ExecuteTransaction(jmeter, transactionName, numOfThreads, rampup*1, 1);
             
           }
        }    
    }
   //Running the target jmx and changing the num of its thread
    int newWorkload = transactionsWorkload[key]+transactionsWorkload[key]/3;
    int rampup =(int) Math.round((double)newWorkload/3.00); 
    boolean success = false;
    success = ExecuteTransaction(jmeter, transaction, newWorkload, rampup*1, 1); 
    if(success == true)
        transactionsWorkload[key]=newWorkload;

    return success;
}


// Executing all transactions with the same Num of threads (Action Base)

public void ExecutingExtTestPlan () throws Exception{
     
    // JMeter Engine
   StandardJMeterEngine jmeter = new StandardJMeterEngine();
   // Initialize Properties, logging, locale, etc.
   JMeterUtils.loadJMeterProperties(JMETER_PROPERTY_PATH);
   JMeterUtils.setJMeterHome(JMETER_HOME_PATH);
  // JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
   JMeterUtils.initLocale();
   // Initialize JMeter SaveService
   SaveService.loadProperties();
  
    
  // Load existing .jmx Test Pln  
    for(int i=0; i<=10; i++)
        {
            String transactionName = transactionsMap.get(Integer.toString(i));
            boolean success = false;
            while(success == false)
                success = ExecuteTransaction(jmeter, transactionName, 3, 1, 1);  
            transactionsWorkload[i]= 3;   
        }  
}



/*
public void ExecutingExtTestPlan (String transaction, Integer key) throws Exception{
    
    // JMeter Engine
   StandardJMeterEngine jmeter = new StandardJMeterEngine();


   // Initialize Properties, logging, locale, etc.
   JMeterUtils.loadJMeterProperties("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1/bin/jmeter.properties");
  
   
   JMeterUtils.setJMeterHome("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1");
  // JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
   JMeterUtils.initLocale();

   // Initialize JMeter SaveService
   SaveService.loadProperties();
 
   String transactionName=null; 
 
   //Running jmx files of other transactions (except the target transaction in the action)
   for (int i=1; i<=11; i++){
	   
	if (i!= key) {
		
		   transactionName = transactionsMap.get(Integer.toString(i)); 
		   FileInputStream in = new FileInputStream("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/"+transactionName+".jmx"); 
		   
		   
		   Summariser summer = null;
		   String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		   if (summariserName.length() > 0) {
		       summer = new Summariser(summariserName);
		   }
		   
		   
		   String logFile = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/Log/Test.jtl";
		   ResultCollector logger = new ResultCollector(summer);
		   logger.setFilename(logFile);



		   HashTree testPlanTree;
		  
		   try{
		       testPlanTree = SaveService.loadTree(new File ("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/"+transactionName+".jmx"));
		     

		     //  testPlanTree.add(testPlanTree.getArray()[0], logger);
		      // System.out.println(testPlanTree.toString());
		      
		       SearchByClass<ThreadGroup> threadGroups = new SearchByClass<>(ThreadGroup.class);
		       testPlanTree.traverse(threadGroups);
		       Collection<ThreadGroup> threadGroupsRes = threadGroups.getSearchResults();
		       for (ThreadGroup threadGroup : threadGroupsRes) {
		                  
		           threadGroup.setNumThreads(transactionsWorkload[i-1]);
		           threadGroup.setRampUp(5);
		           ((LoopController)threadGroup.getSamplerController()).setLoops(1);
		           
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
		           myResultCollector.calculateAverageQualityMeasures(qualityMeasures_n);
		       }catch(Exception e){
		           System.out.println("jmeter run");
		           e.printStackTrace();}
		   }catch(Exception e){
		       System.out.println("testPlanTree");
		       e.printStackTrace();
		   }
		
		
		
		
	}
		
	   
   }
 
   
   //Running the target jmx and changing the num of its thread 
   //FileInputStream in = new FileInputStream("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/"+transaction+".jmx"); 
   
   
   Summariser summer = null;
   String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
   if (summariserName.length() > 0) {
       summer = new Summariser(summariserName);
   }
   
   
   String logFile = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/Log/Test.jtl";
   ResultCollector logger = new ResultCollector(summer);
   logger.setFilename(logFile);



   HashTree testPlanTree;
  
   try{
       testPlanTree = SaveService.loadTree(new File ("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan/"+transaction+".jmx"));
     

     //  testPlanTree.add(testPlanTree.getArray()[0], logger);
     //  System.out.println(testPlanTree.toString());
      
       SearchByClass<ThreadGroup> threadGroups = new SearchByClass<>(ThreadGroup.class);
       testPlanTree.traverse(threadGroups);
       Collection<ThreadGroup> threadGroupsRes = threadGroups.getSearchResults();
       for (ThreadGroup threadGroup : threadGroupsRes) {
           
           //int currentWorkload= Integer.parseInt(threadGroup.getProperty("ThreadGroup.num_threads").toString());
           int newWorkload= transactionsWorkload[key-1]+transactionsWorkload[key-1]/3;
           threadGroup.setNumThreads(newWorkload);
           threadGroup.setRampUp(5);
           ((LoopController)threadGroup.getSamplerController()).setLoops(1);
           transactionsWorkload[key-1]=newWorkload;
           
           System.out.println("Transaction: "+ transaction +", Workload (num of threads): "+threadGroup.getProperty("ThreadGroup.num_threads").toString()); 
           System.out.println("Ramp Up:"+threadGroup.getRampUp());
           System.out.println("Loop: "+ ((LoopController)threadGroup.getSamplerController()).getProperty("LoopController.loops"));
       }
       
       MyResultCollector myResultCollector = new MyResultCollector(summer);
       testPlanTree.add(testPlanTree.getArray()[0], myResultCollector);
       
      
       // Run JMeter Test
       jmeter.configure(testPlanTree);
       try{
           jmeter.run();
           myResultCollector.calculateAverageQualityMeasures(qualityMeasures_n);
       }catch(Exception e){
           System.out.println("jmeter run");
           e.printStackTrace();}
   }catch(Exception e){
       System.out.println("testPlanTree");
       e.printStackTrace();
   }
    
 
   
}


*/

}
