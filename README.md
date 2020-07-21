# Reinforcement Learning-Assisted Performance-Testing
This repository currently contains source code for a self-adaptive fuzzy RL-assisted performance testing framework (agent) that learns the optimal policy (way) to generate the (platform-based) performance test cases meeting the testing objective without access to source code and models, and reuses the learned policy in further testing cases. It uses fuzzy RL and an adaptive action selection strategy for the generation of test cases, and implements two phases of learning:
- Initial learning during which the agent learns the optimal policy for the first time,
- Transfer learning during which the agent replays the learned policy in similar cases while keeping the learning running in the long term.  

The corresponding research article is currently under review (June 2020). 
