package org.bmstu.iu9.cfg.block.instructions.expr;

public class ArithmeticalExpr implements Expr {
    private final Expr lhs, rhs;
    private final OperationType operationType;

    public ArithmeticalExpr(Expr lhs, Expr rhs, OperationType operationType) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operationType = operationType;
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lhs.toString(), operationType.toString(), rhs.toString());
    }
}
