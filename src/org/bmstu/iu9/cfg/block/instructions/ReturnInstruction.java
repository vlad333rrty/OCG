package org.bmstu.iu9.cfg.block.instructions;

import org.bmstu.iu9.cfg.block.instructions.expr.Expr;

public class ReturnInstruction implements Instruction {
    private final Expr returnExpr;

    public ReturnInstruction(Expr returnExpr) {
        this.returnExpr = returnExpr;
    }

    public Expr getReturnExpr() {
        return returnExpr;
    }

    @Override
    public String toString() {
        return String.format("return %s", returnExpr);
    }
}
