package net.whg.impl.libs;

import net.whg.solver.Environment;

public class FitnessEvaluation extends Library {
    @Override
    public void register(Environment env) {

        // ===== DATA TYPES =====

        var objectType = env.getDataType("Object");
        var floatType = env.getDataType("Float");
        floatType.addParentType(objectType);

        // ============================

        // ===== INPUT / OUTPUT NODES =====

        env.addNodeType(n("Input", null, d(), d()));
        env.addNodeType(n("Output", null, d(floatType), d()));

        // ============================
    }
}
