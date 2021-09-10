package org.bmstu.iu9.scanner;

import org.bmstu.iu9.tokens.EndToken;
import org.bmstu.iu9.tokens.Token;
import org.bmstu.iu9.tokens.TokenType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Scanner implements IScanner {
    private final java.util.Scanner scanner;
    private Token peekedToken;

    public Scanner(String fileName) throws IOException {
        scanner = new java.util.Scanner(new FileInputStream(fileName));
    }

    @Override
    public Token getNextToken() {
        if (peekedToken != null) {
            Token t = peekedToken;
            peekedToken = null;
            return t;
        }
        if (!scanner.hasNext()) {
            return EndToken.getInstance();
        }

        String value = scanner.next();
        int tag = scanner.nextInt();

        return new Token(value, Arrays.stream(TokenType.values()).filter(t -> t.ordinal() == tag)
                .findFirst().get());
    }

    @Override
    public Token peekNextToken() {
        if (peekedToken != null) {
            return peekedToken;
        }
        peekedToken = getNextToken();
        return peekedToken;
    }
}
