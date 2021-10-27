package net.whg;

import org.junit.Test;

import net.whg.graph.DataType;
import net.whg.graph.Environment;
import net.whg.graph.NodeType;
import net.whg.impl.axioms.MaxConnectionsAxiom;
import net.whg.impl.heuristics.MaxConnectionsHeuristic;
import net.whg.impl.libs.Arithmetic;
import net.whg.solver.Tree;
import net.whg.solver.Worker;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void shouldAnswerWithTrue() throws InterruptedException {
        var env = new Environment();
        env.loadLibrary(new Arithmetic());
        env.addAxiom(new MaxConnectionsAxiom(20));
        env.addHeuristic(new MaxConnectionsHeuristic());

        var f = env.getDataType("Float");
        env.addNodeType(new NodeType("Input", null, new DataType[0], new DataType[] { f, f, f }));
        env.addNodeType(new NodeType("Output", null, new DataType[] { f }, new DataType[0]));

        var inputs = new Object[] { 113, 12, 7 };
        var outputs = new Object[1];
        env.addAxiom(g -> {
            if (!g.isComplete())
                return true;

            try {
                g.execute(inputs, outputs);

                var a = outputs[0] instanceof Float ? (float) outputs[0] : (int) outputs[0];
                return Float.isFinite(a) && !Float.isNaN(a);
            } catch (Exception e) {
                return false;
            }
        });

        env.addFitnessEvaluator(g -> {
            g.execute(inputs, outputs);

            var a = outputs[0] instanceof Float ? (float) outputs[0] : (int) outputs[0];
            return -Math.abs(a - 60);
        });

        var tree = new Tree("Madd", env);
        var worker = new Worker(tree);

        for (var i = 1; i <= 150_000_000; i++) {
            worker.step();

            if (i % 1000 == 0) {
                var best = tree.peekBestSolution();

                System.out.printf("%08d, %05d, %08d, (Fitness: %.02f)%n", tree.getNumGraphsProcessed(),
                        tree.getNumSolutionsFound(), tree.getNumOpenGraphs(), best.heuristic());
                System.out.println(best.graph());

                if (best.heuristic() == 0)
                    break;
            }
        }
    }
}
