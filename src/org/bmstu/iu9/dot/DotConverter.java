package org.bmstu.iu9.dot;

import org.bmstu.iu9.cfg.ControlFlowGraph;
import org.bmstu.iu9.cfg.Node;
import org.bmstu.iu9.cfg.block.BasicBlock;
import org.bmstu.iu9.cfg.block.instructions.AssignInstruction;
import org.bmstu.iu9.cfg.block.instructions.CondInstruction;

import java.util.Stack;

public class DotConverter {

    public String convertToDot(ControlFlowGraph controlFlowGraph) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph{\n");
        Node root = controlFlowGraph.getRoot();
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node v = stack.pop();
            v.setMark(true);
            addBlockInfo(builder, v.getBasicBlock());
            for (Node u : v.getChildren()) {
                builder.append(v.getBasicBlock().getId()).append(" -> ").
                        append(u.getBasicBlock().getId()).append("\n");
                if (!u.isMarked()) {
                    stack.push(u);
                }
            }
        }
        builder.append("}\n");

        for (Node v : controlFlowGraph.getNodes()) {
            v.setMark(false);
        }

        return builder.toString();
    }

    private void addBlockInfo(StringBuilder builder, BasicBlock bb) {
        builder.append(bb.getId()).append("[shape=box]").append("[label=\"block ").append(bb.getId())
                .append(":\n");
        for (var phi : bb.getPhiFunctions().entrySet()) {
            builder.append(phi.getKey()).append(" = ").append(phi.getValue()).append("\n");
        }
        bb.getInstructions().sort((o1, o2) -> {
            if (o1 instanceof AssignInstruction && o2 instanceof CondInstruction) {
                return -1;
            }
            if (o2 instanceof AssignInstruction && o1 instanceof CondInstruction) {
                return 1;
            }
            return 0;
        });
        for (int i = 0; i < bb.getInstructions().size(); i++) {
            builder.append(bb.getInstructions().get(i)).append("\n");
        }
        builder.append("\"]\n");
    }
}
