package org.bmstu.iu9.tokens;

public class EndToken extends Token {
    private static final Token INSTANCE = new EndToken();

    public static Token getInstance() {
        return INSTANCE;
    }

    private EndToken() {
        super("$", TokenType.END);
    }
}
