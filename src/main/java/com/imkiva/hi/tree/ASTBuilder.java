package com.imkiva.hi.tree;

import com.imkiva.hi.parser.HiLexer;
import com.imkiva.hi.parser.HiParser;
import com.imkiva.hi.parser.HiParser.*;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.stream.Collectors;

public class ASTBuilder {
    public static Program build(String src) {
        HiLexer lexer = new HiLexer(CharStreams.fromString(src));
        HiParser parser = new HiParser(new BufferedTokenStream(lexer));
        return buildProgram(parser.program());
    }

    private static Program buildProgram(ProgramContext programCtx) {
        return new Program(programCtx.statement().stream()
                .map(ASTBuilder::buildStatement)
                .collect(Collectors.toList()));
    }

    private static Statement buildStatement(StatementContext statementCtx) {
        return new Statement(buildDef(statementCtx.definition()));
    }

    private static Def buildDef(DefinitionContext defCtx) {
        if (defCtx instanceof DefFuncContext) {
            return buildDefFunc(((DefFuncContext) defCtx));
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Def.Func buildDefFunc(DefFuncContext ctx) {
        Def.Func func = new Def.Func();
        func.name = ctx.ID().getSymbol().getText();
        func.teles = ctx.tele().stream()
                .map(ASTBuilder::buildTele)
                .collect(Collectors.toList());
        if (ctx.returnExpr() != null) {
            func.returnExpr = buildReturnExpr(ctx.returnExpr());
        }
        func.body = buildFuncBody(ctx.funcBody());
        return func;
    }

    private static Def.Func.Body buildFuncBody(FuncBodyContext ctx) {
        if (ctx instanceof WithoutElimContext) {
            Def.Func.Body.WithoutElim body = new Def.Func.Body.WithoutElim();
            body.expr = buildExpr(((WithoutElimContext) ctx).expr());
            return body;
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildReturnExpr(ReturnExprContext ctx) {
        if (ctx instanceof ReturnExprExprContext) {
            return buildExpr(((ReturnExprExprContext) ctx).expr());
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildExpr(ExprContext ctx) {
        if (ctx instanceof AppContext) {
            return buildAppExpr(((AppContext) ctx).appExpr());
        }

        if (ctx instanceof PiContext) {
            PiContext piContext = (PiContext) ctx;
            Expr.Pi pi = new Expr.Pi();
            pi.body = buildExpr(piContext.expr());
            pi.teles = piContext.tele().stream()
                    .map(ASTBuilder::buildTele)
                    .collect(Collectors.toList());
            return pi;
        }

        if (ctx instanceof LamContext) {
            LamContext piContext = (LamContext) ctx;
            Expr.Lam lam = new Expr.Lam();
            lam.body = buildExpr(piContext.expr());
            lam.teles = piContext.tele().stream()
                    .map(ASTBuilder::buildTele)
                    .collect(Collectors.toList());
            return lam;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildAppExpr(AppExprContext ctx) {
        if (ctx instanceof UniverseContext) {
            Universe u = parseUniverse(((UniverseContext) ctx).UNIVERSE());
            return new Expr.App.AppUni(u);
        }

        if (ctx instanceof ArgumentAppContext) {
            ArgumentAppContext argCtx = (ArgumentAppContext) ctx;
            Expr.App.AppArg arg = new Expr.App.AppArg();
            arg.atom = buildAtom(argCtx.atom());
            arg.arguments = argCtx.argument().stream()
                    .map(ASTBuilder::buildArgument)
                    .collect(Collectors.toList());
            return arg;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Atom buildAtom(AtomContext ctx) {
        if (ctx instanceof AtomLiteralContext) {
            return new Atom.AtomLit(
                    buildLiteral(((AtomLiteralContext) ctx).literal()));
        }

        if (ctx instanceof AtomNumberContext) {
            return new Atom.AtomNum(Integer.parseInt(
                    ((AtomNumberContext) ctx).NUMBER().getSymbol().getText()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Argument buildArgument(ArgumentContext ctx) {
        if (ctx instanceof ArgumentExplicitContext) {
            return new Argument.ArgExpr(
                    buildAtom(((ArgumentExplicitContext) ctx).atom()));
        }

        if (ctx instanceof ArgumentImplicitContext) {
            return new Argument.ArgExpr(
                    buildExpr(((ArgumentImplicitContext) ctx).expr()));
        }

        if (ctx instanceof ArgumentUniverseContext) {
            return new Argument.ArgUni(
                    parseUniverse(((ArgumentUniverseContext) ctx).universeAtom().UNIVERSE()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildTypedExpr(TypedExprContext ctx) {
        if (ctx instanceof NotTypedContext) {
            return buildExpr(((NotTypedContext) ctx).expr());
        }

        if (ctx instanceof TypedContext) {
            TypedContext typedContext = (TypedContext) ctx;
            Expr expr = buildExpr(typedContext.expr(0));
            expr.typed = buildExpr(typedContext.expr(1));
            return expr;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Tele buildTele(TeleContext ctx) {
        if (ctx instanceof TeleLiteralContext) {
            Literal lit = buildLiteral(((TeleLiteralContext) ctx).literal());
            return new Tele.TeleLit(lit);
        }

        if (ctx instanceof TeleUniverseContext) {
            UniverseAtomContext c = ((TeleUniverseContext) ctx).universeAtom();
            Universe u = parseUniverse(c.UNIVERSE());
            return new Tele.TeleUni(u);
        }

        if (ctx instanceof ImplicitContext) {
            return new Tele.TeleExpr(Plicit.IMPLICIT,
                    buildTypedExpr(((ImplicitContext) ctx).typedExpr()));
        }

        if (ctx instanceof ExplicitContext) {
            return new Tele.TeleExpr(Plicit.EXPLICIT,
                    buildTypedExpr(((ExplicitContext) ctx).typedExpr()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Literal buildLiteral(LiteralContext ctx) {
        if (ctx instanceof NameContext) {
            return new Literal(((NameContext) ctx).ID().getSymbol().getText());
        }

        if (ctx instanceof UnknownContext) {
            return Literal.UNKNOWN;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Universe parseUniverse(TerminalNode node) {
        String text = node.getSymbol().getText();
        String levelText = text.substring("\\Type".length()).trim();

        int level = levelText.isEmpty() ? 0 : Integer.parseInt(levelText);
        return new Universe(level);
    }
}
