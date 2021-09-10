package org.bmstu.iu9.cfg.block.instructions.expr;

public enum ComparisonOperationType {
    LESS("<"), GREATER(">"), LESS_OR_EQUAL("<="), GREATER_OR_EQUAL(">="), EQUAL("==");

    private final String sign;

    ComparisonOperationType(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }
}
