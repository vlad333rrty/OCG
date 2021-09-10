package org.bmstu.iu9.parser;

import org.bmstu.iu9.cfg.Node;

import java.util.List;

public class ParseResult {
    private final Node root;
    private final List<Node> vertices;

    public ParseResult(Node root, List<Node> vertices) {
        this.root = root;
        this.vertices = vertices;
    }

    public Node getRoot() {
        return root;
    }

    public List<Node> getVertices() {
        return vertices;
    }
}
