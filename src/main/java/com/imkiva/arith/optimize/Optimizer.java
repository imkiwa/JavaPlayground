package com.imkiva.arith.optimize;

import com.imkiva.arith.parser.Token;
import com.imkiva.arith.tree.abs.*;

public class Optimizer {
    public static Program optimize(Program program) {
        return new Program(foldExpr(program.expr));
    }

    public static Expr foldExpr(Expr expr) {
        if (expr.getClass() == IfExpr.class) {
            return foldIf(((IfExpr) expr));
        }

        if (expr.getClass() == ApplyExpr.class) {
            return foldApply(((ApplyExpr) expr));
        }

        return expr;
    }

    private static Expr foldApply(ApplyExpr expr) {
        if (expr.idToken.tokenText.equals("id")) {
            return foldExpr(expr.argument);
        }
        return expr;
    }

    private static Expr foldIf(IfExpr expr) {
        if (expr.condition.getClass() != LiteralExpr.class) {
            return expr;
        }

        LiteralExpr cond = ((LiteralExpr) expr.condition);
        if (cond.token.tokenType != Token.Type.LITERAL_BOOL) {
            return expr;
        }

        return cond.token.tokenText.equals("true")
                ? foldExpr(expr.trueExpr)
                : foldExpr(expr.falseExpr);
    }
}
