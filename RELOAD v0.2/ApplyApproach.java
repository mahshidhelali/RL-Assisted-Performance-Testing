package org.deeplearning4j.rl4j.examples.advanced.QLearning;


public class ApplyApproach{

	 public static void main(String[] args) throws Exception {

	        int maxResposeTimeThreshold = 1500;
	        double maxErrorRateThreshold = 0.2;
			int maxEpisodeNumber = 10;
	        String filePath = "/Users/IEUser/Desktop/QL_LOGS";
	        int episodeExecutionDelay = 5;


			double learningRate = 0.5;
	        double discountFactor = 0.5;
	        CsvWriter qlCsvWriter = new CsvWriter(filePath,"QLearning",maxResposeTimeThreshold, maxErrorRateThreshold, episodeExecutionDelay,learningRate, discountFactor);
	        QLearning QL = new QLearning(learningRate, discountFactor, maxErrorRateThreshold, maxResposeTimeThreshold, qlCsvWriter,episodeExecutionDelay);
	        QL.Learn(maxEpisodeNumber);

	        CsvWriter randomCsvWriter = new CsvWriter(filePath,"random",maxResposeTimeThreshold, maxErrorRateThreshold,episodeExecutionDelay);
	        RandomLoadRunner randomLoadRunner = new RandomLoadRunner( maxResposeTimeThreshold, maxErrorRateThreshold,randomCsvWriter,episodeExecutionDelay);
	        randomLoadRunner.execute(maxEpisodeNumber);

	        CsvWriter dummyCsvWriter = new CsvWriter(filePath,"dummy",maxResposeTimeThreshold, maxErrorRateThreshold,episodeExecutionDelay);
        	DummyLoadRunner dummyLoadRunner = new DummyLoadRunner( maxResposeTimeThreshold, maxErrorRateThreshold,dummyCsvWriter,episodeExecutionDelay);
       		dummyLoadRunner.execute(maxEpisodeNumber);



	    }
}
