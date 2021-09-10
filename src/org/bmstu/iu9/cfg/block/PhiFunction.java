package org.bmstu.iu9.cfg.block;

import org.bmstu.iu9.cfg.block.instructions.expr.Ident;

import java.util.ArrayList;
import java.util.List;

public class PhiFunction {
    private final List<Ident> args = new ArrayList<>();

    public List<Ident> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("φ(");
        for (int i = 0; i < args.size() - 1; i++) {
            builder.append(args.get(i)).append(",");
        }
        builder.append(args.get(args.size() - 1)).append(")");
        return builder.toString();
    }
}
