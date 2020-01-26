package com.imkiva.arith.tree.abs;

import com.imkiva.arith.parser.EvalException;
import com.imkiva.arith.parser.Token;

public class LiteralExpr extends Expr {
    public Token token;

    public LiteralExpr(Token token) {
        this.token = token;
    }

    @Override
    public Object eval() {
        switch (token.tokenType) {
            case LITERAL_INT:
                return Integer.parseInt(token.tokenText);
            case LITERAL_BOOL:
                return token.tokenText.equals("true");
        }

        throw new EvalException("should never reach here");
    }
}
