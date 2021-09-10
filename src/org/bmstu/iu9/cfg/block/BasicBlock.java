package org.bmstu.iu9.cfg.block;

import org.bmstu.iu9.cfg.block.instructions.Instruction;
import org.bmstu.iu9.cfg.block.instructions.expr.Ident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicBlock {
    private final int id;
    private final List<Instruction> instructions;
    private final Map<Ident, PhiFunction> phiFunctions;

    public BasicBlock(int id) {
        this.id = id;
        this.instructions = new ArrayList<>();
        this.phiFunctions = new HashMap<>();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public Map<Ident, PhiFunction> getPhiFunctions() {
        return phiFunctions;
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addPhiFunction(Ident ident, PhiFunction phi) {
        phiFunctions.put(ident, phi);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("block ").append(id).append("\n");
        for (var phi : phiFunctions.entrySet()) {
            builder.append(phi.getKey()).append(" = ").append(phi.getValue()).append("\n");
        }
        for (Instruction i : instructions) {
            builder.append(i).append("\n");
        }
        return builder.toString();
    }
}
