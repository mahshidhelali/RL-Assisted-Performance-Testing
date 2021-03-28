# RELOAD v0.2 - Q-Learning Approach.
The source code of RELOAD v0.2, a reinforcement learning agent for generating workload based performance test conditions. This version of the tool uses Q-Learning as one of the basic RL methods and algorithms. 

I. Experiment Set up:

Although, this project is supported to run in a single machine (regardless of the operating system), we recommend carrying it out by setting up two virtual machines in order to prevent environmental noise.  

- VM1: Will serve as the application node (System Under the test) 
- VM2: Will serve as the load tester (Apache JMeter + RELOAD v0.2) 

**IMPORTANT**: Make sure that the two VMs can ping/communicate with each other! 

Regarding Apache JMeter: we are using a distributed (remote) testing. 

- Create a test plan by using the Recorder, [JMeter proxy](https://jmeter.apache.org/usermanual/jmeter_proxy_step_by_step.html). 
- Set up [JMeter Distributed Testing](https://jmeter.apache.org/usermanual/jmeter_distributed_testing_step_by_step.html) environment. 

II. To run RELOAD v0.2:

- Install IntelliJ IDEA 
- Clone [deeplearning4j repository](https://github.com/deeplearning4j/deeplearning4j-examples) on your machine. 
- Add RELOAD v0.2 files to the rl4j-examples folder of the above project. 
- Open project to the IntelliJ IDEA, import all the necessary libraries (including Appache JMeter JARs), and make sure to resolve all Maven dependencies. 
- Update local URL's that are found in the files of the QLearning approach. 
- Launch Appache JMeter. 
- Execute ApplyApproach file.
