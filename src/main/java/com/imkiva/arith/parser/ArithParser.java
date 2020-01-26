package com.imkiva.arith.parser;

import com.imkiva.arith.tree.abs.*;

/**
 * @author kiva
 * @date 2020/1/11
 */
public class ArithParser {
    private ArithLexer lexer;

    private ArithParser(String src) {
        lexer = new ArithLexer(src);
    }

    public Program program() {
        return new Program(expr());
    }

    public Expr expr() {
        Token token = nextToken();
        switch (token.tokenType) {
            case LITERAL_INT:
            case LITERAL_BOOL:
                return literalExpr(token);

            case KEYWORD_IF:
                return ifExpr();

            case ID:
                return applyExpr(token);

            case LPAREN:
                return quotedExpr();
        }

        throw new ParseException("Unexpected token " + token.tokenText
                + ", expected <expr>");
    }

    private Expr literalExpr(Token literalToken) {
        return new LiteralExpr(literalToken);
    }

    private IfExpr ifExpr() {
        Expr condition = expr();

        Token thenToken = nextToken();
        if (thenToken.tokenType != Token.Type.KEYWORD_THEN) {
            throw new ParseException("Unexpected token "
                    + thenToken.tokenText + ", expected 'then'");
        }

        Expr trueExpr = expr();

        Token elseToken = nextToken();
        if (elseToken.tokenType != Token.Type.KEYWORD_ELSE) {
            throw new ParseException("Unexpected token "
                    + elseToken.tokenText + ", expected 'else'");
        }

        Expr falseExpr = expr();

        return new IfExpr(condition, trueExpr, falseExpr);
    }

    private ApplyExpr applyExpr(Token idToken) {
        Expr expr = expr();
        return new ApplyExpr(idToken, expr);
    }

    private Expr quotedExpr() {
        Expr body = expr();
        Token rparen = nextToken();
        if (rparen.tokenType != Token.Type.RPAREN) {
            throw new ParseException("Unexpected token "
                    + rparen.tokenText + ", expected ')'");
        }
        return body;
    }

    private Token nextToken() {
        Token token = lexer.nextToken();
        if (token.tokenType == Token.Type.EOF) {
            throw new ParseException("Unexpected <EOF>");
        }
        return token;
    }
}
