package org.imp.jvm.parsing.visitor.expression;

import org.imp.jvm.ImpParser;
import org.imp.jvm.ImpParserBaseVisitor;
import org.imp.jvm.compiler.Logger;
import org.imp.jvm.domain.CompareSign;
import org.imp.jvm.domain.ImpFile;
import org.imp.jvm.domain.Operator;
import org.imp.jvm.domain.scope.Identifier;
import org.imp.jvm.types.UnknownType;
import org.imp.jvm.exception.SemanticErrors;
import org.imp.jvm.expression.*;
import org.imp.jvm.domain.scope.LocalVariable;
import org.imp.jvm.domain.scope.Scope;
import org.imp.jvm.types.BuiltInType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExpressionVisitor extends ImpParserBaseVisitor<Expression> {
    private final LiteralVisitor literalVisitor;
    private final ArithmeticVisitor arithmeticVisitor;

    private final Scope scope;
    private final ImpFile parent;


    public ExpressionVisitor(Scope scope, ImpFile parent) {
        this.scope = scope;
        this.parent = parent;
        literalVisitor = new LiteralVisitor(this);
        arithmeticVisitor = new ArithmeticVisitor(this);
    }

    @Override
    public Expression visitWrappedExpression(ImpParser.WrappedExpressionContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public LocalVariableReference visitIdentifierReferenceExpression(ImpParser.IdentifierReferenceExpressionContext ctx) {
        String name = ctx.getText();

        LocalVariable local = scope.getLocalVariable(name);
        if (scope.variableExists(name)) {

        }
        LocalVariable localVariable = new LocalVariable(name, BuiltInType.INT);

        return new LocalVariableReference(local);
    }


    @Override
    public Literal visitLiteral(ImpParser.LiteralContext ctx) {
        return literalVisitor.visitLiteral(ctx);
    }

    @Override
    public Expression visitUnaryNotExpression(ImpParser.UnaryNotExpressionContext ctx) {
        return super.visitUnaryNotExpression(ctx);
    }


    @Override
    public Relational visitRelationalExpression(ImpParser.RelationalExpressionContext ctx) {
        Expression left = ctx.expression(0).accept(this);
        Expression right = ctx.expression(1).accept(this);
        // ToDo: operator overloading for comparison
        CompareSign compareSign = CompareSign.fromString(ctx.cmp.getText());
        return new Relational(left, right, compareSign);
    }

    @Override
    public Expression visitLogicalExpression(ImpParser.LogicalExpressionContext ctx) {
        Expression left = ctx.expression(0).accept(this);
        left.setCtx(ctx.expression(0));
        Expression right = ctx.expression(1).accept(this);
        right.setCtx(ctx.expression(1));

        Logical.Operator operator = Logical.Operator.AND;
        if (ctx.OR() != null) {
            operator = Logical.Operator.OR;
        }

        return new Logical(left, right, operator);
    }

    @Override
    public FunctionCall visitCallStatementExpression(ImpParser.CallStatementExpressionContext ctx) {
        // Todo: At this point we do not know of any functions that exist.
        // Todo: error handling for nonexistent function signatures. integrate into new error handling pass

        ImpParser.CallStatementContext callCtx = ctx.callStatement();

        // Function name
        String functionName = callCtx.identifier().getText();

        // Function argument expressions
        var arguments = callCtx.expressionList().expression();
        var argExpressions = arguments.stream().map(a -> a.accept(this)).collect(Collectors.toList());

        // Function Call
        FunctionCall call = new FunctionCall(functionName, argExpressions, parent);

        return call;

    }

    private List<Identifier> getArgumentsForCall(List<ImpParser.ExpressionContext> argumentsListCtx) {
        if (argumentsListCtx != null) {
            return argumentsListCtx.stream()
                    .map(arg -> arg.accept(this).type)
                    .map(argType -> {
                        var ident = new Identifier();
                        ident.type = argType;
                        return ident;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Override
    public Arithmetic visitAdditiveExpression(ImpParser.AdditiveExpressionContext ctx) {
        return arithmeticVisitor.visitAdditiveExpression(ctx);
    }

    @Override
    public Expression visitPostIncrementExpression(ImpParser.PostIncrementExpressionContext ctx) {
        Operator op = Operator.ADD;
        if (ctx.DEC() != null) {
            op = Operator.SUBTRACT;
        }
        var expression = ctx.expression().accept(this);
        var postType = expression.type;
        if (postType.isNumeric()) {
            var incrementer = new Arithmetic(expression, new Literal(postType, "1"), op);
            return new AssignmentExpression(expression, incrementer);

        } else {
            Logger.syntaxError(SemanticErrors.IncrementInvalidType, ctx.expression());
            return new EmptyExpression(postType);
        }

        // Todo: maybe this should be moved to the Validation pass?
        // But then we'd need a class for IncrementExpression
    }

    @Override
    public Expression visitAssignmentExpression(ImpParser.AssignmentExpressionContext ctx) {
        Expression recipient = ctx.expression(0).accept(this);
        Expression provider = ctx.expression(1).accept(this);
        return new AssignmentExpression(recipient, provider);
    }

    @Override
    public StructInit visitNewObjectExpression(ImpParser.NewObjectExpressionContext ctx) {
        String structName = ctx.identifier().getText();
        List<Expression> expressions = new ArrayList<>();

        var expressionList = ctx.expressionList();
        if (expressionList != null) {
            var expressionContexts = ctx.expressionList().expression();

            for (var expCtx : expressionContexts) {
                Expression exp = expCtx.accept(this);
                expressions.add(exp);
            }

        }

//        Struct struct = new Struct()
//        if (struct.)
        // Todo: use SemanticErrors
        var si = new StructInit(structName, expressions, null);
        si.setCtx(ctx.identifier());
        return si;
    }

    @Override
    public Expression visitPropertyAccessExpression(ImpParser.PropertyAccessExpressionContext ctx) {
        Expression structExpr = ctx.expression().accept(this);
        LocalVariableReference structRef = (LocalVariableReference) structExpr;


        List<ImpParser.IdentifierContext> fieldPathCtx = ctx.identifier(); // list of identifiers in the chain


        // Types on struct fields have not settled yet, so all we know is a list of field names.
        List<Identifier> fieldPath = new ArrayList<>();
        for (var e : fieldPathCtx) {
            var ident = new Identifier(e.getText(), new UnknownType(e.getText()));
            ident.setCtx(e);
            fieldPath.add(ident);
        }


        // This could be an attempt to reference fields that do not exist.
        // Validation of the field path is performed later.
        StructPropertyAccess access = new StructPropertyAccess(structRef, fieldPath);
        access.setCtx(ctx);
        return access;
    }


}
