package net.whg.train;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import net.whg.graph.Environment;
import net.whg.graph.Graph;
import net.whg.solver.GraphResult;

public class TrainingWorker implements Runnable {
    private final PriorityQueue<GraphTrainingResult> tree = new PriorityQueue<>();
    private final List<GraphTrainingResult> solutions = new ArrayList<>();
    private final List<Graph> firstGeneration = new ArrayList<>();
    private final Random random = new Random();

    private final List<Graph> graphBuffer = new ArrayList<>();
    private final List<Float> heuristicBuffer = new ArrayList<>();

    private final Environment problemEnv;
    private final Environment solutionEnv;
    private final HeuristicProvider heuristicProvider;
    private final int iterations;
    private final float randomHeuristicStd;

    public TrainingWorker(Environment problemEnv, Environment solutionEnv, HeuristicProvider heuristicProvider,
            int iterations, float randomHeuristicStd) {
        this.problemEnv = problemEnv;
        this.solutionEnv = solutionEnv;
        this.heuristicProvider = heuristicProvider;
        this.iterations = iterations;
        this.randomHeuristicStd = randomHeuristicStd;
    }

    private Graph randomGraph(Environment env, boolean complete) {
        Graph graph = null;

        tryGenGraph: while (true) {
            var depth = random.nextInt(5) + 2;

            graph = new Graph("Temp", env.getInputNodeType(), env.getOutputNodeType());

            for (var i = 0; i < depth; i++) {
                graph.getChildGraphs(graphBuffer, env);
                graphBuffer.removeIf(g -> !env.isValid(g));

                if (graphBuffer.isEmpty())
                    continue tryGenGraph;

                var index = random.nextInt(graphBuffer.size());
                graph = graphBuffer.get(index);
                graphBuffer.clear();
            }

            break;
        }

        return graph;
    }

    public void step() {
        var problem = randomGraph(problemEnv, true);
        var state = randomGraph(solutionEnv, false);

        prewarmFirstGeneration(problem, state);

        for (var i = 0; i < iterations && !tree.isEmpty(); i++) {
            preformIteration(problem);
        }

        var results = compileTrainingResults();
        heuristicProvider.trainHeuristics(problem, state, results);

        tree.clear();
        solutions.clear();
        firstGeneration.clear();
    }

    private void prewarmFirstGeneration(Graph problem, Graph state) {
        state.getChildGraphs(firstGeneration, solutionEnv);
        heuristicProvider.getHeuristics(problem, state, firstGeneration, heuristicBuffer);

        for (var i = 0; i < firstGeneration.size(); i++) {
            var child = firstGeneration.get(i);
            var heuristic = heuristicBuffer.get(i);
            var randomHeuristic = (float) (random.nextGaussian() * randomHeuristicStd);
            var result = new GraphTrainingResult(child, heuristic, randomHeuristic, i);

            tree.add(result);
        }

        heuristicBuffer.clear();
    }

    private List<GraphResult> compileTrainingResults() {
        var results = new ArrayList<GraphResult>();
        var bestHeuristics = new ArrayList<Float>();

        for (var i = 0; i < firstGeneration.size(); i++)
            bestHeuristics.add(0f);

        for (var leafGraph : tree) {
            var index = leafGraph.parentIndex();
            var estimate = leafGraph.estimatedHeuristic();
            var current = bestHeuristics.get(index);
            bestHeuristics.set(index, Math.max(estimate, current));
        }

        for (var solutionGraph : solutions) {
            var index = solutionGraph.parentIndex();
            var estimate = solutionGraph.estimatedHeuristic();
            var current = bestHeuristics.get(index);
            bestHeuristics.set(index, Math.max(estimate, current));
        }

        for (var i = 0; i < firstGeneration.size(); i++) {
            var graph = firstGeneration.get(i);
            var heuristic = bestHeuristics.get(i);
            results.add(new GraphResult(graph, heuristic));
        }

        return results;
    }

    private float getFitness(Graph problem, Graph solution) {
        return 1f;
    }

    private void preformIteration(Graph problem) {
        var node = tree.poll();
        var graph = node.graph();
        var parentIndex = node.parentIndex();

        graph.getChildGraphs(graphBuffer, solutionEnv);
        graphBuffer.removeIf(g -> !solutionEnv.isValid(g));

        graphBuffer.removeIf(g -> {
            if (g.isComplete()) {
                var fitness = getFitness(problem, g);
                solutions.add(new GraphTrainingResult(g, fitness, 0f, parentIndex));
                return true;
            } else {
                return false;
            }
        });

        heuristicProvider.getHeuristics(problem, graph, graphBuffer, heuristicBuffer);

        for (var i = 0; i < graphBuffer.size(); i++) {
            var child = graphBuffer.get(i);
            var heuristic = heuristicBuffer.get(i);
            var randomHeuristic = (float) (random.nextGaussian() * randomHeuristicStd);
            tree.add(new GraphTrainingResult(child, heuristic, randomHeuristic, parentIndex));
        }

        graphBuffer.clear();
        heuristicBuffer.clear();
    }
}
