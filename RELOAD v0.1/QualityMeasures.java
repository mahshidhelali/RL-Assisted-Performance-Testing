public class QualityMeasures{

	public double responseTime;
    public double errorRate;
    public double latency;
    public double maxErrorRate;

    public QualityMeasures(double responseTime, double latency, double errorRate, double maxErrorRate){
    	this.responseTime = responseTime;
    	this.latency = latency;
    	this.errorRate = errorRate;
    	this.maxErrorRate = maxErrorRate;
    }

    public QualityMeasures(double responseTime, double latency, double errorRate){
    	this.responseTime = responseTime;
    	this.latency = latency;
    	this.errorRate = errorRate;
    	this.maxErrorRate = -1;
    }

    public QualityMeasures(){
    	responseTime = 0;
    	latency = 0;
    	errorRate = 0;
    	maxErrorRate = 0;
    }

    public void update(QualityMeasures[] qms){
    	
    	double sum_responseTime = 0;
    	double sum_latency = 0;
    	double sum_errorRate = 0;
    	double maxErrorRate = 0;

    	//computing the average
    	for(int i=0; i<qms.length; i++){
    		sum_responseTime += qms[i].responseTime;
    		sum_latency += qms[i].latency;
    		sum_errorRate += qms[i].errorRate;
    		if (qms[i].errorRate > maxErrorRate)
    			maxErrorRate = qms[i].errorRate;
    		
    	}
    	this.responseTime = sum_responseTime/qms.length;
    	this.latency = sum_latency/qms.length;
    	this.errorRate = sum_errorRate/qms.length;
    	this.maxErrorRate = maxErrorRate;
    	
    }

    public void update(QualityMeasures qm){
    	responseTime = qm.responseTime;
    	latency = qm.latency;
    	errorRate = qm.errorRate;
    	maxErrorRate = qm.maxErrorRate;
    }

    public void update(double rt, double lt, double er, double mer){
    	responseTime = rt;
    	latency = lt;
    	errorRate = er;
    	maxErrorRate = mer;
    }

    @Override
    public String toString() { 
    	String rt = "responseTime: "+responseTime;
    	String lt = "latency: "+latency;
    	String er = "errorRate: "+errorRate;
    	String mer = "maxErrorRate: "+maxErrorRate;
        return rt+"\n"+lt+"\n"+er+"\n"+mer; 
    } 

}