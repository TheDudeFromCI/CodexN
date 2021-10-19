package net.whg;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import net.whg.graph.DataType;
import net.whg.graph.Environment;
import net.whg.graph.Graph;
import net.whg.graph.NodeType;

/**
 * Unit test for simple App.
 */
public class AppTest {
    Environment env;
    DataType objectType = new DataType("Object");
    DataType floatType = new DataType("Float");
    DataType integerType = new DataType("Integer");
    DataType booleanType = new DataType("Boolean");
    DataType stringType = new DataType("String");

    @Before
    public void buildEnvironment() {
        env = new Environment();

        floatType.addParentType(objectType);
        integerType.addParentType(floatType);
        booleanType.addParentType(objectType);
        stringType.addParentType(objectType);

        env.addNodeType(new NodeType("Add", d(floatType, floatType), d(floatType)));
        env.addNodeType(new NodeType("Subtract", d(floatType, floatType), d(floatType)));
        env.addNodeType(new NodeType("Multiply", d(floatType, floatType), d(floatType)));
        env.addNodeType(new NodeType("Divide", d(floatType, floatType), d(floatType)));
        env.addNodeType(new NodeType("Modulus", d(floatType, floatType), d(floatType)));
    }

    private DataType[] d(DataType... variables) {
        return variables;
    }

    @Test
    public void shouldAnswerWithTrue() {
        var inputs = new NodeType("Inputs", d(), d(floatType, floatType));
        var outputs = new NodeType("Outputs", d(floatType), d());

        var children = new ArrayList<Graph>();
        children.add(new Graph("Madd", inputs, outputs));

        var time = System.currentTimeMillis();
        var count = 100000;

        for (var i = 0; i < children.size() && i < count; i++) {
            var child = children.get(i);
            child.getChildGraphs(children, env);
            // System.out.println("\n\n" + child);
        }

        time = System.currentTimeMillis() - time;
        var avg = time / (double) count;

        System.out.printf("Finished in %dms. %.2fms/graph", time, avg);
    }
}
