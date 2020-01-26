package com.imkiva.arith.tree.abs;

import com.imkiva.arith.parser.EvalException;

public class IfExpr extends Expr {
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
