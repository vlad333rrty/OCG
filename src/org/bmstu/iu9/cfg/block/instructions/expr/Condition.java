package org.bmstu.iu9.cfg.block.instructions.expr;

public class Condition implements Expr {
    private final Expr lhs, rhs;
    private final ComparisonOperationType comparisonOperationType;

    public Condition(Expr lhs, Expr rhs, ComparisonOperationType comparisonOperationType) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.comparisonOperationType = comparisonOperationType;
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public ComparisonOperationType getComparisonOperationType() {
        return comparisonOperationType;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lhs, comparisonOperationType, rhs);
    }
}
