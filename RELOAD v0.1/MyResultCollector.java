import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

public class MyResultCollector extends ResultCollector {
     
	private long responseTime;
	private long latency;
	private int errorCount;
    private int numofSamples;
    private int numofSuccessSamples;
    //public double aveResponseTime;
    //public double avelatency;
    //public double errorRate;
	
	public MyResultCollector(Summariser summer) {
		// TODO Auto-generated constructor stub
		super (summer);
	}
	
	@Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult r = e.getResult();
        if (r.isSuccessful()) {
            responseTime = responseTime + r.getTime();
            latency = latency + r.getLatency();
            numofSuccessSamples = numofSuccessSamples + r.getSampleCount();
        }
        
        //if (r.getErrorCount()!= 0)
       // System.out.println("Error Count: " + r.getErrorCount()); 
        errorCount = errorCount + r.getErrorCount();
        numofSamples = numofSamples + r.getSampleCount();
    }
	
	public void calculateAverageQualityMeasures (double[] qualityMeasures) {
		qualityMeasures[0] = responseTime / Double.valueOf(numofSuccessSamples);
		qualityMeasures[1] = latency / Double.valueOf(numofSuccessSamples);
		qualityMeasures[2] = Double.valueOf(errorCount / Double.valueOf(numofSamples));
		System.out.println(" Ave.Respose Time (ms): " + Math.round(qualityMeasures[0])); 
		System.out.println(" Ave.latency (ms): " + Math.round(qualityMeasures[1])); 
		System.out.println(" Error Rate: " + (double) Math.round(qualityMeasures[2]*100)/100); 
		
	}
	
	public void calculateAverageQualityMeasures (QualityMeasures qualityMeasures) {
        double avgResponseTime = responseTime / Double.valueOf(numofSuccessSamples);
        double avgLatency = latency / Double.valueOf(numofSuccessSamples);
        double avgErrorRate = Double.valueOf(errorCount / Double.valueOf(numofSamples));
        System.out.println(" Ave.Respose Time (ms): " + Math.round(avgResponseTime));
        System.out.println(" Ave.latency (ms):" + Math.round(avgLatency));
        System.out.println("Error Rate:" + (double) Math.round(avgErrorRate*100)/100);
 
    qualityMeasures.update(avgResponseTime,avgLatency,avgErrorRate,0);
    }
	
	public boolean allTestSamplesPassed(){
	    if(numofSamples == 0)
	      return false;
	    else
	      return true;
	  }

}
