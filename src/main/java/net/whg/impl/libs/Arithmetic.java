package net.whg.impl.libs;

import net.whg.solver.Environment;

public class Arithmetic extends Library {
    @Override
    public void register(Environment env) {

        // ===== DATA TYPES =====

        var objectType = env.getDataType("Object");
        var floatType = env.getDataType("Float");
        var integerType = env.getDataType("Integer");
        floatType.addParentType(objectType);
        integerType.addParentType(floatType);

        // ============================

        // ===== FLOAT OPERATIONS =====

        env.addNodeType(n("Add", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            var b = inputs[1] instanceof Float ? (float) inputs[1] : (int) inputs[1];
            outputs[0] = a + b;
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Subtract", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            var b = inputs[1] instanceof Float ? (float) inputs[1] : (int) inputs[1];
            outputs[0] = a - b;
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Multiply", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            var b = inputs[1] instanceof Float ? (float) inputs[1] : (int) inputs[1];
            outputs[0] = a * b;
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Divide", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            var b = inputs[1] instanceof Float ? (float) inputs[1] : (int) inputs[1];
            outputs[0] = a / b;
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Power", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            var b = inputs[1] instanceof Float ? (float) inputs[1] : (int) inputs[1];
            outputs[0] = (float) Math.pow(a, b);
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Floor", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            outputs[0] = (int) Math.floor(a);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("Ceiling", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            outputs[0] = (int) Math.ceil(a);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("Round", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            outputs[0] = (int) Math.round(a);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("SquareRoot", (inputs, outputs) -> {
            var a = inputs[0] instanceof Float ? (float) inputs[0] : (int) inputs[0];
            outputs[0] = (float) Math.sqrt(a);
        }, d(floatType), d(floatType)));

        // ==============================

        // ===== INTEGER OPERATIONS =====

        env.addNodeType(n("Add", (inputs, outputs) -> {
            outputs[0] = (int) inputs[0] + (int) inputs[1];
        }, d(integerType, integerType), d(integerType)));

        env.addNodeType(n("Subtract", (inputs, outputs) -> {
            outputs[0] = (int) inputs[0] - (int) inputs[1];
        }, d(integerType, integerType), d(integerType)));

        env.addNodeType(n("Multiply", (inputs, outputs) -> {
            outputs[0] = (int) inputs[0] * (int) inputs[1];
        }, d(integerType, integerType), d(integerType)));

        env.addNodeType(n("Divide", (inputs, outputs) -> {
            outputs[0] = (int) inputs[0] / (int) inputs[1];
        }, d(integerType, integerType), d(integerType)));

        env.addNodeType(n("Power", (inputs, outputs) -> {
            outputs[0] = (int) Math.pow((int) inputs[0], (int) inputs[1]);
        }, d(integerType, integerType), d(integerType)));

        env.addNodeType(n("Modulus", (inputs, outputs) -> {
            outputs[0] = (int) inputs[0] % (int) inputs[1];
        }, d(integerType, integerType), d(integerType)));

        // ==============================
    }
}
