# AI-GraphSolver-Test

An experimental AI project where an algorithm attempts to generate node graphs to complete tasks in a neural network guided heuristic search pattern.

## Project Heirarchy

The project is broken down into two applications: The Java server and the Python server. The Java server is the application that preforms the graph search and handles the execution of the generated program. Meanwhile, the Python server manages the machine learning instance via Pytorch in order to execute the neural network code and generate heuristics to send back to the Java server to aid in the search.

![Training Mode and Evaluation Mode](./img/Codex%20Training%20vs%20Testing%20Model%20Architectures.png)