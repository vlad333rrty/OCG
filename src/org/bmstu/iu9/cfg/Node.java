package org.bmstu.iu9.cfg;

import org.bmstu.iu9.cfg.block.BasicBlock;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final BasicBlock bb;
    private final List<Node> children = new ArrayList<>();
    private boolean mark;

    public Node(BasicBlock bb) {
        this.bb = bb;
    }

    public BasicBlock getBasicBlock() {
        return bb;
    }

    public boolean isMarked() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node node) {
        children.add(node);
    }
}
