package org.deeplearning4j.rl4j.examples.advanced.DQN1;

import java.io.*;
import java.time.LocalTime;


public class CsvWriter {


    private String fullLogFileName;
    private String logFileName;

  public void writeRowInFullLog(int episodeNumber, int episodeStep, String transactionName, SUTstate curr_SUT_state, LocalTime startTime, LocalTime endTime){
      String str = episodeNumber+","+episodeStep+","+transactionName+","+curr_SUT_state.qualityMeasures.responseTime+","+curr_SUT_state.qualityMeasures.errorRate+","+curr_SUT_state.workload;
      String ts = "";
      for(Transaction t: curr_SUT_state.transactions){
          ts = ts+","+t.workLoad;
      }
      str = str+ts+","+startTime.toString()+","+endTime.toString()+"\n";

      writeString(str, fullLogFileName);
  }

    public void writeRowInLog(int episodeNumber, int episodeStep, SUTstate curr_SUT_state){
        String str = episodeNumber+","+curr_SUT_state.workload+","+episodeStep+","+curr_SUT_state.qualityMeasures.responseTime+","+curr_SUT_state.qualityMeasures.errorRate+"\n";
        writeString(str, logFileName);
    }

    CsvWriter(DqnProperties dqnProperties, int maxResponseTimeThreshold, Double maxErrorRateThreshold, int episodeExecutionDelay){
      this.fullLogFileName = createFile("fullLog");
      String str1 = dqnProperties.toCsvString();
      String str2 = "maxResponseTimeThreshold,maxErrorRateThreshold,episodeExecutionDelay\n"+ maxResponseTimeThreshold+","+maxErrorRateThreshold+","+episodeExecutionDelay+"\n";
      String columns = "episode,episodeStep,StepReply,responseTime,errorRate,totalWorkload,HomePage,RegisterPage,RegisterUser,BrowsePage,BrowseInCategory,BrowseInRegions,SellPage,SellItem,AboutMePage,AboutMeUser,BidOnItem,BuyItem, startTime,endTime\n";
//        String columns = "episode,episodeStep,StepReply,responseTime,errorRate,totalWorkload,HomePage, Logn, AddCart, Fish, Dogs, Reptiles, Cats, Birds,endTime\n";
      String str = str1+str2+columns;
      writeString(str, fullLogFileName);

    this.logFileName = createFile("log");
    columns = "episode,totalWorkload,episodeStep,responseTime,errorRate\n";
    str = str1+str2+columns;
    writeString(str, logFileName);
    }


    private void writeString(String str, String fileName){
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(fileName),true);
            os.write(str.getBytes(), 0, str.length());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String createFile(String type){
        String logFileName = "/Users/IEUser/Desktop/DQN LOGS/DQN_"+java.time.LocalDate.now()+"_"+type+".csv";
        try {
            File myObj = new File(logFileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the log file.");
            e.printStackTrace();
        }
        return logFileName;
    }

    public void writeRowInFullLog(int iterationNumber, int iterationStep, String transactionName, SUTstate curr_sut_state) {
    }
}
