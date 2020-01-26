package com.imkiva.arith.parser;

public class ArithLexer {
    private static final int EOF = -1;

    private char[] src;
    private int position;

    public ArithLexer(String src) {
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
