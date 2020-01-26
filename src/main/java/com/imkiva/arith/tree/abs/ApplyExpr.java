package com.imkiva.arith.tree.abs;

import com.imkiva.arith.parser.EvalException;
import com.imkiva.arith.parser.Token;

public class ApplyExpr extends Expr {
    public Token idToken;
    public Expr argument;

    public ApplyExpr(Token idToken, Expr argument) {
        this.idToken = idToken;
        this.argument = argument;
    }

    @Override
    public Object eval() {
        if (idToken.tokenText.equals("id")) {
            return argument.eval();
        }

        // TODO: support more functions
        throw new EvalException("Function not found: " + idToken.tokenText);
    }
}
