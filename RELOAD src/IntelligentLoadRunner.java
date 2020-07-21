
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.SearchByClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.concurrent.TimeUnit;





public class IntelligentLoadRunner {
	 
	
	    public static void main(String[] args) throws Exception {
	        // TODO code application logic here
	        
	        
	    	//double Cap_ResTime=3000.00; //ms
	        double Requirement_ResTimes=500;
	        double errorRateThreshold=0.5;
	        double learningRate=0.1;
	        double discountFactor=0.5;
	        
	        
	        LinkedList<double[]> toPrint = new LinkedList<double[]>();
	        float epsilon =(float) 0.99; 
	        ReinforcementLearning RL = new ReinforcementLearning();
	        RL.Initializing(learningRate, discountFactor, Requirement_ResTimes, errorRateThreshold);
	      
	        
	    	for (int j=0; j<10; j++) {
	    	
	        epsilon = (float) 0.99;
	        
	        /*
	        if (j==0) 
    			epsilon =(float) 0.85; 
    	   else 
    	     {  
    		   
    	        epsilon = (float) Math.round((1.00/(j+1))*100)/100;
    	     }
	     */
	    
	    	//ReinforcementLearning RL = new ReinforcementLearning();
	    	System.out.println("Response time requirement: "+ Requirement_ResTimes + "  ErrorRate Threshold: "+ errorRateThreshold);
	    	System.out.println("epsilon: "+ epsilon);

	    	
	    	
	    	//RL.Initializing(learningRate, discountFactor, Requirement_ResTimes, errorRateThreshold);
	    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~ Applying Action Base ~~~~~~~~~~~~~~~~~~~~~~");
	        RL.ApplyAction_base();
	        System.out.println("~~~~~~~~~~~~~~~~~~~~~~ End of Action Base ~~~~~~~~~~~~~~~~~~~~~~");
	    	
	    	
	    	int IndexofCurrentState = 0;
	    	IndexofCurrentState = RL.DetectState();
	    	double QM_Pre_responseTime;
	    	double QM_Pre_errorRate;
	    	double QM_Pre_latency;
	    	double QM_Pre_maxErrorRate;
	    	QM_Pre_responseTime = RL.qualityMeasures_curr[0];
	    	QM_Pre_latency = RL.qualityMeasures_curr[1];
	    	QM_Pre_errorRate= RL.qualityMeasures_curr[2];
	    	QM_Pre_maxErrorRate =RL.qualityMeasures_curr[3];
	    	RL.qualityMeasures_pre[0]=QM_Pre_responseTime;
	    	RL.qualityMeasures_pre[1]=QM_Pre_latency;
	    	RL.qualityMeasures_pre[2]=QM_Pre_errorRate;
	    	RL.qualityMeasures_pre[3]=QM_Pre_maxErrorRate;
	    
	    	 
	    	 double numoflearningTrial=0;
	    	 double appliednumofThreads =0;
	    	 LinkedList<double[]> Measures_episode = new LinkedList <double[]> ();
	    	 int numTemp;
	    
	    	 while ((RL.qualityMeasures_curr[3]< 0.4) && (RL.qualityMeasures_curr[0]<= 2000))
	    		 
	    		{
	    		 
	    		 /*
	    		 if (numoflearningTrial==0) 
		    			epsilon =(float) 0.85; 
		    	else 
		    	     {  
		    	    	float reducedepsilon=0;
		    	     	reducedepsilon = ((float) Math.round((epsilon-0.06)*100.0)/100);
		    	    	 if (epsilon < 0.2)
		    	    		 epsilon =(float)0.2;
		    	    	 else
		    	    		 epsilon = reducedepsilon;
		    	     }
		    	     
		    	 */    
		     	           
		    	System.out.println("epsilon= "+ epsilon);
	            IndexofCurrentState= RL.Learn(IndexofCurrentState, epsilon);
	            numTemp=0;
	            for (int num: RL.transactionsWorkload)
	            	numTemp= numTemp+ num;
	            
	            appliednumofThreads = numTemp;
	            numoflearningTrial++;
	            double[] parameters = new double [5];
	            parameters[0]= RL.qualityMeasures_curr[0];
	            parameters[1]= RL.qualityMeasures_curr[1];
	            parameters[2]= RL.qualityMeasures_curr[2];
	            parameters[3]= RL.qualityMeasures_curr[3];
	            parameters[4]= appliednumofThreads;
	            Measures_episode.add(parameters);
	            
	       }
	    	 double[] TWnumTrial = new double[(2+Measures_episode.size())];
	    	 double totalWorkload=33;
	    	 for(int i=0; i<Measures_episode.size(); i++)
	    	 { 
	    		 TWnumTrial[i+2]= Measures_episode.get(i)[4];
	    		 System.out.println("Episode "+ j+ " Workload: " + TWnumTrial[i+2]);
	    		 totalWorkload =totalWorkload + Measures_episode.get(i)[4];
	    	 }
	    	 
	    	 TWnumTrial[0]= numoflearningTrial;
	    	 TWnumTrial[1]= totalWorkload;
	    	 
	    	 System.out.println("*****************************************");
	    	 System.out.println("Episode "+ j+ ": Total Workload: " + totalWorkload);
	    	 System.out.println("Episode "+ j+ ": number of Trials: " + numoflearningTrial);
	    	 
	    	
	    	 toPrint.add(TWnumTrial);
	    	 
	    	 TimeUnit.MINUTES.sleep(30);
	    	 
	    	} 
	    	 
	    	
	    	 WriteToExcel(toPrint,1, String.valueOf(epsilon));
	    	 
	    
	   /*
	   
	    	 //Executing Dummy Load Testing
	    
	    	 LinkedList<double[]> toPrint_1 = new LinkedList<double[]>();
	    	 
	    	 for (int j=0; j<10; j++) { 
	    	DummyLoadRunner DL = new DummyLoadRunner(Requirement_ResTimes, errorRateThreshold);

	     	DL.ApplyWorkLoad();
	     	int numofIncStep = 0;
	     	LinkedList<double[]> Measuresdummy_episode = new LinkedList <double[]> ();
	     	while(DL.QM_curr.maxErrorRate< 0.4) {	
	 	    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~Step: "+(numofIncStep)+" ~~~~~~~~~~~~~~~~~~~~~~");
	 	    	DL.IncreaseWorkload();
	 	    	DL.ApplyWorkLoad();
	 	    	numofIncStep++;
	 	    	double[] parameters_Dummy = new double [5];
	 	    	//DL.QM_curr.update(DL.QM_transactions);
	            parameters_Dummy[0]= Math.round(DL.QM_curr.responseTime);
	            parameters_Dummy[1]= Math.round(DL.QM_curr.latency);
	            parameters_Dummy[2]= Math.round(DL.QM_curr.errorRate*100)/100.00;
	            parameters_Dummy[3]= Math.round(DL.QM_curr.maxErrorRate*100)/100.00;
	            parameters_Dummy[4]= DL.GetTotalNumOfThreads();
	            Measuresdummy_episode.add(parameters_Dummy);
	 	    	
	 	    }
	     	
	     	double[] TWnumTrial = new double[(2+Measuresdummy_episode.size())];
	     	double totalDummyWorkload=33;
	     	for(int i=0; i<Measuresdummy_episode.size(); i++){
	     		TWnumTrial[i+2]= Measuresdummy_episode.get(i)[4];
	     		System.out.println("Episode "+ j+ ": Workload: " + TWnumTrial[i+2]);
	     		totalDummyWorkload = totalDummyWorkload + Measuresdummy_episode.get(i)[4];
	     		
	     		}
	     	
	     	
	     	TWnumTrial[0]= numofIncStep;
	    	TWnumTrial[1]= totalDummyWorkload;
	    	 
	     	System.out.println("*****************************************");
	    	System.out.println("Episode "+ j+ ": Total Workload in dummy: " + totalDummyWorkload);
	    	System.out.println("Episode "+ j+ ": Num of Inc steps in dummy: " + numofIncStep);
	    	
	    	
	    	 
	    	 toPrint_1.add(TWnumTrial);
	    	 
	    	 
	    	 }
	     	
	 	   WriteToExcel(toPrint_1, 0,  "Dummy");
	     
	*/
	    	 
	    }
	    
	    
	    
	    public static void WriteToExcel(LinkedList toPrint, int typeofTesting, String epsilon ){
	        
	        
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet;
            
            if (typeofTesting==1)
            	 sheet = workbook.createSheet(String.valueOf(epsilon));
            else 
            	 sheet = workbook.createSheet("Dummy");    
            
            
            int rowNum=0;
            
            for (int i=0; i<toPrint.size(); i++) {
            Row row = sheet.createRow(++rowNum);
             
            int columnCount = 0;
            
            Cell cell = row.createCell(columnCount);
            cell.setCellValue((int)(i+1)); 
            
            for (int j=0; j<((double[])toPrint.get(i)).length; j++) {
            
            columnCount++;
            Cell cell0 = row.createCell(columnCount);
            cell0.setCellValue((double)((double[])toPrint.get(i))[j]); 
            /*
            columnCount++;
            Cell cell_1 = row.createCell(columnCount);
            cell_1.setCellValue((double)((double[])toPrint.get(i))[1]); 
       
            columnCount++;
            Cell cell_2 = row.createCell(columnCount);
            cell_2.setCellValue((double)((double[])toPrint.get(i))[2]); 
            columnCount++;
            Cell cell_3 = row.createCell(columnCount);
            cell_3.setCellValue((double)((double[])toPrint.get(i))[3]); 
            columnCount++;
            Cell cell_4 = row.createCell(columnCount);
            cell_4.setCellValue((double)((double[])toPrint.get(i))[4]);
            */ 
            }
            
            }
            
            try {
            FileOutputStream outputStream;
            if (typeofTesting==1)
            	outputStream= new FileOutputStream("New Structure- Random- epsilon "+String.valueOf(epsilon)+".xlsx");
            else 
            	outputStream= new FileOutputStream("Dummy Load Testing-corresponding to"+String.valueOf(epsilon)+".xlsx");
            
            workbook.write(outputStream);
            outputStream.close();
            System.out.println("wrote in file");
             } catch (FileNotFoundException e) {
            e.printStackTrace();
             } catch (IOException e) {
            e.printStackTrace();
             }

        } 
        
/*	    
	    
 public static void WriteToExcel(double[] ToPrint, int typeofTesting, String epsilon ){
	        
	        
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet;
            
            if (typeofTesting==1)
            	 sheet = workbook.createSheet(String.valueOf(epsilon));
            else 
            	 sheet = workbook.createSheet("Dummy");    
            
            
            int rowNum=0;
            
            for (int i=0; i<ToPrint.length; i++) {
            Row row = sheet.createRow(++rowNum);
             
            int columnCount = 0;
            
            Cell cell = row.createCell(columnCount);
            cell.setCellValue((int)(i+1)); 
            columnCount++;
            Cell cell0 = row.createCell(columnCount);
            cell0.setCellValue((double)ToPrint[i]); 
            
            
            }
            
            try {
            FileOutputStream outputStream;
            if (typeofTesting==1)
            	outputStream= new FileOutputStream("Type 2 Result- RL-Based Load Testing decaying from "+String.valueOf(epsilon)+".xlsx");
            else 
            	outputStream= new FileOutputStream("Dummy Load Testing"+".xlsx");
            
            workbook.write(outputStream);
            outputStream.close();
            System.out.println("wrote in file");
             } catch (FileNotFoundException e) {
            e.printStackTrace();
             } catch (IOException e) {
            e.printStackTrace();
             }

        } 
	    
	*/    
	    
	    
	    
	    public static void CreatingTestPlan () throws Exception{
	    	
	    	String slash = System.getProperty("file.separator");
	        
	        // Engine
	        StandardJMeterEngine jm = new StandardJMeterEngine();
	       
	        
	        // jmeter.properties
	        JMeterUtils.setJMeterHome("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1");
	        JMeterUtils.loadJMeterProperties("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1/bin/jmeter.properties");
	        //JMeterUtils.loadJMeterProperties("/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/apache-jmeter-5.1.1/bin/user.properties");
	        JMeterUtils.initLocale();
	        
	        //JMeter TestPlan, HashTree
	        HashTree hashTree = new HashTree();     

	        //First HTTP Sampler- WWW.google.com
	        HTTPSamplerProxy googleSampler = new HTTPSamplerProxy();
	        googleSampler.setDomain("WWW.google.com");
	        googleSampler.setPort(80);
	        googleSampler.setPath("/");
	        googleSampler.setMethod("GET");
	        googleSampler.setName("Open google.com");
	        googleSampler.setProperty(TestElement.TEST_CLASS, HTTPSampler.class.getName());
	        googleSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
	        
	        
	     // Second HTTP Sampler - open blazemeter.com
	       
            HTTPSamplerProxy blazemetercomSampler = new HTTPSamplerProxy();
            blazemetercomSampler.setDomain("www.blazemeter.com/");
            blazemetercomSampler.setPort(80);
            blazemetercomSampler.setPath("/");
            blazemetercomSampler.setMethod("GET");
            blazemetercomSampler.setName("Open blazemeter.com");
            blazemetercomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
            blazemetercomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
            
            
            // Third HTTP Sampler - .
            HTTPSamplerProxy SUTSampler = new HTTPSamplerProxy();
            SUTSampler.setDomain("www.southernaccountants.com/rlsut/");
           // SUTSampler.setPath("rlsut/");
            SUTSampler.setPort(80);
            SUTSampler.setPath("/");
            SUTSampler.setMethod("GET");
            SUTSampler.setName("Open blazemeter.com");
            SUTSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
            SUTSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
	        
	        // Loop Controller
	        LoopController loopCtrl = new LoopController();
	        ((LoopController)loopCtrl).setLoops(1);
	        //((LoopController)loopCtrl).addTestElement(httpSampler);
	        ((LoopController)loopCtrl).setFirst(true);
	        loopCtrl.initialize();
	        loopCtrl.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
	        loopCtrl.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());

	        // Thread Group
	        ThreadGroup threadGroup = new ThreadGroup();
	        threadGroup.setNumThreads(3);
	        threadGroup.setRampUp(1);
	        threadGroup.setSamplerController((LoopController)loopCtrl);
	        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
	        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

	        // Test plan
	        TestPlan testPlan = new TestPlan("MY TEST PLAN");
	        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
	        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
	        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
	        
	      
	        hashTree.add(testPlan);
            HashTree threadGroupHashTree = hashTree.add(testPlan, threadGroup);
            threadGroupHashTree.add(blazemetercomSampler);
            threadGroupHashTree.add(googleSampler);
            threadGroupHashTree.add(SUTSampler);
	        // hashTree.add("testPlan", testPlan);
	        //hashTree.add("loopCtrl", loopCtrl);
	        //hashTree.add("threadGroup", threadGroup);
	        //hashTree.add("httpSampler", httpSampler);       
	        
	        // save generated test plan to JMeter's .jmx file format
	        String jmxFile= "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/TestPlan" + slash + "example1.jmx";
	        try{
	        	
	            SaveService.saveTree(hashTree, new FileOutputStream(jmxFile));
	        }catch(Exception e){
	            System.out.println("problem with FileOutputStream _____________");
	            e.printStackTrace();
	        }	        
	        //add Summarizer output to get test progress in stdout like:
	                    // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
	        Summariser summer = null;
	        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary1");
	        if (summariserName.length() > 0) {
	            summer = new Summariser(summariserName);
	        }
	        
	         // Store execution results into a .jtl file
	        
	        String logFile = "/Users/mahshidhelalimoghadam/eclipse-workspace/IntelligentLoadRunner/Log" + slash + "example1.jtl";
	        ResultCollector logger = new ResultCollector(summer);
	        logger.setFilename(logFile);
	        hashTree.add(hashTree.getArray()[0], logger);
	        
	        // Run Test Plan
	        try{
	        	jm.configure(hashTree);
		        jm.run();
            }catch(Exception e){
                System.out.println("Exception in jmeter run");
                e.printStackTrace();}
	        
	        
	        System.out.println("Test completed. See " + logFile+ "for results");
	        System.out.println("JMeter .jmx script is available at " + jmxFile);
	        System.exit(0);
	
	    	
	    }
	    

}
