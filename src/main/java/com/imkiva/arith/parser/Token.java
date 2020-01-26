package com.imkiva.arith.parser;

public class Token {
    public enum Type {
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
