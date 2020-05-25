package com.imkiva.playground.jdk14;

public class Java14 {
    static class Node {
    }

    abstract static class Expr extends Node {
    }

    static class Add extends Expr {
        Expr l;
        Expr r;

        public Add(Expr l, Expr r) {
            this.l = l;
            this.r = r;
        }
    }

    static class Lit extends Expr {
        int lit;

        public Lit(int lit) {
            this.lit = lit;
        }
    }

    public static int run(Expr e) {
        if (e instanceof Add a) {
            return run(a.l) + run(a.r);
        } else if (e instanceof Lit l) {
            return l.lit;
        } else {
            throw new RuntimeException("Invalid node");
        }
    }

    public static void main(String[] args) {
        var prog = new Add(new Lit(3), new Add(new Lit(5), new Lit(7)));
        System.out.println(run(prog));
    }
}
