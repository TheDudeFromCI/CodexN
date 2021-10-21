package net.whg;

import org.junit.Test;

import net.whg.graph.DataType;
import net.whg.graph.Environment;
import net.whg.graph.NodeType;
import net.whg.impl.axioms.MaxConnectionsAxiom;
import net.whg.impl.heuristics.MinConnectionsHeuristic;
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
        env.addHeuristic(new MinConnectionsHeuristic());

        var f = env.getDataType("Float");
        env.addNodeType(new NodeType("Input", null, new DataType[0], new DataType[] { f, f, f }));
        env.addNodeType(new NodeType("Output", null, new DataType[] { f }, new DataType[0]));

        var inputs = new Object[] { 10, 20, 30 };
        var outputs = new Object[1];
        env.addAxiom((g) -> {
            if (!g.isComplete())
                return true;

            g.execute(inputs, outputs);
            return (int) outputs[0] == 230;
        });

        var tree = new Tree("Madd", env);
        var worker = new Worker(tree);

        for (var i = 0; tree.getNumSolutionsFound() == 0; i++) {
            worker.step();

            if (i % 1000 == 0) {
                System.out.printf("%08d, %05d, %08d%n", tree.getNumGraphsProcessed(), tree.getNumSolutionsFound(),
                        tree.getNumOpenGraphs());
                System.out.println(tree.peekNextGraph());
            }
        }

        System.out.printf("%08d, %05d, %08d%n", tree.getNumGraphsProcessed(), tree.getNumSolutionsFound(),
                tree.getNumOpenGraphs());
        System.out.println(tree.nextGraph());
    }
}
