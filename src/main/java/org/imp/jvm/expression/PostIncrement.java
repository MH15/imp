package org.imp.jvm.expression;

import org.imp.jvm.compiler.Logger;
import org.imp.jvm.domain.Operator;
import org.imp.jvm.domain.scope.Scope;
import org.imp.jvm.exception.SemanticErrors;
import org.objectweb.asm.MethodVisitor;

public class PostIncrement extends Expression {
    public final Expression expression;
    public final Operator op;

    private AssignmentExpression assignmentExpression;

    public PostIncrement(Expression expression, Operator op) {
        this.expression = expression;
        this.op = op;
    }

    @Override
    public void generate(MethodVisitor mv, Scope scope) {
        assignmentExpression.generate(mv, scope);
    }

    @Override
    public void validate(Scope scope) {
        expression.validate(scope);
        if (expression.type.isNumeric()) {
            var incrementer = new Arithmetic(expression, new Literal(expression.type, "1"), op);
            this.assignmentExpression = new AssignmentExpression(expression, incrementer);
        } else {
            Logger.syntaxError(SemanticErrors.IncrementInvalidType, getCtx());
        }
    }
}
