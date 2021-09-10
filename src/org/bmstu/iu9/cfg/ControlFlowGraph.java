package org.bmstu.iu9.cfg;

import org.bmstu.iu9.cfg.block.BasicBlock;
import org.bmstu.iu9.cfg.block.PhiFunction;
import org.bmstu.iu9.cfg.block.instructions.AssignInstruction;
import org.bmstu.iu9.cfg.block.instructions.CondInstruction;
import org.bmstu.iu9.cfg.block.instructions.Instruction;
import org.bmstu.iu9.cfg.block.instructions.ReturnInstruction;
import org.bmstu.iu9.cfg.block.instructions.expr.ArithmeticalExpr;
import org.bmstu.iu9.cfg.block.instructions.expr.Condition;
import org.bmstu.iu9.cfg.block.instructions.expr.Expr;
import org.bmstu.iu9.cfg.block.instructions.expr.Ident;
import org.bmstu.iu9.dominant.tree.DominantTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ControlFlowGraph {
    private final Node root;
    private final List<Node> nodes;

    private final Map<Ident, Stack<Integer>> stacks = new HashMap<>();
    private final Map<Ident, Integer> counters = new HashMap<>();


    public ControlFlowGraph(Node root, List<Node> nodes) {
        this.root = root;
        this.nodes = nodes;
    }

    public Node getRoot() {
        return root;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void buildSSA() {
        Map<String, Set<Node>> defNodes = new HashMap<>();
        Set<String> names = new HashSet<>();

        for (Node node : nodes) {
            for (Instruction instruction : node.getBasicBlock().getInstructions()) {
                if (instruction instanceof AssignInstruction) {
                    AssignInstruction assign = (AssignInstruction) instruction;
                    String name = assign.getLhs().getName();
                    defNodes.computeIfAbsent(name, k -> new HashSet<>());
                    defNodes.get(name).add(node);

                    names.add(name);
                }
            }
        }

        DominantTree dominantTree = new DominantTree(nodes, root);
        Map<Integer, Set<Node>> dominanceFrontier = dominantTree.getDominanceFrontier();

        propagatePhiFunctions(names, defNodes, dominanceFrontier);

        prepare();
        for (Node node : nodes) {
            rename(node);
        }
    }

    private void propagatePhiFunctions(Set<String> names, Map<String, Set<Node>> defNodes,
                                       Map<Integer, Set<Node>> dominanceFrontier) {
        Map<String, Set<Node>> next = new HashMap<>();
        for (String name : names) {
            Set<Node> defs = defNodes.get(name);
            if (defs == null) {
                continue;
            }
            for (Node node : defs) {
                Set<Node> frontier = dominanceFrontier.get(node.getBasicBlock().getId());
                for (Node n : frontier) {
                    if (addPhiFunction(n, defs, name, dominanceFrontier)) {
                        next.computeIfAbsent(name, k -> new HashSet<>());
                        next.get(name).add(n);
                    }
                }
            }
        }

        if (!next.isEmpty()) {
            propagatePhiFunctions(names, next, dominanceFrontier);
        }
    }

    private boolean addPhiFunction(Node frontierNode, Set<Node> definitionNodes, String name,
                                   Map<Integer, Set<Node>> dominanceFrontier) {
        BasicBlock bb = frontierNode.getBasicBlock();
        Ident ident = new Ident(name);
        if (bb.getPhiFunctions().get(ident) == null) {
            bb.getPhiFunctions().put(ident, new PhiFunction());
        } else {
            return false;
        }
        for (Node node : definitionNodes) {
            Set<Node> frontier = dominanceFrontier.get(node.getBasicBlock().getId());
            if (frontier.contains(frontierNode)) {
                bb.getPhiFunctions().get(ident).getArgs().add(findIdentByName(name, node.getBasicBlock()));
            }
        }
        if (bb.getPhiFunctions().get(ident).getArgs().size() < 2) {
            bb.getPhiFunctions().remove(ident);
            return false;
        }
        return true;
    }

    private Ident findIdentByName(String name, BasicBlock bb) {
        for (Instruction instruction : bb.getInstructions()) {
            if (instruction instanceof AssignInstruction) {
                Ident ident = ((AssignInstruction) instruction).getLhs();
                if (ident.getName().equals(name)) {
                    return ident;
                }
            }
        }
        for (var entry : bb.getPhiFunctions().entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("No ident can be found");
    }

    private void rename(Node node) {
        for (var entry : node.getBasicBlock().getPhiFunctions().entrySet()) {
            generateNextName(entry.getKey());
        }

        for (Instruction instruction : node.getBasicBlock().getInstructions()) {
            if (instruction instanceof AssignInstruction) {
                AssignInstruction assign = (AssignInstruction) instruction;
                renameExpr(assign.getRhs());
                generateNextName(assign.getLhs());
            } else if (instruction instanceof CondInstruction) {
                renameExpr(((CondInstruction) instruction).getCondition());
            } else if (instruction instanceof ReturnInstruction) {
                renameExpr(((ReturnInstruction) instruction).getReturnExpr());
            }
        }
    }

    private void renameExpr(Expr expr) {
        if (expr instanceof Ident) {
            Ident ident = (Ident) expr;
            ident.setIndex(stacks.get(ident).isEmpty() ? 0 : stacks.get(ident).pop());
        } else if (expr instanceof ArithmeticalExpr) {
            ArithmeticalExpr arithmetical = (ArithmeticalExpr) expr;
            renameExpr(arithmetical.getLhs());
            renameExpr(arithmetical.getRhs());
        } else if (expr instanceof Condition) {
            Condition condition = (Condition) expr;
            renameExpr(condition.getLhs());
            renameExpr(condition.getRhs());
        }
    }

    private void prepare() {
        gatherIdents();
        for (var entry : counters.entrySet()) {
            stacks.put(entry.getKey(), new Stack<>());
        }
    }

    private void gatherIdents() {
        for (Node node : nodes) {
            for (Instruction instruction : node.getBasicBlock().getInstructions()) {
                if (instruction instanceof AssignInstruction) {
                    AssignInstruction assign = (AssignInstruction) instruction;
                    gatherExpr(assign.getLhs());
                    gatherExpr(assign.getRhs());
                }
            }
        }
    }

    private void gatherExpr(Expr expr) {
        if (expr instanceof Ident) {
            counters.put((Ident) expr, 0);
        } else if (expr instanceof ArithmeticalExpr) {
            ArithmeticalExpr arithmetical = (ArithmeticalExpr) expr;
            gatherExpr(arithmetical.getLhs());
            gatherExpr(arithmetical.getRhs());
        }
    }

    private void generateNextName(Ident ident) {
        int i = counters.get(ident);
        ident.setIndex(i);
        stacks.get(ident).push(i);
        counters.put(ident, i + 1);
    }
}
