package org.deeplearning4j.rl4j.examples.advanced.QLearning;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

public class MyResultCollector extends ResultCollector {

	private long responseTime ;
	private long latency;
	private int errorCount;
    private int numofSamples;


	public MyResultCollector(Summariser summer) {
        // TODO Auto-generated constructor stub
		super (summer);

        responseTime = 0;
        latency =0 ;
        errorCount = 0;
        numofSamples = 0;
    }

	@Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult r = e.getResult();
        if (r.isSuccessful()) {
            //System.out.println("Response time in milliseconds: " + r.getTime());
            responseTime = responseTime + r.getTime();
           //System.out.println("Latency in milliseconds: " + r.getLatency());
            //#latency = latency + r.getLatency();
           // System.out.println("Samples Count : " + r.getSampleCount());

        }

       // System.out.println("Error Count: " + r.getErrorCount());
        errorCount = errorCount + r.getErrorCount();
        numofSamples = numofSamples + r.getSampleCount();
    }

    //for seperate transitions
    /*
	public void calculateAverageQualityMeasures (Transaction t) {
		double avgResponseTime = responseTime / (double) numofSamples;
		//#double avgLatency = latency / Double.valueOf(numofSamples);
		double avgErrorRate = errorCount / (double) numofSamples;
		System.out.println("Average Respose Time in milliseconds: " + avgResponseTime);
		//#System.out.println("Average latency in milliseconds: " + avgLatency);
		System.out.println("Error Rate: " + avgErrorRate);

		//#t.qualityMeasures = new QualityMeasures(avgResponseTime,avgLatency,avgErrorRate,0);
        t.qualityMeasures = new QualityMeasures(avgResponseTime,avgErrorRate);
    }*/

    public void calculateAverageQualityMeasures (QualityMeasures QM) {
        double avgResponseTime = responseTime / (double) numofSamples;
        //#double avgLatency = latency / Double.valueOf(numofSamples);
        double avgErrorRate = errorCount / (double) numofSamples;
        System.out.println("Average Respose Time in milliseconds: " + avgResponseTime);
        //#System.out.println("Average latency in milliseconds: " + avgLatency);
        System.out.println("Error Rate: " + avgErrorRate);

        QM.update(avgResponseTime,avgErrorRate);
    }

  public boolean allTestSamplesPassed(){
    if(numofSamples == 0)
      return false;
    else
      return true;
  }


}
