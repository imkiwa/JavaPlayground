package com.imkiva.hi.tree;

import java.util.List;

public class Program extends Node {
    private List<Statement> statementList;

    public Program(List<Statement> statementList) {
        this.statementList = statementList;
    }
}
