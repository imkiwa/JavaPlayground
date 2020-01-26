package com.imkiva.hi.tree;

import java.util.List;

public class Expr extends Node {
    public Expr typed;

    public static class App extends Expr {
        public static class AppUni extends App {
            public Universe universe;

            public AppUni(Universe u) {
                this.universe = u;
            }
        }

        public static class AppArg extends App {
            public Atom atom;
            public List<Argument> arguments;
        }
    }

    public static abstract class Callable extends Expr implements ICallable {
        public List<Tele> teles;
        public Expr body;
    }

    public static class Lam extends Callable {
    }

    public static class Pi extends Callable {
    }

    public boolean isTyped() {
        return typed != null;
    }
}
