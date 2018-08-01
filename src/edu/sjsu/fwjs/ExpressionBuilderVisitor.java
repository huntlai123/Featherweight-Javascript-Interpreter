package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.fwjs.parser.FeatherweightJavaScriptBaseVisitor;
import edu.sjsu.fwjs.parser.FeatherweightJavaScriptParser;

public class ExpressionBuilderVisitor extends FeatherweightJavaScriptBaseVisitor<Expression>{
    @Override
    public Expression visitProg(FeatherweightJavaScriptParser.ProgContext ctx) {
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=0; i<ctx.stat().size(); i++) {
            Expression exp = visit(ctx.stat(i));
            if (exp != null) stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }

    @Override
    public Expression visitBareExpr(FeatherweightJavaScriptParser.BareExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expression visitIfThenElse(FeatherweightJavaScriptParser.IfThenElseContext ctx) {
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block(0));
        Expression els = visit(ctx.block(1));
        return new IfExpr(cond, thn, els);
    }

    @Override
    public Expression visitIfThen(FeatherweightJavaScriptParser.IfThenContext ctx) {
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block());
        return new IfExpr(cond, thn, null);
    }

    @Override
    public Expression visitInt(FeatherweightJavaScriptParser.IntContext ctx) {
        int val = Integer.valueOf(ctx.INT().getText());
        return new ValueExpr(new IntVal(val));
    }

    @Override
    public Expression visitParens(FeatherweightJavaScriptParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expression visitFullBlock(FeatherweightJavaScriptParser.FullBlockContext ctx) {
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=1; i<ctx.getChildCount()-1; i++) {
            Expression exp = visit(ctx.getChild(i));
            stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }

    /**
     * Converts a list of expressions to one sequence expression,
     * if the list contained more than one expression.
     */
    private Expression listToSeqExp(List<Expression> stmts) {
        if (stmts.isEmpty()) return null;
        Expression exp = stmts.get(0);
        for (int i=1; i<stmts.size(); i++) {
            exp = new SeqExpr(exp, stmts.get(i));
        }
        return exp;
    }

    @Override
    public Expression visitSimpBlock(FeatherweightJavaScriptParser.SimpBlockContext ctx) {
        return visit(ctx.stat());
    }
    
    public Expression visitBool(FeatherweightJavaScriptParser.BoolContext ctx)
    {
        return visit(ctx.stat());
    }
    
    public Expression visitWhile(FeatherweightJavaScriptParser.WhileContext ctx)
    {
        Expression cond = visit(ctx.expr());
        Expression block = visit(ctx.block(0));
        return new WhileExpr(cond, block);
    }
    
    public Expression visitPrint(FeatherweightJavaScriptParser.PrintContext ctx)
    {
        return new PrintExpr(ctx.expr());
    }
    
    public Expression visitEmpty(FeatherweightJavaScriptParser.EmptyContext ctx)
    {
        
    }
    
    public Expression visitMulDivMod(FeatherweightJavaScriptParser.MulDivModContext ctx)
    {
        Op token = ctx.op;
        Expression expr1 = ctx.expr(1);
        Expression expr2 = ctx.expr(2);
        return new BinOpExpr(token, expr1, expr2);
    }
    
    public Expression AddSub(FeatherweightJavaScriptParser.AddSubContext ctx)
    {
        Op token = ctx.op;
        Expression expr1 = ctx.expr(1);
        Expression expr2 = ctx.expr(2);
        return new BinOpExpr(token, expr1, expr2);
    }
    
    public Expression FuncDecl(FeatherweightJavaScriptParser.FuncDecl ctx)
    {
        List<String> params = ctx.ID();
        Expression body = ctx.body();
        return new FunctionDeclExpr(params, body);
    }
    
    public Expression FuncAppl(FeatherweightJavaScriptParser.FuncApplContext ctx)
    {
        
    }
    
    public Expression VarDecl(FeatherweightJavaScriptParser.VarDeclContext ctx)
    {
        String name = ctx.VAR();
        Expression expr = ctx.expr();
        return new VarDeclExpr(name, expr);
    }
}