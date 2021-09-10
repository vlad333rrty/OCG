package org.bmstu.iu9.cfg.block.instructions.expr;

import java.util.Objects;

public class Ident implements Expr {
    private String name;
    private int index;

    public Ident(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ident ident = (Ident) o;
        return Objects.equals(name, ident.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s_%d", name, index);
    }
}
