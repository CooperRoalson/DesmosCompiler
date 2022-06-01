public class Expression {
    public enum ExpressionType {
        OPERATOR,
        VARIABLE,
        TYPE,
        NUM_LITERAL,
        LIST,
        INDEX
    }

    private Expression[] children;
    private ExpressionType type;
    private Token srcToken;

    public Expression(ExpressionType type, Expression[] children, Token srcToken) {
        this(type, children.length, srcToken);
        this.children = children;
    }
    public Expression(ExpressionType type, int numChildren, Token srcToken) {
        this.type = type;
        children = new Expression[numChildren];
        this.srcToken = srcToken;
    }

    public void setChild(int i, Expression e) {
        children[i] = e;
    }
    public Expression getChild(int i) {
        return children[i];
    }
    public Token getSrcToken() {
        return srcToken;
    }
    public ExpressionType getType() {return type;}

    public String compile() {
        switch(type) {
            default:
                return "";
        }
    }

}
