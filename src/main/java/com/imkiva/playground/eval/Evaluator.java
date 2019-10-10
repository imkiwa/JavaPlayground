package com.imkiva.playground.eval;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiva
 * @date 2019-10-10
 */
public class Evaluator {
    private static double eval(String text) {
        Parser parser = new Parser(text);
        return parser.expr().eval().getValue();
    }

    private static void eval(String text, double value) {
        assert eval(text) == value;
    }

    public static void main(String[] args) {
        eval("1 + 2 + 3", 1 + 2 + 3);
        eval("1 + 2 * 3", 1 + 2 * 3);
        eval("1 * 2 + 3", 1 * 2 + 3);
        eval("5 + 6 * 2 + 3", 5 + 6 * 2 + 3);
        eval("5 + 6 * (2 + 3)", 5 + 6 * (2 + 3));
        eval("5 + 6.5 * (2 + 3)", 5 + 6.5 * (2 + 3));
        System.out.println("Yeah!");
    }

    static class Node {
    }

    static class Token {
    }

    static class Number extends Token {
        double value;

        public Number(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(getValue());
        }
    }

    static class Operator extends Token {
        enum OperatorType {
            ADD, SUB, MUL, DIV
        }

        OperatorType operatorType;

        Operator(OperatorType operatorType) {
            this.operatorType = operatorType;
        }

        public Number apply(Number lhs, Number rhs) {
            double lhsD = lhs.getValue();
            double rhsD = rhs.getValue();
            switch (operatorType) {
                case ADD: return new Number(lhsD + rhsD);
                case SUB: return new Number(lhsD - rhsD);
                case MUL: return new Number(lhsD * rhsD);
                case DIV: return new Number(lhsD / rhsD);
            }
            return new Number(0);
        }
    }

    static abstract class Expr extends Node {
        AdditiveExpr expr;

        abstract Number eval();
    }

    static class AdditiveExpr extends Expr {
        MultiplicativeExpr lhs;
        List<AdditiveExprRest> rests;

        @Override
        Number eval() {
            Number value = lhs.eval();
            for (var rest: rests) {
                value = rest.op.apply(value, rest.rhs.eval());
            }
            return value;
        }
    }

    static class AdditiveExprRest {
        MultiplicativeExpr rhs;
        Operator op;
    }

    static class MultiplicativeExpr extends Expr {
        Term lhs;
        List<MultiplicativeExprRest> rests;

        @Override
        Number eval() {
            Number value = lhs.eval();
            for (var rest: rests) {
                value = rest.op.apply(value, rest.rhs.eval());
            }
            return value;
        }
    }

    static class MultiplicativeExprRest {
        Term rhs;
        Operator op;
    }

    static class Term extends Expr {
        QuotedExpr quotedExpr;
        LiteralExpr literalExpr;

        public Term(QuotedExpr quotedExpr) {
            this.quotedExpr = quotedExpr;
        }

        public Term(LiteralExpr literalExpr) {
            this.literalExpr = literalExpr;
        }

        @Override
        Number eval() {
            return quotedExpr != null ? quotedExpr.eval() : literalExpr.eval();
        }
    }

    static class QuotedExpr extends Expr {
        Expr expr;

        public QuotedExpr(Expr expr) {
            this.expr = expr;
        }

        @Override
        Number eval() {
            return expr.eval();
        }
    }

    static class LiteralExpr extends Expr {
        Number literal;

        public LiteralExpr(Number literal) {
            this.literal = literal;
        }

        @Override
        Number eval() {
            return literal;
        }
    }

    static class ParseFailed extends RuntimeException {
        public ParseFailed(String message) {
            super(message);
        }
    }

    static class Parser {
        private static final char EOF = Character.UNASSIGNED;

        private int position = 0;
        private char[] text;

        public Parser(String text) {
            this.text = text.toCharArray();
        }

        public Expr expr() {
            return additiveExpr();
        }

        public AdditiveExpr additiveExpr() {
            var expr = new AdditiveExpr();
            expr.lhs = multiplicativeExpr();

            expr.rests = new ArrayList<>();
            var rest = additiveExprRest();
            while (rest != null) {
                expr.rests.add(rest);
                rest = additiveExprRest();
            }
            return expr;
        }

        public AdditiveExprRest additiveExprRest() {
            var op = opAddOrSub();
            if (op == null) {
                return null;
            }
            var rest = new AdditiveExprRest();
            rest.op = op;
            rest.rhs = multiplicativeExpr();
            return rest;
        }

        public MultiplicativeExpr multiplicativeExpr() {
            var expr = new MultiplicativeExpr();
            expr.lhs = term();

            expr.rests = new ArrayList<>();
            var rest = multiplicativeExprRest();
            while (rest != null) {
                expr.rests.add(rest);
                rest = multiplicativeExprRest();
            }
            return expr;
        }

        public MultiplicativeExprRest multiplicativeExprRest() {
            var op = opMulOrDiv();
            if (op == null) {
                return null;
            }
            var rest = new MultiplicativeExprRest();
            rest.op = op;
            rest.rhs = term();
            return rest;
        }

        public QuotedExpr quotedExpr() {
            match('(');
            var expr = expr();
            match(')');
            return new QuotedExpr(expr);
        }

        public LiteralExpr literalExpr() {
            return new LiteralExpr(number());
        }

        public Term term() {
            if (peek() == '(') {
                return new Term(quotedExpr());
            } else if (Character.isDigit(peek())) {
                return new Term(literalExpr());
            }
            throw new ParseFailed("Expected digit or '(', but got " + peek());
        }

        private Number number() {
            boolean floatingPart = false;
            int integral = 0;
            int floating = 0;
            int floatingCount = 1;

            while (true) {
                char c = peek();
                if (c == '.') {
                    if (floatingPart) {
                        throw new ParseFailed("Expected digit, but got '.'(dot)");
                    }
                    match(c);
                    floatingPart = true;
                } else if (Character.isDigit(c)) {
                    match(c);
                    if (floatingPart) {
                        floating = floating * 10 + c - '0';
                        floatingCount *= 10;
                    } else {
                        integral = integral * 10 + c - '0';
                    }
                } else {
                    break;
                }
            }

            double value = 1.0 * integral + 1.0 * floating / floatingCount;
            return new Number(value);
        }

        private Operator opAddOrSub() {
            char c = peek();
            switch (c) {
                case '+':
                    match('+');
                    return new Operator(Operator.OperatorType.ADD);
                case '-':
                    match('-');
                    return new Operator(Operator.OperatorType.SUB);
            }
            return null;
        }

        private Operator opMulOrDiv() {
            char c = peek();
            switch (c) {
                case '*':
                    match('*');
                    return new Operator(Operator.OperatorType.MUL);
                case '/':
                    match('/');
                    return new Operator(Operator.OperatorType.DIV);
            }
            return null;
        }

        private boolean isEOF() {
            return position >= text.length;
        }

        private char peek() {
            if (isEOF()) {
                return EOF;
            }

            char c = text[position];
            while (c == ' ') {
                c = text[++position];
            }
            return c;
        }

        private char next() {
            char c = peek();
            ++position;
            return c;
        }

        private void match(char expected) {
            if (isEOF()) {
                throw new ParseFailed("Expected " + expected + ", but got <EOF>");
            }
            char got = next();
            if (expected != got) {
                throw new ParseFailed("Expected " + expected + ", but got " + got);
            }
        }
    }
}
