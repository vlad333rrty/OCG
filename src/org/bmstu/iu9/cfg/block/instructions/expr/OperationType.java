package org.bmstu.iu9.cfg.block.instructions.expr;

public enum OperationType {
    PLUS("+"), MINUS("-"), DIV("/"), MUL("*");

    private final String sign;

    OperationType(String s) {
        this.sign = s;
    }

    @Override
    public String toString() {
        return sign;
    }
}
