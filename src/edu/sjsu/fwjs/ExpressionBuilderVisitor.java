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
		boolean val = Boolean.valueOf(ctx.BOOL().getText()); //Nick: Re-writing with same structure as visitInt
        return new ValueExpr(new BoolVal(val));
    }
	
	public Expression visitNull(FeatherweightJavaScriptParser.NullContext ctx) {
		return new ValueExpr(new NullVal());
	}
    
    public Expression visitWhile(FeatherweightJavaScriptParser.WhileContext ctx)
    {
        Expression cond = visit(ctx.expr());
        Expression block = visit(ctx.block(0));
        return new WhileExpr(cond, block);
    }
    
    public Expression visitPrint(FeatherweightJavaScriptParser.PrintContext ctx)
    {
        return new PrintExpr(visit(ctx.expr())); //Nick: Adding visit()
    }
	
    public Expression visitMulDivMod(FeatherweightJavaScriptParser.MulDivModContext ctx)
    {
        Token token = ctx.op; //Nick: this doesn't get enum but needs one for BinOpExpr?
        Expression expr1 = visit(ctx.expr(0)); //Nick: Adding visit(), fixing index
        Expression expr2 = visit(ctx.expr(1)); //Nick: Adding visit(), fixing index
        return new BinOpExpr(token, expr1, expr2);
    }
    
    public Expression visitAddSub(FeatherweightJavaScriptParser.AddSubContext ctx) //Nick: renamed method
    {
        Op token = ctx.op; //Nick: this doesn't get enum but needs one for BinOpExpr?
        Expression expr1 = visit(ctx.expr(0)); //Nick: Adding visit(), fixing index
        Expression expr2 = visit(ctx.expr(1)); //Nick: Adding visit(), fixing index
        return new BinOpExpr(token, expr1, expr2);
    }
	
	public Expression visitCompare(FeatherweightJavaScriptParser.CompareContext ctx)
	{
		Op token = ctx.op;
        Expression expr1 = visit(ctx.expr(0));
        Expression expr2 = visit(ctx.expr(1));
		return new BinOpExpr(op, expr1, expr2);
	}
    
    public Expression visitFuncDecl(FeatherweightJavaScriptParser.FuncDecl ctx) //Nick: renamed method
    {
        List<String> params = ctx.ID();
        Expression body = visit(ctx.body()); //Nick: Adding visit()
        return new FunctionDeclExpr(params, body);
    }
    
    public Expression visitFuncAppl(FeatherweightJavaScriptParser.FuncApplContext ctx) //Nick: renamed method
    {
        Expression expr = visit(ctx.expr());
		List<Expression> args = new ArrayList<>();
		for (ExprContext ec : ctx.args().expr())
			args.add(visit(ec));
		return new FunctionAppExpr(expr, args);
    }
    
    public Expression visitVarDecl(FeatherweightJavaScriptParser.VarDeclContext ctx) //Nick: renamed method
    {
        String name = ctx.VAR();
        Expression expr = visit(ctx.expr()); //Nick: Adding visit()
        return new VarDeclExpr(name, expr);
    }
	
	public Expression visitVarRef(FeatherweightJavaScriptParser.VarRefContext ctx) {
		return new VarExpr(ctx.ID());
	}
	
	public Expression visitAssign(FeatherweightJavaScriptParser.AssignContext ctx) {
		Expression expr = visit(ctx.expr());
		String var = ctx.ID();
		return new AssignExpr(var, expr);
	}
}