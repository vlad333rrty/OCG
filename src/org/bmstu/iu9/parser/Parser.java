package org.bmstu.iu9.parser;

import org.bmstu.iu9.cfg.Node;
import org.bmstu.iu9.cfg.block.BasicBlock;
import org.bmstu.iu9.cfg.block.instructions.AssignInstruction;
import org.bmstu.iu9.cfg.block.instructions.CondInstruction;
import org.bmstu.iu9.cfg.block.instructions.ReturnInstruction;
import org.bmstu.iu9.cfg.block.instructions.expr.ArithmeticalExpr;
import org.bmstu.iu9.cfg.block.instructions.expr.ComparisonOperationType;
import org.bmstu.iu9.cfg.block.instructions.expr.Condition;
import org.bmstu.iu9.cfg.block.instructions.expr.Expr;
import org.bmstu.iu9.cfg.block.instructions.expr.Ident;
import org.bmstu.iu9.cfg.block.instructions.expr.Number;
import org.bmstu.iu9.cfg.block.instructions.expr.OperationType;
import org.bmstu.iu9.scanner.Scanner;
import org.bmstu.iu9.tokens.Token;
import org.bmstu.iu9.tokens.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser { // todo
    private static final String UNEXPECTED_TOKEN = "Unexpected token";

    private final Scanner scanner;

    private final List<Node> nodes = new ArrayList<>();
    private int id;

    public Parser(String fileName) throws IOException {
        this.scanner = new Scanner(fileName);
    }

    public ParseResult parse() throws ParseException {
        while (scanner.getNextToken().getType() != TokenType.LBRACE) {
            // skip main(...){
        }
        Node root = createNode();
        parseE(root);
        return new ParseResult(root, nodes);
    }

    private Node parseE(Node node) throws ParseException {
        Token token = scanner.peekNextToken();
        switch (token.getType()) {
            case IDENT:
                scanner.getNextToken();
                Ident ident = new Ident(token.getValue());
                assertEquals(scanner.getNextToken().getType(), TokenType.ASSIGN);
                Expr rhs = parseQ();
                AssignInstruction assign = new AssignInstruction(ident, rhs);

                assertEquals(scanner.getNextToken().getType(), TokenType.SEMICOLON);

                node.getBasicBlock().addInstruction(assign);
                parseE(node);
                break;
            case IF:
                scanner.getNextToken();
                assertEquals(scanner.getNextToken().getType(), TokenType.LPAREN);

                Expr expr = parseCondition();
                assertEquals(scanner.getNextToken().getType(), TokenType.RPAREN);
                assertEquals(scanner.getNextToken().getType(), TokenType.LBRACE);

                Node thenNode = createNode();
                Node thenResult = parseE(thenNode);

                assertEquals(scanner.getNextToken().getType(), TokenType.RBRACE);

                node.addChild(thenNode);
                Node elseResult = null;
                if (scanner.peekNextToken().getType() == TokenType.ELSE) {
                    scanner.getNextToken();

                    assertEquals(scanner.getNextToken().getType(), TokenType.LBRACE);

                    Node elseNode = createNode();
                    elseResult = parseE(elseNode);

                    assertEquals(scanner.getNextToken().getType(), TokenType.RBRACE);

                    node.addChild(elseNode);
                }
                Node nextNode = createNode();
                parseE(nextNode);
                thenResult.addChild(nextNode);

                int elseBlockId;

                if (elseResult == null) {
                    node.addChild(nextNode);
                    elseBlockId = nextNode.getBasicBlock().getId();
                } else {
                    elseResult.addChild(nextNode);
                    elseBlockId = elseResult.getBasicBlock().getId();
                }

                CondInstruction condInstruction = new CondInstruction(expr, thenResult.getBasicBlock().getId(), elseBlockId);
                node.getBasicBlock().addInstruction(condInstruction);

                return nextNode;
            case WHILE:
                scanner.getNextToken();
                assertEquals(scanner.getNextToken().getType(), TokenType.LPAREN);

                expr = parseCondition();
                assertEquals(scanner.getNextToken().getType(), TokenType.RPAREN);
                assertEquals(scanner.getNextToken().getType(), TokenType.LBRACE);

                Node whileNode = createNode();
                Node whileResult = parseE(whileNode);

                assertEquals(scanner.getNextToken().getType(), TokenType.RBRACE);

                nextNode = createNode();
                parseE(nextNode);

                node.addChild(whileNode);
                node.addChild(nextNode);

                whileResult.addChild(nextNode);
                whileResult.addChild(whileNode);

                condInstruction = new CondInstruction(expr, whileNode.getBasicBlock().getId(), whileResult.getBasicBlock().getId());
                node.getBasicBlock().addInstruction(condInstruction);

                return nextNode;
            case RETURN:
                scanner.getNextToken();
                expr = parseQ();
                Node returnNode = createNode();
                returnNode.getBasicBlock().addInstruction(new ReturnInstruction(expr));
                node.addChild(returnNode);
                break;
            default:
                // e
        }
        return node;
    }

    private Expr parseQ() throws ParseException {
        Token token = scanner.getNextToken();
        switch (token.getType()) {
            case IDENT:
                Ident ident = new Ident(token.getValue());
                if (isOperation(scanner.peekNextToken())) {
                    OperationType type = getOperationType(scanner.getNextToken());
                    return new ArithmeticalExpr(ident, parseQ(), type);
                }
                return ident;
            case NUMBER:
                Number number = new Number(Integer.parseInt(token.getValue()));
                if (isOperation(scanner.peekNextToken())) {
                    OperationType type = getOperationType(scanner.getNextToken());
                    return new ArithmeticalExpr(number, parseQ(), type);
                }
                return number;
            default:
                throw new ParseException("kek lol orbidol");
        }
    }

    private Expr parseCondition() throws ParseException {
        Expr lhs = parseQ();
        if (isComparisonOperationType(scanner.peekNextToken())) {
            ComparisonOperationType type = getComparisonOperationType(scanner.getNextToken());
            Expr rhs = parseQ();
            return new Condition(lhs, rhs, type);
        }
        return lhs;
    }

    private boolean isOperation(Token token) {
        return token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS
                || token.getType() == TokenType.MUL || token.getType() == TokenType.DIV;
    }

    private OperationType getOperationType(Token token) {
        switch (token.getType()) {
            case PLUS:
                return OperationType.PLUS;
            case MINUS:
                return OperationType.MINUS;
            case MUL:
                return OperationType.MUL;
            case DIV:
                return OperationType.DIV;
            default:
                throw new IllegalArgumentException("Unexpected type");
        }
    }

    private boolean isComparisonOperationType(Token token) {
        return token.getType() == TokenType.LESS || token.getType() == TokenType.GREATER
                || token.getType() == TokenType.LESS_OR_EQUAL || token.getType() == TokenType.GREATER_OR_EQUAL
                || token.getType() == TokenType.EQUAL;
    }

    private ComparisonOperationType getComparisonOperationType(Token token) {
        switch (token.getType()) {
            case LESS:
                return ComparisonOperationType.LESS;
            case GREATER:
                return ComparisonOperationType.GREATER;
            case LESS_OR_EQUAL:
                return ComparisonOperationType.LESS_OR_EQUAL;
            case GREATER_OR_EQUAL:
                return ComparisonOperationType.GREATER_OR_EQUAL;
            case EQUAL:
                return ComparisonOperationType.EQUAL;
            default:
                throw new IllegalArgumentException("Unexpected type");
        }
    }

    private void assertEquals(TokenType type1, TokenType type2) throws ParseException {
        if (type1 != type2) {
            throw new ParseException(UNEXPECTED_TOKEN);
        }
    }

    private Node createNode() {
        Node node = new Node(new BasicBlock(id++));
        nodes.add(node);
        return node;
    }
}
