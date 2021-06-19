package org.imp.jvm.expression;

import org.imp.jvm.compiler.Logger;
import org.imp.jvm.domain.CompareSign;
import org.imp.jvm.domain.scope.Scope;
import org.imp.jvm.domain.types.BuiltInType;
import org.imp.jvm.exception.SemanticErrors;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Logical extends Expression {
    public final Expression left;
    public final Expression right;
    public final Operator operator;

    public enum Operator {
        AND,
        OR
    }

    public Logical(Expression left, Expression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.type = left.type;
    }

    public void generate(MethodVisitor mv, Scope scope) {
        // Generated bytecode is slightly different between operators.
        
        if (operator == Operator.AND) {
            Label failureLabel = new Label(); // short-circuit evaluation, if left is false, skip remaining clauses and return "false"
            Label successLabel = new Label(); // both clauses are truthy, return "true"

            left.generate(mv, scope); // get the truthiness of the left expression
            mv.visitInsn(Opcodes.ICONST_1); // get "true" constant, actually "1"
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, failureLabel); // if the left expression is false, skip to end

            right.generate(mv, scope); // get the truthiness of the right expression
            mv.visitInsn(Opcodes.ICONST_1); // get "true" constant, actually "1"
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, failureLabel); // if the right expression is false, skip to end

            // if the right expression isn't false, here we store a success value of "1" and skip to the end
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitJumpInsn(Opcodes.GOTO, successLabel);

            mv.visitLabel(failureLabel);
            mv.visitInsn(Opcodes.ICONST_0);

            mv.visitLabel(successLabel);
        } else if (operator == Operator.OR) {

            Label failureLabel = new Label(); //
            Label successLabel = new Label(); // both clauses are truthy, return "true"
            Label endLabel = new Label();

            left.generate(mv, scope); // get the truthiness of the left expression
            mv.visitInsn(Opcodes.ICONST_1); // get "true" constant, actually "1"
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, successLabel); // if the left expression is true, skip to end

            right.generate(mv, scope); // get the truthiness of the right expression
            mv.visitInsn(Opcodes.ICONST_1); // get "true" constant, actually "1"
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, failureLabel); // if the right expression is not true, skip to end

            mv.visitLabel(successLabel);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);

            mv.visitLabel(failureLabel);
            mv.visitInsn(Opcodes.ICONST_0);

            mv.visitLabel(endLabel);

        } else {
            Logger.syntaxError(SemanticErrors.ImplementationError, left.getCtx());
        }

    }

    @Override
    public void validate(Scope scope) {
        left.validate(scope);
        right.validate(scope);

        if (left.type != BuiltInType.BOOLEAN) {
            Logger.syntaxError(SemanticErrors.LogicalOperationInvalidType, left.getCtx());
        }
        if (right.type != BuiltInType.BOOLEAN) {
            Logger.syntaxError(SemanticErrors.LogicalOperationInvalidType, right.getCtx());
        }
    }

}
