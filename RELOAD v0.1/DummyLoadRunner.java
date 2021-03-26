import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DummyLoadRunner {
	 

    public QualityMeasures QM_curr = new QualityMeasures();
    public QualityMeasures QM_prev = new QualityMeasures();
    public QualityMeasures [] QM_transactions = new QualityMeasures[11]; 
    public double[] qualityMeasures_curr = new double [4];
    public double[] qualityMeasures_pre = new double [4];
    public LinkedList<double[]> qualityMeasuresTrans = new LinkedList<double []>();
    
    public LoadTester loadTester = new LoadTester();

    public int[] transactionsWorkload = new int [11];
    private double requirement_ResTimes;
    private double errorRateThreshold;
    Map<Integer, String> transactionsMap = new HashMap<Integer, String>();

    /* 
    public static void main(String[] args) throws Exception {

    	double requirement_ResTimes = 500;
	    double errorRateThreshold = 50.0;

    	DummyLoadRunner DL = new DummyLoadRunner(requirement_ResTimes, errorRateThreshold);

    	DL.ApplyWorkLoad();
    	int i = 2;
    	while(DL.QM_curr.errorRate <0.5) {	
	    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~Step: "+i+" ~~~~~~~~~~~~~~~~~~~~~~");
	    	DL.IncreaseWorkload();
	    	DL.ApplyWorkLoad();
	    	i++;
	    }
	    System.out.println("TotalNumOfThreads: "+DL.GetTotalNumOfThreads());
	
    }
*/

    public DummyLoadRunner(double requirement_ResTimes, double errorRateThreshold){
    	Initialize(requirement_ResTimes, errorRateThreshold);
    }

    public  void Initialize(double requirement_ResTimes, double errorRateThreshold) {

        this.requirement_ResTimes = requirement_ResTimes;
        this.errorRateThreshold = errorRateThreshold;

        transactionsMap.put(0, "home");
        transactionsMap.put(1,"register_page");
        transactionsMap.put(2,"register");
        transactionsMap.put(3, "login_page");
        transactionsMap.put(4,"login");
        transactionsMap.put(5, "search_page");
        transactionsMap.put(6, "select_product");
        transactionsMap.put(7, "add_to_card");
        transactionsMap.put(8, "payment");
        transactionsMap.put(9, "confirm");
        transactionsMap.put(10,"log_out");

        for(int i=0; i<QM_transactions.length; i++){
            QM_transactions[i] = new QualityMeasures();
        }

        for(int i=0; i<transactionsWorkload.length; i++){
        	transactionsWorkload[i] = 3;
        } 
    }

    public int GetTotalNumOfThreads(){
	    int numOfThreads=0;
	    for(int i=0; i<transactionsWorkload.length; i++)
	        numOfThreads +=  transactionsWorkload[i];
	    return numOfThreads;
    }

    public boolean ExecuteTestPlan () {

        boolean success = false;
        for (int i=0; i<=10; i++){  
            String transactionName = transactionsMap.get(i);  
            int numOfThreads = transactionsWorkload[i];
            int rampup =(int) Math.round((double)numOfThreads/3.00); 
            success = loadTester.ExecuteTransaction(transactionName, numOfThreads, rampup*1, 1, QM_transactions[i]);
            if(success == false) {
                break;
            }
        } 

        return success;
    }

    public void ApplyWorkLoad(){

    	boolean success = false;
    	while(success == false){
	    	success = ExecuteTestPlan();
	    	if(success == true){
	    		QM_curr.update(QM_transactions);
	    	}
	    }

    }

    public void IncreaseWorkload(){
    	for(int i=0; i<transactionsWorkload.length; i++){
        	transactionsWorkload[i] = transactionsWorkload[i]+transactionsWorkload[i]/3;
        }
    }

}