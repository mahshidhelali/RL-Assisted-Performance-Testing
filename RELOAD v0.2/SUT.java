package org.deeplearning4j.rl4j.examples.advanced.QLearning;

public class SUT {

    private double threadPerSecond;
    private int initialWorkLoadPerTransaction;
    private double workLoadIncreasingStepRatio;
    public LoadTester loadTester;
    public Transaction[] transactions;
    public  QualityMeasures qualityMeasures;


    public SUT() {

        Initialize();

        /*
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~ Applying Action Base ~~~~~~~~~~~~~~~~~~~~~~");
        applyAction_base();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~ End of Action Base ~~~~~~~~~~~~~~~~~~~~~~");
        */
    }

    public void Initialize() {

        this.threadPerSecond = 10.00;
        this.initialWorkLoadPerTransaction = 3;
       this.workLoadIncreasingStepRatio = 4.0 / 3.0;
        //this.workLoadIncreasingStepRatio = 2.0;

        loadTester = new LoadTester();
        transactions = new Transaction[13];
        qualityMeasures = new QualityMeasures();


        transactions[0] = new Transaction("HomePage", initialWorkLoadPerTransaction);
        transactions[1] = new Transaction("RegisterPage", initialWorkLoadPerTransaction);
        transactions[2] = new Transaction("RegisterUser", initialWorkLoadPerTransaction);
        transactions[3] = new Transaction("BrowsePage", initialWorkLoadPerTransaction);
        transactions[4] = new Transaction("BrowseInCategory", initialWorkLoadPerTransaction);
        transactions[5] = new Transaction("BrowseInRegion", initialWorkLoadPerTransaction);
        transactions[6] = new Transaction("SellPage", initialWorkLoadPerTransaction);
        transactions[7] = new Transaction("SellItem", initialWorkLoadPerTransaction);
        transactions[8] = new Transaction("AboutMePage", initialWorkLoadPerTransaction);
        transactions[9] = new Transaction("AboutMeUser", initialWorkLoadPerTransaction);
        transactions[10] = new Transaction("BidOnItem", initialWorkLoadPerTransaction);
        transactions[11] = new Transaction("SellItem", initialWorkLoadPerTransaction);
        transactions[12] = new Transaction("BuyItem", initialWorkLoadPerTransaction);


    }


    public void applyAction_base() // executing the test
    {
        boolean success = false;
        //body of action
        while (!success) {
            try {
                success = executeTestPlan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


public void applyAction() //Modifying load of all Transactions then executing the test
    {
        boolean success = false;

        for(Transaction t : transactions)
        {
            int prevWorkLoad = t.workLoad;
            t.workLoad = (int) (prevWorkLoad * workLoadIncreasingStepRatio);
        }

        //body of action
        while (!success) {
            try {
                success = executeTestPlan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void applyAction(int action) //Modifying load of a chosen Transaction then executing the test
    {
        boolean success = false;


        int prevWorkLoad = transactions[action].workLoad;
        transactions[action].workLoad = (int) (prevWorkLoad * workLoadIncreasingStepRatio);


        //body of action
        while (!success) {
            try {
                success = executeTestPlan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean executeTestPlan() throws Exception {

        boolean success = false;
        int rampUpTime = (int) Math.round((double)  GetTotalWorkLoad()  / threadPerSecond);
        success = loadTester.ExecuteAllTransactions(transactions, rampUpTime,1,qualityMeasures);
        return success;
    }

    //for seperate transitions
    /*public boolean executeTestPlan() throws Exception {

        boolean success = false;
        for (Transaction t : transactions) {
            int rampup = (int) Math.round((double) t.workLoad / threadPerSecond);
            success = loadTester.ExecuteTransaction(t, rampup, 1);
            if (!success) {
                break;
            }
        }
        return success;
    }
*/

    public int GetTotalWorkLoad() {
        int totalWorkLoad = 0;
        for (Transaction t : transactions) totalWorkLoad += t.workLoad;
        return totalWorkLoad;
    }

    public int GetInitialTotalWorkLoad() {
        return initialWorkLoadPerTransaction * transactions.length;
    }

    //for seperate transactions
    /*
    public SUTstate getSUTState() {

        double sum_responseTime = 0;
        //double sum_latency = 0;
        double sum_errorRate = 0;
        //double maxErrorRate = 0;

        //computing the average
        for (Transaction t : transactions) {
            sum_responseTime += t.qualityMeasures.responseTime;
            //sum_latency += t.qualityMeasures.latency;
            sum_errorRate += t.qualityMeasures.errorRate;
            //if (t.qualityMeasures.errorRate > maxErrorRate)
              //  maxErrorRate = t.qualityMeasures.errorRate;
        }
        double avg_responseTime = sum_responseTime / transactions.length;
        //double avg_latency = sum_latency / transactions.length;
        double avg_errorRate = sum_errorRate / transactions.length;

        //QualityMeasures QM = new QualityMeasures(avg_responseTime, avg_latency, avg_errorRate, maxErrorRate);
        QualityMeasures QM = new QualityMeasures(avg_responseTime, avg_errorRate);


        int totalWorkLoad = 0;
        for (Transaction t : transactions) totalWorkLoad += t.workLoad;


        return new SUTstate(QM, totalWorkLoad);
    }*/

    public SUTstate getSUTState() {
        return new SUTstate(qualityMeasures, GetTotalWorkLoad(),transactions);
    }

}


