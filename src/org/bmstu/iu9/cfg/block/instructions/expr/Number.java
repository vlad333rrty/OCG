package org.bmstu.iu9.cfg.block.instructions.expr;

public class Number implements Expr {
    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s", value);
    }
}
