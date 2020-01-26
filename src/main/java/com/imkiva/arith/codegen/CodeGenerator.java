package com.imkiva.arith.codegen;

import com.imkiva.arith.parser.Token;
import com.imkiva.arith.tree.abs.*;

public class CodeGenerator {
    public static String genHaskell(Program program) {
        return "main :: IO ()\n"
                + "main = putStrLn $ show $ "
                + genExpr(program.expr);
    }

    public static String genExpr(Expr expr) {
        if (expr.getClass() == IfExpr.class) {
            return genIfExpr(((IfExpr) expr));

        } else if (expr.getClass() == LiteralExpr.class) {
            return genLiteralExpr(((LiteralExpr) expr));

        } else if (expr.getClass() == ApplyExpr.class) {
            return genApplyExpr(((ApplyExpr) expr));
        }

        throw new CodeGenException("should never reach here");
    }

    private static String genApplyExpr(ApplyExpr expr) {
        return String.format("(%s (%s))", expr.idToken.tokenText,
                genExpr(expr.argument));
    }

    private static String genLiteralExpr(LiteralExpr expr) {
        if (expr.token.tokenType == Token.Type.LITERAL_BOOL) {
            switch (expr.token.tokenText) {
                case "true":
                    return "True";
                case "false":
                    return "False";
            }
        }
        return expr.token.tokenText;
    }

    private static String genIfExpr(IfExpr expr) {
        return String.format("if (%s) then (%s) else (%s)",
                genExpr(expr.condition),
                genExpr(expr.trueExpr),
                genExpr(expr.falseExpr));
    }
}
