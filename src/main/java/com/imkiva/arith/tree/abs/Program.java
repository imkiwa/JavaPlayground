package com.imkiva.arith.tree.abs;

public class Program extends Node {
    public Expr expr;

    public Program(Expr expr) {
        this.expr = expr;
    }

    public Object eval() {
        return expr.eval();
    }
}
