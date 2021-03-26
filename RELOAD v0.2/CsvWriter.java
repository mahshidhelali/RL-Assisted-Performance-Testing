package org.deeplearning4j.rl4j.examples.advanced.QLearning;

import java.io.*;


public class CsvWriter {


    private String fullLogFileName;
    private String logFileName;
    private String filePath;


    //q-learning
    CsvWriter(String filePath, String fileTitle,int maxResponseTimeThreshold, Double maxErrorRateThreshold, int episodeExecutionDelay,double learningRate, double discountFactor){
      this.filePath = filePath;
      this.fullLogFileName = createFile(filePath,fileTitle,"fullLog");
      String str1 = "maxResponseTimeThreshold,maxErrorRateThreshold,episodeExecutionDelay,learningRate,discountFactor\n"+ maxResponseTimeThreshold+","+maxErrorRateThreshold+","+episodeExecutionDelay+","+learningRate+","+discountFactor+"\n";
      String columns = "episode,episodeStep,StepReply,responseTime,errorRate,totalWorkload,agentState,epsilon,learningRate,HomePage,RegisterPage,RegisterUser,BrowsePage,BrowseInCategory,BrowseInRegions,SellPage,SellItem,AboutMePage,AboutMeUser,BidOnItem, SellItem, BuyItem\n";
      String str = str1+columns;
      writeString(str,fullLogFileName);

      this.logFileName = createFile(filePath,fileTitle,"log");
      columns = "episode,totalWorkload,episodeStep,responseTime,errorRate\n";
      str = str1+columns;
      writeString(str, logFileName);

    }

     CsvWriter(String filePath, String fileTitle,int maxResponseTimeThreshold, Double maxErrorRateThreshold, int episodeExecutionDelay){
      this.fullLogFileName = createFile(filePath,fileTitle,"fullLog");
      String str1 = "maxResponseTimeThreshold,maxErrorRateThreshold,episodeExecutionDelay\n"+ maxResponseTimeThreshold+","+maxErrorRateThreshold+","+episodeExecutionDelay+"\n";
      String columns = "episode,episodeStep,StepReply,responseTime,errorRate,totalWorkload,HomePage,RegisterPage,RegisterUser,BrowsePage,BrowseInCategory,BrowseInRegions,SellPage,SellItem,AboutMePage,AboutMeUser,BidOnItem, SellItem, BuyItem\n";
      String str = str1+columns;
      writeString(str, fullLogFileName);

      this.logFileName = createFile(filePath,fileTitle,"log");
      columns = "episode,totalWorkload,episodeStep,responseTime,errorRate\n";
      str = str1+columns;
      writeString(str, logFileName);
    }

    public void writeRowInFullLog(int episodeNumber, int episodeStep, String transactionName, SUTstate curr_SUT_state,String agentStateDescribtion,double epsilon, double learningRate){
      String str = episodeNumber+","+episodeStep+","+transactionName+","+curr_SUT_state.qualityMeasures.responseTime+","+curr_SUT_state.qualityMeasures.errorRate+","+curr_SUT_state.workload+","+agentStateDescribtion+","+epsilon+","+learningRate;
      String ts = "";
      for(Transaction t: curr_SUT_state.transactions){
          ts = ts+","+t.workLoad;
      }
      str = str+ts+"\n";

      writeString(str, fullLogFileName);

  }

    public void writeRowInFullLog(int iterationNumber, int iterationStep, String transactionName, SUTstate curr_SUT_state){
      String str = iterationNumber+","+iterationStep+","+transactionName+","+curr_SUT_state.qualityMeasures.responseTime+","+curr_SUT_state.qualityMeasures.errorRate+","+curr_SUT_state.workload;
      String ts = "";
      for(Transaction t: curr_SUT_state.transactions){
          ts = ts+","+t.workLoad;
      }
      str = str+ts+"\n";

      writeString(str, fullLogFileName);
  }

   public void writeRowInLog(int iterationNumber, int iterationStep, SUTstate curr_SUT_state){
      String str = iterationNumber+","+curr_SUT_state.workload+","+iterationStep+","+curr_SUT_state.qualityMeasures.responseTime+","+curr_SUT_state.qualityMeasures.errorRate+"\n";
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

    public static String createFile(String filePath, String fileTitle, String type){
        String logFileName = filePath+"/"+fileTitle+"_"+java.time.LocalDate.now()+"_"+type+".csv";
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

    public void saveQTable(stateAction[][] Qtable){
      String qTableFileName = createFile(this.filePath,"Q_Table","_");

      for (int x = 0; x < Qtable.length; x++) {
        String str = "";
        for (int y=0; y< Qtable[x].length ; y++){
          str = str+Qtable[x][y]+",";
        }
        str = str+"\n";
        writeString(str, qTableFileName);
      }

    }
}
