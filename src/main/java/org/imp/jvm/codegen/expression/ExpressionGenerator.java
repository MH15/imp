package org.imp.jvm.codegen.expression;

import org.imp.jvm.domain.expression.*;
import org.imp.jvm.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;

public class ExpressionGenerator {
    private final RelationalExpressionGenerator relationalExpressionGenerator;
    private final LiteralExpressionGenerator literalExpressionGenerator;
    private final AdditiveGenerator additiveGenerator;

    public ExpressionGenerator(MethodVisitor methodVisitor, Scope scope) {
        relationalExpressionGenerator = new RelationalExpressionGenerator(this, methodVisitor);
        literalExpressionGenerator = new LiteralExpressionGenerator(methodVisitor);
        additiveGenerator = new AdditiveGenerator(this, methodVisitor);
    }

    public void generate(FunctionCall functionCall) {
//        callExpressionGenerator.generate(functionCall);
    }

    public void generate(RelationalExpression relationalExpression) {
        relationalExpressionGenerator.generate(relationalExpression);
    }

    public void generate(Literal literal) {
        literalExpressionGenerator.generate(literal);
    }


    public void generate(AdditiveExpression additiveExpression) {
        additiveGenerator.generate(additiveExpression);
    }
}
