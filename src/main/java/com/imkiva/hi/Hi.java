package com.imkiva.hi;

import com.imkiva.hi.tree.ASTBuilder;
import com.imkiva.hi.tree.Program;

public class Hi {
    public static void main(String[] args) {
        show("\\func id {A : \\Type} (a : A) => a");
    }

    private static void show(String src) {
        Program program = ASTBuilder.build(src);
        System.out.println(program);
    }
}
