package org.bmstu.iu9.scanner;

import org.bmstu.iu9.tokens.Token;

public interface IScanner {
    Token getNextToken();

    Token peekNextToken();
}
