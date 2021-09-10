package org.bmstu.iu9.cfg.block.instructions;

import org.bmstu.iu9.cfg.block.instructions.expr.Expr;
import org.bmstu.iu9.cfg.block.instructions.expr.Ident;

public class AssignInstruction implements Instruction {
    private final Ident lhs;
    private final Expr rhs;

    public AssignInstruction(Ident lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Ident getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return lhs + " = " + rhs;
    }
}
