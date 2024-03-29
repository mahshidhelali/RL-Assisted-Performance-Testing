# Reinforcement Learning-Assisted Performance-Testing
This repository contains source code for a smart RL-assisted performance testing framework (agent) that is able to learn the efficient generation of performance test cases to meet testing objective, without access to source code and models, and replay the leant policy in further testing situations. It generates platform-based and workload-based test conditions, and involves two parts:
SaFReL: self-adaptive fuzzy reinforcement learning performance testing through platform-based test cases.
RELOAD: Adaptive reinforcement learning-driven load testing.
A summarized description of the framework is available at: "Performance Testing Driven by Reinforcement Learning" https://ieeexplore.ieee.org/abstract/document/9159096

I. How the autonomous agents work:

Q-learning as a model-free RL is the core learning algorithm in the autonomous agent.  
The smart tester agent assumes two phases of learning:
- Initial learning during which the agent learns an optimal policy for the first time.
- Transfer learning during which the agent replays the learnt policy in similar cases while 

II. Strengths of the RL-assisted performance testing agents:

- Eliminating dependency on the system models, source code during the testing.
- The capability of 
  * knowledge formation (in terms of Q-values and policy) during the learning
  * storing the gained knowledge 
  * reusing the knowledge in further potential situations
which could lead to efficiency improvement, i.e. less required computation time, compared to 	common performance testing approaches.

III. Application Areas:

Evolving systems during Continuous Integration & Continuous Delivery, performance regression testing 

=========================================================================

SaFReL:

It is a self-adaptive fuzzy reinforcement learning performance test agent generating platform-based test cases. It learns how to tune the resource availability to reach the intended performance breaking point for different types of SUTs (in terms of their sensitivity to resources).
In summary, it learns the optimal policy to generate platform-based performance test cases resulting in reaching the intended performance breaking point for different types of SUTs, and replays the learnt policy on further testing cases. 
In the current version it uses a performance estimation module to estimate the performance behavior of software programs of CPU-intensive, memory-intensive and disk-intensive types running on the hardware with various configurations. More information about the structure of SaFReL and its mechanism is available at: 
https://link.springer.com/article/10.1007/s11219-020-09532-z



=========================================================================

RELOAD:

It is an intelligent reinforcement learning-driven test agent which generates an effective workload efficiently to meet the test objective and executes it through Apache JMeter on SUT. RELOAD learns the optimal policy for the test workload generation, without access to a model or source code of SUT. It effectively learns the effects of different transactions involved in the workload and how to tune the load of transactions to meet the test objective. 

The intelligent tester agent can reuse the learned policy in further similar testing scenarios, for example, reaching different target values of response time or error rate. The learning-based load testing can reach the testing objective with lower cost compared to common load testing procedures, and results in higher test efficiency. It is generally beneficial to the continuous testing activities such as varying scenarios and performance regression testing. 

RELOAD uses Apache JMeter 5.1.1. More information about the structure of RELOAD and its learning procedure is available at: 
https://ieeexplore.ieee.org/document/9504763

Find out more information about RL-assisted performance assurance at: "Machine Learning-Assisted Performance Assurance"
http://urn.kb.se/resolve?urn=urn:nbn:se:mdh:diva-47501
