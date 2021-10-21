package net.whg.impl.libs;

import net.whg.graph.Environment;

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
            outputs[0] = (float) inputs[0] + (float) inputs[1];
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Subtract", (inputs, outputs) -> {
            outputs[0] = (float) inputs[0] - (float) inputs[1];
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Multiply", (inputs, outputs) -> {
            outputs[0] = (float) inputs[0] * (float) inputs[1];
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Divide", (inputs, outputs) -> {
            outputs[0] = (float) inputs[0] / (float) inputs[1];
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Power", (inputs, outputs) -> {
            outputs[0] = (float) Math.pow((float) inputs[0], (float) inputs[1]);
        }, d(floatType, floatType), d(floatType)));

        env.addNodeType(n("Floor", (inputs, outputs) -> {
            outputs[0] = (int) Math.floor((float) inputs[0]);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("Ceiling", (inputs, outputs) -> {
            outputs[0] = (int) Math.ceil((float) inputs[0]);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("Round", (inputs, outputs) -> {
            outputs[0] = (int) Math.round((float) inputs[0]);
        }, d(floatType), d(integerType)));

        env.addNodeType(n("SquareRoot", (inputs, outputs) -> {
            outputs[0] = (float) Math.sqrt((float) inputs[0]);
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
