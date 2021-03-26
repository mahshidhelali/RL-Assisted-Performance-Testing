/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package performancetest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 *
 * @author mahshidhelalimoghadam
 */
public class ReinforcementLearning {
    
    private  stateAction[][] Qtable = new stateAction[24][];
    private  double alpha = 0.1;
    private  double gamma = 0.9; 
    
    
     
    public  void InitializingstateActions() {
    for (int x = 0; x < Qtable.length; x++) {
                Qtable[x] = new stateAction[13];
                for (int y=0; y< Qtable[x].length ; y++)
                {  Qtable[x][y]= new stateAction();
//                   if (x==23)
//                       if ((y==0)||(y==1)||(y==2)||(y==3)||(y==4)||(7==5)||(y==6)||(y==7)||(y==8)||(y==9))
//                       Qtable[x][y].Q_value=-1;
                   
                   //||(7==10)||(y==11)||(y==12)
                   
                }
                
            }
    }
    
    public  int Learn (int IndexofCurrentState, VirtualMachine VM, float epsilon)
    {
        
        
       //Select An Action
        int action=0;
        boolean Success= false;
        while (Success== false){
        action = chooseAnAction(IndexofCurrentState, epsilon);
         
         //Applying The Selected Action
                if (action == 0) {
                 Success = true;
                 System.out.println("Action 0 (nothing) ");
                } 
                else if (action == 1) {
                 Success = ApplyAction1(VM); 
                 if (Success== true )System.out.println("Action 1 ");
                } 
                else if (action == 2) {
                 Success = ApplyAction2(VM);
                 if (Success== true )System.out.println("Action 2 ");
                } 
                else if (action == 3) {
                 Success =  ApplyAction3(VM);
                 if (Success== true )System.out.println("Action 3 ");
                }
                else if (action == 4) {
                 Success =  ApplyAction4(VM);
                 if (Success== true )System.out.println("Action 4 ");
                }
                else if (action == 5) {
                 Success =  ApplyAction5(VM);
                 if (Success== true ) System.out.println("Action 5 ");
                }
                else if (action==6) {
                    Success= ApplyAction6(VM);
                    if (Success== true ) System.out.println("Action 6 ");
                } 
                else if (action == 7) {
                 Success = ApplyAction7(VM);
                 if (Success== true )System.out.println("Action 7 ");
                } 
                else if (action == 8) {
                 Success = ApplyAction8(VM);
                 if (Success== true )System.out.println("Action 8 ");
                } 
                else if (action == 9) {
                 Success = ApplyAction9(VM);
                 if (Success== true )System.out.println("Action 9 ");
                } 
                else if (action == 10) {
                 Success = ApplyAction10(VM);
                 if (Success== true )System.out.println("Action 10 ");
                } 
                 else if (action == 11) {
                 Success = ApplyAction11(VM);
                 if (Success== true )System.out.println("Action 11 ");
                 }
                 else if (action == 12) {
                 Success = ApplyAction12(VM);
                 if (Success== true )System.out.println("Action 12 ");
                 }
                
        }
        
        // Detection of new state
        
        List DetectedStates;
        int IndexofDetectedNewState = 0;
        DetectedStates = this.DetectState(VM);
        
        // Extracting the Index of state with Max Membership degree
        
        List FinalDetectedState = new LinkedList();
        Double MaxMemdegree=0.0;
        String[] pair = new String [2];
        
        for (Object StateMember: DetectedStates )
        {
           
           
           Double Degree=  Double.valueOf(((String[])StateMember)[1]); 
               if (Degree > MaxMemdegree )
                  { pair[0]=((String[])StateMember)[0];
                    pair[1]=((String[])StateMember)[1];
                    MaxMemdegree= Degree;
                  }
        }
        FinalDetectedState.add(pair);
        
        if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLL"))
            IndexofDetectedNewState =0 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLA"))
            IndexofDetectedNewState =1 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLH"))
            IndexofDetectedNewState =2 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHL"))
            IndexofDetectedNewState =3 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHA"))
            IndexofDetectedNewState =4 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHH"))
            IndexofDetectedNewState =5 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLL"))
            IndexofDetectedNewState =6 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLA"))
            IndexofDetectedNewState =7 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLH"))
            IndexofDetectedNewState =8 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHL"))
            IndexofDetectedNewState =9;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHA"))
            IndexofDetectedNewState =10 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHH"))
            IndexofDetectedNewState =11 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLL"))
            IndexofDetectedNewState =12 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLA"))
            IndexofDetectedNewState =13 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLH"))
            IndexofDetectedNewState =14 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHL"))
            IndexofDetectedNewState =15 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHA"))
            IndexofDetectedNewState =16;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHH"))
            IndexofDetectedNewState =17; 
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLL"))
            IndexofDetectedNewState =18;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLA"))
            IndexofDetectedNewState =19;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLH"))
            IndexofDetectedNewState =20;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHL"))
            IndexofDetectedNewState =21 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHA"))
            IndexofDetectedNewState =22 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHH"))
            IndexofDetectedNewState =23;
        
        System.out.println("New State after action: "+IndexofDetectedNewState);
        
         // Calculating The Reward
        Double Reward= 0.00;
        Reward = CalculateReward(VM);
        
       
        
        //Updating Qvalue
       Qtable[IndexofCurrentState][action].Q_value = Double.valueOf(((String[])(FinalDetectedState.get(0)))[1]) *((1 - alpha) * Qtable[IndexofCurrentState][action].Q_value) + alpha * (Reward + gamma * Maximum(IndexofDetectedNewState, false));
       
       //Setting the Next State as the Current State for the next loop
        
        
       return IndexofDetectedNewState;

   }
    
    
    public  List DetectState(VirtualMachine VM)
 {
     
       VM.CalculateCPUtilImprov();;
       VM.CalculateMemUtilImprov();
       VM.CalculateDiskUtilImprov();
       VM.CalculateVMThroughput_ResponseTime();
       VM.NormalizeResponseTime();
       
       List OutputStateDegreePairs= new LinkedList();
       
       OutputStateDegreePairs = FuzzyInference (1.0/VM.VM_CPUtil, 1.0/VM.VM_MemUtil, 1.0/VM.VM_DiskUtil, VM.NormalizedResponsetime);
       return OutputStateDegreePairs;
     
 }
    
    public  List FuzzyInference(Double CPUU, Double MemU, Double DiskU, Double NormalizedRT){
        
//        Engine engine = new Engine();
//        engine.setName("StateDetection");
//        engine.setDescription("");
//        
//        InputVariable CPUtilImprov = new InputVariable();
//        CPUtilImprov.setName("CPUtilImprovement");
//        CPUtilImprov.setDescription("");
//        CPUtilImprov.setEnabled(true);
//        CPUtilImprov.setRange(0.000, 1.000);
//        CPUtilImprov.setLockValueInRange(false);
//        CPUtilImprov.addTerm(new Ramp("Low", 1.000, 0.000));
//        CPUtilImprov.addTerm(new Ramp("High", 0.000, 1.000));
//        engine.addInputVariable(CPUtilImprov);
          
            // Load from 'FCL' file
        String fileName = "//Users//mahshidhelalimoghadam//Documents//Industrial PhD//NetBeansProjects//StateDetection.fcl.txt";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return null;
        }

        // Show 
       

        // Set inputs
        
        fis.setVariable("CPUU", CPUU);
        fis.setVariable("MemU", MemU);
        fis.setVariable("DiskU", DiskU);
        fis.setVariable("RT", NormalizedRT);
        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable State =fis.getVariable("State");
        List OutputLinguisticTerms= new LinkedList();
        HashMap<String, LinguisticTerm> hmp = new HashMap <String, LinguisticTerm>();
        
        hmp= State.getLinguisticTerms();
        Set set = hmp.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        Map.Entry mentry = (Map.Entry)iterator.next();
//        System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
//        System.out.println(mentry.getValue());
        OutputLinguisticTerms.add(mentry.getKey());
        }
        
        
        List OutputStateDegreePairs= new LinkedList();
        
//        for(Object Lingterm: OutputLinguisticTerms)
//           {
//               
//               String[] pair = new String [2];
//               Double Degree= State.getMembership(Lingterm.toString());
//               
//               if (Degree > 0.00 )
//                  { pair[0]=Lingterm.toString();
//                    pair[1]=Degree.toString();
//                    OutputStateDegreePairs.add(pair);
//                  }
//               
//           } 
//        
        
        
        for( Rule r : fis.getFunctionBlock("StateDetection").getFuzzyRuleBlock("No1").getRules() )
        {
            String str=null;
            List conseq= r.getConsequents();
            for (Object Obj:conseq)
                str = ((RuleTerm)Obj).toString();
            String[] strArray =str.split(" ");
            String[] pair = new String [2];
            Double Degree= r.getDegreeOfSupport();
               
               if (Degree > 0.00 )
                  { pair[0]=strArray[2].toString();
                    pair[1]=Degree.toString();
                    OutputStateDegreePairs.add(pair);
                  }
            
        }
        
        for(Object pair: OutputStateDegreePairs)
           {
              System.out.println(((String[])pair)[0] + "  " + Math.round (Double.parseDouble(((String[])pair)[1])*1000)/1000.0);
           }
        //System.out.println(State.toString() );
//        JFuzzyChart.get().chart(State, State.getDefuzzifier(), true);
//        JFuzzyChart.get().chart(fis);

        // Print ruleSet
        //System.out.println(fis);

        return OutputStateDegreePairs;
        
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
    
    public  boolean ApplyAction0(VirtualMachine VM)
    {
        boolean Success= false;
        Success = true;
        return Success;
    }
    
    public  boolean ApplyAction1(VirtualMachine VM)
    {
        boolean Success= false;
        if ((VM.VM_CPU_g- 0.25)>0)
        {VM.VM_CPU_g = VM.VM_CPU_g- 0.25;
        Success = true;
        }
        return Success;
    }
    
    public  boolean ApplyAction2(VirtualMachine VM)
    {
        boolean Success= false;
        if ((VM.VM_CPU_g- 0.5)>0)
        {VM.VM_CPU_g = VM.VM_CPU_g- 0.5;
        Success = true;
        }
        return Success;
    }
     
    public  boolean ApplyAction3(VirtualMachine VM)
    {
        boolean Success= false;
        if ((VM.VM_CPU_g- 0.75)>0)
        {VM.VM_CPU_g = VM.VM_CPU_g- 0.75;
        Success = true;
        }
        return Success;
    }
    
     public  boolean ApplyAction4(VirtualMachine VM)
    {
        boolean Success= false;
        if ((VM.VM_CPU_g- 1.0)>0)
        {VM.VM_CPU_g = VM.VM_CPU_g- 1.0;
        Success = true;
        }
        return Success;
    }
      
    public  boolean ApplyAction5(VirtualMachine VM)
    {
        boolean Success= false;
        if ((VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.25)>0)
        {VM.VM_Mem_g = VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.25;
        Success = true;
        }
        return Success;
    }
       
    public  boolean ApplyAction6(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.5)>0)
        {VM.VM_Mem_g = VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.5;
        Success = true;
        }
        return Success;
    }
       
    public  boolean ApplyAction7(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.75)>0)
        {VM.VM_Mem_g = VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 0.75;
        Success = true;
        }
        return Success;
    }
    
    public  boolean ApplyAction8(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 1.0)>0)
        {VM.VM_Mem_g = VM.VM_Mem_g - (double)(VM.VM_Mem_g /4.0)* 1.0;
        Success = true;
        }
        return Success;
    }
    public  boolean ApplyAction9(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.25)>0)
        {VM.VM_Disk_g = VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.25;
        Success = true;
        }
        return Success;
    }
    
    public  boolean ApplyAction10(VirtualMachine VM)
    {
        boolean Success= false;
        
         if ((VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.5)>0)
        {VM.VM_Disk_g = VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.5;
        Success = true;
        }
        return Success;
    }
    
    public  boolean ApplyAction11(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.75)>0)
        {VM.VM_Disk_g = VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 0.75;
        Success = true;
        }
        return Success;
    }
    
     public  boolean ApplyAction12(VirtualMachine VM)
    {
        boolean Success= false;
        
        if ((VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 1.0)>0)
        {VM.VM_Disk_g = VM.VM_Disk_g - (double)(VM.VM_Disk_g /4.0)* 1.0;
        Success = true;
        }
        return Success;
    }
     
     
     
    public  Double CalculateReward(VirtualMachine VM)
 {
     Double Beta=0.3;
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
 }
    
    
    
    
    
    
//*********************************************************************************************************************************
    
    
    
    
    
    
    
    
    public  int Operate (int IndexofCurrentState, VirtualMachine VM, List AppliedEffectiveActions)
    {
        
       //Select An Action
        int action=0;
        boolean Success= false;
        while (Success== false){
        action = extractAction(IndexofCurrentState);
         
         //Applying The Selected Action
                if (action == 0) {
                 Success = true;
                 System.out.println("Action 0 (nothing) ");
                } 
                else if (action == 1) {
                 Success = ApplyAction1(VM); 
                 if (Success== true )
                 {
                     System.out.println("Action 1 "); 
                     AppliedEffectiveActions.add(1);
                 }
                } 
                else if (action == 2) {
                 Success = ApplyAction2(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 2 ");
                     AppliedEffectiveActions.add(2);
                 }
                } 
                else if (action == 3) {
                 Success =  ApplyAction3(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 3 ");
                     AppliedEffectiveActions.add(3);
                 }
                }
                else if (action == 4) {
                 Success =  ApplyAction4(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 4 ");
                     AppliedEffectiveActions.add(4);
                 }
                }
                else if (action == 5) {
                 Success =  ApplyAction5(VM);
                 if (Success== true ) 
                 {
                     System.out.println("Action 5 ");
                     AppliedEffectiveActions.add(5);
                 }
                }
                else if (action==6) {
                    Success= ApplyAction6(VM);
                    if (Success== true ) 
                    {
                        System.out.println("Action 6 ");
                        AppliedEffectiveActions.add(6);
                    }
                } 
                else if (action == 7) {
                 Success = ApplyAction7(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 7 ");
                     AppliedEffectiveActions.add(7);
                 }
                } 
                else if (action == 8) {
                 Success = ApplyAction8(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 8 ");
                     AppliedEffectiveActions.add(8);
                 }
                } 
                else if (action == 9) {
                 Success = ApplyAction9(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 9 ");
                     AppliedEffectiveActions.add(9);
                 }
                } 
                else if (action == 10) {
                 Success = ApplyAction10(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 10 ");
                     AppliedEffectiveActions.add(10);
                 }
                } 
                 else if (action == 11) {
                 Success = ApplyAction11(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 11 ");
                     AppliedEffectiveActions.add(11);
                 }
                 }
                 else if (action == 12) {
                 Success = ApplyAction12(VM);
                 if (Success== true )
                 {
                     System.out.println("Action 12 ");
                     AppliedEffectiveActions.add(12);
                 }
                 }
                
        }
        
        // Detection of new state
        
        List DetectedStates;
        int IndexofDetectedNewState = 0;
        DetectedStates = this.DetectState(VM);
        
        // Extracting the Index of state with Max Membership degree
        
        List FinalDetectedState = new LinkedList();
        for (Object StateMember: DetectedStates )
        {
           Double MaxMemdegree=0.0;
           String[] pair = new String [2];
           Double Degree=  Double.valueOf(((String[])StateMember)[1]); 
               if (Degree > MaxMemdegree )
                  { pair[0]=((String[])StateMember)[0];
                    pair[1]=((String[])StateMember)[1];
                    FinalDetectedState.add(pair);
                  }
            
         
        }
        
        if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLL"))
            IndexofDetectedNewState =0 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLA"))
            IndexofDetectedNewState =1 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLLH"))
            IndexofDetectedNewState =2 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHL"))
            IndexofDetectedNewState =3 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHA"))
            IndexofDetectedNewState =4 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LLHH"))
            IndexofDetectedNewState =5 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLL"))
            IndexofDetectedNewState =6 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLA"))
            IndexofDetectedNewState =7 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHLH"))
            IndexofDetectedNewState =8 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHL"))
            IndexofDetectedNewState =9;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHA"))
            IndexofDetectedNewState =10 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("LHHH"))
            IndexofDetectedNewState =11 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLL"))
            IndexofDetectedNewState =12 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLA"))
            IndexofDetectedNewState =13 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLLH"))
            IndexofDetectedNewState =14 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHL"))
            IndexofDetectedNewState =15 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHA"))
            IndexofDetectedNewState =16;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HLHH"))
            IndexofDetectedNewState =17; 
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLL"))
            IndexofDetectedNewState =18;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLA"))
            IndexofDetectedNewState =19;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHLH"))
            IndexofDetectedNewState =20;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHL"))
            IndexofDetectedNewState =21 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHA"))
            IndexofDetectedNewState =22 ;
        else if (((String[])(FinalDetectedState.get(0)))[0].equals("HHHH"))
            IndexofDetectedNewState =23;
        
        System.out.println("New State after action: "+IndexofDetectedNewState);
        
       
       //Setting the Next State as the Current State for the next loop
        
        
       return IndexofDetectedNewState;
 
    }
    
    public  int extractAction(int IndexofCurrentState){
        
        // Randomly choose a possible action connected to the current state.
        double randomNumber = 0;
        boolean choiceIsValid = false;
        int possibleAction =0;
        
        // Randomly choose a possible action connected to the current state.
        while (choiceIsValid == false) {
            // Get a random value between 0(inclusive) and 1(exclusive).

                possibleAction = (int) Maximum(IndexofCurrentState, true);
                if (Qtable[IndexofCurrentState][possibleAction].Q_value > -1) {
                choiceIsValid = true;
                System.out.println("action with maximum Qvalue");
               }
               // choiceIsValid = true;    
       
    }   
        return possibleAction;
    }

}
