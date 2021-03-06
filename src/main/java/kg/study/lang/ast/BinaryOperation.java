package kg.study.lang.ast;

import kg.study.lang.lexer.Symbol;
import kg.study.lang.parser.ParserContext;

public abstract class BinaryOperation extends Node {

    protected Node left;
    protected Node right;

    protected BinaryOperation(NodeType type, Node left, Node right) {
        super(type);
        this.right = right;
        this.left = left;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public static Node parse(Node left, ParserContext ctx) {
        switch ((Symbol) ctx.nextToken()) {
            case PLUS:
                return new SumNode(left, ExpressionNode.parse(ctx));
            case MINUS:
                return new SubNode(left, ExpressionNode.parse(ctx));
            case LT:
                return new LTNode(left, ExpressionNode.parse(ctx));
            default:
                throw new UnsupportedOperationException("Not implemented yet for symbol : " + ctx.currentToken);
        }
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
