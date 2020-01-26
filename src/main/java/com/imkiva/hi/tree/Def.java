package com.imkiva.hi.tree;

import java.util.List;

public class Def extends Node {
    public static class Func extends Def implements ICallable {
        public static class Body extends Node {
            public static class WithoutElim extends Body {
                public Expr expr;
            }
        }

        public String name;
        public List<Tele> teles;
        public Expr returnExpr;
        public Body body;
    }
}
