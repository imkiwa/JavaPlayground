package com.imkiva.playground.parser;

/**
 * The simple Arith Language syntax are as follows:
 * <p>
 * program := expr;
 * <p>
 * expr := literalExpr
 *       | ifExpr
 *       | applyExpr
 *       | '(' expr ')'
 *       ;
 * <p>
 * literalExpr := "true"
 *              | "false"
 *              | [0-9]+
 *              ;
 * <p>
 * ifExpr := 'if' expr 'then' expr 'else' expr ;
 * <p>
 * applyExpr := ID expr ;
 * <p>
 * ID := [a-zA-Z_$][a-zA-Z_$0-9]* ;
 *
 * @author kiva
 * @date 2020/1/11
 */
public class ArithParser {
    public static void main(String[] args) {
        run("1");
        run("false");
        run("if true then (id if false then 233 else 666) else 0");
        run("if false then 0 " +
                "else if true then 1 " +
                "else 2");
    }

    private static void run(String src) {
        Parser parser = new Parser(src);
        Program program = parser.program();
        Program optimized = Optimizer.optimize(program);

        System.out.println("======== Parsing code [" + src + "]");
        System.out.println(":: Directly evaluated: " + optimized.eval());
        System.out.println();
        System.out.println(":: Generate code for unoptimized code: ");
        System.out.println(CodeGenerator.genHaskell(program));
        System.out.println();
        System.out.println(":: Generate code for optimized code: ");
        System.out.println(CodeGenerator.genHaskell(optimized));
        System.out.println("\n");
    }

    static class Token {
        enum Type {
            KEYWORD_IF,     // 'if'
            KEYWORD_THEN,   // 'then'
            KEYWORD_ELSE,   // 'else'
            LITERAL_BOOL,   // 'true', 'false'
            LITERAL_INT,    // '[0-9]+'
            ID,             // identifier
            LPAREN,         // '('
            RPAREN,         // ')'
            EOF,            // end-of-file
        }

        public Type tokenType;
        public String tokenText;

        public Token(Type tokenType, String tokenText) {
            this.tokenType = tokenType;
            this.tokenText = tokenText;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "tokenType: " + tokenType +
                    ", tokenText: '" + tokenText + '\'' +
                    '}';
        }
    }

    private static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }

    private static class Lexer {
        private static final int EOF = -1;

        private char[] src;
        private int position;

        public Lexer(String src) {
            this.src = src.toCharArray();
            this.position = 0;
        }

        public Token nextToken() {
            int ch = peek();

            if (ch == '(') {
                match(ch);
                return new Token(Token.Type.LPAREN, "(");
            }

            if (ch == ')') {
                match(ch);
                return new Token(Token.Type.RPAREN, ")");
            }

            if (isDigit(ch)) {
                // LITERAL_INT
                int literal = 0;
                while (isDigit(ch)) {
                    literal = literal * 10 + (ch - '0');
                    match(ch);
                    ch = peek();
                }
                return new Token(Token.Type.LITERAL_INT, String.valueOf(literal));
            }

            if (isIdStart(ch)) {
                StringBuilder builder = new StringBuilder();
                while (isIdPart(ch)) {
                    builder.append((char) ch);
                    match(ch);
                    ch = peek(false);
                }

                String tokenText = builder.toString();

                switch (tokenText) {
                    case "true":
                    case "false":
                        return new Token(Token.Type.LITERAL_BOOL, tokenText);
                    case "if":
                        return new Token(Token.Type.KEYWORD_IF, tokenText);
                    case "else":
                        return new Token(Token.Type.KEYWORD_ELSE, tokenText);
                    case "then":
                        return new Token(Token.Type.KEYWORD_THEN, tokenText);
                    default:
                        return new Token(Token.Type.ID, tokenText);
                }
            }

            return new Token(Token.Type.EOF, "<EOF>");
        }

        private boolean isDigit(int ch) {
            return ch >= '0' && ch <= '9';
        }

        private boolean isIdPart(int ch) {
            return isIdStart(ch) || isDigit(ch);
        }

        private boolean isIdStart(int ch) {
            return (ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || ch == '_'
                    || ch == '$';
        }

        private boolean isEOF() {
            return position >= src.length;
        }

        private int peek() {
            return peek(true);
        }

        private int peek(boolean skipWs) {
            if (isEOF()) {
                return EOF;
            }

            int c = src[position];

            // skip linebreaks
            while (c == '\n' || c == '\r') {
                if (position == src.length - 1) {
                    return EOF;
                }
                c = src[++position];
            }

            // skip whitespaces if required
            while (skipWs && Character.isSpaceChar(c)) {
                if (position == src.length - 1) {
                    return EOF;
                }
                c = src[++position];
            }
            return c;
        }

        private int next() {
            int c = peek();
            ++position;
            return c;
        }

        private void match(int expected) {
            if (isEOF()) {
                throw new ParseException("Expected " + expected + ", but got <EOF>");
            }
            int got = next();
            if (expected != got) {
                throw new ParseException("Expected " + expected + ", but got " + got);
            }
        }
    }

    static class EvalException extends RuntimeException {
        public EvalException(String message) {
            super(message);
        }
    }

    static abstract class Node {
        public abstract Object eval();
    }

    static class Program extends Node {
        public Expr expr;

        public Program(Expr expr) {
            this.expr = expr;
        }

        public Object eval() {
            return expr.eval();
        }
    }

    static abstract class Expr extends Node {
    }

    static class LiteralExpr extends Expr {
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

    static class IfExpr extends Expr {
        public Expr condition;
        public Expr trueExpr;
        public Expr falseExpr;

        public IfExpr(Expr condition, Expr trueExpr, Expr falseExpr) {
            this.condition = condition;
            this.trueExpr = trueExpr;
            this.falseExpr = falseExpr;
        }

        @Override
        public Object eval() {
            Object cond = condition.eval();
            if (cond instanceof Boolean) {
                boolean condResult = (Boolean) cond;
                return condResult ? trueExpr.eval() : falseExpr.eval();
            }
            throw new EvalException("Integers cannot be cast to boolean implicitly");
        }
    }

    static class ApplyExpr extends Expr {
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

    private static class Parser {
        private Lexer lexer;

        private Parser(String src) {
            lexer = new Lexer(src);
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

    static class Optimizer {
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

    static class CodeGenException extends RuntimeException {
        public CodeGenException(String message) {
            super(message);
        }
    }

    static class CodeGenerator {
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
}
