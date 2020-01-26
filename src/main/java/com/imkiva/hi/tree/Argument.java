package com.imkiva.hi.tree;

public class Argument extends Node {
    public static class ArgUni extends Argument {
        public Universe universe;

        public ArgUni(Universe u) {
            this.universe = u;
        }
    }

    public static class ArgExpr extends Argument {
        public Plicit plicit;
        public Atom explicitAtom;
        public Expr implicitExpr;

        public ArgExpr(Atom explicitAtom) {
            this.explicitAtom = explicitAtom;
            this.plicit = Plicit.EXPLICIT;
        }

        public ArgExpr(Expr implicitExpr) {
            this.implicitExpr = implicitExpr;
            this.plicit = Plicit.IMPLICIT;
        }
    }
}
