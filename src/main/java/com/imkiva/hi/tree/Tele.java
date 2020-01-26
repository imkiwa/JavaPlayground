package com.imkiva.hi.tree;

public class Tele extends Node {
    public static class TeleLit extends Tele {
        public Literal literal;

        public TeleLit(Literal lit) {
            this.literal = lit;
        }
    }

    public static class TeleUni extends Tele {
        public Universe universe;

        public TeleUni(Universe u) {
            this.universe = u;
        }
    }

    public static class TeleExpr extends Tele {
        public Plicit plicit;
        public Expr expr;

        public TeleExpr(Plicit plicit, Expr expr) {
            this.plicit = plicit;
            this.expr = expr;
        }

        public Plicit getPlicit() {
            return plicit;
        }
    }
}
