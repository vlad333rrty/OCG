package org.bmstu.iu9.cfg.block.instructions;

import org.bmstu.iu9.cfg.block.instructions.expr.Expr;

public class CondInstruction implements Instruction {
    private final Expr condition;
    private final int thenBlockId, elseBlockId;

    public CondInstruction(Expr condition, int thenBlockId, int elseBlockId) {
        this.condition = condition;
        this.thenBlockId = thenBlockId;
        this.elseBlockId = elseBlockId;
    }

    public Expr getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return String.format("%s ? block %d : block %d", condition, thenBlockId, elseBlockId);
    }
}
