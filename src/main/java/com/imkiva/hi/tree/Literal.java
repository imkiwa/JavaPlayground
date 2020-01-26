package com.imkiva.hi.tree;

public class Literal extends Node {
    public static Literal UNKNOWN = new Literal("_");

    public String name;

    public Literal(String name) {
        this.name = name;
    }

    public boolean isUnknown() {
        return this == UNKNOWN
                || "_".equals(name);
    }
}
